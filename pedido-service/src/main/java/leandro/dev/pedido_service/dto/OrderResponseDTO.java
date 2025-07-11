package leandro.dev.pedido_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponseDTO {
    private UUID id;
    private String clienteNome;
    private String clienteEmail;
    private String clienteCpf;
    private BigDecimal descricao;
    private String status;
    private LocalDateTime dataCriacao;
}
