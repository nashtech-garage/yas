package com.yas.order.specification;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
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
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.domain.Specification;

/**
 * Additional branch coverage tests for OrderSpecification.
 * Covers null/empty parameter branches that the existing OrderSpecificationTest misses.
 */
class OrderSpecificationBranchTest {

    private final CriteriaBuilder cb = mock(CriteriaBuilder.class);
    private final Root<Order> root = mock(Root.class);
    private final CriteriaQuery<?> query = mock(CriteriaQuery.class);
    private final Predicate conjunction = mock(Predicate.class);

    @Test
    void testHasOrderStatus_whenNull_returnsConjunction() {
        when(cb.conjunction()).thenReturn(conjunction);
        Specification<Order> spec = OrderSpecification.hasOrderStatus(null);
        Predicate result = spec.toPredicate(root, query, cb);
        assertNotNull(result);
    }

    @Test
    void testWithEmail_whenEmpty_returnsConjunction() {
        when(cb.conjunction()).thenReturn(conjunction);
        Specification<Order> spec = OrderSpecification.withEmail("");
        Predicate result = spec.toPredicate(root, query, cb);
        assertNotNull(result);
    }

    @Test
    void testWithEmail_whenNull_returnsConjunction() {
        when(cb.conjunction()).thenReturn(conjunction);
        Specification<Order> spec = OrderSpecification.withEmail(null);
        Predicate result = spec.toPredicate(root, query, cb);
        assertNotNull(result);
    }

    @Test
    void testWithOrderStatus_whenEmpty_returnsConjunction() {
        when(cb.conjunction()).thenReturn(conjunction);
        Specification<Order> spec = OrderSpecification.withOrderStatus(List.of());
        Predicate result = spec.toPredicate(root, query, cb);
        assertNotNull(result);
    }

    @Test
    void testWithOrderStatus_whenNull_returnsConjunction() {
        when(cb.conjunction()).thenReturn(conjunction);
        Specification<Order> spec = OrderSpecification.withOrderStatus(null);
        Predicate result = spec.toPredicate(root, query, cb);
        assertNotNull(result);
    }

    @Test
    void testWithProductName_whenEmpty_returnsConjunction() {
        when(cb.conjunction()).thenReturn(conjunction);
        Specification<Order> spec = OrderSpecification.withProductName("");
        Predicate result = spec.toPredicate(root, query, cb);
        assertNotNull(result);
    }

    @Test
    void testWithProductName_whenNullQuery_returnsConjunction() {
        when(cb.conjunction()).thenReturn(conjunction);
        Specification<Order> spec = OrderSpecification.withProductName("product");
        Predicate result = spec.toPredicate(root, null, cb);
        assertNotNull(result);
    }

    @Test
    void testWithBillingPhoneNumber_whenEmpty_returnsConjunction() {
        when(cb.conjunction()).thenReturn(conjunction);
        Specification<Order> spec = OrderSpecification.withBillingPhoneNumber("");
        Predicate result = spec.toPredicate(root, query, cb);
        assertNotNull(result);
    }

    @Test
    void testWithBillingPhoneNumber_whenNull_returnsConjunction() {
        when(cb.conjunction()).thenReturn(conjunction);
        Specification<Order> spec = OrderSpecification.withBillingPhoneNumber(null);
        Predicate result = spec.toPredicate(root, query, cb);
        assertNotNull(result);
    }

    @Test
    void testWithCountryName_whenEmpty_returnsConjunction() {
        when(cb.conjunction()).thenReturn(conjunction);
        Specification<Order> spec = OrderSpecification.withCountryName("");
        Predicate result = spec.toPredicate(root, query, cb);
        assertNotNull(result);
    }

    @Test
    void testWithCountryName_whenNull_returnsConjunction() {
        when(cb.conjunction()).thenReturn(conjunction);
        Specification<Order> spec = OrderSpecification.withCountryName(null);
        Predicate result = spec.toPredicate(root, query, cb);
        assertNotNull(result);
    }

    @Test
    void testWithDateRange_whenBothNull_returnsConjunction() {
        when(cb.conjunction()).thenReturn(conjunction);
        Specification<Order> spec = OrderSpecification.withDateRange(null, null);
        Predicate result = spec.toPredicate(root, query, cb);
        assertNotNull(result);
    }

    @Test
    void testWithDateRange_whenFromNull_returnsConjunction() {
        when(cb.conjunction()).thenReturn(conjunction);
        Specification<Order> spec = OrderSpecification.withDateRange(null, java.time.ZonedDateTime.now());
        Predicate result = spec.toPredicate(root, query, cb);
        assertNotNull(result);
    }

    @Test
    void testHasProductInOrderItems_whenQueryNull_returnsConjunction() {
        when(cb.conjunction()).thenReturn(conjunction);
        Specification<Order> spec = OrderSpecification.hasProductInOrderItems(List.of(1L));
        Predicate result = spec.toPredicate(root, null, cb);
        assertNotNull(result);
    }

    @Test
    void testHasProductNameInOrderItems_whenQueryNull_returnsConjunction() {
        when(cb.conjunction()).thenReturn(conjunction);
        Specification<Order> spec = OrderSpecification.hasProductNameInOrderItems("product");
        Predicate result = spec.toPredicate(root, null, cb);
        assertNotNull(result);
    }

    @Test
    void testHasProductNameInOrderItems_whenEmptyProductName() {
        Subquery<Long> subqueryMock = mock(Subquery.class);
        when(query.subquery(Long.class)).thenReturn(subqueryMock);
        Root<OrderItem> orderItemRoot = mock(Root.class);
        when(subqueryMock.from(OrderItem.class)).thenReturn(orderItemRoot);
        when(subqueryMock.select(any())).thenReturn(subqueryMock);
        when(subqueryMock.where(any(Predicate.class))).thenReturn(subqueryMock);
        when(cb.conjunction()).thenReturn(conjunction);
        CriteriaBuilder.In inMock = mock(CriteriaBuilder.In.class);
        when(cb.in(any())).thenReturn(inMock);
        when(inMock.value(any())).thenReturn(inMock);

        Specification<Order> spec = OrderSpecification.hasProductNameInOrderItems("");
        Predicate result = spec.toPredicate(root, query, cb);
        assertNotNull(result);
    }

    @Test
    void testFindOrderByWithMulCriteria_whenQueryResultTypeIsLong() {
        when(query.getResultType()).thenReturn((Class) Long.class);
        when(root.get(anyString())).thenReturn(mock(Path.class));
        when(cb.and(any(), any(), any(), any(), any(), any())).thenReturn(conjunction);
        when(cb.conjunction()).thenReturn(conjunction);

        Subquery subquery = mock(Subquery.class);
        when(query.subquery(any(Class.class))).thenReturn(subquery);
        when(subquery.from(any(Class.class))).thenReturn(mock(Root.class));
        when(subquery.select(any())).thenReturn(subquery);
        CriteriaBuilder.In inMock = mock(CriteriaBuilder.In.class);
        when(cb.in(any())).thenReturn(inMock);
        when(inMock.value(any())).thenReturn(inMock);

        Specification<Order> spec = OrderSpecification.findOrderByWithMulCriteria(
            List.of(), null, null, null, null, null, null);
        Predicate result = spec.toPredicate(root, query, cb);
        assertNotNull(result);
        verify(root, never()).fetch(anyString(), any());
    }
}
