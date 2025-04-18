package uninotas;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;

public class AppMenu {

	public static TableView<DadosLinha> table;
	public static ConfiguracoesVO configuracao;
    private MenuBar menuBar;

    public AppMenu(FirefoxAutomation automation, BorderPane rootWindow, TableView<DadosLinha> lTable, ConfiguracoesVO lConfiguracao) {
        menuBar = new MenuBar();
        table = lTable;
        configuracao = lConfiguracao;

        // Menu WhatsApp
        MenuItem whatsappMenuItem = new MenuItem("WhatsApp");
        whatsappMenuItem.setOnAction(event -> {
            Janela.drawWhatsappTab(automation, rootWindow, table, Janela.colunas, lConfiguracao); // Chama o método estático da Janela
        });

        // Menu Campanha
        MenuItem campanhaMenuItem = new MenuItem("Campanha");
        campanhaMenuItem.setOnAction(event -> {
            Janela.drawCampanhaTab(automation, rootWindow, table, Janela.colunas); // Chama o método estático da Janela
        });

        // Menu Configuracoes
        MenuItem configuracaoMenuItem = new MenuItem("Configuracoes");
        configuracaoMenuItem.setOnAction(event -> {
            Janela.drawConfigTab(automation, rootWindow, table, Janela.colunas); // Chama o método estático da Janela
        });
        // Menu Sair
        MenuItem sairMenuItem = new MenuItem("Sair");
        sairMenuItem.setOnAction(event -> {
            automation.finalizar(); // Chama o método para encerrar o WebDriver
            System.exit(0); // Fecha o aplicativo
        });

        // Criar menus principais
        Menu menuPrincipal = new Menu("Menu");
        menuPrincipal.getItems().addAll(whatsappMenuItem, campanhaMenuItem, configuracaoMenuItem, sairMenuItem);

        // Adicionar menus ao MenuBar
        menuBar.getMenus().add(menuPrincipal);
    }

    public MenuBar getMenuBar() {
        return menuBar;
    }
}
