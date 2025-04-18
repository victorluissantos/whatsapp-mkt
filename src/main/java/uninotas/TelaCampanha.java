package uninotas;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

public class TelaCampanha {
	
	public static String[] colunas;

    public VBox criarTela(FirefoxAutomation automation, BorderPane rootWindow, TableView<DadosLinha> table, String[] colunas) {
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(20));

        configurarTabela(table);

        // Botões acima da tabela organizados em HBox
        HBox topButtons = new HBox(10);
        Button importarCSVButton = new Button("Importar CSV");
        Button limparTabelaButton = new Button("Limpar Tabela");
        Button exportarTabelaButton = new Button("Exportar Tabela");
        topButtons.getChildren().addAll(importarCSVButton, limparTabelaButton, exportarTabelaButton);

        // Configurar ações dos botões
        importarCSVButton.setOnAction(event -> importarCSV(table, rootWindow));
        limparTabelaButton.setOnAction(event -> table.getItems().clear());
        exportarTabelaButton.setOnAction(event -> exportarTabela(table, rootWindow));

        // Botões abaixo da tabela organizados em HBox
        HBox bottomButtons = new HBox(10);

        // Botão "Avançar >>" no rodapé
        Button avancarButton = new Button("Avançar >>");
        avancarButton.setMaxWidth(Double.MAX_VALUE); // Botão grande de ponta a ponta
        avancarButton.setOnAction(event -> {
        	if (Janela.colunas == null || Janela.colunas.length == 0) {
        	    Alert alerta = new Alert(Alert.AlertType.WARNING);
        	    alerta.setTitle("Aviso");
        	    alerta.setHeaderText("Importação necessária");
        	    alerta.setContentText("Por favor, importe um arquivo CSV antes de avançar.");
        	    alerta.showAndWait();
        	} else {
        	    Janela.drawConfigTab(automation, rootWindow, table, Janela.colunas);
        	}
        });
        
        if (!table.getItems().isEmpty()) {
            carregarTabelaComItens(table, colunas); // Preenche a tabela com os itens existentes
        }
        
        // Adicionando os elementos ao VBox principal
        vbox.getChildren().addAll(topButtons, table, bottomButtons, avancarButton);

