package uninotas;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import javafx.scene.image.Image;

import java.time.Duration;
import java.util.Base64;

public class FirefoxAutomation {

    private WebDriver driver;
    private Boolean debug = false;

    // Inicializar o WebDriver
    public FirefoxAutomation(Boolean debug) {
        this.debug = (debug != null) ? debug : false;

        System.setProperty("webdriver.gecko.driver", "/usr/bin/geckodriver");

        FirefoxOptions options = new FirefoxOptions();
        options.addArguments("--no-sandbox", "--disable-dev-shm-usage");
        if (!this.debug) {
            options.addArguments("--headless"); // Se quiser rodar sem interface gráfica
        }

        driver = new FirefoxDriver(options);
    }

    // Navegar para o WhatsApp Web
    public void iniciarWhatsAppWeb() {
        driver.get("https://web.whatsapp.com");
        System.out.println("Navegando para o WhatsApp Web...");
    }

    // Método para capturar a tela em base64
    public String capturarTelaBase64() {
        try {
            TakesScreenshot screenshot = (TakesScreenshot) driver;
            return screenshot.getScreenshotAs(OutputType.BASE64);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Método para capturar a tela como Image (para exibir no JavaFX)
    public Image capturarTelaComoImagem() {
        String base64String = capturarTelaBase64();
        byte[] imageBytes = Base64.getDecoder().decode(base64String);
        return new Image(new java.io.ByteArrayInputStream(imageBytes));
    }

    // Método para enviar mensagem longa
    public Status enviarLongMessage(String telefone, String mensagem) {
        try {
            // Navegar para o chat do número
            String url = "https://web.whatsapp.com/send?phone=" + telefone + "&text=" + mensagem;
            driver.get(url);

            // Esperar carregar o campo de mensagem
            Thread.sleep(5000);

            WebElement sendButton = driver.findElement(By.xpath("//span[@data-icon='send']"));
            sendButton.click();

            return Status.ENVIADO;
        } catch (Exception e) {
            e.printStackTrace();
            return Status.NAO_ENVIADO;
        }
    }

    // Método para validar se o número é válido
    public boolean validarNumero(String telefone) {
        try {
            String url = "https://web.whatsapp.com/send?phone=" + telefone;
            driver.get(url);

            // Esperar pela validação
            Thread.sleep(5000);

            WebElement alertElement = driver.findElement(By.xpath("//div[contains(text(), 'número não está registrado')]"));
            return alertElement == null; // Se não encontrar o alerta, o número é válido
        } catch (Exception e) {
            return true; // Se não houve exceção, o número é válido
        }
    }

    public void navegarPara(String url) {
        driver.get(url); // driver é sua instância do FirefoxDriver
    }

    public boolean enviarMensagem() {
        try {
            // Tentar rolar a página até o botão de envio
            WebElement botaoEnviar = driver.findElement(By.xpath("//button[@aria-label='Enviar']"));
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("arguments[0].scrollIntoView(true);", botaoEnviar);
            
            // Aguardar até que o botão esteja visível e clicável
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(60));
            botaoEnviar = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[@aria-label='Enviar']")));
            
            // Clicar no botão de enviar
            botaoEnviar.click();
            return true;
        } catch (Exception e) {
            System.err.println("Erro ao enviar mensagem: " + e.getMessage());
            return false;
        }
    }
    
    // Encerrar o WebDriver
    public void finalizar() {
        if (driver != null) {
            driver.quit();
        }
    }

    // Enum para status de envio
    public enum Status {
        ENVIADO,
        ENTREGUE,
        NAO_ENVIADO,
        PENDENTE
    }
}
