package com.yas.recommendation.chat;

import java.io.IOException;
import java.nio.charset.Charset;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component
public class PromptFactory {

    private final String systemPrompt;

    public PromptFactory(
        @Value("classpath:prompt/systemPrompt.pt") Resource systemPromptResource
    ) throws IOException {
        this.systemPrompt = systemPromptResource.getContentAsString(Charset.defaultCharset());
    }

    public String getSystemPrompt() {
        return systemPrompt;
    }
}
