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
