package leandro.dev.pedido_service.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "pedidos")
@Data
public class Order {
    @Id
    @GeneratedValue
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(name = "cliente_nome", nullable = false)
    private String clienteNome;

    @Column(name = "cliente_email", nullable = false)
    private String clienteEmail;

    @Column(name = "cliente_cpf", nullable = false , length = 14)
    private String clienteCpf;

    @Column(name = "valor", nullable = false , precision = 10,scale = 2)
    private BigDecimal valor;

    @Column(name = "descricao", columnDefinition = "TEXT")
    private String descricao;

    @Column(name = "status", nullable = false ,length = 50)
    private String status = "Criado";

    @CreationTimestamp
    @Column(name =  "data_criacao", nullable = false)
    private LocalDateTime dataCriacao;
}
