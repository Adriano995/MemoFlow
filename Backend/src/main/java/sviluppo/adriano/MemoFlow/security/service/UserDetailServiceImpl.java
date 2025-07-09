// File: sviluppo/adriano/MemoFlow/security/service/UserDetailServiceImpl.java
package sviluppo.adriano.MemoFlow.security.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import sviluppo.adriano.MemoFlow.entity.Credenziali;
import sviluppo.adriano.MemoFlow.entity.Utente;
import sviluppo.adriano.MemoFlow.entity.UtenteAuthority; // Importa l'entità UtenteAuthority
import sviluppo.adriano.MemoFlow.repository.CredenzialiRepository;

@Service
public class UserDetailServiceImpl implements UserDetailsService {

    private final CredenzialiRepository credenzialiRepository;

    public UserDetailServiceImpl(CredenzialiRepository credenzialiRepository) {
        this.credenzialiRepository = credenzialiRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Credenziali credenziali = credenzialiRepository.findByEmailWithAuthorities(username)
                .orElseThrow(() -> new UsernameNotFoundException("Utente non trovato con email: " + username));

        Utente utente = credenziali.getUtente();
        if (utente == null) {
            throw new UsernameNotFoundException("Nessun utente associato alle credenziali: " + username);
        }

        return UserPrincipal.build(utente, credenziali);
    }

    public static class UserPrincipal implements UserDetails {
        private Long id;
        private String email;
        private String password;
        private List<? extends GrantedAuthority> authorities;

        public UserPrincipal(Long id, String email, String password,
                             List<? extends GrantedAuthority> authorities) {
            this.id = id;
            this.email = email;
            this.password = password;
            this.authorities = authorities;
        }

        public static UserPrincipal build(Utente utente, Credenziali credenziali) {
            // Estrai le authorities dall'entità Utente tramite la collezione userAuthorities
            List<GrantedAuthority> authorities = utente.getUserAuthorities().stream()
                    .map(UtenteAuthority::getAuthority) // Ottieni l'oggetto Authority da UtenteAuthority
                    .map(authority -> new SimpleGrantedAuthority(authority.getAuthorityEnum().name())) // Ottieni l'Enum e il suo nome
                    .collect(Collectors.toList());

            return new UserPrincipal(
                    utente.getId(),
                    credenziali.getEmail(),
                    credenziali.getPassword(),
                    authorities
            );
        }

        public Long getId() { return id; }
        @Override public String getUsername() { return email; }
        @Override public String getPassword() { return password; }
        @Override public List<? extends GrantedAuthority> getAuthorities() { return authorities; }
        @Override public boolean isAccountNonExpired() { return true; }
        @Override public boolean isAccountNonLocked() { return true; }
        @Override public boolean isCredentialsNonExpired() { return true; }
        @Override public boolean isEnabled() { return true; }
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            UserPrincipal that = (UserPrincipal) o;
            return id != null && id.equals(that.id);
        }
        @Override
        public int hashCode() {
            return id != null ? id.hashCode() : 0;
        }
    }
}