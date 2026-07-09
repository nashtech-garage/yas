package com.yas.order.mapper;

import com.yas.order.model.Order;
import com.yas.order.model.csv.OrderItemCsv;
import com.yas.order.viewmodel.order.OrderBriefVm;
import com.yas.order.viewmodel.order.OrderVm;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE 
)
public interface OrderMapper {
    @Mapping(source = "id", target = "id") // Ví dụ ánh xạ ID nếu tên biến giống nhau
    // @Mapping(source = "totalPrice", target = "totalAmount") // Mở ra nếu tên biến ở nguồn và đích khác nhau
    OrderItemCsv toCsv(OrderBriefVm orderBriefVm);

    // Bổ sung thêm hàm map cơ bản từ Entity sang chi tiết VM để dùng cho các logic khác
    OrderVm toVm(Order order);
}