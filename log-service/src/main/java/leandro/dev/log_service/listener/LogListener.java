package leandro.dev.log_service.listener;

import leandro.dev.log_service.config.RabbitMQConfig;
import leandro.dev.log_service.dto.LogDTO;
import leandro.dev.log_service.service.LogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class LogListener {

    private final LogService logService;

    @RabbitListener(queues = RabbitMQConfig.LOGS_QUEUE)
    public void receberLog(LogDTO logDTO){
        log.info("Recebido log da fila {} - serviço: {} - Tipo: {}",
                RabbitMQConfig.LOGS_QUEUE, logDTO.getServico(), logDTO.getTipo());
        try {
            logService.salvarLog(logDTO);
            log.debug("Log processado e salvo com sucesso - serviço: {} ", logDTO.getServico());
        }catch (Exception e){
            log.error("Erro ao processar log - serviço: {} - {}",
                    logDTO.getServico(), e.getMessage(), e);
        }
    }
}
