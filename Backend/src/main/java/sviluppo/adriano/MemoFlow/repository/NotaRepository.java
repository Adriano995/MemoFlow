package sviluppo.adriano.MemoFlow.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sviluppo.adriano.MemoFlow.entity.Nota;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NotaRepository extends JpaRepository<Nota, Long> {

    List<Nota> findByUtenteId(Long utenteId);

    List<Nota> findAllByDataCreazioneBetween(LocalDateTime start, LocalDateTime end);

}