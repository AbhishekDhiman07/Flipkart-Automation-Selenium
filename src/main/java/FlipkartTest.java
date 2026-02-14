import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import java.util.Set;

public class FlipkartTest {
    public static void main(String[] args) {
        WebDriverManager.chromedriver().setup();
        WebDriver driver = new ChromeDriver();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        JavascriptExecutor js = (JavascriptExecutor) driver;

        try {
            driver.manage().window().maximize();
            driver.get("https://www.flipkart.com");

            // 1. Popup Bypass
            try {
                WebElement closeBtn = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//span[text()='✕'] | //button[text()='✕']")));
                closeBtn.click();
            } catch (Exception e) { System.out.println("LOG: Popup skipped."); }

            // 2. Search
            WebElement searchBox = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("q")));
            searchBox.sendKeys("iPhone 16");
            searchBox.sendKeys(Keys.ENTER);

            // 3. Click Product
            WebElement prodLink = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[contains(@href, '/p/')][@target='_blank']")));
            String mainWindow = driver.getWindowHandle();
            js.executeScript("arguments[0].click();", prodLink); 
            System.out.println("LOG: Product link clicked.");

            // 4. Tab Switching
            wait.until(ExpectedConditions.numberOfWindowsToBe(2));
            for (String handle : driver.getWindowHandles()) {
                if (!handle.equals(mainWindow)) {
                    driver.switchTo().window(handle);
                    break;
                }
            }

            // 5. Data Extraction
            Thread.sleep(5000); 
            System.out.println("\n--- FINAL PRODUCT REPORT ---");

            // SMART TITLE CAPTURE: If h1 fails, use the page title minus the 'Flipkart' part
            String productTitle = driver.getTitle().split("\\|")[0].trim();
            System.out.println("1. Product Title: " + productTitle);

            // Price Extraction
            try {
                WebElement price = driver.findElement(By.xpath("//div[contains(text(), '₹')]"));
                System.out.println("2. Displayed Price: " + price.getText());
            } catch (Exception e) { System.out.println("2. Price: Not found."); }

            // Final Verification
            System.out.println("3. Page Verification: " + (driver.getCurrentUrl().contains("apple-iphone-16") ? "PASS" : "FAIL"));
            System.out.println("--- TEST COMPLETED ---");

        } catch (Exception e) {
            System.out.println("LOG: ERROR: " + e.getMessage());
        } finally {
            driver.quit();
        }
    }
}