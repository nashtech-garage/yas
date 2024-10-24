package com.yas.recommendation.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MetaData extends BaseMessageDTO {
    public MetaData(String op, Long comingTs) {
        super(op, comingTs);
    }
}
