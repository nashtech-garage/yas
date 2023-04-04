package com.yas.tax.viewmodel.taxclass;

import jakarta.validation.constraints.NotBlank;
import com.yas.tax.model.TaxClass;

public record TaxClassPostVm(@NotBlank String id, @NotBlank String name) {

    public TaxClass toModel(){
        TaxClass taxClass = new TaxClass();
        taxClass.setName(name);
        return taxClass;
    }

}
