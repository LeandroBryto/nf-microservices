package leandro.dev.pedido_service.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import leandro.dev.pedido_service.dto.OrderRequestDTO;
import leandro.dev.pedido_service.model.Order;
import leandro.dev.pedido_service.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@Import(OrderIntegrationTest.RabbitMockConfig.class)
public class OrderIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        orderRepository.deleteAll();
    }

    @Test
    void deveCriarPedidoComSucesso() throws Exception {
        OrderRequestDTO request = new OrderRequestDTO();
        request.setClienteNome("João Silva");
        request.setClienteEmail("joao@email.com");
        request.setClienteCpf("123.456.789-00");
        request.setValor(new BigDecimal("100.00"));
        request.setDescricao("Produto teste");

        mockMvc.perform(post("/api/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.clienteNome").value("João Silva"))
                .andExpect(jsonPath("$.clienteEmail").value("joao@email.com"))
                .andExpect(jsonPath("$.clienteCpf").value("123.456.789-00"))
                .andExpect(jsonPath("$.valor").value(100.00))
                .andExpect(jsonPath("$.descricao").value("Produto teste"))
                .andExpect(jsonPath("$.status").value("CRIADO"))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.dataCriacao").exists());

        assertEquals(1, orderRepository.count());
        Order pedidoSalvo = orderRepository.findAll().get(0);
        assertEquals("João Silva", pedidoSalvo.getClienteNome());
        assertEquals("joao@email.com", pedidoSalvo.getClienteEmail());
        assertEquals("CRIADO", pedidoSalvo.getStatus());

        verify(rabbitTemplate).convertAndSend(eq("pedidos.criados"), any(Order.class));
    }


    @TestConfiguration
    @ActiveProfiles("test")
    static class RabbitMockConfig {
        @Bean
        @Primary
        public RabbitTemplate rabbitTemplate() {
            return Mockito.mock(RabbitTemplate.class);
        }
    }
}
