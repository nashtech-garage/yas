package com.yas.order.repository;

import com.yas.order.model.Order;
import com.yas.order.model.enumeration.EOrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;


@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    @Query("SELECT EXISTS (" +
            "SELECT 1 FROM Order o INNER JOIN o.orderItems oi " +
            "WHERE o.createdBy=:createdBy " +
            "       AND o.orderStatus=COMPLETED " +
            "       AND oi.productId in :productId" +
            ")")
    boolean existsByCreatedByAndInProductIdAndOrderStatusCompleted(
            String createdBy,
            List<Long> productId
    );

    @Query("SELECT DISTINCT o FROM Order o JOIN FETCH o.orderItems " +
            "WHERE o.createdBy=:userId " +
            "AND (:orderStatus is null or o.orderStatus=:orderStatus) " +
            "AND o.id in (select oi.orderId.id from OrderItem oi " +
            "where (:productName = '' OR lower(oi.productName) like concat('%', lower(:productName), '%'))) " +
            "ORDER BY o.createdOn DESC")
    List<Order> findMyOrders(String userId, String productName, EOrderStatus orderStatus);

    @Query("SELECT o FROM Order o JOIN o.orderItems oi " +
            "WHERE LOWER(o.email) LIKE %:email% " +
            "AND (o.orderStatus IN (:orderStatus)) " +
            "AND LOWER(oi.productName) LIKE %:productName% " +
            "AND LOWER(o.billingAddressId.phone) LIKE %:billingPhoneNumber% " +
            "AND LOWER(o.billingAddressId.countryName) LIKE %:countryName% " +
            "AND o.createdOn BETWEEN :createdFrom AND :createdTo " +
            "ORDER BY o.createdOn DESC")
    Page<Order> findOrderByWithMulCriteria(
            @Param("orderStatus") List<EOrderStatus> orderStatus,
            @Param("billingPhoneNumber") String billingPhoneNumber,
            @Param("countryName") String countryName,
            @Param("email") String email,
            @Param("productName") String productName,
            @Param("createdFrom") ZonedDateTime createdFrom,
            @Param("createdTo") ZonedDateTime createdTo,
            Pageable pageable);

    @Query("SELECT o FROM Order o JOIN o.orderItems oi " +
            "WHERE LOWER(o.email) LIKE %:email% " +
            "AND (o.orderStatus IN (:orderStatus)) " +
            "AND LOWER(oi.productName) LIKE %:productName% " +
            "AND LOWER(o.billingAddressId.phone) LIKE %:billingPhoneNumber% " +
            "AND LOWER(o.billingAddressId.countryName) LIKE %:countryName% " +
            "AND o.createdOn BETWEEN :createdFrom AND :createdTo " +
            "ORDER BY o.createdOn DESC")
    List<Order> exportOrders(
            @Param("orderStatus") List<EOrderStatus> orderStatus,
            @Param("billingPhoneNumber") String billingPhoneNumber,
            @Param("countryName") String countryName,
            @Param("email") String email,
            @Param("productName") String productName,
            @Param("createdFrom") ZonedDateTime createdFrom,
            @Param("createdTo") ZonedDateTime createdTo);

    Optional<Order> findByCheckoutId(String checkoutId);
}
