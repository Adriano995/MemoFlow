package sviluppo.adriano.MemoFlow.dto.credenzialiDTO;

public class CambiaEmailDTO {

    private Long utenteId;
    private String vecchiaEmail;
    private String nuovaEmail;

    public CambiaEmailDTO(Long utenteId, String vecchiaEmail, String nuovaEmail) {
        this.utenteId = utenteId;
        this.vecchiaEmail = vecchiaEmail;
        this.nuovaEmail = nuovaEmail;
    }

    public Long getUtenteId() {
        return utenteId;
    }

    public void setUtenteId(Long utenteId) {
        this.utenteId = utenteId;
    }

    public String getVecchiaEmail() {
        return vecchiaEmail;
    }

    public void setVecchiaEmail(String vecchiaEmail) {
        this.vecchiaEmail = vecchiaEmail;
    }

    public String getNuovaEmail() {
        return nuovaEmail;
    }

    public void setNuovaEmail(String nuovaEmail) {
        this.nuovaEmail = nuovaEmail;
    }
}
