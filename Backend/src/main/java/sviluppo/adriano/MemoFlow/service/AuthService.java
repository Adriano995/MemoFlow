package sviluppo.adriano.MemoFlow.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sviluppo.adriano.MemoFlow.entity.Credenziali;
import sviluppo.adriano.MemoFlow.repository.CredenzialiRepository;

import java.util.Optional;

@Service
public class AuthService {

    private CredenzialiRepository credenzialiRepository;

    @Autowired
    public AuthService(CredenzialiRepository credenzialiRepository){
        this.credenzialiRepository = credenzialiRepository;
    }

    public Optional<Credenziali> login(String email, String password) {
        return credenzialiRepository.findByEmailAndPassword(email, password);
    }
}
