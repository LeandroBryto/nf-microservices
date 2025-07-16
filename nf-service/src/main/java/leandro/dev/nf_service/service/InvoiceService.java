package leandro.dev.nf_service.service;

import jakarta.validation.Valid;
import leandro.dev.nf_service.config.RabbitMQConfig;
import leandro.dev.nf_service.dto.InvoiceDTO;
import leandro.dev.nf_service.dto.SaleDTO;
import leandro.dev.nf_service.generator.PdfGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class InvoiceService {

    private final PdfGenerator pdfGenerator;
    private final RabbitTemplate rabbitTemplate;

    @Value("${app.pdf.storage.path:/tmp/notas-fiscais}")
    private String pdfStoragePath;

    public void processarVenda(SaleDTO venda){
        log.info("Processando venda ID: {} para geração de nota fiscal", venda.getId());

        try {
            // Gerar número da nota fiscal
            String numeroNf = gerarNumeroNotaFiscal();

            // Gerat PDF
            byte[] pdfBytes = pdfGenerator.gererNotaFiscalPdf(venda , numeroNf);

             // Salvar PDF no Sistema de arquivos
            String caminhoArquivo = salvaPdf(pdfBytes , numeroNf);

            InvoiceDTO notaFiscal = criarNotaFiscal(venda,numeroNf,caminhoArquivo);

            // Enviar para próxima fila
            rabbitTemplate.convertAndSend(RabbitMQConfig.NOTAS_EMAIL_QUEUE, notaFiscal);
            log.info("Nota fiscal {} criado e enviada para fila {} - Venda ID: {}",
                    numeroNf,RabbitMQConfig.NOTAS_EMAIL_QUEUE,venda.getId());
        }catch (Exception e){
            log.error("Erro ao processar venda ID: {}- {}", venda.getId(), e.getMessage(), e);
             throw new RuntimeException("Erro ao gerar nota fiscal", e);
        }
    }
    private  String gerarNumeroNotaFiscal(){
        // Gerar número baseado na data e hora atual + UUID
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String uuid = UUID.randomUUID().toString().substring(0,8).toUpperCase();
        return "NF" + timestamp + uuid;
    }
    private String salvaPdf(byte[] pdfBytes, String numeroNf) throws IOException {
        // cria diretório se não existir
        Path diretorio = Paths.get(pdfStoragePath);
        if (!Files.exists(diretorio)){
            Files.createDirectories(diretorio);
            log.info("Diretório criado: {}", diretorio);
        }
        // definir caminho do arquivo
        String nomeArquivo = numeroNf + ".pdf";
        Path caminhoArquivado = diretorio.resolve(nomeArquivo);

        // Salva arquivo
        Files.write(caminhoArquivado, pdfBytes);
        log.info("PDF salva em: {}" , caminhoArquivado);

        return caminhoArquivado.toString();
    }

    private InvoiceDTO criarNotaFiscal(SaleDTO venda , String numeroNf , String caminhoArquivo){
        InvoiceDTO notaFiscal = new InvoiceDTO();

        notaFiscal.setId(UUID.randomUUID());
        notaFiscal.setNumeroNf(numeroNf);
        notaFiscal.setSaleId(venda.getId());
        notaFiscal.setOrderId(venda.getOrderId());
        notaFiscal.setClienteNome(venda.getClienteNome());
        notaFiscal.setClienteEmail(venda.getClienteEmail());
        notaFiscal.setClienteCpf(venda.getClienteCpf());
        notaFiscal.setValor(venda.getValor());
        notaFiscal.setDescricao(venda.getDescricao());
        notaFiscal.setDataEmissao(LocalDateTime.now());
        notaFiscal.setCaminhoArquivoPdf(caminhoArquivo);
        notaFiscal.setStatus("GERADA");

        log.info("Nota fiscal criada - ID: {} número: {} para venda: {}",
                notaFiscal.getId(), numeroNf,venda.getId());
        return notaFiscal;
    }
}
