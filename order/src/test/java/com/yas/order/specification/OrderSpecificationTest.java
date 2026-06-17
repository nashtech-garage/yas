package com.yas.order.specification;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.yas.order.model.Order;
import com.yas.order.model.OrderItem;
import com.yas.order.model.enumeration.OrderStatus;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.domain.Specification;

class OrderSpecificationTest {

    private final CriteriaBuilder criteriaBuilder = mock(CriteriaBuilder.class);
    private final Root<Order> root = mock(Root.class);
    private final CriteriaQuery<?> query = mock(CriteriaQuery.class);
    private final Root<OrderItem> orderItemRoot = mock(Root.class);

    @Test
    void testHasCreatedBy_whenNormalCase_thenSuccess() {

        String createdBy = "user123";

        when(root.get("createdBy")).thenReturn(mock(Path.class));

        Predicate expectedPredicate = mock(Predicate.class);
        when(criteriaBuilder.equal(root.get("createdBy"), createdBy)).thenReturn(expectedPredicate);

        Specification<Order> spec = OrderSpecification.hasCreatedBy(createdBy);
        Predicate resultPredicate = spec.toPredicate(root, query, criteriaBuilder);

        assertNotNull(resultPredicate, "Predicate should not be null");
        assertEquals(expectedPredicate, resultPredicate, "Predicate should match expected predicate");
    }

    @Test
    void testHasOrderStatus_whenNormalCase_thenSuccess() {
        when(root.get("orderStatus")).thenReturn(mock(Path.class));
        Predicate expectedPredicate = mock(Predicate.class);
        when(criteriaBuilder.equal(root.get("orderStatus"), OrderStatus.COMPLETED)).thenReturn(expectedPredicate);

        Specification<Order> spec = OrderSpecification.hasOrderStatus(OrderStatus.COMPLETED);
        Predicate predicate = spec.toPredicate(root, query, criteriaBuilder);

        assertNotNull(predicate);
        assertEquals(expectedPredicate, predicate);
    }

    @Test
    void testHasProductNameInOrderItems_whenNormalCase_thenSuccess() {

        Subquery<Long> subqueryMock = mock(Subquery.class);
        when(query.subquery(Long.class)).thenReturn(subqueryMock);

        when(subqueryMock.from(OrderItem.class)).thenReturn(orderItemRoot);
        when(criteriaBuilder.like(any(), anyString())).thenReturn(mock(Predicate.class));

        Subquery subquery1 = mock(Subquery.class);
        when(subqueryMock.select(any())).thenReturn(subquery1);

        Subquery subquery2 = mock(Subquery.class);
        when(subquery1.where(any(Predicate.class))).thenReturn(subquery2);

        CriteriaBuilder.In inMock = mock(CriteriaBuilder.In.class);
        when(criteriaBuilder.in(any())).thenReturn(inMock);
        when(inMock.value(any())).thenReturn(mock(CriteriaBuilder.In.class));

        Specification<Order> spec = OrderSpecification.hasProductNameInOrderItems("SampleProduct");
        Predicate predicate = spec.toPredicate(root, query, criteriaBuilder);

        assertNotNull(predicate);
    }

    @Test
    void testWithEmail_whenNormalCase_thenSuccess() {
        when(root.get("email")).thenReturn(mock(Path.class));
        when(criteriaBuilder.like(any(), anyString())).thenReturn(mock(Predicate.class));

        Specification<Order> spec = OrderSpecification.withEmail("test@example.com");
        Predicate predicate = spec.toPredicate(root, query, criteriaBuilder);

        assertNotNull(predicate);
    }

    @Test
    void testWithOrderStatusList_whenNormalCase_thenSuccess() {
        Path pathMock = mock(Path.class);
        when(root.get("orderStatus")).thenReturn(pathMock);
        when(pathMock.in(any(Collection.class))).thenReturn(mock(Predicate.class));

        Specification<Order> spec = OrderSpecification.withOrderStatus(List.of(OrderStatus.COMPLETED, OrderStatus.CANCELLED));
        Predicate predicate = spec.toPredicate(root, query, criteriaBuilder);

        assertNotNull(predicate);
    }

