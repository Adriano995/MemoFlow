package sviluppo.adriano.MemoFlow.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sviluppo.adriano.MemoFlow.entity.Utente;

import java.util.Optional;

public interface UtenteRepository extends JpaRepository<Utente, Long> {

    boolean existsByCredenzialiEmail(String email);

    Optional<Utente> findByCredenzialiEmail(String email);

    Optional<Utente> findByCredenzialiEmailIgnoreCase(String email);

}

