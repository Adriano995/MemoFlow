package sviluppo.adriano.MemoFlow.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.CascadeType;
import jakarta.persistence.FetchType;
import sviluppo.adriano.MemoFlow.enums.AuthorityEnum;

import java.util.HashSet;
import java.util.Set;

@Entity
public class Authority {

    @Id
    @Enumerated(EnumType.STRING)
    private AuthorityEnum authorityEnum;

    @OneToMany(mappedBy = "authority", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<UtenteAuthority> userAuthorities = new HashSet<>();

    public Authority(){}

    public Authority(AuthorityEnum authorityEnum){
        this.authorityEnum = authorityEnum;
    }

    public AuthorityEnum getAuthorityEnum() {
        return authorityEnum;
    }

    public void setAuthorityEnum(AuthorityEnum authorityEnum) {
        this.authorityEnum = authorityEnum;
    }

    // --- Getter e Setter per le associazioni UtenteAuthority ---
    public Set<UtenteAuthority> getUserAuthorities() {
        return userAuthorities;
    }

    public void setUserAuthorities(Set<UtenteAuthority> userAuthorities) {
        this.userAuthorities = userAuthorities;
    }
}