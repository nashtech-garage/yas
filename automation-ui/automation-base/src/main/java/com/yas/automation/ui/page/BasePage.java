package com.yas.automation.ui.page;

import com.yas.automation.ui.hook.WebDriverFactory;
import lombok.Getter;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.function.Supplier;

/**
 * BasePage provides common setup for web pages in the automation framework.
 *
 * <p>It includes:</p>
 * <ul>
 *   <li>WebDriver setup.</li>
 *   <li>WebDriverWait setup.</li>
 *   <li>Common utility methods.</li>
 * </ul>
 *
 * <p>Extend this class to use these features in your page classes.</p>
 */
@Getter
public class BasePage {

    private final WebDriverFactory webDriverFactory;

    public BasePage(WebDriverFactory webDriverFactory) {
        this.webDriverFactory = webDriverFactory;
    }

    public void wait(Duration duration) {
        try {
            Thread.sleep(duration);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void waitWithRetry(Duration duration, Supplier<Boolean> booleanSupplier) {
        int attempts = 0;
        int maxRetries = 5;

        // Keep checking the supplier until it returns false or max retries are reached
        while (attempts < maxRetries) {
            // If the condition returns false, stop retrying
            if (!booleanSupplier.get()) {
                System.out.println("Condition met on attempt " + (attempts + 1));
                return;
            }

            // Sleep for the provided duration between retries
            try {
                System.out.println("Attempt " + (attempts + 1) + ": Condition not met, retrying after " + duration.toSeconds() + " seconds.");
                Thread.sleep(duration.toMillis());
            } catch (InterruptedException e) {
                // Handle interruption and exit the retry loop if interrupted
                Thread.currentThread().interrupt();
                throw new RuntimeException("Thread was interrupted", e);
            }

            // Increment the retry counter
            attempts++;
        }

        // If max retries are reached, log a message or throw an exception
        System.out.println("Max retry limit reached, condition was not met.");
    }

    public void scrollDown() {
        JavascriptExecutor javascriptExecutor = (JavascriptExecutor) getWebDriver();
        javascriptExecutor.executeScript("window.scrollBy(0,document.body.scrollHeight)");
        wait(Duration.ofSeconds(1));
    }

    public void scrollTo(WebElement webElement) {
        JavascriptExecutor javascriptExecutor = (JavascriptExecutor) getWebDriver();
        javascriptExecutor.executeScript("arguments[0].scrollIntoView(true);", webElement);
        wait(Duration.ofSeconds(1));
    }

    public boolean isElementPresent(Supplier<WebElement> webElementConsumer) {
        try {
            return webElementConsumer.get().isDisplayed();
        } catch (Exception exception) {
            return false;
        }
    }

    public WebDriver getWebDriver() {
        return webDriverFactory.getChromeDriver();
    }

    public WebDriverWait getWait() {
        return new WebDriverWait(getWebDriver(), Duration.ofSeconds(30));
    }
}
