package leandro.dev.pedido_service.service;

import leandro.dev.pedido_service.dto.OrderRequestDTO;
import leandro.dev.pedido_service.dto.OrderResponseDTO;
import leandro.dev.pedido_service.model.Order;
import leandro.dev.pedido_service.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private OrderService orderService;

    private OrderRequestDTO orderRequest;
    private Order pedidoSalvo;

    @BeforeEach
    void setUp() {
        orderRequest = new OrderRequestDTO();
        orderRequest.setClienteNome("Leandro barreto");
        orderRequest.setClienteEmail("leandro@gmail.com");
        orderRequest.setClienteCpf("064.123.456-00");
        orderRequest.setValor(new BigDecimal("100.00"));
        orderRequest.setDescricao("Produto de test");

        pedidoSalvo = new Order();
        pedidoSalvo.setId(UUID.randomUUID());
        pedidoSalvo.setClienteNome(orderRequest.getClienteNome());
        pedidoSalvo.setClienteEmail(orderRequest.getClienteEmail());
        pedidoSalvo.setClienteCpf(orderRequest.getClienteCpf());
        pedidoSalvo.setValor(orderRequest.getValor());
        pedidoSalvo.setDescricao(orderRequest.getDescricao());
        pedidoSalvo.setStatus("CRIADO");
        pedidoSalvo.setDataCriacao(LocalDateTime.now());
    }

    @Test
    void deveCriarPedidoComSucesso() {
        when(orderRepository.save(any(Order.class))).thenReturn(pedidoSalvo);

        OrderResponseDTO response = orderService.criaPedido(orderRequest);

        assertNotNull(response);
        assertEquals(pedidoSalvo.getId(), response.getId());
        assertEquals(orderRequest.getClienteNome(), response.getClienteNome());
        assertEquals(orderRequest.getClienteEmail(), response.getClienteEmail());
        assertEquals(orderRequest.getClienteCpf(), response.getClienteCpf());
        assertEquals(orderRequest.getValor(), response.getValor());
        assertEquals(orderRequest.getDescricao(), response.getDescricao());
        assertEquals("CRIADO", response.getStatus());

        verify(orderRepository).save(any(Order.class));
        verify(rabbitTemplate).convertAndSend(eq("pedidos.criados"), any(Order.class));
    }
}
