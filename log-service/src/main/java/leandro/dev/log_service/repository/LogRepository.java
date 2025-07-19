package leandro.dev.log_service.repository;

import leandro.dev.log_service.enums.TipoLog;
import leandro.dev.log_service.model.Log;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface LogRepository extends JpaRepository<Log, UUID> {

    // Busca por serviço, ordenado pela data/hora decrescente
    List<Log> findByServicoOrderByDataHoraDesc(String servico);

    // Busca por tipo, ordenado pela data/hora decrescente
    List<Log> findByTipoOrderByDataHoraDesc(TipoLog tipo);

    // Busca por serviço e tipo com paginação e ordenação por data/hora decrescente
    Page<Log> findByServicoAndTipoOrderByDataHoraDesc(String servico, TipoLog tipo, Pageable pageable);

    // Busca logs entre duas datas, ordenados por data/hora decrescente
    @Query("SELECT l FROM Log l WHERE l.dataHora BETWEEN :inicio AND :fim ORDER BY l.dataHora DESC")
    List<Log> findByDataHoraBetween(@Param("inicio") LocalDateTime inicio,
                                    @Param("fim") LocalDateTime fim);

    // Contagem de logs por tipo
    @Query("SELECT COUNT(l) FROM Log l WHERE l.tipo = :tipo")
    Long countByTipo(@Param("tipo") TipoLog tipo);

    // Contagem de logs por serviço e tipo
    @Query("SELECT COUNT(l) FROM Log l WHERE l.servico = :servico AND l.tipo = :tipo")
    Long countByServicoAndTipo(@Param("servico") String servico,
                               @Param("tipo") TipoLog tipo);
}
