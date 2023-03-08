package com.yas.rating.model;
import com.yas.rating.listener.CustomAuditingEntityListener;
import jakarta.persistence.EntityListeners;
import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.MappedSuperclass;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.ZonedDateTime;

@MappedSuperclass
@Getter
@Setter
@EntityListeners(CustomAuditingEntityListener.class)
public class AbstractAuditEntity {
    @CreatedDate
    private ZonedDateTime createdOn;

    @CreatedBy
    private String createdBy;

    @LastModifiedDate
    private ZonedDateTime lastModifiedOn;

    @LastModifiedBy
    private String lastModifiedBy;
}
