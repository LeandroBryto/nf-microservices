package leandro.dev.pedido_service.service;

import jakarta.transaction.Transactional;
import leandro.dev.pedido_service.config.RabbitMQConfig;
import leandro.dev.pedido_service.dto.OrderRequestDTO;
import leandro.dev.pedido_service.dto.OrderResponseDTO;
import leandro.dev.pedido_service.model.Order;
import leandro.dev.pedido_service.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {
    private final OrderRepository orderRepository;
    private final RabbitTemplate rabbitTemplate;

    @Transactional
    public OrderResponseDTO criaPedido(OrderRequestDTO request){
        log.info("Criando novo pedido para cliente: {}",request.getClienteNome());

        // criar e salvar o pedido
        Order order = new Order();
        order.setClienteNome(request.getClienteNome());
        order.setClienteEmail(request.getClienteEmail());
        order.setClienteCpf(request.getClienteCpf());
        order.setValor(request.getValor());
        order.setDescricao(request.getDescricao());
        order.setStatus("CRIADO");
        order.setDataCriacao(LocalDateTime.now());

        Order perdidoSalvo = orderRepository.save(order);
        log.info("Pedido criado com ID: {}", perdidoSalvo.getId());

        // Envior mensagem para a fila
        try {
            rabbitTemplate.convertAndSend(RabbitMQConfig.PEDIDOS_CRIADOS_QUEUE, perdidoSalvo);
            log.info("Mensagem envioda para fila {} com pedido ID: {}",
                    RabbitMQConfig.PEDIDOS_CRIADOS_QUEUE,perdidoSalvo.getId());
        }catch (Exception e){
            log.error("Erro ao envior mensagem para fila: {}", e.getMessage(),e);
            throw new RuntimeException("Erro ao processar pedido", e);
        }

        // converter para DTO de resposta

        return new OrderResponseDTO(
                perdidoSalvo.getId(),
                perdidoSalvo.getClienteNome(),
                perdidoSalvo.getClienteEmail(),
                perdidoSalvo.getClienteCpf(),
                perdidoSalvo.getValor(),
                perdidoSalvo.getDescricao(),
                perdidoSalvo.getStatus(),
                perdidoSalvo.getDataCriacao()
        );
    }
}
