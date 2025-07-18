package leandro.dev.email_service.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import leandro.dev.email_service.config.RabbitMQConfig;
import leandro.dev.email_service.dto.InvoiceDTO;
import leandro.dev.email_service.dto.LogDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.File;

import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final RabbitTemplate rabbitTemplate;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy  HH:mm:ss");
    private static final NumberFormat CURRENCY_FORMATTER = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
    public void enviarNotaFiscalPorEmail(InvoiceDTO notaFiscal){
        log.info("Enviondo nota fiscal {} por email para: {}",
                notaFiscal.getNumeroNf(), notaFiscal.getClienteEmail());

        try {
            // cria e enviar email
            MimeMessage message = criarMensagemEmail(notaFiscal);
            mailSender.send(message);

            // log sucesso
            String mensagemSucesso = String.format(
                    "Email enviando com sucesso para %s - Nota Fiscsl: %s",
                    notaFiscal.getClienteEmail(), notaFiscal.getNumeroNf()
            );
            enviarLog("Email-service ","SUCESSO", mensagemSucesso);

            log.info("Email enviado com sucesso - NF: {} PARA {}",
                    notaFiscal.getNumeroNf(),notaFiscal.getClienteEmail());
        } catch (Exception e){
            // log erro
            String mensagemErro = String.format(
                    "Erro ao enviar email para %s - Nota Fiscal: %s - Erro: %s",
                    notaFiscal.getClienteEmail(), notaFiscal.getNumeroNf(), e.getMessage()
            );
            enviarLog("email-service", "ERRO", mensagemErro);

            log.error("Erro ao enciar email - NF: {} para: {} - {}",
                    notaFiscal.getNumeroNf(), notaFiscal.getClienteEmail(), e.getMessage(), e);
            throw new RuntimeException("Erro ao enviar email", e);
        }
    }

    private MimeMessage criarMensagemEmail(InvoiceDTO notaFiscal) throws MessagingException{
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true , "UTF-8");

        //configur destinatário e remetente
        helper.setTo(notaFiscal.getClienteEmail());
        helper.setFrom("leandro.brito@iesb.edu.br");
        helper.setSubject("Nota Fiscal Eletrônica - " + notaFiscal.getNumeroNf());

        // cria corpo do email
//
        String corpoEmail = criarCorpoEmail(notaFiscal);
        helper.setText(corpoEmail, true);

        // Anexar PDF

        File arquivoPdf = new File(notaFiscal.getCaminhoArquivoPdf());
        if (arquivoPdf.exists()){
            FileSystemResource arquivo = new FileSystemResource(arquivoPdf);
            helper.addAttachment(notaFiscal.getNumeroNf() + ".pdf",arquivo);
            log.debug("PDF anexado ao email: {}", notaFiscal.getCaminhoArquivoPdf());
        }else {
            log.warn("Arquivo PDF não encontrado: {}" , notaFiscal.getCaminhoArquivoPdf());
        }
        return message;
    }

    private String criarCorpoEmail(InvoiceDTO notaFiscal){
        return String.format("""
             <html>
            <body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333;">
                <div style="max-width: 600px; margin: 0 auto; padding: 20px;">
                    <h2 style="color: #2c3e50; border-bottom: 2px solid #3498db; padding-bottom: 10px;">
                        Nota Fiscal Eletrônica
                    </h2>
                    
                    <p>Prezado(a) <strong>%s</strong>,</p>
                    
                    <p>Sua nota fiscal foi gerada com sucesso! Segue abaixo os detalhes:</p>
                    
                    <div style="background-color: #f8f9fa; padding: 15px; border-radius: 5px; margin: 20px 0;">
                        <h3 style="margin-top: 0; color: #2c3e50;">Detalhes da Nota Fiscal</h3>
                        <p><strong>Número:</strong> %s</p>
                        <p><strong>Data de Emissão:</strong> %s</p>
                        <p><strong>Valor:</strong> %s</p>
                        <p><strong>Descrição:</strong> %s</p>
                    </div>
                    
                    <div style="background-color: #e8f5e8; padding: 15px; border-radius: 5px; margin: 20px 0;">
                        <h3 style="margin-top: 0; color: #27ae60;">Dados do Cliente</h3>
                        <p><strong>Nome:</strong> %s</p>
                        <p><strong>CPF:</strong> %s</p>
                        <p><strong>Email:</strong> %s</p>
                    </div>
                    
                    <p>O arquivo PDF da nota fiscal está anexado a este email.</p>
                    
                    <hr style="border: none; border-top: 1px solid #ddd; margin: 30px 0;">
                    
                    <p style="font-size: 12px; color: #666;">
                        Este é um email automático do sistema <strong>Nota Rápida</strong>.<br>
                        Por favor, não responda a este email.
                    </p>
                    
                    <p style="font-size: 12px; color: #666;">
                        <strong>IDs de Controle:</strong><br>
                        Nota Fiscal: %s<br>
                        Venda: %s<br>
                        Pedido: %s
                    </p>
                </div>
            </body>
            </html>
                """,
                notaFiscal.getClienteNome(),
                notaFiscal.getNumeroNf(),
                notaFiscal.getDataEmissao().format(DATE_FORMATTER),
                CURRENCY_FORMATTER.format(notaFiscal.getValor()),
                notaFiscal.getDescricao() != null ? notaFiscal.getDescricao() : "Produto/Serviço",
                notaFiscal.getClienteNome(),
                notaFiscal.getClienteCpf(),
                notaFiscal.getClienteEmail(),
                notaFiscal.getId(),
                notaFiscal.getSaleId(),
                notaFiscal.getOrderId()
        );

    }
    private void enviarLog(String servico , String tipo , String mensagem){
        try {
            LogDTO logDTO = new LogDTO(servico,tipo,mensagem);
            rabbitTemplate.convertAndSend(RabbitMQConfig.LOGS_QUEUE, logDTO);
            log.debug("Log enviando para fila: {} - {} ", tipo , mensagem);
        }catch (Exception e){
            log.error("Erro ao enviar log para fila: {}",e.getMessage());
        }
    }
}
