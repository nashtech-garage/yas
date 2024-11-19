package com.yas.order.service;

import static com.yas.order.utils.SecurityContextUtils.setSubjectUpSecurityContext;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.yas.commonlibrary.exception.BadRequestException;
import com.yas.commonlibrary.exception.Forbidden;
import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.order.mapper.CheckoutMapperImpl;
import com.yas.order.model.Checkout;
import com.yas.order.model.CheckoutItem;
import com.yas.order.model.enumeration.CheckoutState;
import com.yas.order.repository.CheckoutItemRepository;
import com.yas.order.repository.CheckoutRepository;
import com.yas.order.viewmodel.checkout.CheckoutPaymentMethodPutVm;
import com.yas.order.viewmodel.checkout.CheckoutPostVm;
import com.yas.order.viewmodel.checkout.CheckoutVm;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {CheckoutMapperImpl.class, CheckoutService.class})
class CheckoutServiceTest {

    @MockBean
    CheckoutRepository checkoutRepository;

    @MockBean
    CheckoutItemRepository checkoutItemRepository;

    @MockBean
    OrderService orderService;

    @Autowired
    CheckoutService checkoutService;

    CheckoutPostVm checkoutPostVm;
    List<CheckoutItem> checkoutItems;
    Checkout checkoutCreated;
    String checkoutId = UUID.randomUUID().toString();

    @BeforeEach
    void setUp() {

        checkoutPostVm = Instancio.create(CheckoutPostVm.class);
        checkoutCreated = Checkout.builder()
            .id(checkoutId)
            .checkoutState(CheckoutState.PENDING)
            .note(checkoutPostVm.note())
            .email(checkoutPostVm.email())
            .couponCode(checkoutPostVm.couponCode())
            .build();

        checkoutItems = checkoutPostVm.checkoutItemVms().stream()
            .map(itemVm -> CheckoutItem.builder()
                .id(Instancio.create(Long.class))
                .productId(itemVm.productId())
                .productName(itemVm.productName())
                .quantity(itemVm.quantity())
                .productPrice(itemVm.productPrice())
                .discountAmount(itemVm.discountAmount())
                .taxAmount(itemVm.taxAmount())
                .taxPercent(itemVm.taxPercent())
                .note(itemVm.note())
                .checkoutId(checkoutId)
                .build()
            ).toList();

    }

    @Test
    void testCreateCheckout_whenNormalCase_returnCheckout() {

        when(checkoutRepository.save(any())).thenReturn(checkoutCreated);
        when(checkoutItemRepository.saveAll(anyCollection())).thenReturn(checkoutItems);
        var res = checkoutService.createCheckout(checkoutPostVm);

        assertThat(res)
            .hasFieldOrPropertyWithValue("id", checkoutId)
            .hasFieldOrPropertyWithValue("email", checkoutPostVm.email())
            .hasFieldOrPropertyWithValue("couponCode", checkoutPostVm.couponCode())
            .hasFieldOrPropertyWithValue("note", checkoutPostVm.note());

        assertThat(res.checkoutItemVms())
            .hasSize(checkoutPostVm.checkoutItemVms().size())
            .allMatch(item -> item.checkoutId().equals(checkoutId));
    }

    @Test
    void testCreateCheckout_whenCheckoutItemsIsEmpty_returnCheckoutWithoutCheckoutItems() {

        when(checkoutRepository.save(any())).thenReturn(checkoutCreated);
        when(checkoutItemRepository.saveAll(anyCollection())).thenReturn(List.of());
        var res = checkoutService.createCheckout(checkoutPostVm);

        assertThat(res)
            .hasFieldOrPropertyWithValue("id", checkoutId)
            .hasFieldOrPropertyWithValue("email", checkoutPostVm.email())
            .hasFieldOrPropertyWithValue("couponCode", checkoutPostVm.couponCode())
            .hasFieldOrPropertyWithValue("note", checkoutPostVm.note());

        assertThat(res.checkoutItemVms()).isEmpty();
    }

    @Test
    void testGetCheckoutPendingStateWithItemsById_whenNormalCase_returnCheckoutVm() {

        checkoutCreated.setCreatedBy("test-create-by");
        when(checkoutRepository.findByIdAndCheckoutState(anyString(), eq(CheckoutState.PENDING)))
            .thenReturn(Optional.ofNullable(checkoutCreated));

        setSubjectUpSecurityContext(checkoutCreated.getCreatedBy());
        when(checkoutItemRepository.findAllByCheckoutId(anyString())).thenReturn(checkoutItems);
        var res = checkoutService.getCheckoutPendingStateWithItemsById("1");

        assertThat(res)
            .hasFieldOrPropertyWithValue("id", checkoutId)
            .hasFieldOrPropertyWithValue("couponCode", checkoutPostVm.couponCode())
            .hasFieldOrPropertyWithValue("email", checkoutPostVm.email())
            .hasFieldOrPropertyWithValue("note", checkoutPostVm.note());

        assertThat(res.checkoutItemVms())
            .allMatch(item -> item.checkoutId().equals(checkoutId))
            .hasSize(checkoutPostVm.checkoutItemVms().size());
    }

    @Test
    void testGetCheckoutPendingStateWithItemsById_whenNotEqualsCreateBy_throwForbidden() {

        checkoutCreated.setCreatedBy("test-create-by");
        when(checkoutRepository.findByIdAndCheckoutState(anyString(), eq(CheckoutState.PENDING)))
            .thenReturn(Optional.ofNullable(checkoutCreated));
        setSubjectUpSecurityContext("test--by");

        assertThrows(Forbidden.class,
            () -> checkoutService.getCheckoutPendingStateWithItemsById("1"),
            "You don't have permission to access this page");


    }

