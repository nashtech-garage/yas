package com.yas.automation.ui.ultil;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public final class WebElementUtil {

    private WebElementUtil() {}

    public static WebElement getWebElementBy(WebDriver driver, How how, String identity) {
        return getWebElementBy(driver, how, identity, 30);
    }

    public static WebElement getWebElementBy(WebDriver driver, How how, String identity, long waitTime) {
        By locator = how.buildBy(identity);
        WebDriverWait waitForEle = new WebDriverWait(driver, Duration.ofSeconds(waitTime));
        waitForEle.until(ExpectedConditions.presenceOfElementLocated(locator));
        return driver.findElement(locator);
    }

}
