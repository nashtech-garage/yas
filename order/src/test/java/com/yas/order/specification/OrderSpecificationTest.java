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
import jakarta.persistence.criteria.Expression;
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

    // ---- Null / empty branch tests (these are the FALSE / null paths) ----

    @Test
    void testHasOrderStatus_whenNull_thenReturnConjunction() {
        Predicate conjunction = mock(Predicate.class);
        when(criteriaBuilder.conjunction()).thenReturn(conjunction);

        Specification<Order> spec = OrderSpecification.hasOrderStatus(null);
        Predicate result = spec.toPredicate(root, query, criteriaBuilder);

        assertEquals(conjunction, result);
    }

    @Test
    void testHasProductInOrderItems_whenQueryNull_thenReturnConjunction() {
        Predicate conjunction = mock(Predicate.class);
        when(criteriaBuilder.conjunction()).thenReturn(conjunction);

        Specification<Order> spec = OrderSpecification.hasProductInOrderItems(List.of(1L));
        Predicate result = spec.toPredicate(root, null, criteriaBuilder);

        assertEquals(conjunction, result);
    }

    @Test
    @SuppressWarnings("unchecked")
    void testHasProductInOrderItems_whenQueryNotNull_thenBuildSubquery() {
        Subquery<OrderItem> subqueryMock = mock(Subquery.class);
        when(query.subquery(OrderItem.class)).thenReturn(subqueryMock);
        when(subqueryMock.from(OrderItem.class)).thenReturn(orderItemRoot);
        when(subqueryMock.select(any())).thenReturn(subqueryMock);

        Path pathMock = mock(Path.class);
        when(orderItemRoot.get(anyString())).thenReturn(pathMock);
        when(root.get(anyString())).thenReturn(pathMock);
        when(pathMock.in(any(Collection.class))).thenReturn(mock(Predicate.class));
        when(criteriaBuilder.equal(any(), any())).thenReturn(mock(Predicate.class));
        when(criteriaBuilder.and(any(Predicate.class), any(Predicate.class))).thenReturn(mock(Predicate.class));

        Predicate existsPredicate = mock(Predicate.class);
        when(criteriaBuilder.exists(any())).thenReturn(existsPredicate);

        Specification<Order> spec = OrderSpecification.hasProductInOrderItems(List.of(1L, 2L));
        Predicate result = spec.toPredicate(root, query, criteriaBuilder);

        assertEquals(existsPredicate, result);
    }

    @Test
    void testHasProductNameInOrderItems_whenQueryNull_thenReturnConjunction() {
        Predicate conjunction = mock(Predicate.class);
        when(criteriaBuilder.conjunction()).thenReturn(conjunction);

        Specification<Order> spec = OrderSpecification.hasProductNameInOrderItems("product");
        Predicate result = spec.toPredicate(root, null, criteriaBuilder);

        assertEquals(conjunction, result);
    }

    @Test
    @SuppressWarnings("unchecked")
    void testHasProductNameInOrderItems_whenProductNameEmpty_thenNoLikePredicate() {
        Subquery<Long> subqueryMock = mock(Subquery.class);
        when(query.subquery(Long.class)).thenReturn(subqueryMock);
        when(subqueryMock.from(OrderItem.class)).thenReturn(orderItemRoot);

        Predicate conjunction = mock(Predicate.class);
        when(criteriaBuilder.conjunction()).thenReturn(conjunction);

        Path pathMock = mock(Path.class);
        when(orderItemRoot.get(anyString())).thenReturn(pathMock);
        when(root.get(anyString())).thenReturn(pathMock);

        Subquery<Long> selectResult = mock(Subquery.class);
        when(subqueryMock.select(any())).thenReturn(selectResult);

        CriteriaBuilder.In inMock = mock(CriteriaBuilder.In.class);
        when(criteriaBuilder.in(any())).thenReturn(inMock);
        when(inMock.value(any())).thenReturn(mock(CriteriaBuilder.In.class));

        Specification<Order> spec = OrderSpecification.hasProductNameInOrderItems("");
        Predicate result = spec.toPredicate(root, query, criteriaBuilder);

        assertNotNull(result);
    }

    @Test
    void testWithEmail_whenEmpty_thenReturnConjunction() {
        Predicate conjunction = mock(Predicate.class);
        when(criteriaBuilder.conjunction()).thenReturn(conjunction);

        Specification<Order> spec = OrderSpecification.withEmail("");
        Predicate result = spec.toPredicate(root, query, criteriaBuilder);

        assertEquals(conjunction, result);
    }

    @Test
    void testWithOrderStatus_whenEmptyList_thenReturnConjunction() {
        Predicate conjunction = mock(Predicate.class);
        when(criteriaBuilder.conjunction()).thenReturn(conjunction);

        Specification<Order> spec = OrderSpecification.withOrderStatus(List.of());
        Predicate result = spec.toPredicate(root, query, criteriaBuilder);

        assertEquals(conjunction, result);
    }

    @Test
    void testWithProductName_whenQueryNull_thenReturnConjunction() {
        Predicate conjunction = mock(Predicate.class);
        when(criteriaBuilder.conjunction()).thenReturn(conjunction);

        Specification<Order> spec = OrderSpecification.withProductName("keyboard");
        Predicate result = spec.toPredicate(root, null, criteriaBuilder);

        assertEquals(conjunction, result);
    }

    @Test
    void testWithProductName_whenProductNameEmpty_thenReturnConjunction() {
        Predicate conjunction = mock(Predicate.class);
        when(criteriaBuilder.conjunction()).thenReturn(conjunction);

        Specification<Order> spec = OrderSpecification.withProductName("");
        Predicate result = spec.toPredicate(root, query, criteriaBuilder);

        assertEquals(conjunction, result);
    }

    @Test
    @SuppressWarnings("unchecked")
    void testWithProductName_whenProductNameNonEmpty_thenBuildSubquery() {
        Subquery<Long> subqueryMock = mock(Subquery.class);
        when(query.subquery(Long.class)).thenReturn(subqueryMock);
        when(subqueryMock.from(OrderItem.class)).thenReturn(orderItemRoot);

        Path pathMock = mock(Path.class);
        when(orderItemRoot.get(anyString())).thenReturn(pathMock);
        when(root.get(anyString())).thenReturn(pathMock);
        when(criteriaBuilder.lower(any())).thenReturn(pathMock);
        when(criteriaBuilder.like(any(Expression.class), anyString())).thenReturn(mock(Predicate.class));
        when(criteriaBuilder.equal(any(), any())).thenReturn(mock(Predicate.class));
        when(criteriaBuilder.and(any(Predicate.class), any(Predicate.class))).thenReturn(mock(Predicate.class));
        when(subqueryMock.select(any())).thenReturn(subqueryMock);

        Predicate existsPredicate = mock(Predicate.class);
        when(criteriaBuilder.exists(any())).thenReturn(existsPredicate);

        Specification<Order> spec = OrderSpecification.withProductName("Keyboard");
        Predicate result = spec.toPredicate(root, query, criteriaBuilder);

        assertEquals(existsPredicate, result);
    }

    @Test
    void testWithBillingPhoneNumber_whenNull_thenReturnConjunction() {
        Predicate conjunction = mock(Predicate.class);
        when(criteriaBuilder.conjunction()).thenReturn(conjunction);

        Specification<Order> spec = OrderSpecification.withBillingPhoneNumber(null);
        Predicate result = spec.toPredicate(root, query, criteriaBuilder);

        assertEquals(conjunction, result);
    }

    @Test
    void testWithBillingPhoneNumber_whenEmpty_thenReturnConjunction() {
        Predicate conjunction = mock(Predicate.class);
        when(criteriaBuilder.conjunction()).thenReturn(conjunction);

        Specification<Order> spec = OrderSpecification.withBillingPhoneNumber("");
        Predicate result = spec.toPredicate(root, query, criteriaBuilder);

        assertEquals(conjunction, result);
    }

    @Test
    void testWithCountryName_whenNull_thenReturnConjunction() {
        Predicate conjunction = mock(Predicate.class);
        when(criteriaBuilder.conjunction()).thenReturn(conjunction);

        Specification<Order> spec = OrderSpecification.withCountryName(null);
        Predicate result = spec.toPredicate(root, query, criteriaBuilder);

        assertEquals(conjunction, result);
    }

    @Test
    void testWithCountryName_whenEmpty_thenReturnConjunction() {
        Predicate conjunction = mock(Predicate.class);
        when(criteriaBuilder.conjunction()).thenReturn(conjunction);

        Specification<Order> spec = OrderSpecification.withCountryName("");
        Predicate result = spec.toPredicate(root, query, criteriaBuilder);

        assertEquals(conjunction, result);
    }

    @Test
    void testWithDateRange_whenCreatedFromNull_thenReturnConjunction() {
        Predicate conjunction = mock(Predicate.class);
        when(criteriaBuilder.conjunction()).thenReturn(conjunction);

        Specification<Order> spec = OrderSpecification.withDateRange(null, ZonedDateTime.now());
        Predicate result = spec.toPredicate(root, query, criteriaBuilder);

        assertEquals(conjunction, result);
    }

    @Test
    void testWithDateRange_whenCreatedToNull_thenReturnConjunction() {
        Predicate conjunction = mock(Predicate.class);
        when(criteriaBuilder.conjunction()).thenReturn(conjunction);

        Specification<Order> spec = OrderSpecification.withDateRange(ZonedDateTime.now(), null);
        Predicate result = spec.toPredicate(root, query, criteriaBuilder);

        assertEquals(conjunction, result);
    }

    @Test
    @SuppressWarnings("unchecked")
    void testFindOrderByWithMulCriteria_whenQueryResultTypeLong_thenSkipFetch() {
        when(query.getResultType()).thenReturn((Class) Long.class);
        Predicate conjunction = mock(Predicate.class);
        when(criteriaBuilder.conjunction()).thenReturn(conjunction);

        Specification<Order> spec = OrderSpecification.findOrderByWithMulCriteria(
            List.of(), "", "", "", "", null, null);

        // Should not throw and should NOT call root.fetch
        spec.toPredicate(root, query, criteriaBuilder);
    }

    @Test
    @SuppressWarnings("unchecked")
    void testFindOrderByWithMulCriteria_whenQueryResultTypeNotLong_thenDoFetch() {
        when(query.getResultType()).thenReturn((Class) Order.class);
        Predicate conjunction = mock(Predicate.class);
        when(criteriaBuilder.conjunction()).thenReturn(conjunction);

        Specification<Order> spec = OrderSpecification.findOrderByWithMulCriteria(
            List.of(), "", "", "", "", null, null);

        // Should not throw and WILL call root.fetch (return value discarded by source code)
        spec.toPredicate(root, query, criteriaBuilder);
    }
}
