package sviluppo.adriano.MemoFlow.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sviluppo.adriano.MemoFlow.entity.Nota;

import java.time.LocalDateTime;
import java.util.List;

public interface NotaRepository extends JpaRepository<Nota, Long> {

    List<Nota> findByUtenteId(Long utenteId);

    List<Nota> findAllByDataCreazioneBetween(LocalDateTime start, LocalDateTime end);

    // Nuovo metodo che combina i due per filtrare per data e utente
    List<Nota> findAllByDataCreazioneBetweenAndUtenteId(LocalDateTime start, LocalDateTime end, Long utenteId);

}