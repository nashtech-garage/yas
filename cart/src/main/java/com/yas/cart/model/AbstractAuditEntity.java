package com.yas.cart.model;

import com.yas.cart.listener.CustomAuditingEntityListener;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;

import java.time.ZonedDateTime;

@MappedSuperclass
@Getter
@Setter
@EntityListeners(CustomAuditingEntityListener.class)
public class AbstractAuditEntity {
    private ZonedDateTime createdOn = ZonedDateTime.now();

    @CreatedBy
    private String createdBy;

    private ZonedDateTime lastModifiedOn = ZonedDateTime.now();

    @LastModifiedBy
    private String lastModifiedBy;
}
