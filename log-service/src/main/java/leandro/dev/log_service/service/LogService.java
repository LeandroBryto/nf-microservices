package leandro.dev.log_service.service;

import jakarta.persistence.Table;
import leandro.dev.log_service.dto.LogDTO;
import leandro.dev.log_service.enums.TipoLog;
import leandro.dev.log_service.model.Log;
import leandro.dev.log_service.repository.LogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class LogService {

    private final LogRepository logRepository;

    @Transactional
    public void  salvarLog(LogDTO logDTO){
        log.info("Salvando log do serviço: {} - Tipo: {} - Mensagem: {}",
                logDTO.getServico(), logDTO.getTipo(), logDTO.getMensagem());

        try {
            Log logEntity = new Log();
            logEntity.setServico(logDTO.getServico());
            logEntity.setTipo(TipoLog.valueOf(logDTO.getTipo()));
            logEntity.setMensagem(logDTO.getMensagem());

            // se dataHora não foi informanda, usar a atual
            if (logDTO.getDataHora() != null){
                logEntity.setDataHora(logDTO.getDataHora());
            }
            Log logSalvo = logRepository.save(logEntity);
            log.debug("Log ao salva com ID: {}", logDTO.getId());
        }catch (Exception e){
            log.error("Erro ao salvo log: {}", e.getMessage(), e);
            throw  new RuntimeException("Erro ao salvar log no banco de dados", e);
        }
    }
    public List<LogDTO> buscarLogsPorServico(String servico){
        log.debug("Buscando logs do serviço: {}", servico);
        List<Log> logs = logRepository.findByServicoOrderByDataHoraDesc(servico);
        return logs.stream().map(this::convertToDTO).collect(Collectors.toList());
    }
    public List<LogDTO> buscarLogsPorTipo(String tipo){
        log.debug("Buscando logs do tipo: {]", tipo);
        TipoLog tipoLog = TipoLog.valueOf(tipo);
        List<Log> logs = logRepository.findByTipoOrderByDataHoraDesc(tipoLog);
        return logs.stream().map(this::convertToDTO).collect(Collectors.toList());
    }
    public Page<LogDTO> buscarLogsComPaginacao(String servico,String tipo, int page, int size){
        log.debug("Buscando logs com paginação - Serviço: {} , Tipo: {}, Page: {}, Size: {}",
                servico,tipo,page,size);
        Pageable pageable = PageRequest.of(page,size);
        TipoLog tipoLog = tipo != null ? TipoLog.valueOf(tipo) : null;

        Page<Log> logs;
        if (servico != null && tipoLog != null){
            logs = logRepository.findByServicoAndTipoOrderByDataHoraDesc(servico,tipoLog,pageable);
        }else {
            logs = logRepository.findAll(pageable);
        }
        return logs.map(this::convertToDTO);
    }
    
    public List<LogDTO> buscarLogsPorPeriodo(LocalDateTime inicio, LocalDateTime fim){
        log.debug("Buscando logs entre {} e {}", inicio , fim);
        List<Log> logs = logRepository.findByDataHoraBetween(inicio, fim);
        return logs.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public Map<String ,Long> obterEstatisticas(){
        log.debug("Obtendo estatísticas de logs");

        Long totalSucesso = logRepository.countByTipo(TipoLog.SUCESSO);
        Long totalErro = logRepository.countByTipo(TipoLog.ERRO);
        Long total = totalSucesso + totalErro;

        return Map.of(
                "total",total,
                "sucessos", totalSucesso,
                "erros", totalErro
        );
    }

    public Map<String , Map<String, Long>> obterEstatitíscasPorServico(){
        log.debug("Obtendo estatísticas por serviço");

        List<Log> todosLogs = logRepository.findAll();

        return todosLogs.stream()
                .collect(Collectors.groupingBy(
                        Log::getServico,
                        Collectors.groupingBy(
                                log -> log.getTipo().name(),
                                Collectors.counting()
                        )
                ));
    }

    private LogDTO convertToDTO(Log log){
        return new LogDTO(
                log.getId(),
                log.getServico(),
                log.getTipo().name(),
                log.getMensagem(),
                log.getDataHora()
        );
    }
}
