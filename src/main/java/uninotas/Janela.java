package uninotas;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class Janela extends Application {

    private Boolean debug = false; // set false to open firefox windows to debug
    private static Janela instance; // Singleton
    private TableView<DadosLinha> table; // Sem inicialização estática
    public static String[] colunas;
    public static ConfiguracoesVO configuracao = new ConfiguracoesVO(
    	    "Mensagem padrão", "08", "30", "17", "30", new boolean[7], "Nenhuma"
    	);

    @Override
    public void start(Stage primaryStage) {
        instance = this;

        table = new TableView<>(); // Inicialize aqui
        BorderPane rootWindow = new BorderPane();
        FirefoxAutomation automation = new FirefoxAutomation(this.debug);
        automation.iniciarWhatsAppWeb();

        Scene scene = new Scene(rootWindow, 1024, 680);
//        scene.getStylesheets().add(getClass().getResource("src/main/resources/styles/style.css").toExternalForm());

        primaryStage.setTitle("Uninota - WA 0.1b");
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        primaryStage.show();

        // Adicionar o menu no topo
        AppMenu appMenu = new AppMenu(automation, rootWindow, table, configuracao);
        rootWindow.setTop(appMenu.getMenuBar());

        // Carregar a aba inicial (WhatsApp)
        drawCampanhaTab(automation, rootWindow, table, colunas);
    }


    public static void drawWhatsappTab(FirefoxAutomation automation, BorderPane rootWindow, TableView<DadosLinha> lTable, String[] lColunas, ConfiguracoesVO configuracoes) {
        colunas = lColunas;
        TelaWhatsapp telaWhatsapp = new TelaWhatsapp();
        rootWindow.setCenter(telaWhatsapp.criarTela(automation, rootWindow, lTable, configuracoes));
    }

    public static void drawCampanhaTab(FirefoxAutomation automation, BorderPane rootWindow, TableView<DadosLinha> lTable, String[] lColunas) {
    	colunas = lColunas;
        TelaCampanha telaCampanha = new TelaCampanha();
        rootWindow.setCenter(telaCampanha.criarTela(automation, rootWindow, lTable, colunas));
    }

    public static void drawConfigTab(FirefoxAutomation automation, BorderPane rootWindow, TableView<DadosLinha> lTable, String[] lColunas) {
    	colunas = lColunas;
        TelaConfiguracoes telaConfiguracoes = new TelaConfiguracoes();
        rootWindow.setCenter(telaConfiguracoes.criarTela(automation, rootWindow, lTable, colunas));
    }

    public static Janela getInstance() {
        return instance;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
