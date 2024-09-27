package com.yas.automation.ui.service;

import org.openqa.selenium.WebElement;

public interface InputService {

    <T> void setValue(WebElement webElement, T value);

}
