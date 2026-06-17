package com.yas.order.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.yas.commonlibrary.csv.BaseCsv;
import com.yas.commonlibrary.utils.AuthenticationUtils;
import com.yas.order.mapper.OrderMapper;
import com.yas.order.model.Order;
import com.yas.order.model.csv.OrderItemCsv;
import com.yas.order.model.request.OrderRequest;
import com.yas.order.repository.OrderRepository;
import com.yas.order.viewmodel.order.OrderExistsByProductAndUserGetVm;
import com.yas.order.viewmodel.product.ProductVariationVm;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
class OrderServiceOtherTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private ProductService productService;
    @Mock
    private OrderMapper orderMapper;

    @InjectMocks
    private OrderService orderService;

    private MockedStatic<AuthenticationUtils> authenticationUtilsMock;

    @BeforeEach
    void setUp() {
        authenticationUtilsMock = Mockito.mockStatic(AuthenticationUtils.class);
    }

    @AfterEach
    void tearDown() {
        authenticationUtilsMock.close();
    }

    @Test
    void isOrderCompletedWithUserIdAndProductId_whenHasVariationsAndExists_thenReturnTrue() {
        authenticationUtilsMock.when(AuthenticationUtils::extractUserId).thenReturn("user-1");
        when(productService.getProductVariations(1L)).thenReturn(List.of(
                new ProductVariationVm(2L, "var1", "sku1")
        ));
        
        when(orderRepository.findOne(any(Specification.class))).thenReturn(Optional.of(new Order()));

        OrderExistsByProductAndUserGetVm result = orderService.isOrderCompletedWithUserIdAndProductId(1L);

        assertThat(result.isPresent()).isTrue();
    }

    @Test
    void isOrderCompletedWithUserIdAndProductId_whenNoVariationsAndNotExists_thenReturnFalse() {
        authenticationUtilsMock.when(AuthenticationUtils::extractUserId).thenReturn("user-1");
        when(productService.getProductVariations(1L)).thenReturn(List.of());
        
        when(orderRepository.findOne(any(Specification.class))).thenReturn(Optional.empty());

        OrderExistsByProductAndUserGetVm result = orderService.isOrderCompletedWithUserIdAndProductId(1L);

        assertThat(result.isPresent()).isFalse();
    }

    @Test
    void exportCsv_whenNoOrders_thenReturnEmptyCsv() throws IOException {
        OrderRequest request = new OrderRequest();
        request.setPageNo(0);
        request.setPageSize(10);
        request.setOrderStatus(List.of());
        request.setCreatedFrom(ZonedDateTime.now());
        request.setCreatedTo(ZonedDateTime.now());
        request.setBillingCountry("VN");
        request.setBillingPhoneNumber("123456789");

        when(orderRepository.findAll(Mockito.<Specification<Order>>any(), Mockito.<org.springframework.data.domain.Pageable>any())).thenReturn(Page.empty());

        byte[] result = orderService.exportCsv(request);
        
        assertThat(result).isNotEmpty(); // CSV header is present
    }

    @Test
    void exportCsv_whenHasOrders_thenReturnCsvWithData() throws IOException {
        OrderRequest request = new OrderRequest();
        request.setPageNo(0);
        request.setPageSize(10);
        request.setOrderStatus(List.of());
        request.setCreatedFrom(ZonedDateTime.now());
        request.setCreatedTo(ZonedDateTime.now());
        request.setBillingCountry("VN");
        request.setBillingPhoneNumber("123456789");

        Order order = new Order();
        order.setId(1L);
        com.yas.order.model.OrderAddress address = com.yas.order.model.OrderAddress.builder()
                .id(1L)
                .contactName("Test")
                .phone("123")
                .build();
        order.setBillingAddressId(address);
        order.setShippingAddressId(address);
        Page<Order> page = new PageImpl<>(List.of(order));
        
        when(orderRepository.findAll(Mockito.<Specification<Order>>any(), Mockito.<org.springframework.data.domain.Pageable>any())).thenReturn(page);
        
        OrderItemCsv csvMock = OrderItemCsv.builder().id(1L).build();
        when(orderMapper.toCsv(any())).thenReturn(csvMock);

        byte[] result = orderService.exportCsv(request);
        
        assertThat(result).isNotEmpty();
    }
}