    @Test
    void testWithBillingPhoneNumber_whenNormalCase_thenSuccess() {

        Path pathMock = mock(Path.class);
        when(root.get("billingAddressId")).thenReturn(pathMock);
        when(pathMock.get("phone")).thenReturn(mock(Path.class));
        when(criteriaBuilder.like(any(), anyString())).thenReturn(mock(Predicate.class));

        Specification<Order> spec = OrderSpecification.withBillingPhoneNumber("1234567890");
        Predicate predicate = spec.toPredicate(root, query, criteriaBuilder);

        assertNotNull(predicate);
    }

    @Test
    void testWithCountryName_whenNormalCase_thenSuccess() {

        Path path = mock(Path.class);
        when(root.get("billingAddressId")).thenReturn(path);
        when(path.get("countryName")).thenReturn(mock(Path.class));
        when(criteriaBuilder.like(any(), anyString())).thenReturn(mock(Predicate.class));

        Specification<Order> spec = OrderSpecification.withCountryName("USA");
        Predicate predicate = spec.toPredicate(root, query, criteriaBuilder);

        assertNotNull(predicate);
    }

    @Test
    void testWithDateRange_whenNormalCase_thenSuccess() {
        ZonedDateTime createdFrom = ZonedDateTime.now().minusDays(7);
        ZonedDateTime createdTo = ZonedDateTime.now();
        when(root.get("createdOn")).thenReturn(mock(Path.class));
        when(criteriaBuilder.between(root.get("createdOn"), createdFrom, createdTo)).thenReturn(mock(Predicate.class));

        Specification<Order> spec = OrderSpecification.withDateRange(createdFrom, createdTo);
        Predicate predicate = spec.toPredicate(root, query, criteriaBuilder);

        assertNotNull(predicate);
    }

    // ==========================================
    // TESTS CHO CÁC NHÁNH RỖNG/NULL (TĂNG MẠNH BRANCH COVERAGE)
    // ==========================================

    @Test
    void testHasOrderStatus_whenNull_thenReturnConjunction() {
        Predicate expected = mock(Predicate.class);
        when(criteriaBuilder.conjunction()).thenReturn(expected);
        Specification<Order> spec = OrderSpecification.hasOrderStatus(null);
        assertEquals(expected, spec.toPredicate(root, query, criteriaBuilder));
    }

    @Test
    void testWithEmail_whenEmpty_thenReturnConjunction() {
        Predicate expected = mock(Predicate.class);
        when(criteriaBuilder.conjunction()).thenReturn(expected);
        Specification<Order> spec = OrderSpecification.withEmail("");
        assertEquals(expected, spec.toPredicate(root, query, criteriaBuilder));
    }

    @Test
    void testWithOrderStatus_whenEmpty_thenReturnConjunction() {
        Predicate expected = mock(Predicate.class);
        when(criteriaBuilder.conjunction()).thenReturn(expected);
        Specification<Order> spec = OrderSpecification.withOrderStatus(List.of());
        assertEquals(expected, spec.toPredicate(root, query, criteriaBuilder));
    }

    @Test
    void testWithBillingPhoneNumber_whenEmpty_thenReturnConjunction() {
        Predicate expected = mock(Predicate.class);
        when(criteriaBuilder.conjunction()).thenReturn(expected);
        Specification<Order> spec = OrderSpecification.withBillingPhoneNumber("");
        assertEquals(expected, spec.toPredicate(root, query, criteriaBuilder));
    }

    @Test
    void testWithCountryName_whenEmpty_thenReturnConjunction() {
        Predicate expected = mock(Predicate.class);
        when(criteriaBuilder.conjunction()).thenReturn(expected);
        Specification<Order> spec = OrderSpecification.withCountryName("");
        assertEquals(expected, spec.toPredicate(root, query, criteriaBuilder));
    }

    @Test
    void testWithDateRange_whenNull_thenReturnConjunction() {
        Predicate expected = mock(Predicate.class);
        when(criteriaBuilder.conjunction()).thenReturn(expected);
        Specification<Order> spec = OrderSpecification.withDateRange(null, null);
        assertEquals(expected, spec.toPredicate(root, query, criteriaBuilder));
    }

