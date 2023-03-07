package com.yas.rating.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "rating")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Rating extends AbstractAuditEntity{
    private String content;

    private int ratingStar;

    private Long productId;

    private String lastName;

    private String firstName;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Rating)) {
            return false;
        }
        return getId() != null && getId().equals(((Rating) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }
}