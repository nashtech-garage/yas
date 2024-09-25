package com.yas.automation.ui.service;

import com.yas.automation.ui.form.InputType;
import org.openqa.selenium.WebElement;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class InputDelegateService {

    private final Map<String, InputService> inputServiceMap;

    public InputDelegateService(Map<String, InputService> inputServiceMap) {
        this.inputServiceMap = inputServiceMap;
    }

    public void setInputValue(InputType inputType, WebElement webElement, Object value) {
        if (inputServiceMap.containsKey(inputType.getServiceName())) {
            inputServiceMap.get(inputType.getServiceName()).setValue(webElement, value);
        } else {
            throw new IllegalArgumentException("No input service found for: %s".formatted(inputType.getServiceName()));
        }
    }

}
