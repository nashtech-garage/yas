package com.yas.order.specification;

import com.yas.order.model.Order;
import com.yas.order.model.OrderItem;
import com.yas.order.model.enumeration.OrderStatus;
import com.yas.order.utils.Constants;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

public class OrderSpecification {

    private OrderSpecification() {}

    public static Specification<Order> existsByCreatedByAndInProductIdAndOrderStatusCompleted(
        String createdBy, List<Long> productIds) {

        return Specification.where(hasCreatedBy(createdBy))
            .and(hasOrderStatus(OrderStatus.COMPLETED))
            .and(hasProductInOrderItems(productIds));
    }

    public static Specification<Order> findMyOrders(String userId, String productName, OrderStatus orderStatus) {
        return (root, query, criteriaBuilder) -> {

            Predicate hasCreatedByPredicate
                = hasCreatedBy(userId).toPredicate(root, query, criteriaBuilder);
            Predicate hasOrderStatusPredicate
                = hasOrderStatus(orderStatus).toPredicate(root, query, criteriaBuilder);
            Predicate hasProductNamePredicate
                = hasProductNameInOrderItems(productName).toPredicate(root, query, criteriaBuilder);

            return criteriaBuilder.and(
                hasCreatedByPredicate,
                hasOrderStatusPredicate,
                hasProductNamePredicate
            );
        };
    }

    public static Specification<Order> findOrderByWithMulCriteria(
        List<OrderStatus> orderStatus,
        String billingPhoneNumber,
        String countryName,
        String email,
        String productName,
        ZonedDateTime createdFrom,
        ZonedDateTime createdTo) {

        return (root, query, criteriaBuilder) -> {

            if (query != null && Long.class != query.getResultType()) {
                root.fetch(Constants.Column.ORDER_SHIPPING_ADDRESS_ID_COLUMN, JoinType.LEFT);
                root.fetch(Constants.Column.ORDER_BILLING_ADDRESS_ID_COLUMN, JoinType.LEFT);
            }

            Predicate withEmail
                = withEmail(email).toPredicate(root, query, criteriaBuilder);
            Predicate withOrderStatus
                = withOrderStatus(orderStatus).toPredicate(root, query, criteriaBuilder);
            Predicate withProductName
                = withProductName(productName).toPredicate(root, query, criteriaBuilder);
            Predicate withBillingPhoneNumber
                = withBillingPhoneNumber(billingPhoneNumber).toPredicate(root, query, criteriaBuilder);
            Predicate withCountryName
                = withCountryName(countryName).toPredicate(root, query, criteriaBuilder);
            Predicate withDateRange
                = withDateRange(createdFrom, createdTo).toPredicate(root, query, criteriaBuilder);

            return criteriaBuilder.and(
                withEmail,
                withOrderStatus,
                withProductName,
                withBillingPhoneNumber,
                withCountryName,
                withDateRange
            );
        };
    }

    public static Specification<Order> hasProductInOrderItems(List<Long> productIds) {
        return (root, query, criteriaBuilder) -> {

            if (query == null) {
                return criteriaBuilder.conjunction();
            }

            Subquery<OrderItem> subquery = query.subquery(OrderItem.class);
            Root<OrderItem> orderItemRoot = subquery.from(OrderItem.class);
            subquery.select(orderItemRoot)
                .where(
                    criteriaBuilder.and(
                        criteriaBuilder.equal(
                            orderItemRoot.get(Constants.Column.ORDER_ORDER_ID_COLUMN),
                            root.get(Constants.Column.ID_COLUMN)
                        ),
                        orderItemRoot.get(Constants.Column.ORDER_ITEM_PRODUCT_ID_COLUMN)
                            .in(Optional.ofNullable(productIds).orElse(List.of()))
                    )
                );

            return criteriaBuilder.exists(subquery);
        };
    }

    public static Specification<Order> hasCreatedBy(String createdBy) {
        return (root, query, criteriaBuilder) ->
            criteriaBuilder.equal(root.get(Constants.Column.CREATE_BY_COLUMN), createdBy);
    }

    public static Specification<Order> hasOrderStatus(OrderStatus orderStatus) {
        return (root, query, criteriaBuilder) -> {
            if (orderStatus != null) {
                return criteriaBuilder.equal(
                    root.get(Constants.Column.ORDER_ORDER_STATUS_COLUMN),
                    orderStatus);
            }
            return criteriaBuilder.conjunction();
        };
    }

