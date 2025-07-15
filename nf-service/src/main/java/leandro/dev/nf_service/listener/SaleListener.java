package leandro.dev.nf_service.listener;

import leandro.dev.nf_service.config.RabbitMQConfig;
import leandro.dev.nf_service.dto.SaleDTO;
import leandro.dev.nf_service.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class SaleListener {

    private final InvoiceService notaFiscalService;

    @RabbitListener(queues = RabbitMQConfig.VENDAS_NOVAS_QUEUE)
    public void receberVenda(SaleDTO venda){
        log.info("Recebida venda da Fila {} - ID: {}",
                RabbitMQConfig.VENDAS_NOVAS_QUEUE, venda.getId());

        try{
            notaFiscalService.processarVenda(venda);
            log.info("Venda Processada com Sucesso - ID: {}",venda.getId());
        }catch (Exception e){
            log.error("Erro ao processar venda ID: {} - {} ", venda.getId(), e.getMessage(),e);
        }
    }



}
