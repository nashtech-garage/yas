package com.yas.commonlibrary.kafka.cdc.message;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class KafkaMessageModelsTest {

    @Test
    void testOperationEnum() {
        // Given & Then
        assertEquals("c", Operation.CREATE.getName());
        assertEquals("u", Operation.UPDATE.getName());
        assertEquals("d", Operation.DELETE.getName());
        assertEquals("r", Operation.READ.getName());
        
        // Test toString
        assertEquals("CREATE", Operation.CREATE.toString());
        assertEquals("UPDATE", Operation.UPDATE.toString());
    }

    @Test
    void testProductMsgKey() {
        // Given
        Long productId = 123L;
        
        // When
        ProductMsgKey key = new ProductMsgKey(productId);
        
        // Then
        assertNotNull(key);
        assertEquals(productId, key.getId());
    }

    @Test
    void testProduct() {
        // Given
        Long id = 1L;
        Boolean isPublished = true;
        
        // When
        Product product = new Product(id, isPublished);
        
        // Then
        assertNotNull(product);
        assertEquals(id, product.getId());
        assertEquals(isPublished, product.isPublished());
    }

    @Test
    void testProductBuilder() {
        // Given
        Long id = 2L;
        Boolean isPublished = false;
        
        // When
        Product product = Product.builder()
                .id(id)
                .isPublished(isPublished)
                .build();
        
        // Then
        assertNotNull(product);
        assertEquals(id, product.getId());
        assertEquals(isPublished, product.isPublished());
    }

    @Test
    void testProductCdcMessage() {
        // Given
        Operation op = Operation.CREATE;
        Product before = null;
        Product after = new Product(1L, true);
        
        // When
        ProductCdcMessage message = new ProductCdcMessage(after, before, op);
        
        // Then
        assertNotNull(message);
        assertEquals(op, message.getOp());
        assertNull(message.getBefore());
        assertNotNull(message.getAfter());
        assertEquals(1L, message.getAfter().getId());
    }

    @Test
    void testProductCdcMessageUpdate() {
        // Given
        Operation op = Operation.UPDATE;
        Product before = new Product(1L, false);
        Product after = new Product(1L, true);
        
        // When
        ProductCdcMessage message = new ProductCdcMessage(after, before, op);
        
        // Then
        assertNotNull(message);
        assertEquals(Operation.UPDATE, message.getOp());
        assertNotNull(message.getBefore());
        assertNotNull(message.getAfter());
        assertFalse(message.getBefore().isPublished());
        assertTrue(message.getAfter().isPublished());
    }

    @Test
    void testProductCdcMessageDelete() {
        // Given
        Operation op = Operation.DELETE;
        Product before = new Product(1L, true);
        Product after = null;
        
        // When
        ProductCdcMessage message = new ProductCdcMessage(after, before, op);
        
        // Then
        assertNotNull(message);
        assertEquals(Operation.DELETE, message.getOp());
        assertNotNull(message.getBefore());
        assertNull(message.getAfter());
    }

    @Test
    void testProductCdcMessageBuilder() {
        // Given
        Operation op = Operation.CREATE;
        Product product = new Product(5L, true);
        
        // When
        ProductCdcMessage message = ProductCdcMessage.builder()
                .op(op)
                .after(product)
                .before(null)
                .build();
        
        // Then
        assertNotNull(message);
        assertEquals(op, message.getOp());
        assertNotNull(message.getAfter());
        assertNull(message.getBefore());
    }

    @Test
    void testProductEquality() {
        // Given
        Product product1 = new Product(1L, true);
        Product product2 = new Product(1L, true);
        
        // Then
        assertEquals(product1.getId(), product2.getId());
        assertEquals(product1.isPublished(), product2.isPublished());
    }

    @Test
    void testProductMsgKeyEquality() {
        // Given
        ProductMsgKey key1 = new ProductMsgKey(123L);
        ProductMsgKey key2 = new ProductMsgKey(123L);
        
        // Then
        assertEquals(key1.getId(), key2.getId());
    }
}
