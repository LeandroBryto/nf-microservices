package leandro.dev.log_service.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import leandro.dev.log_service.enums.TipoLog;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Log {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "servico", nullable = false, length = 100)
    private String servico;

    @Column(name = "tipo", nullable = false , length = 30)
    @Enumerated(EnumType.STRING)
    private TipoLog tipo;

    @Column(name = "mensagem", nullable = false , columnDefinition = "TEXT")
    private  String mensagem;

    @CreationTimestamp
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "data_hora",nullable = false)
    private LocalDateTime dataHora;


}
