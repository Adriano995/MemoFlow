package sviluppo.adriano.MemoFlow.dto.CreaDTO;

public class CredenzialiCreateDTO {

    private String email;
    private String password;

    public CredenzialiCreateDTO() {}

    public CredenzialiCreateDTO(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}