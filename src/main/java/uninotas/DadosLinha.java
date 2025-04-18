package uninotas;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class DadosLinha {
    private final String[] valores;
    private final StringProperty status;

    public DadosLinha(String[] valores, String status) {
        this.valores = valores;
        this.status = new SimpleStringProperty(status);
    }

    public String[] getValores() {
        return valores;
    }

    public StringProperty getColuna(int index) {
        return new SimpleStringProperty(valores[index]);
    }

    public StringProperty statusProperty() {
        return status;
    }

    public String getStatus() {
        return status.get();
    }

    public void setStatus(String status) {
        this.status.set(status);
    }
}
