package sviluppo.adriano.MemoFlow.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import sviluppo.adriano.MemoFlow.entity.Evento;
import sviluppo.adriano.MemoFlow.enums.EventoStato;

public interface EventoRepository extends JpaRepository<Evento, Long>  {

    
    List<Evento> findAllByUtenteId(Long utenteId);

    List<Evento> findAllByDataInizioBetween(LocalDateTime start, LocalDateTime end);

    List<Evento> findAllByStato(EventoStato stato);

    List<Evento> findAllByStatoAndUtenteId(String stato, Long utenteId);

    List<Evento> findAllByDataInizioAfterAndUtenteId(LocalDateTime dataInizio, Long utenteId);

    List<Evento> findAllByDataFineBeforeAndUtenteId(LocalDateTime dataFine, Long utenteId);

        @Query("SELECT e FROM Evento e " +
           "WHERE e.utente.id = :userId " +
           "AND (" +
           "    (e.dataFine IS NULL AND e.dataInizio >= :dataInizio AND e.dataInizio <= :dataFine) " +
           "    OR " +
           "    (e.dataInizio <= :dataFine AND e.dataFine >= :dataInizio)" +
           ")")
    List<Evento> findEventsInDateRange(@Param("dataInizio") LocalDateTime dataInizio, @Param("dataFine") LocalDateTime dataFine, @Param("userId") Long userId);

    @Query("SELECT e FROM Evento e " +
           "WHERE e.utente.id = :userId " +
           "AND (" +
           "    (e.dataFine IS NULL AND e.dataInizio >= :dataInizio AND e.dataInizio <= :dataFine) " +
           "    OR " +
           "    (e.dataInizio <= :dataFine AND e.dataFine >= :dataInizio)" +
           ")")
    List<Evento> findMonthlyEvents(@Param("dataInizio") LocalDateTime dataInizio, @Param("dataFine") LocalDateTime dataFine, @Param("userId") Long userId);

}