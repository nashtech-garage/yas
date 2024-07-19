package com.yas.tax.viewmodel.taxclass;

import jakarta.validation.constraints.NotBlank;
import com.yas.tax.model.TaxClass;
import org.hibernate.validator.constraints.Length;

public record TaxClassPostVm(@NotBlank String id, @NotBlank @Length(max = 450) String name) {

    public TaxClass toModel(){
        TaxClass taxClass = new TaxClass();
        taxClass.setName(name);
        return taxClass;
    }

}
