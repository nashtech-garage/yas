package com.yas.recommendation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public abstract class BaseMessageDTO {
    protected String op;
    protected Long comingTs = System.currentTimeMillis();
}
