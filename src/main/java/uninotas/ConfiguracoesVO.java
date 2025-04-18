package uninotas;

public class ConfiguracoesVO {
	
    private String mensagem;
    private String horaInicio;
    private String minutoInicio;
    private String horaFinal;
    private String minutoFinal;
    private boolean[] diasSemana;
    private String campanhaRealizada;

    public ConfiguracoesVO(String mensagem, String horaInicio, String minutoInicio, String horaFinal, String minutoFinal,
                         boolean[] diasSemana, String campanhaRealizada) {
        this.mensagem = mensagem;
        this.horaInicio = horaInicio;
        this.minutoInicio = minutoInicio;
        this.horaFinal = horaFinal;
        this.minutoFinal = minutoFinal;
        this.diasSemana = diasSemana;
        this.campanhaRealizada = campanhaRealizada;
    }

    // Getters
    public String getMensagem() {
        return mensagem;
    }

    public String getHoraInicio() {
        return horaInicio;
    }

    public String getMinutoInicio() {
        return minutoInicio;
    }

    public String getHoraFinal() {
        return horaFinal;
    }

    public String getMinutoFinal() {
        return minutoFinal;
    }

    public boolean[] getDiasSemana() {
        return diasSemana;
    }

    public String getCampanhaRealizada() {
        return campanhaRealizada;
    }
}
