package sviluppo.adriano.MemoFlow.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sviluppo.adriano.MemoFlow.entity.Credenziali;

import java.util.Optional;

public interface CredenzialiRepository extends JpaRepository<Credenziali, Long> {

    Optional<Credenziali> findByEmail(String email);

}
