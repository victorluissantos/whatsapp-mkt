package uninotas;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Task;
import javafx.scene.control.TableView;
import javafx.util.Duration;

public class TelaWhatsapp {

    private ImageView imageView;
    private boolean enviando = false;

    public TelaWhatsapp() {
        this.imageView = new ImageView();
        // Configurar a imagem para manter proporção e centralizar
        imageView.setPreserveRatio(true);
        imageView.setFitHeight(580); // Define a altura máxima visível
    }

    public BorderPane criarTela(FirefoxAutomation automation, BorderPane rootWindow, TableView<DadosLinha> lTable, ConfiguracoesVO configuracoes) {
        BorderPane root = new BorderPane();

        // Seção superior com os contadores de status
        HBox statusBar = criarStatusBar(lTable);
        root.setTop(statusBar);

        // Conteúdo principal com a imagem
        VBox centerContent = criarCenterContent(automation);
        root.setCenter(centerContent);

        // Seção inferior com botões
        HBox buttonBar = criarButtonBar(lTable, automation, configuracoes);
        root.setBottom(buttonBar);

        return root;
    }

    private void atualizarTela(FirefoxAutomation automation) {
        if (!enviando) {
            Image imagemCapturada = automation.capturarTelaComoImagem();
            imageView.setImage(imagemCapturada);
        }
    }
    
    private HBox criarStatusBar(TableView<DadosLinha> lTable) {
        // Contadores baseados no status da tabela
        long pendentes = lTable.getItems().stream().filter(d -> "Pendente".equalsIgnoreCase(d.getStatus())).count();
        long enviados = lTable.getItems().stream().filter(d -> "Enviado".equalsIgnoreCase(d.getStatus())).count();
        long incansavel = lTable.getItems().stream().filter(d -> "Incansável".equalsIgnoreCase(d.getStatus())).count();

        // Labels para os contadores
        Label pendentesLabel = new Label("Pendentes: " + pendentes);
        Label enviadosLabel = new Label("Enviados: " + enviados);
        Label incansavelLabel = new Label("Incansável: " + incansavel);

        // Layout horizontal para os labels
        HBox statusBar = new HBox(20, pendentesLabel, enviadosLabel, incansavelLabel);
        statusBar.setAlignment(Pos.CENTER);
        statusBar.setStyle("-fx-padding: 10; -fx-background-color: #f0f0f0;");

        return statusBar;
    }

    private HBox criarButtonBar(TableView<DadosLinha> lTable, FirefoxAutomation automation, ConfiguracoesVO configuracoes) {
        Button pararButton = new Button("Parar Campanha");
        Button iniciarButton = new Button("Iniciar Campanha");
        Button pausarButton = new Button("Pausar Campanha");

        // Ações dos botões
        pararButton.setOnAction(event -> {
            System.out.println("Parar Campanha clicado");
            // Lógica para parar campanha
        });

        iniciarButton.setOnAction(event -> {
            rodarCampanha(lTable, automation, configuracoes); // Chama o método para rodar a campanha
        });

        pausarButton.setOnAction(event -> {
            System.out.println("Pausar Campanha clicado");
            // Lógica para pausar campanha
        });

        HBox buttonBar = new HBox(10, pararButton, iniciarButton, pausarButton);
        buttonBar.setAlignment(Pos.CENTER);
        buttonBar.setStyle("-fx-padding: 10; -fx-background-color: #f0f0f0;");

        return buttonBar;
    }
    