    @Test
    void testGetCheckoutPendingStateWithItemsById_whenNormalCase_returnCheckoutVmWithoutCheckoutItems() {

        checkoutCreated.setCreatedBy("test-create-by");
        setSubjectUpSecurityContext(checkoutCreated.getCreatedBy());
        when(checkoutRepository.findByIdAndCheckoutState(anyString(), eq(CheckoutState.PENDING)))
            .thenReturn(Optional.ofNullable(checkoutCreated));
        when(checkoutItemRepository.findAllByCheckoutId(anyString())).thenReturn(List.of());

        var res = checkoutService.getCheckoutPendingStateWithItemsById("1");

        assertThat(res)
            .hasFieldOrPropertyWithValue("id", checkoutId)
            .hasFieldOrPropertyWithValue("couponCode", checkoutPostVm.couponCode())
            .hasFieldOrPropertyWithValue("note", checkoutPostVm.note())
            .hasFieldOrPropertyWithValue("email", checkoutPostVm.email());

        assertThat(res.checkoutItemVms()).isNull();
    }

    @Test
    void testUpdateCheckoutPaymentMethod_whenCheckoutExists_thenUpdatePaymentMethod() {
        // Arrange
        String id = "123";
        Checkout checkout = new Checkout();
        checkout.setId(id);

        CheckoutPaymentMethodPutVm request = new CheckoutPaymentMethodPutVm("BANKING");

        when(checkoutRepository.findById(id)).thenReturn(Optional.of(checkout));

        // Act
        checkoutService.updateCheckoutPaymentMethod(id, request);

        // Assert
        verify(checkoutRepository).save(checkout);
        assertThat(checkout.getPaymentMethodId().name()).isEqualTo(request.paymentMethodId());
    }

    @Test
    void testUpdateCheckoutPaymentMethod_whenCheckoutNotFound_thenThrowNotFoundException() {
        // Arrange
        String id = "invalid-id";
        CheckoutPaymentMethodPutVm request = new CheckoutPaymentMethodPutVm("new-payment-method-id");

        when(checkoutRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> checkoutService.updateCheckoutPaymentMethod(id, request));
    }

    @Test
    void testUpdateCheckoutPaymentMethod_whenPaymentMethodIdIsNull_thenDoNotUpdate() {
        // Arrange
        String id = "123";
        Checkout checkout = new Checkout();
        checkout.setId(id);

        CheckoutPaymentMethodPutVm request = new CheckoutPaymentMethodPutVm(null);

        when(checkoutRepository.findById(id)).thenReturn(Optional.of(checkout));

        // Act
        checkoutService.updateCheckoutPaymentMethod(id, request);

        // Assert
        verify(checkoutRepository, never()).save(checkout);
        assertThat(checkout.getPaymentMethodId()).isNull();
    }

    @Test
    void testFindCheckoutById_whenNotFound_throwNotFoundException() {
        String id = "123";
        when(checkoutRepository.findById(id))
            .thenReturn(Optional.empty());
        NotFoundException notFound = assertThrows(NotFoundException.class,
            () -> checkoutService.findCheckoutById(id));
        assertThat(notFound.getMessage()).isEqualTo("Checkout 123 is not found");
    }

    @Test
    void testFindCheckoutWithItemsById_whenNormalCase_returnCheckoutVm() {
        String id = "123";
        Checkout checkout = new Checkout();
        checkout.setId(id);
        when(checkoutRepository.findById(id))
            .thenReturn(Optional.of(checkout));

        List<CheckoutItem> checkoutItemVms = getCheckoutItems();
        when(checkoutItemRepository.findAllByCheckoutId(id))
            .thenReturn(checkoutItemVms);
        CheckoutVm checkoutVm = checkoutService.findCheckoutWithItemsById(id);
        assertThat(checkoutVm.id()).isEqualTo(id);
        assertThat(checkoutVm.checkoutItemVms()).hasSize(2);
    }

    @Test
    void testUpdateCheckout_whenNormalCase_thenUpdateCheckout() {
        String id = "123";
        Checkout checkout = new Checkout();
        checkout.setId(id);
        checkoutService.updateCheckout(checkout);
        verify(checkoutRepository, times(1)).save(checkout);
    }

    @Test
    void testUpdateCheckout_whenIdIsNull_throwBadRequestException() {
        Checkout checkout = new Checkout();
        BadRequestException badRequestException = assertThrows(BadRequestException.class,
            () -> checkoutService.updateCheckout(checkout));
        assertThat(badRequestException.getMessage()).isEqualTo("ID is not existed");
    }

    private List<CheckoutItem > getCheckoutItems() {
        return List.of(
            CheckoutItem.builder()
                .id(1L)
                .productId(101L)
                .checkoutId("checkout123")
                .productName("Product A")
                .quantity(2)
                .productPrice(BigDecimal.valueOf(50.0))
                .note("Note A")
                .discountAmount(BigDecimal.valueOf(5.0))
                .taxAmount(BigDecimal.valueOf(1.0))
                .taxPercent(BigDecimal.valueOf(0.02))
                .shipmentTax(BigDecimal.valueOf(0.5))
                .shipmentFee(BigDecimal.valueOf(3.0))
                .build(),
            CheckoutItem.builder()
                .id(2L)
                .productId(102L)
                .checkoutId("checkout124")
                .productName("Product B")
                .quantity(1)
                .productPrice(BigDecimal.valueOf(30.0))
                .note("Note B")
                .discountAmount(BigDecimal.valueOf(3.0))
                .taxAmount(BigDecimal.valueOf(0.6))
                .taxPercent(BigDecimal.valueOf(0.02))
                .shipmentTax(BigDecimal.valueOf(0.3))
                .shipmentFee(BigDecimal.valueOf(2.0))
                .build()
        );
    }
}