package com.yas.recommendation.vector.common.formatter;

import java.util.Map;

public interface DocumentFormatter {

    String format(Map<String, String> entityMap, final String template);

}
