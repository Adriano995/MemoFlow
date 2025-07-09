package sviluppo.adriano.MemoFlow.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import sviluppo.adriano.MemoFlow.entity.Credenziali;

public interface CredenzialiRepository extends JpaRepository<Credenziali, Long> {

    Optional<Credenziali> findByEmail(String email);

    Optional<Credenziali> findByUtenteId(Long utenteId);

    Optional<Credenziali> findByEmailAndPassword(String email, String password);

    @Query("SELECT c FROM Credenziali c JOIN FETCH c.utente u LEFT JOIN FETCH u.userAuthorities WHERE c.email = :email")
    Optional<Credenziali> findByEmailWithAuthorities(@Param("email") String email);
}