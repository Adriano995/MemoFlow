package sviluppo.adriano.MemoFlow.enums;

import org.springframework.security.core.GrantedAuthority;

public enum  AuthorityEnum implements GrantedAuthority {
    
     ROLE_USER,
     ROLE_DEVELOPER,
     ROLE_PROPRIETARIO_GRUPPO;

     public String getAuthority(){
          return name();
     }
}