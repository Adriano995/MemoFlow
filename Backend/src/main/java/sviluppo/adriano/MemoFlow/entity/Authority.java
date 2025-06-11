package sviluppo.adriano.MemoFlow.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import sviluppo.adriano.MemoFlow.enums.AuthorityEnum;

@Entity
public class Authority {

    @Id
    @Enumerated(EnumType.STRING)
    private AuthorityEnum authorityEnum; // es: ROLE_USER, ROLE_ADMIN, ROLE_PROPRIETARIO_GRUPPO, ecc.

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
}