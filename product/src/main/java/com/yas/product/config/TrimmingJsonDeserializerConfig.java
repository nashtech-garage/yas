package com.yas.product.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;

@JsonComponent
public class TrimmingJsonDeserializerConfig extends JsonDeserializer<String> {
    @Override
    public String deserialize(JsonParser parser, DeserializationContext deserializationContext) throws IOException {
        return parser.getText() != null ? parser.getText().trim() : null;
    }
}