    private VBox criarCenterContent(FirefoxAutomation automation) {
        // Envolver a imagem em um VBox para centralização
        VBox centerContent = new VBox(imageView);
        centerContent.setAlignment(Pos.CENTER);
        centerContent.setStyle("-fx-padding: 10;"); // Espaçamento ao redor da imagem

        // Atualizar a imagem periodicamente
        Timeline timeline = new Timeline(
            new KeyFrame(Duration.seconds(10), event -> atualizarTela(automation))
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();

        return centerContent;
    }
	    
	private void rodarCampanha(TableView<DadosLinha> lTable, FirefoxAutomation automation, ConfiguracoesVO configuracoes) {
	    // Pausar a atualização automática de imagem
	    enviando = true;
	
	    // Usar Task para enviar mensagens em segundo plano
	    Task<Void> task = new Task<Void>() {
	        @Override
	        protected Void call() throws Exception {
	            // Coleta a listagem da tabela
	            List<DadosLinha> linhas = new ArrayList<>(lTable.getItems());
	
	            for (DadosLinha linha : linhas) {
	            	enviando = true;
	                String status = "Pendente"; // Atualize para obter o valor real da coluna status
	                if ("Pendente".equalsIgnoreCase(status)) {
	                    String telefone = linha.getColuna(0).getValue(); // Atualize para o nome correto da coluna
	                    String url = "https://web.whatsapp.com/send/?phone=" + telefone + "&text=" + makeDinamicMessage(configuracoes.getMensagem(), linha, Janela.colunas);
	
	                    // Navegar para a URL do WhatsApp
	                    automation.navegarPara(url);
	
	                    // Aguardar que a página seja carregada antes de enviar a mensagem
	                    try {
	                        Thread.sleep(15000); // Esperar 15 segundos para garantir que a página está pronta
	                    } catch (InterruptedException e) {
	                        System.err.println("Erro na pausa: " + e.getMessage());
	                    }
	
	                    // Enviar a mensagem
	                    if (automation.enviarMensagem()) {
	                        System.out.println("Mensagem enviada para: " + telefone);
	                        enviando = false;
	                    } else {
	                        System.out.println("Falha ao enviar mensagem para: " + telefone);
	                    }
	
	                    // Atualizar a tela a cada envio
	                    updateMessage("Enviando para: " + telefone); // Mensagem de status
	                    atualizarTela(automation); // Atualizar a tela após cada envio
	
	                    // Pausa adicional entre envios
	                    try {
	                        Thread.sleep(12000); // Pausa de 12 segundos antes do próximo envio
	                    } catch (InterruptedException e) {
	                        System.err.println("Erro na pausa: " + e.getMessage());
	                    }
	                }
	            }
	
	            // Retornar à página inicial do WhatsApp
	            automation.navegarPara("https://web.whatsapp.com/");
	
	            return null;
	        }
	    };
	
	    // Monitorar o progresso e iniciar a execução da Task
	    task.setOnSucceeded(event -> {
	        // Retomar a atualização automática da tela
	        enviando = false;
	        JOptionPane.showMessageDialog(null, "Campanha concluída com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
	    });
	
	    task.setOnFailed(event -> {
	        enviando = false;
	        JOptionPane.showMessageDialog(null, "Erro durante a execução da campanha.", "Erro", JOptionPane.ERROR_MESSAGE);
	    });
	
	    // Executar a tarefa em segundo plano
	    new Thread(task).start();
	}

	private String makeDinamicMessage(String mensagem, DadosLinha linha, String[] colunas) {
	    // Formatação da data e hora
	    String data = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
	    String hora = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
	    String dataHora = data + " " + hora;

	    // Substituindo as chaves para variáveis dinâmicas
	    mensagem = mensagem.replace("[DATA_HORA]", dataHora)
	                       .replace("[DATA]", data)
	                       .replace("[HORA]", hora);
	    
	    // Percorre as colunas e substitui cada chave encontrada na mensagem
	    for (int i = 0; i < colunas.length; i++) {
	        // A chave na mensagem está no formato [CHAVE], então criamos esse padrão
	        String chave = "[" + colunas[i].trim().toUpperCase() + "]";
	        
	        
	        // Obtém o valor como String se for um StringProperty
	        String valor = linha.getColuna(i) instanceof StringProperty
	            ? ((StringProperty) linha.getColuna(i)).get()
	            : linha.getColuna(i).toString();
	        
	        // Substitui a chave pelo valor
	        mensagem = mensagem.replace(chave, valor);
	    }
	    return mensagem;
	}

}
