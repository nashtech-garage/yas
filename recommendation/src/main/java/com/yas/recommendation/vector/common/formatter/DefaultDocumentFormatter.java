package com.yas.recommendation.vector.common.formatter;

import java.util.Map;
import org.apache.commons.text.StringSubstitutor;

public class DefaultDocumentFormatter implements DocumentFormatter {
    @Override
    public String format(Map<String, String> entityMap, String template) {
        StringSubstitutor sub = new StringSubstitutor(entityMap, "{", "}");
        return sub.replace(template);
    }
}
