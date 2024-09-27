package com.yas.automation.ui.service.impl;

import com.yas.automation.ui.service.InputService;
import org.openqa.selenium.WebElement;
import org.springframework.stereotype.Component;

@Component
public class CheckBoxService implements InputService {
    @Override
    public void setValue(WebElement webElement, Object value) {
        webElement.click();
    }
}
