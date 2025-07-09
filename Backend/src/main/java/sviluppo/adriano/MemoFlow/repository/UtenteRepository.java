package sviluppo.adriano.MemoFlow.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import sviluppo.adriano.MemoFlow.entity.Utente;

public interface UtenteRepository extends JpaRepository<Utente, Long> {

    boolean existsByCredenzialiEmail(String email);

    Optional<Utente> findByCredenzialiEmail(String email);

    Optional<Utente> findByCredenzialiEmailIgnoreCase(String email);

    @Query("SELECT u FROM Utente u LEFT JOIN FETCH u.userAuthorities WHERE u.id = :id")
    Optional<Utente> findByIdWithAuthorities(@Param("id") Long id);
}

