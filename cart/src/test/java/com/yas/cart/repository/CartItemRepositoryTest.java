package com.yas.cart.repository;

import com.yas.cart.model.CartItem;
import com.yas.cart.model.CartItemId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@Import(CartItemRepositoryTest.TestAuditingConfiguration.class)
class CartItemRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CartItemRepository cartItemRepository;

    private CartItem testCartItem1;
    private CartItem testCartItem2;
    private CartItem testCartItem3;

    private static final String CUSTOMER_ID_1 = "customer1";
    private static final String CUSTOMER_ID_2 = "customer2";
    private static final Long PRODUCT_ID_1 = 1L;
    private static final Long PRODUCT_ID_2 = 2L;

    @BeforeEach
    void setUp() {
        // Clear any existing data
        cartItemRepository.deleteAll();
        entityManager.flush();
        entityManager.clear();

        // Create test data
        testCartItem1 = CartItem.builder()
                .customerId(CUSTOMER_ID_1)
                .productId(PRODUCT_ID_1)
                .quantity(5)
                .build();

        testCartItem2 = CartItem.builder()
                .customerId(CUSTOMER_ID_1)
                .productId(PRODUCT_ID_2)
                .quantity(10)
                .build();

        testCartItem3 = CartItem.builder()
                .customerId(CUSTOMER_ID_2)
                .productId(PRODUCT_ID_1)
                .quantity(3)
                .build();
    }

    @Test
    void testFindByCustomerIdAndProductId_whenExists_shouldReturnCartItem() {
        // Given
        entityManager.persist(testCartItem1);
        entityManager.flush();

        // When
        Optional<CartItem> result = cartItemRepository.findByCustomerIdAndProductId(
                CUSTOMER_ID_1, PRODUCT_ID_1);

        // Then
        assertTrue(result.isPresent());
        assertEquals(CUSTOMER_ID_1, result.get().getCustomerId());
        assertEquals(PRODUCT_ID_1, result.get().getProductId());
        assertEquals(5, result.get().getQuantity());
    }

    @Test
    void testFindByCustomerIdAndProductId_whenNotExists_shouldReturnEmpty() {
        // Given
        entityManager.persist(testCartItem1);
        entityManager.flush();

        // When
        Optional<CartItem> result = cartItemRepository.findByCustomerIdAndProductId(
                CUSTOMER_ID_1, 999L);

        // Then
        assertFalse(result.isPresent());
    }

    @Test
    void testFindByCustomerIdAndProductId_withDifferentCustomer_shouldReturnEmpty() {
        // Given
        entityManager.persist(testCartItem1);
        entityManager.flush();

        // When
        Optional<CartItem> result = cartItemRepository.findByCustomerIdAndProductId(
                "different-customer", PRODUCT_ID_1);

        // Then
        assertFalse(result.isPresent());
    }

    @Test
    void testFindByCustomerIdOrderByCreatedOnDesc_whenMultipleItems_shouldReturnOrderedList() {
        // Given
        entityManager.persist(testCartItem1);
        entityManager.flush();

        // Small delay to ensure different timestamps
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        entityManager.persist(testCartItem2);
        entityManager.flush();

        // When
        List<CartItem> result = cartItemRepository.findByCustomerIdOrderByCreatedOnDesc(CUSTOMER_ID_1);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(PRODUCT_ID_2, result.get(0).getProductId()); // Most recent first
        assertEquals(PRODUCT_ID_1, result.get(1).getProductId());
    }

    @Test
    void testFindByCustomerIdOrderByCreatedOnDesc_whenNoItems_shouldReturnEmptyList() {
        // When
        List<CartItem> result = cartItemRepository.findByCustomerIdOrderByCreatedOnDesc("non-existent");

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testFindByCustomerIdOrderByCreatedOnDesc_whenDifferentCustomers_shouldReturnOnlyMatchingCustomer() {
        // Given
        entityManager.persist(testCartItem1);
        entityManager.persist(testCartItem2);
        entityManager.persist(testCartItem3);
        entityManager.flush();

        // When
        List<CartItem> result = cartItemRepository.findByCustomerIdOrderByCreatedOnDesc(CUSTOMER_ID_1);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(item -> CUSTOMER_ID_1.equals(item.getCustomerId())));
    }

    @Test
    void testFindByCustomerIdAndProductIdIn_whenMultipleProductIds_shouldReturnMatchingItems() {
        // Given
        entityManager.persist(testCartItem1);
        entityManager.persist(testCartItem2);
        entityManager.persist(testCartItem3);
        entityManager.flush();

        List<Long> productIds = List.of(PRODUCT_ID_1, PRODUCT_ID_2);

        // When
        List<CartItem> result = cartItemRepository.findByCustomerIdAndProductIdIn(
                CUSTOMER_ID_1, productIds);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(item -> CUSTOMER_ID_1.equals(item.getCustomerId())));
        assertTrue(result.stream().anyMatch(item -> PRODUCT_ID_1.equals(item.getProductId())));
        assertTrue(result.stream().anyMatch(item -> PRODUCT_ID_2.equals(item.getProductId())));
    }

    @Test
    void testFindByCustomerIdAndProductIdIn_whenSingleProductId_shouldReturnSingleItem() {
        // Given
        entityManager.persist(testCartItem1);
        entityManager.persist(testCartItem2);
        entityManager.flush();

        List<Long> productIds = List.of(PRODUCT_ID_1);

        // When
        List<CartItem> result = cartItemRepository.findByCustomerIdAndProductIdIn(
                CUSTOMER_ID_1, productIds);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(PRODUCT_ID_1, result.get(0).getProductId());
    }

    @Test
    void testFindByCustomerIdAndProductIdIn_whenNoMatch_shouldReturnEmptyList() {
        // Given
        entityManager.persist(testCartItem1);
        entityManager.flush();

        List<Long> productIds = List.of(999L, 888L);

        // When
        List<CartItem> result = cartItemRepository.findByCustomerIdAndProductIdIn(
                CUSTOMER_ID_1, productIds);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testFindByCustomerIdAndProductIdIn_withEmptyProductIds_shouldReturnEmptyList() {
        // Given
        entityManager.persist(testCartItem1);
        entityManager.flush();

        List<Long> productIds = List.of();

        // When
        List<CartItem> result = cartItemRepository.findByCustomerIdAndProductIdIn(
                CUSTOMER_ID_1, productIds);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testDeleteByCustomerIdAndProductId_whenExists_shouldDeleteCartItem() {
        // Given
        entityManager.persist(testCartItem1);
        entityManager.persist(testCartItem2);
        entityManager.flush();

        // When
        cartItemRepository.deleteByCustomerIdAndProductId(CUSTOMER_ID_1, PRODUCT_ID_1);
        entityManager.flush();

        // Then
        Optional<CartItem> result = cartItemRepository.findByCustomerIdAndProductId(
                CUSTOMER_ID_1, PRODUCT_ID_1);
        assertFalse(result.isPresent());

        // Other item should still exist
        Optional<CartItem> other = cartItemRepository.findByCustomerIdAndProductId(
                CUSTOMER_ID_1, PRODUCT_ID_2);
        assertTrue(other.isPresent());
    }

    @Test
    void testDeleteByCustomerIdAndProductId_whenNotExists_shouldNotThrowException() {
        // Given
        entityManager.persist(testCartItem1);
        entityManager.flush();

        // When & Then - should not throw exception
        assertDoesNotThrow(() -> {
            cartItemRepository.deleteByCustomerIdAndProductId(CUSTOMER_ID_1, 999L);
            entityManager.flush();
        });
    }

    @Test
    void testDeleteByCustomerIdAndProductId_withDifferentCustomer_shouldNotDeleteOtherCustomerItem() {
        // Given
        entityManager.persist(testCartItem1);
        entityManager.persist(testCartItem3); // Same product, different customer
        entityManager.flush();

        // When
        cartItemRepository.deleteByCustomerIdAndProductId(CUSTOMER_ID_1, PRODUCT_ID_1);
        entityManager.flush();

        // Then
        Optional<CartItem> customer1Item = cartItemRepository.findByCustomerIdAndProductId(
                CUSTOMER_ID_1, PRODUCT_ID_1);
        assertFalse(customer1Item.isPresent());

        // Customer 2 item should still exist
        Optional<CartItem> customer2Item = cartItemRepository.findByCustomerIdAndProductId(
                CUSTOMER_ID_2, PRODUCT_ID_1);
        assertTrue(customer2Item.isPresent());
    }

    @Test
    void testSave_whenNewCartItem_shouldPersist() {
        // Given
        CartItem newCartItem = CartItem.builder()
                .customerId("new-customer")
                .productId(100L)
                .quantity(7)
                .build();

        // When
        CartItem saved = cartItemRepository.save(newCartItem);
        entityManager.flush();
        entityManager.clear();

        // Then
        Optional<CartItem> result = cartItemRepository.findByCustomerIdAndProductId(
                "new-customer", 100L);
        assertTrue(result.isPresent());
        assertEquals(7, result.get().getQuantity());
    }

    @Test
    void testSave_whenUpdateExistingCartItem_shouldUpdate() {
        // Given
        entityManager.persist(testCartItem1);
        entityManager.flush();
        entityManager.clear();

        // When
        CartItem toUpdate = cartItemRepository.findByCustomerIdAndProductId(
                CUSTOMER_ID_1, PRODUCT_ID_1).orElseThrow();
        toUpdate.setQuantity(20);
        cartItemRepository.save(toUpdate);
        entityManager.flush();
        entityManager.clear();

        // Then
        CartItem updated = cartItemRepository.findByCustomerIdAndProductId(
                CUSTOMER_ID_1, PRODUCT_ID_1).orElseThrow();
        assertEquals(20, updated.getQuantity());
    }

    @Test
    void testFindById_whenExists_shouldReturnCartItem() {
        // Given
        entityManager.persist(testCartItem1);
        entityManager.flush();

        CartItemId id = new CartItemId(CUSTOMER_ID_1, PRODUCT_ID_1);

        // When
        Optional<CartItem> result = cartItemRepository.findById(id);

        // Then
        assertTrue(result.isPresent());
        assertEquals(CUSTOMER_ID_1, result.get().getCustomerId());
        assertEquals(PRODUCT_ID_1, result.get().getProductId());
    }

    @Test
    void testFindById_whenNotExists_shouldReturnEmpty() {
        // Given
        CartItemId id = new CartItemId("non-existent", 999L);

        // When
        Optional<CartItem> result = cartItemRepository.findById(id);

        // Then
        assertFalse(result.isPresent());
    }

    @Test
    void testDeleteAll_shouldRemoveAllCartItems() {
        // Given
        entityManager.persist(testCartItem1);
        entityManager.persist(testCartItem2);
        entityManager.persist(testCartItem3);
        entityManager.flush();

        // When
        cartItemRepository.deleteAll(List.of(testCartItem1, testCartItem2));
        entityManager.flush();

        // Then
        List<CartItem> remaining = cartItemRepository.findAll();
        assertEquals(1, remaining.size());
        assertEquals(CUSTOMER_ID_2, remaining.get(0).getCustomerId());
    }

    @Test
    void testSaveAll_whenMultipleItems_shouldPersistAll() {
        // Given
        List<CartItem> items = List.of(testCartItem1, testCartItem2, testCartItem3);

        // When
        List<CartItem> saved = cartItemRepository.saveAll(items);
        entityManager.flush();

        // Then
        assertEquals(3, saved.size());
        assertEquals(3, cartItemRepository.findAll().size());
    }

    @TestConfiguration
    @EnableJpaAuditing(auditorAwareRef = "auditorAware")
    static class TestAuditingConfiguration {
        @Bean
        public AuditorAware<String> auditorAware() {
            return () -> Optional.of("test-user");
        }
    }
}
