package sviluppo.adriano.MemoFlow.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import sviluppo.adriano.MemoFlow.entity.Authority;
import sviluppo.adriano.MemoFlow.enums.AuthorityEnum;
import sviluppo.adriano.MemoFlow.repository.AuthorityRepository;

import java.util.Arrays;
import java.util.Optional;

@Component
public class DataInitializer implements CommandLineRunner {

    private final AuthorityRepository authorityRepository;

    public DataInitializer(AuthorityRepository authorityRepository) {
        this.authorityRepository = authorityRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // Popola i ruoli Authority nel database se non esistono già
        Arrays.stream(AuthorityEnum.values()).forEach(authorityEnum -> {
            Optional<Authority> existingAuthority = authorityRepository.findById(authorityEnum);
            if (existingAuthority.isEmpty()) {
                Authority newAuthority = new Authority(authorityEnum);
                authorityRepository.save(newAuthority);
                System.out.println("Ruolo '" + authorityEnum.name() + "' salvato nel database.");
            }
        });
    }
}