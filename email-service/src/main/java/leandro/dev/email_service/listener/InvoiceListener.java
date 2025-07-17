package leandro.dev.email_service.listener;

import leandro.dev.email_service.config.RabbitMQConfig;
import leandro.dev.email_service.dto.InvoiceDTO;
import leandro.dev.email_service.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class InvoiceListener {

    private final EmailService emailService;

    @RabbitListener(queues = RabbitMQConfig.NOTAS_EMAIL_QUEUE)
    public  void  receberNotaFiscal(InvoiceDTO notaFiscal){
        log.info("Recebida nota fiscal da fila {} - Número: {}",
                RabbitMQConfig.NOTAS_EMAIL_QUEUE, notaFiscal.getNumeroNf());

        try {
            emailService.enviarNotaFiscalPorEmail(notaFiscal);
            log.info("Nota fiscal processada com sucesso  - Número: {}", notaFiscal.getNumeroNf());
        }catch (Exception e){
            log.error("Erro ao processar nota fiscal - numero: {} - {}",
                    notaFiscal.getNumeroNf(),e.getMessage(), e);
        }
    }
}
