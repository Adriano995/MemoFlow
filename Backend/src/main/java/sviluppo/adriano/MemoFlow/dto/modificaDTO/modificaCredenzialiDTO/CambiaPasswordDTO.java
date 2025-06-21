package sviluppo.adriano.MemoFlow.dto.modificaDTO.modificaCredenzialiDTO;

public class CambiaPasswordDTO {
    private Long utenteId;
    private String vecchiaPassword;
    private String nuovaPassword;

    public CambiaPasswordDTO(Long utenteId, String vecchiaPassword, String nuovaPassword) {
        this.utenteId = utenteId;
        this.vecchiaPassword = vecchiaPassword;
        this.nuovaPassword = nuovaPassword;
    }

    public Long getUtenteId() {
        return utenteId;
    }

    public void setUtenteId(Long utenteId) {
        this.utenteId = utenteId;
    }

    public String getVecchiaPassword() {
        return vecchiaPassword;
    }

    public void setVecchiaPassword(String vecchiaPassword) {
        this.vecchiaPassword = vecchiaPassword;
    }

    public String getNuovaPassword() {
        return nuovaPassword;
    }

    public void setNuovaPassword(String nuovaPassword) {
        this.nuovaPassword = nuovaPassword;
    }
}
