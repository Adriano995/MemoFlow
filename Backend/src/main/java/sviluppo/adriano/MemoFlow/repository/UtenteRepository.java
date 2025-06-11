package sviluppo.adriano.MemoFlow.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import sviluppo.adriano.MemoFlow.entity.Utente;

import java.util.Optional;

public interface UtenteRepository extends JpaRepository<Utente, Long> {

    boolean existsByCredenzialiEmail(String email);

    Optional<Utente> findByCredenzialiEmail(String email);

    Optional<Utente> findByCredenzialiEmailIgnoreCase(String email);

}

