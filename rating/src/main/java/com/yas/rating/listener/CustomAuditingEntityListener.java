package com.yas.rating.listener;

import com.yas.rating.model.AbstractAuditEntity;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.data.auditing.AuditingHandler;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.ZonedDateTime;

@Configurable
public class CustomAuditingEntityListener extends AuditingEntityListener {
    public CustomAuditingEntityListener(ObjectFactory<AuditingHandler> handler) {
        super.setAuditingHandler(handler);
    }

    @Override
    @PrePersist
    public void touchForCreate(Object target) {
        AbstractAuditEntity entity = (AbstractAuditEntity) target;
        setCreatedTime(entity);
        if (entity.getCreatedBy() == null) {
            super.touchForCreate(target);
        } else {
            if (entity.getLastModifiedBy() == null) {
                entity.setLastModifiedBy(entity.getCreatedBy());
            }
        }
    }

    @Override
    @PreUpdate
    public void touchForUpdate(Object target) {
        AbstractAuditEntity entity = (AbstractAuditEntity) target;
        setUpdatedTime(entity);
        if (entity.getLastModifiedBy() == null) {
            super.touchForUpdate(target);
        }
    }

    private void setCreatedTime(AbstractAuditEntity target) {
        target.setLastModifiedOn(ZonedDateTime.now());
        target.setCreatedOn(ZonedDateTime.now());
    }

    private void setUpdatedTime(AbstractAuditEntity target) {
        target.setLastModifiedOn(ZonedDateTime.now());
    }
}
