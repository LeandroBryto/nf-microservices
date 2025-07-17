package leandro.dev.email_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class LogDTO {

    private UUID id;
    private String servico;
    private String tipo;
    private String mensagem;
    private LocalDateTime dataHora;

    public LogDTO(String servico, String tipo, String mensagem){
        this.id = UUID.randomUUID();
        this.servico = servico;
        this.tipo = tipo;
        this.mensagem = mensagem;
        this.dataHora = LocalDateTime.now();
    }
}
