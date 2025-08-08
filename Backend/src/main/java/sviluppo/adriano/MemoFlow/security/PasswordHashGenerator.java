package sviluppo.adriano.MemoFlow.security; // O qualsiasi altro package che preferisci, es. com.example.utility

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordHashGenerator {

    public static void main(String[] args) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String rawPassword = "Cocomero123"; // <-- Inserire password in chiaro da usare per il test
        String encodedPassword = passwordEncoder.encode(rawPassword);

        System.out.println("--------------------------------------------------");
        System.out.println("Password in chiaro: '" + rawPassword + "'");
        System.out.println("Hash BCrypt generato: '" + encodedPassword + "'");
        System.out.println("--------------------------------------------------");
    }
}