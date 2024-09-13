package com.yas.automation.ui.util;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public final class WebElementUtil {

    private WebElementUtil() {
    }

    public static WebElement getWebElementBy(WebDriver driver, How how, String identity) {
        return getWebElementBy(driver, how, identity, 30);
    }

    public static WebElement getWebElementBy(WebDriver driver, How how, String identity, long waitTime) {
        By locator = how.buildBy(identity);
        WebDriverWait waitForEle = new WebDriverWait(driver, Duration.ofSeconds(waitTime));
        waitForEle.until(ExpectedConditions.presenceOfElementLocated(locator));
        return driver.findElement(locator);
    }

    public static boolean isCorrectUrl(WebDriver driver, String expectedUrl) {
        String currentUrl = driver.getCurrentUrl();
        return expectedUrl.equals(currentUrl);
    }

    public static String createFieldText(String fieldName) {
        return fieldName + System.currentTimeMillis();
    }

    public static boolean isElementPresent(WebDriver driver, How how, String identity) {
        By locator = how.buildBy(identity);
        List<WebElement> elements = driver.findElements(locator);
        return !elements.isEmpty();  // Returns true if element exists, false if not
    }

}
