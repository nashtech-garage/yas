package com.yas.media.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class StringUtils {
  public static boolean hasText(String input) {
    return input == null || input.trim().isEmpty();
  }
}