        return vbox;
    }

    private void carregarTabelaComItens(TableView<DadosLinha> lTable, String[] colunas) {
        // Obtém os itens atuais do lTable
        List<DadosLinha> linhas = new ArrayList<>(lTable.getItems());

        if (linhas.isEmpty()) {
            JOptionPane.showMessageDialog(
                    null, 
                    "A tabela está vazia.", 
                    "Aviso", 
                    JOptionPane.WARNING_MESSAGE
                );
            return; // Evita erros ao tentar acessar itens de uma tabela vazia
        }

        configurarTabelaDinamica(colunas, lTable);
        
        // Limpa os itens existentes na tabela e adiciona novamente
        lTable.getItems().clear();
        lTable.getItems().addAll(linhas);
    }

    private void carregarCSV(File arquivo, TableView<DadosLinha> lTable) {
		try (BufferedReader br = new BufferedReader(new FileReader(arquivo))) {
            String cabecalho = br.readLine();
            if (cabecalho == null) return;

            // Configura as colunas dinâmicas com base no cabeçalho do CSV
            colunas = cabecalho.split(",");
            Janela.colunas = colunas;
            configurarTabelaDinamica(colunas, lTable);

            // Carrega os dados das linhas do CSV
            List<DadosLinha> linhas = new ArrayList<>();
            String linha;
            while ((linha = br.readLine()) != null) {
                String[] valores = linha.split(",", -1); // -1 para manter campos vazios
                DadosLinha dados = new DadosLinha(valores, "Pendente");
                linhas.add(dados);
            }

            // Adiciona os itens na tabela
            lTable.getItems().setAll(linhas);
        } catch (IOException e) {
            Alert alertaErro = new Alert(Alert.AlertType.ERROR);
            alertaErro.setTitle("Erro");
            alertaErro.setHeaderText("Erro ao carregar o arquivo CSV.");
            alertaErro.setContentText("Verifique o arquivo e tente novamente.");
            alertaErro.showAndWait();
        }
    }
    
    @SuppressWarnings("unchecked")
	private void configurarTabela(TableView<DadosLinha> lTable) {
    	lTable.getColumns().clear();

        // Coluna fixa "Status"
        TableColumn<DadosLinha, String> statusColuna = new TableColumn<>("Status");
        statusColuna.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Coluna fixa "Ações"
        TableColumn<DadosLinha, Void> acoesColuna = new TableColumn<>("Ações");
        acoesColuna.setCellFactory(param -> new TableCell<>() {
            private final Button button = new Button("X");

            {
                button.setOnAction(event -> {
                    DadosLinha linha = getTableView().getItems().get(getIndex());
                    confirmarExclusao(linha, lTable);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(button);
                }
            }
        });

        // Adiciona colunas fixas
        lTable.getColumns().addAll(statusColuna, acoesColuna);
    }

    private void importarCSV(TableView<DadosLinha> lTable, BorderPane rootWindow) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Selecionar Arquivo CSV");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Arquivos CSV", "*.csv"));

        File arquivo = fileChooser.showOpenDialog((Stage) rootWindow.getScene().getWindow());

        if (arquivo != null) {
        	carregarCSV(arquivo, lTable);
        }
    }

    @SuppressWarnings("unchecked")
	private void configurarTabelaDinamica(String[] colunas, TableView<DadosLinha> lTable) {
        // Evita sobrescrever colunas existentes
        lTable.getColumns().clear();

        // Adiciona colunas dinâmicas baseadas no cabeçalho do CSV
        for (int i = 0; i < colunas.length; i++) {
            int index = i; // Índice usado na lambda
            TableColumn<DadosLinha, String> coluna = new TableColumn<>(colunas[i]);
            coluna.setCellValueFactory(data -> data.getValue().getColuna(index)); // Acessa os dados da linha dinamicamente
            lTable.getColumns().add(coluna);
        }

        // Adiciona colunas fixas ("Status" e "Ações")
        TableColumn<DadosLinha, String> statusColuna = new TableColumn<>("Status");
        statusColuna.setCellValueFactory(new PropertyValueFactory<>("status"));

        TableColumn<DadosLinha, Void> acoesColuna = new TableColumn<>("Ações");
        acoesColuna.setCellFactory(param -> new TableCell<>() {
            private final Button button = new Button("X");

            {
                button.setOnAction(event -> {
                    DadosLinha linha = getTableView().getItems().get(getIndex());
                    confirmarExclusao(linha, lTable);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(button);
                }
            }
        });

        lTable.getColumns().addAll(statusColuna, acoesColuna);
    }

    private void confirmarExclusao(DadosLinha linha, TableView<DadosLinha> lTable) {
        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);
        alerta.setTitle("Confirmação");
        alerta.setHeaderText("Tem certeza que deseja excluir o número: " + linha.getColuna(0).get() + "?");
        alerta.setContentText("Esta ação não poderá ser desfeita.");

        ButtonType sim = new ButtonType("Sim", ButtonBar.ButtonData.OK_DONE);
        ButtonType nao = new ButtonType("Não", ButtonBar.ButtonData.CANCEL_CLOSE);

        alerta.getButtonTypes().setAll(sim, nao);

        alerta.showAndWait().ifPresent(result -> {
            if (result == sim) {
            	lTable.getItems().remove(linha);
            }
        });
    }

    private void exportarTabela(TableView<DadosLinha> lTable, BorderPane rootWindow) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Salvar CSV");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Arquivos CSV", "*.csv"));
        File arquivo = fileChooser.showSaveDialog((Stage) rootWindow.getScene().getWindow());

        if (arquivo != null) {
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(arquivo))) {
                // Escreve cabeçalho
                for (TableColumn<DadosLinha, ?> coluna : lTable.getColumns()) {
                    if (!coluna.getText().equals("Ações")) {
                        bw.write(coluna.getText() + ",");
                    }
                }
                bw.newLine();

                // Escreve linhas
                for (DadosLinha linha : lTable.getItems()) {
                    for (int i = 0; i < linha.getValores().length; i++) {
                        bw.write(linha.getValores()[i] + ",");
                    }
                    bw.newLine();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
