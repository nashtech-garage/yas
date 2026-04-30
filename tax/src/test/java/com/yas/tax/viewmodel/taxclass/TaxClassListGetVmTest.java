package com.yas.tax.viewmodel.taxclass;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;

class TaxClassListGetVmTest {

    @Test
    void testTaxClassListGetVmConstructor() {
        // 1. Chuẩn bị dữ liệu mẫu cho các tham số
        List<TaxClassVm> taxClassContent = new ArrayList<>();
        int pageNo = 1;
        int pageSize = 10;
        int totalElements = 100;
        int totalPages = 10;
        boolean isLast = false;

        // 2. Gọi hàm khởi tạo (Constructor) để JaCoCo ghi nhận coverage
        // Thứ tự tham số: (List<TaxClassVm>, int pageNo, int pageSize, int totalElements, int totalPages, boolean isLast)
        TaxClassListGetVm vm = new TaxClassListGetVm(
            taxClassContent, 
            pageNo, 
            pageSize, 
            totalElements, 
            totalPages, 
            isLast
        );

        // 3. Kiểm tra xem đối tượng có được tạo thành công không
        assertNotNull(vm);
        assertEquals(pageNo, vm.pageNo());
        assertEquals(pageSize, vm.pageSize());
        assertEquals(totalElements, vm.totalElements());
        assertEquals(totalPages, vm.totalPages());
        assertEquals(isLast, vm.isLast());
        assertEquals(taxClassContent, vm.taxClassContent());
    }
    
    @Test
    void testTaxClassListGetVmWithData() {
        // Test với dữ liệu có nội dung
        List<TaxClassVm> taxClassContent = new ArrayList<>();
        taxClassContent.add(new TaxClassVm(1L, "Standard")); // TaxClassVm record với (Long id, String name)
        
        TaxClassListGetVm vm = new TaxClassListGetVm(
            taxClassContent, 
            0, 
            20, 
            150, 
            8, 
            true
        );

        assertNotNull(vm);
        assertEquals(1, vm.taxClassContent().size());
        assertTrue(vm.isLast());
    }
}