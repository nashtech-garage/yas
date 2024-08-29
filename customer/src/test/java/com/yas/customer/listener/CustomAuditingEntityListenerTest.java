package com.yas.customer.listener;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.yas.customer.model.AbstractAuditEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.data.auditing.AuditingHandler;

class CustomAuditingEntityListenerTest {

    private ObjectFactory<AuditingHandler> auditingHandlerFactory;

    private AuditingHandler auditingHandler;

    private CustomAuditingEntityListener listener;

    private AbstractAuditEntity entity;

    @BeforeEach
    void setUp() {
        entity = mock(AbstractAuditEntity.class);
        auditingHandlerFactory = mock(ObjectFactory.class);
        auditingHandler = mock(AuditingHandler.class);
        when(auditingHandlerFactory.getObject()).thenReturn(auditingHandler);
        listener = new CustomAuditingEntityListener(auditingHandlerFactory);
    }

    @Test
    void testTouchForCreate_whenCreatedByIsNull_createSuccess() {

        when(entity.getCreatedBy()).thenReturn(null);
        listener.touchForCreate(entity);
        verify(auditingHandler).markCreated(entity);
        verify(entity, never()).setLastModifiedBy(any());
    }

    @Test
    void testTouchForCreate_whenCreatedByIsNotNull_setLastModifiedBy() {

        when(entity.getCreatedBy()).thenReturn("user1");
        when(entity.getLastModifiedBy()).thenReturn(null);
        listener.touchForCreate(entity);
        verify(entity).setLastModifiedBy("user1");
    }

    @Test
    void testTouchForUpdate_henLastModifiedByIsNull_markModified() {

        when(entity.getLastModifiedBy()).thenReturn(null);
        listener.touchForUpdate(entity);
        verify(auditingHandler).markModified(entity);
    }

    @Test
    void testTouchForUpdate_whenLastModifiedByIsNotNull_markModified() {

        when(entity.getLastModifiedBy()).thenReturn("user1");
        listener.touchForUpdate(entity);
        verify(auditingHandler, never()).markModified(entity);
    }
}
