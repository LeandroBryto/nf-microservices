package leandro.dev.pedido_service.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequestDTO {

    @NotBlank(message = "Nome do cliente é obrigatório")
    @Size(min = 2,max = 255,message = "Nome deve ter entre 2 e 255 caracteres")
    private String clienteNome;

    @NotBlank(message = "Email do cliente é obrigatório")
    @Email(message = "Email deve ter um formato válido")
    private String clienteEmail;

    @NotBlank(message = "CPF do cliente é obrigatório")
    @Pattern(regexp = "\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}")
    private String clienteCpf;

    @NotNull(message = "Valor é obrigatório")
    @DecimalMin(value = "0.1",message = "Valor deve ser maior que zero")
    @Digits(integer = 8 , fraction = 2 , message = "Valor deve ter no máximo 8 dígitos inteiros e 2 decimaís")
    private BigDecimal valor;

    @Size(max = 1000 , message = "Descrição deve ter no máximo 1000 caracteres")
    private String descricao;
}
