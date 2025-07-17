package sviluppo.adriano.MemoFlow.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import sviluppo.adriano.MemoFlow.entity.Evento;

public interface EventoRepository extends JpaRepository<Evento, Long>  {

    Optional<Evento> findByUtenteId(Long utenteId);

    Optional<Evento> findAllByDataInizioBetween(LocalDateTime start, LocalDateTime end);

    Optional<Evento> findAllByStato(String stato);

    Optional<Evento> findAllByStatoAndUtenteId(String stato, Long utenteId);

    Optional<Evento> findAllByDataInizioAfterAndUtenteId(LocalDateTime dataInizio, Long utenteId);

    Optional<Evento> findAllByDataFineBeforeAndUtenteId(LocalDateTime dataFine, Long utenteId);

    Optional<Evento> findAllByDataInizioAfterAndDataFineBeforeAndUtenteId(LocalDateTime dataInizio, LocalDateTime dataFine, Long utenteId);
}