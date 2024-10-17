package com.yas.automation.base.service.impl;

import com.yas.automation.base.service.InputService;
import org.openqa.selenium.WebElement;
import org.springframework.stereotype.Component;

import java.nio.file.Paths;

@Component
public class FileService implements InputService {
    @Override
    public void setValue(WebElement webElement, Object value) {
        webElement.sendKeys(Paths.get((String) value).toAbsolutePath().toString());
    }
}