    public static Specification<Order> hasProductNameInOrderItems(String productName) {
        return (root, query, criteriaBuilder) -> {

            if (query == null) {
                return criteriaBuilder.conjunction();
            }

            Subquery<Long> subquery = query.subquery(Long.class);
            Root<OrderItem> orderItemRoot = subquery.from(OrderItem.class);

            Predicate productNamePredicate = criteriaBuilder.conjunction();
            if (StringUtils.isNotEmpty(productName)) {
                productNamePredicate = criteriaBuilder.like(
                    criteriaBuilder.lower(orderItemRoot.get(Constants.Column.ORDER_ITEM_PRODUCT_NAME_COLUMN)),
                    "%" + productName.toLowerCase() + "%"
                );
            }

            subquery.select(orderItemRoot.get(Constants.Column.ORDER_ORDER_ID_COLUMN))
                .where(productNamePredicate);

            return criteriaBuilder.in(root.get(Constants.Column.ID_COLUMN)).value(subquery);
        };
    }

    public static Specification<Order> withEmail(String email) {
        return (root, query, criteriaBuilder) -> {
            if (StringUtils.isNotEmpty(email)) {
                return criteriaBuilder.like(
                    criteriaBuilder.lower(root.get(Constants.Column.ORDER_EMAIL_COLUMN)),
                    "%" + email.toLowerCase() + "%"
                );
            }
            return criteriaBuilder.conjunction();
        };
    }

    public static Specification<Order> withOrderStatus(List<OrderStatus> orderStatus) {
        return (root, query, criteriaBuilder) -> {
            if (CollectionUtils.isNotEmpty(orderStatus)) {
                return root.get(Constants.Column.ORDER_ORDER_STATUS_COLUMN).in(orderStatus);
            }
            return criteriaBuilder.conjunction();
        };
    }

    public static Specification<Order> withProductName(String productName) {
        return (root, query, criteriaBuilder) -> {

            if (query == null || StringUtils.isEmpty(productName)) {
                return criteriaBuilder.conjunction();
            }

            Subquery<Long> subquery = query.subquery(Long.class);
            Root<OrderItem> orderItemRoot = subquery.from(OrderItem.class);
            subquery.select(orderItemRoot.get(Constants.Column.ORDER_ORDER_ID_COLUMN));
            subquery.where(
                criteriaBuilder.and(
                    criteriaBuilder.equal(
                        orderItemRoot.get(Constants.Column.ORDER_ORDER_ID_COLUMN),
                        root.get(Constants.Column.ID_COLUMN)),
                    criteriaBuilder.like(
                        criteriaBuilder.lower(orderItemRoot.get(Constants.Column.ORDER_ITEM_PRODUCT_NAME_COLUMN)),
                        "%" + productName.toLowerCase() + "%"
                    )
                )
            );
            return criteriaBuilder.exists(subquery);

        };
    }

    public static Specification<Order> withBillingPhoneNumber(String billingPhoneNumber) {
        return (root, query, criteriaBuilder) -> {
            if (billingPhoneNumber != null && !billingPhoneNumber.isEmpty()) {
                return criteriaBuilder.like(
                    criteriaBuilder.lower(
                        root.get(Constants.Column.ORDER_BILLING_ADDRESS_ID_COLUMN)
                            .get(Constants.Column.ORDER_PHONE_COLUMN)
                    ),
                    "%" + billingPhoneNumber.toLowerCase() + "%"
                );
            }
            return criteriaBuilder.conjunction();
        };
    }

    public static Specification<Order> withCountryName(String countryName) {
        return (root, query, criteriaBuilder) -> {
            if (countryName != null && !countryName.isEmpty()) {
                return criteriaBuilder.like(
                    criteriaBuilder.lower(
                        root.get(Constants.Column.ORDER_BILLING_ADDRESS_ID_COLUMN)
                            .get(Constants.Column.ORDER_COUNTRY_NAME_COLUMN)
                    ),
                    "%" + countryName.toLowerCase() + "%"
                );
            }
            return criteriaBuilder.conjunction();
        };
    }

    public static Specification<Order> withDateRange(ZonedDateTime createdFrom, ZonedDateTime createdTo) {
        return (root, query, criteriaBuilder) -> {
            if (createdFrom != null && createdTo != null) {
                return criteriaBuilder.between(
                    root.get(Constants.Column.CREATE_ON_COLUMN),
                    createdFrom,
                    createdTo
                );
            }
            return criteriaBuilder.conjunction();
        };
    }

}
