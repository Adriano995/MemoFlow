package sviluppo.adriano.MemoFlow.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import sviluppo.adriano.MemoFlow.entity.Evento;
import sviluppo.adriano.MemoFlow.enums.EventoStato;

public interface EventoRepository extends JpaRepository<Evento, Long>  {

    
    List<Evento> findAllByUtenteId(Long utenteId);

    List<Evento> findAllByDataInizioBetween(LocalDateTime start, LocalDateTime end);

    List<Evento> findAllByStato(EventoStato stato);

    List<Evento> findAllByStatoAndUtenteId(String stato, Long utenteId);

    List<Evento> findAllByDataInizioAfterAndUtenteId(LocalDateTime dataInizio, Long utenteId);

    List<Evento> findAllByDataFineBeforeAndUtenteId(LocalDateTime dataFine, Long utenteId);

    List<Evento> findAllByDataInizioAfterAndDataFineBeforeAndUtenteId(LocalDateTime dataInizio, LocalDateTime dataFine, Long utenteId);

}