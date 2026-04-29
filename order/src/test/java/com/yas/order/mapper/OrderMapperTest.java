package com.yas.order.mapper;

import com.yas.order.model.csv.OrderItemCsv;
import com.yas.order.viewmodel.order.OrderBriefVm;
import com.yas.order.viewmodel.orderaddress.OrderAddressVm;
import org.assertj.core.api.Assertions;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {OrderMapperImpl.class})
class OrderMapperTest {

    @Autowired
    OrderMapper orderMapper;

    @Test
    void testToCsv_convertToCorrectOrderItemCsv() {
        OrderAddressVm billingAddressVm = Instancio.create(OrderAddressVm.class);
        OrderBriefVm orderBriefVm = Instancio.of(OrderBriefVm.class)
                .set(org.instancio.Select.field("billingAddressVm"), billingAddressVm)
                .create();

        OrderItemCsv result = orderMapper.toCsv(orderBriefVm);

        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getId()).isEqualTo(orderBriefVm.id());
        Assertions.assertThat(result.getEmail()).isEqualTo(orderBriefVm.email());
        Assertions.assertThat(result.getPhone()).isEqualTo(billingAddressVm.phone());
        Assertions.assertThat(result.getTotalPrice()).isEqualTo(orderBriefVm.totalPrice());
        Assertions.assertThat(result.getOrderStatus()).isEqualTo(orderBriefVm.orderStatus());
        Assertions.assertThat(result.getDeliveryStatus()).isEqualTo(orderBriefVm.deliveryStatus());
        Assertions.assertThat(result.getPaymentStatus()).isEqualTo(orderBriefVm.paymentStatus());
        Assertions.assertThat(result.getCreatedOn()).isEqualTo(orderBriefVm.createdOn());
    }
}
