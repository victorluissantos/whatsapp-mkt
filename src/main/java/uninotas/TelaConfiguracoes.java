package uninotas;

import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;

public class TelaConfiguracoes {
	public static TableView<DadosLinha> table;

    public VBox criarTela(FirefoxAutomation automation, BorderPane rootWindow, TableView<DadosLinha> lTable, String[] colunas) {
    	table = lTable;
        
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(20));

        // TextArea com label
        Label mensagemLabel = new Label("Escreva aqui a mensagem a ser enviada:");
        TextArea mensagemTextArea = new TextArea("Olá [NOME]! Sou o UniChat da Uninotas.");
        mensagemTextArea.setPrefHeight(100);

        // Área de texto explicativo
        Label explicacaoLabel = new Label("Explicação:");
        Label explicacaoTexto = new Label("""
                [NOME] = referência à coluna "nome" do CSV importado - mala direta
                [DATA] = envia a data de hoje, ex: 03/01/2025
                [HORA] = envia a hora de hoje, ex: 12:32
                [DATA_HORA] = envia data e hora de hoje, ex: 03/01/2025 12:32
                Você pode utilizar isso para qualquer coluna do seu CSV. Exemplo:
                [CUPOM] = irá substituir a tag pela coluna cupom do CSV importado.
                """);
        explicacaoTexto.setStyle("-fx-font-size: 12; -fx-text-fill: #555;");
        VBox textoBox = new VBox(5, explicacaoLabel, explicacaoTexto);

        // Container horizontal para TextArea e explicação
        HBox textAreaBox = new HBox(10, mensagemTextArea, textoBox);
        HBox.setHgrow(mensagemTextArea, Priority.ALWAYS);

        // Linha horizontal (ajustada para ocupar toda a largura)
        Line linha1 = criarLinhaCompleta();
        Line linha2 = criarLinhaCompleta();

        // Configuração de horário
        Label inicioLabel = new Label("Início:");
        ComboBox<String> horaInicio = new ComboBox<>();
        ComboBox<String> minutoInicio = new ComboBox<>();
        preencherHorario(horaInicio, minutoInicio);
        horaInicio.setValue("08");
        minutoInicio.setValue("30");

        HBox inicioBox = new HBox(10, inicioLabel, horaInicio, minutoInicio);

        Label finalLabel = new Label("Final:");
        ComboBox<String> horaFinal = new ComboBox<>();
        ComboBox<String> minutoFinal = new ComboBox<>();
        preencherHorario(horaFinal, minutoFinal);
        horaFinal.setValue("17");
        minutoFinal.setValue("30");

        HBox finalBox = new HBox(10, finalLabel, horaFinal, minutoFinal);

        // Checkbox para dias da semana
        VBox diasSemanaBox = new VBox(5);
        String[] diasSemana = {"Domingo", "Segunda-feira", "Terça-feira", "Quarta-feira", "Quinta-feira", "Sexta-feira", "Sábado"};
        for (int i = 0; i < diasSemana.length; i++) {
            CheckBox diaCheckBox = new CheckBox(diasSemana[i]);
            if (i >= 1 && i <= 5) {
                diaCheckBox.setSelected(true); // Segunda a sexta marcados por padrão
            }
            diasSemanaBox.getChildren().add(diaCheckBox);
        }

        // Input: Quantas campanhas nos últimos 7 dias
        Label campanhasLabel = new Label("Quantas campanhas realizou nos últimos 7 dias:");
        ComboBox<String> campanhasSelect = new ComboBox<>();
        campanhasSelect.getItems().addAll("Nenhuma", "Uma", "Duas", "Três", "Quatro");
        campanhasSelect.setValue("Nenhuma");

        VBox campanhasBox = new VBox(5, campanhasLabel, campanhasSelect);

        // Botões no rodapé
        HBox rodapeBox = new HBox(10);
        rodapeBox.setPadding(new Insets(10, 0, 0, 0));

        Button voltarButton = new Button("<< Voltar");
        voltarButton.setOnAction(event -> Janela.drawCampanhaTab(automation, rootWindow, table, colunas));

        Button iniciarEnvioButton = new Button("Iniciar Envio >>");
        iniciarEnvioButton.setMaxWidth(Double.MAX_VALUE); // Botão grande de ponta a ponta
        iniciarEnvioButton.setOnAction(event -> {
            if (mensagemTextArea.getText().isEmpty()) {
                Alert alerta = new Alert(Alert.AlertType.WARNING);
                alerta.setTitle("Aviso");
                alerta.setHeaderText("Mensagem obrigatória");
                alerta.setContentText("Por favor, escreva uma mensagem antes de avançar.");
                alerta.showAndWait();
            } else {
                // Coleta os dados do formulário
                String mensagem = mensagemTextArea.getText();
                
                // Coleta os valores selecionados nas ComboBox de hora e minuto
                String horaInicioSelecionada = horaInicio.getValue();
                String minutoInicioSelecionado = minutoInicio.getValue();
                String horaFinalSelecionada = horaFinal.getValue();
                String minutoFinalSelecionado = minutoFinal.getValue();
                
                // Renomeando a variável para evitar conflito
                boolean[] diasSemanaSelecionados = new boolean[7]; // Mudança do nome da variável
                for (int i = 0; i < diasSemanaSelecionados.length; i++) {
                    diasSemanaSelecionados[i] = ((CheckBox) diasSemanaBox.getChildren().get(i)).isSelected();
                }
                
                // Coleta a campanha realizada
                String campanhaRealizada = campanhasSelect.getValue();
                
                // Cria um objeto Configuracoes com os dados do formulário
                ConfiguracoesVO configuracoes = new ConfiguracoesVO(mensagem, horaInicioSelecionada, minutoInicioSelecionado,
                        horaFinalSelecionada, minutoFinalSelecionado, diasSemanaSelecionados, campanhaRealizada);

                Janela.drawWhatsappTab(automation, rootWindow, table, colunas, configuracoes);
            }
        });

        HBox.setHgrow(iniciarEnvioButton, Priority.ALWAYS);
        rodapeBox.getChildren().addAll(voltarButton, iniciarEnvioButton);

        vbox.getStyleClass().add("vbox");
        mensagemLabel.getStyleClass().add("label");
        iniciarEnvioButton.getStyleClass().add("button");
        
        // Adicionando os elementos ao VBox principal
        vbox.getChildren().addAll(
                mensagemLabel,
                textAreaBox,
                linha1,
                inicioBox,
                finalBox,
                linha2,
                diasSemanaBox,
                campanhasBox,
                rodapeBox
        );

        return vbox;
    }

    private void preencherHorario(ComboBox<String> horas, ComboBox<String> minutos) {
        for (int i = 0; i < 24; i++) {
            horas.getItems().add(String.format("%02d", i));
        }
        for (int i = 0; i < 60; i++) {
            minutos.getItems().add(String.format("%02d", i));
        }
    }

    private Line criarLinhaCompleta() {
        Line linha = new Line();
        linha.setStartX(0);
        linha.setEndX(1000); // Largura suficiente para ocupar a tela
        linha.setStyle("-fx-stroke: #ccc; -fx-stroke-width: 1;");
        return linha;
    }
}
