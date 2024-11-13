package com.yas.recommendation.chat;

public record User(
    String name,
    String jwt,
    boolean isAuthenticated
) {}
