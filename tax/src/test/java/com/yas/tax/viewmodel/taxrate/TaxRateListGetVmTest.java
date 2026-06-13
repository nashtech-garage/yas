package com.yas.tax.viewmodel.taxrate;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class TaxRateListGetVmTest {

    private List<TaxRateGetDetailVm> sampleList() {
        return List.of(
            new TaxRateGetDetailVm(1L, 10.0, "10000", "VAT", "HCM", "Vietnam"),
            new TaxRateGetDetailVm(2L, 20.0, "20000", "GST", "Hanoi", "Vietnam")
        );
    }

    @Test
    void constructor_ShouldCreateCorrectly() {
        List<TaxRateGetDetailVm> content = sampleList();
        TaxRateListGetVm vm = new TaxRateListGetVm(content, 0, 10, 2, 1, true);

        assertEquals(content, vm.taxRateGetDetailContent());
        assertEquals(0, vm.pageNo());
        assertEquals(10, vm.pageSize());
        assertEquals(2, vm.totalElements());
        assertEquals(1, vm.totalPages());
        assertTrue(vm.isLast());
    }

    @Test
    void constructor_WithEmptyList_ShouldWork() {
        TaxRateListGetVm vm = new TaxRateListGetVm(List.of(), 0, 10, 0, 0, true);

        assertTrue(vm.taxRateGetDetailContent().isEmpty());
        assertEquals(0, vm.totalElements());
        assertEquals(0, vm.totalPages());
    }

    @Test
    void isLast_WhenFalse_ShouldReturnFalse() {
        TaxRateListGetVm vm = new TaxRateListGetVm(sampleList(), 0, 10, 20, 2, false);

        assertFalse(vm.isLast());
    }

    @Test
    void equals_ShouldReturnTrueForSameValues() {
        List<TaxRateGetDetailVm> content = sampleList();
        TaxRateListGetVm vm1 = new TaxRateListGetVm(content, 0, 10, 2, 1, true);
        TaxRateListGetVm vm2 = new TaxRateListGetVm(content, 0, 10, 2, 1, true);

        assertEquals(vm1, vm2);
    }

    @Test
    void equals_ShouldReturnFalseForDifferentValues() {
        TaxRateListGetVm vm1 = new TaxRateListGetVm(sampleList(), 0, 10, 2, 1, true);
        TaxRateListGetVm vm2 = new TaxRateListGetVm(sampleList(), 1, 10, 2, 1, false);

        assertNotEquals(vm1, vm2);
    }

    @Test
    void hashCode_ShouldBeEqualForSameValues() {
        List<TaxRateGetDetailVm> content = sampleList();
        TaxRateListGetVm vm1 = new TaxRateListGetVm(content, 0, 10, 2, 1, true);
        TaxRateListGetVm vm2 = new TaxRateListGetVm(content, 0, 10, 2, 1, true);

        assertEquals(vm1.hashCode(), vm2.hashCode());
    }

    @Test
    void toString_ShouldContainPageInfo() {
        TaxRateListGetVm vm = new TaxRateListGetVm(sampleList(), 0, 10, 2, 1, true);
        String result = vm.toString();

        assertTrue(result.contains("0"));
        assertTrue(result.contains("10"));
        assertTrue(result.contains("2"));
        assertTrue(result.contains("1"));
    }
}