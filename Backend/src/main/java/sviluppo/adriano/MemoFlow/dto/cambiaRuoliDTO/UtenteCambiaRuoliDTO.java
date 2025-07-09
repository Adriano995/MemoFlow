
package sviluppo.adriano.MemoFlow.dto.cambiaRuoliDTO;

import java.util.Set;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class UtenteCambiaRuoliDTO {

    @NotNull(message = "La lista dei ruoli non può essere null")
    @NotEmpty(message = "La lista dei ruoli non può essere vuota")
    private Set<String> roles;

    public UtenteCambiaRuoliDTO() {}

    public UtenteCambiaRuoliDTO(Set<String> roles) {
        this.roles = roles;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }
}