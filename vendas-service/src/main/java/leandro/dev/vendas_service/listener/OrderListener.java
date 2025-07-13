package leandro.dev.vendas_service.listener;

import leandro.dev.vendas_service.config.RabbitMQConfig;
import leandro.dev.vendas_service.dto.OrderDTO;
import leandro.dev.vendas_service.service.SaleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderListener {

    private final SaleService saleService;

    @RabbitListener(queues = RabbitMQConfig.PEDIDOS_CRIADOS_QUEUE)
    public void receberPedido(OrderDTO pedido){
        log.info("Recebido pedido da fila {} - ID: {}",
                RabbitMQConfig.PEDIDOS_CRIADOS_QUEUE, pedido.getId());
        try {
            saleService.processorPedido(pedido);
            log.info("Pedido processar com sucesso - ID: {}", pedido.getId());
        }catch (Exception e){
            log.error("Erro  ao  processar  pedido  ID: {} - {}",pedido.getId(),e.getMessage(),e);
        }
    }
}
