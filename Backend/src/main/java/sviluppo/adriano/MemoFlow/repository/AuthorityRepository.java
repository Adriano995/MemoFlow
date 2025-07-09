package sviluppo.adriano.MemoFlow.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sviluppo.adriano.MemoFlow.entity.Authority;
import sviluppo.adriano.MemoFlow.enums.AuthorityEnum;

public interface AuthorityRepository extends JpaRepository<Authority, AuthorityEnum> {
}