package leandro.dev.nf_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SaleDTO {

    private UUID id;
    private UUID orderId;
    private String clienteNome;
    private String clienteEmail;
    private String clienteCpf;
    private BigDecimal valor;
    private String descricao;
    private LocalDateTime dataVenda;
    private String status;

}
