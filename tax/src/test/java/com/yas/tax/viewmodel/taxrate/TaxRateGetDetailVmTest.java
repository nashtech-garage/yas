package com.yas.tax.viewmodel.taxrate;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class TaxRateGetDetailVmTest {

    @Test
    void constructor_ShouldCreateCorrectly() {
        TaxRateGetDetailVm vm = new TaxRateGetDetailVm(1L, 10.0, "10000", "VAT", "HCM", "Vietnam");

        assertEquals(1L, vm.id());
        assertEquals(10.0, vm.rate());
        assertEquals("10000", vm.zipCode());
        assertEquals("VAT", vm.taxClassName());
        assertEquals("HCM", vm.stateOrProvinceName());
        assertEquals("Vietnam", vm.countryName());
    }

    @Test
    void constructor_WithNullValues_ShouldAllowNulls() {
        TaxRateGetDetailVm vm = new TaxRateGetDetailVm(null, null, null, null, null, null);

        assertNull(vm.id());
        assertNull(vm.rate());
        assertNull(vm.zipCode());
        assertNull(vm.taxClassName());
        assertNull(vm.stateOrProvinceName());
        assertNull(vm.countryName());
    }

    @Test
    void equals_ShouldReturnTrueForSameValues() {
        TaxRateGetDetailVm vm1 = new TaxRateGetDetailVm(1L, 10.0, "10000", "VAT", "HCM", "Vietnam");
        TaxRateGetDetailVm vm2 = new TaxRateGetDetailVm(1L, 10.0, "10000", "VAT", "HCM", "Vietnam");

        assertEquals(vm1, vm2);
    }

    @Test
    void equals_ShouldReturnFalseForDifferentValues() {
        TaxRateGetDetailVm vm1 = new TaxRateGetDetailVm(1L, 10.0, "10000", "VAT", "HCM", "Vietnam");
        TaxRateGetDetailVm vm2 = new TaxRateGetDetailVm(2L, 20.0, "20000", "GST", "Hanoi", "Vietnam");

        assertNotEquals(vm1, vm2);
    }

    @Test
    void hashCode_ShouldBeEqualForSameValues() {
        TaxRateGetDetailVm vm1 = new TaxRateGetDetailVm(1L, 10.0, "10000", "VAT", "HCM", "Vietnam");
        TaxRateGetDetailVm vm2 = new TaxRateGetDetailVm(1L, 10.0, "10000", "VAT", "HCM", "Vietnam");

        assertEquals(vm1.hashCode(), vm2.hashCode());
    }

    @Test
    void toString_ShouldContainAllFields() {
        TaxRateGetDetailVm vm = new TaxRateGetDetailVm(1L, 10.0, "10000", "VAT", "HCM", "Vietnam");
        String result = vm.toString();

        assertTrue(result.contains("1"));
        assertTrue(result.contains("10.0"));
        assertTrue(result.contains("10000"));
        assertTrue(result.contains("VAT"));
        assertTrue(result.contains("HCM"));
        assertTrue(result.contains("Vietnam"));
    }
}