    @Test
    void testHasProductInOrderItems_whenQueryNull_thenReturnConjunction() {
        Predicate expected = mock(Predicate.class);
        when(criteriaBuilder.conjunction()).thenReturn(expected);
        Specification<Order> spec = OrderSpecification.hasProductInOrderItems(List.of(1L));
        // Truyền query = null để rơi vào nhánh if (query == null)
        assertEquals(expected, spec.toPredicate(root, null, criteriaBuilder)); 
    }

    @Test
    void testHasProductNameInOrderItems_whenQueryNull_thenReturnConjunction() {
        Predicate expected = mock(Predicate.class);
        when(criteriaBuilder.conjunction()).thenReturn(expected);
        Specification<Order> spec = OrderSpecification.hasProductNameInOrderItems("Name");
        assertEquals(expected, spec.toPredicate(root, null, criteriaBuilder)); 
    }

    @Test
    void testWithProductName_whenQueryNull_thenReturnConjunction() {
        Predicate expected = mock(Predicate.class);
        when(criteriaBuilder.conjunction()).thenReturn(expected);
        Specification<Order> spec = OrderSpecification.withProductName("Name");
        assertEquals(expected, spec.toPredicate(root, null, criteriaBuilder));
    }

    // ==========================================
    // TESTS CHO CÁC HÀM TỔNG HỢP (COMPOSITE SPECIFICATIONS)
    // ==========================================

    @Test
    void testFindOrderByWithMulCriteria_whenQueryIsNull_thenSkipFetch() {
        Predicate expected = mock(Predicate.class);
        when(criteriaBuilder.conjunction()).thenReturn(expected);
        when(criteriaBuilder.and(any(), any(), any(), any(), any(), any())).thenReturn(expected);
        
        Specification<Order> spec = OrderSpecification.findOrderByWithMulCriteria(
            null, null, null, null, null, null, null);
        
        Predicate result = spec.toPredicate(root, null, criteriaBuilder);
        assertNotNull(result);
        
        // Đảm bảo không gọi root.fetch() vì query == null
        org.mockito.Mockito.verify(root, org.mockito.Mockito.never()).fetch(anyString(), any());
    }

   @Test
    void testFindMyOrders_Success() {
        Predicate expected = mock(Predicate.class);
        when(criteriaBuilder.conjunction()).thenReturn(expected);
        when(criteriaBuilder.equal(any(), any())).thenReturn(expected);
        when(criteriaBuilder.and(any(), any(), any())).thenReturn(expected);
        
        Path pathMock = mock(Path.class);
        when(root.get(anyString())).thenReturn(pathMock);

        // FIX: Lót đường sẵn cho Subquery bên trong hàm hasProductNameInOrderItems
        Subquery subqueryMock = mock(Subquery.class);
        when(query.subquery(Long.class)).thenReturn(subqueryMock);
        when(subqueryMock.from(OrderItem.class)).thenReturn(orderItemRoot);
        when(subqueryMock.select(any())).thenReturn(subqueryMock);
        when(subqueryMock.where(any(Predicate.class))).thenReturn(subqueryMock);

        when(orderItemRoot.get(anyString())).thenReturn(pathMock);
        jakarta.persistence.criteria.Expression expressionMock = mock(jakarta.persistence.criteria.Expression.class);
        when(criteriaBuilder.lower(any())).thenReturn(expressionMock);
        when(criteriaBuilder.like(any(), anyString())).thenReturn(expected);

        CriteriaBuilder.In inMock = mock(CriteriaBuilder.In.class);
        when(criteriaBuilder.in(any())).thenReturn(inMock);
        when(inMock.value(any())).thenReturn(inMock); // FIX: Trả về inMock thay vì expected

        Specification<Order> spec = OrderSpecification.findMyOrders("user1", "prod", OrderStatus.COMPLETED);
        Predicate result = spec.toPredicate(root, query, criteriaBuilder);
        
        assertNotNull(result);
    }
    
    @Test
    void testExistsByCreatedByAndInProductIdAndOrderStatusCompleted() {
        Specification<Order> spec = OrderSpecification.existsByCreatedByAndInProductIdAndOrderStatusCompleted("user", List.of(1L));
        assertNotNull(spec);
    }
}
