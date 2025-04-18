package uninotas;

// WhatsappAutomation.java
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeOptions;

public class WhatsappAutomation {

    public static void startWhatsappAutomation() {
        // Caminho do ChromeDriver
        System.setProperty("webdriver.chrome.driver", "/home/witcher/Downloads/chromedriver_linux64/chromedriver");
        
        // Configuração dos argumentos do Chrome
        ChromeOptions options = new ChromeOptions();
        options.setBinary("/usr/bin/google-chrome"); // Caminho para o binário do Google Chrome
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
//        options.addArguments("--remote-debugging-port=9222");

        // Configuração do driver Chrome
        WebDriver driver = new ChromeDriver(options);

        // Navegar para o WhatsApp Web
        driver.get("https://web.whatsapp.com");

        try {
            // Esperar o usuário fazer a autenticação (QR code)
            Thread.sleep(30000);  // Espera 30 segundos para o QR Code ser lido

            // Exemplo de interação com o WhatsApp Web: enviar mensagem
            WebElement messageInput = driver.findElement(By.xpath("//div[@contenteditable='true']"));
            messageInput.sendKeys("Mensagem automatizada!");

            // Aguardar e enviar a mensagem
            WebElement sendButton = driver.findElement(By.xpath("//span[@data-icon='send']"));
            sendButton.click();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Fechar o navegador
        // driver.quit();
    }
}
