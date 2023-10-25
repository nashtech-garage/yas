package com.yas.product.service;

import com.yas.product.exception.BadRequestException;
import com.yas.product.exception.DuplicatedException;
import com.yas.product.exception.NotFoundException;
import com.yas.product.model.attribute.ProductAttribute;
import com.yas.product.model.attribute.ProductAttributeTemplate;
import com.yas.product.model.attribute.ProductTemplate;
import com.yas.product.repository.ProductAttributeGroupRepository;
import com.yas.product.repository.ProductAttributeRepository;
import com.yas.product.repository.ProductAttributeTemplateRepository;
import com.yas.product.repository.ProductTemplateRepository;
import com.yas.product.utils.Constants;
import com.yas.product.viewmodel.producttemplate.*;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ProductTemplateService {
    private final ProductAttributeRepository productAttributeRepository;
    private final ProductAttributeTemplateRepository productAttributeTemplateRepository;
    private final ProductAttributeGroupRepository productAttributeGroupRepository ;
    private final ProductTemplateRepository productTemplateRepository;

    public ProductTemplateService(ProductAttributeRepository productAttributeRepository, ProductAttributeTemplateRepository productAttributeTemplateRepository, ProductAttributeGroupRepository productAttributeGroupRepository, ProductTemplateRepository productTemplateRepository) {
        this.productAttributeRepository = productAttributeRepository;
        this.productAttributeTemplateRepository = productAttributeTemplateRepository;
        this.productAttributeGroupRepository = productAttributeGroupRepository;
        this.productTemplateRepository = productTemplateRepository;
    }

    public ProductTemplateListGetVm getPageableProductTemplate(int pageNo, int pageSize){
        List<ProductTemplateGetVm> productTemplateListGetVms = new ArrayList<>();
        Pageable pageable = PageRequest.of(pageNo,pageSize);
        Page<ProductTemplate> productTemplatePage = productTemplateRepository.findAll(pageable);
        List<ProductTemplate> productTemplates = productTemplatePage.getContent();
        for(ProductTemplate productTemplate: productTemplates){
            productTemplateListGetVms.add(ProductTemplateGetVm.fromModel(productTemplate));
        }
        return new ProductTemplateListGetVm(
                productTemplateListGetVms,
                productTemplatePage.getNumber(),
                productTemplatePage.getSize(),
                (int) productTemplatePage.getTotalElements(),
                productTemplatePage.getTotalPages(),
                productTemplatePage.isLast()
        );
    }
    public ProductTemplateVm getProductTemplate(Long id){
        Optional<ProductTemplate> productTemplate = productTemplateRepository.findById(id);
        if(productTemplate.isEmpty()){
            throw new NotFoundException(Constants.ERROR_CODE.PRODUCT_TEMPlATE_IS_NOT_FOUND, id);
        }

        List<ProductAttributeTemplate> productAttributeTemplates = productAttributeTemplateRepository
                .findAllByProductTemplateId(id);
        return new ProductTemplateVm(
                id,
                productTemplate.get().getName(),
                productAttributeTemplates.stream().map(ProductAttributeTemplateGetVm::fromModel).toList()
        );
    }
    public ProductTemplateVm saveProductTemplate(ProductTemplatePostVm productTemplatePostVm){
        validateExistedName(productTemplatePostVm.name(), null);
        ProductTemplate productTemplate = new ProductTemplate();
        productTemplate.setName(productTemplatePostVm.name());
        List<ProductAttributeTemplate> productAttributeTemplates = setAttributeTemplates(productTemplatePostVm.ProductAttributeTemplates(),productTemplate);
        ProductTemplate mainSavedProductTemplate = productTemplateRepository.save(productTemplate);
        productAttributeTemplateRepository.saveAllAndFlush(productAttributeTemplates);
        return getProductTemplate(mainSavedProductTemplate.getId());
    }
    public void updateProductTemplate(long productTemplateId, ProductTemplatePostVm productTemplatePostVm){
        ProductTemplate productTemplate = productTemplateRepository.findById(productTemplateId).orElseThrow(()
                -> new NotFoundException(Constants.ERROR_CODE.PRODUCT_TEMPlATE_IS_NOT_FOUND, productTemplateId));
        List<ProductAttributeTemplate> attributeTemplateList = productAttributeTemplateRepository.findAllByProductTemplateId(productTemplateId);
        productAttributeTemplateRepository.deleteAllByIdInBatch(attributeTemplateList.stream().map(ProductAttributeTemplate::getId).toList());
        List<ProductAttributeTemplate> productAttributeTemplates = setAttributeTemplates(productTemplatePostVm.ProductAttributeTemplates(),productTemplate);
        productTemplate.setName(productTemplatePostVm.name());
        productTemplateRepository.save(productTemplate);
        productAttributeTemplateRepository.saveAllAndFlush(productAttributeTemplates);
    }
    private List<ProductAttributeTemplate> setAttributeTemplates(List<ProductAttributeTemplatePostVm> ProductAttributeTemplates, ProductTemplate productTemplate){
        List<ProductAttributeTemplate> attributeTemplateList = new ArrayList<>();
        List<Long> idAttributes = ProductAttributeTemplates.stream().map(ProductAttributeTemplatePostVm::ProductAttributeId).toList();
        if(CollectionUtils.isNotEmpty(idAttributes)){
            List<Long> attributes = productTemplate
                    .getProductAttributeTemplates()
                    .stream()
                    .map(productAttributeTemplate -> productAttributeTemplate.getProductAttribute().getId()).sorted().toList();
            if(!CollectionUtils.isEqualCollection(attributes,idAttributes.stream().sorted().toList())){
                List<ProductAttribute> productAttributes = productAttributeRepository.findAllById(idAttributes);
                if(productAttributes.isEmpty()){
                    throw new BadRequestException(Constants.ERROR_CODE.PRODUCT_ATTRIBUTE_NOT_FOUND, idAttributes);
                }else if(productAttributes.size() < idAttributes.size()){
                    idAttributes.removeAll(productAttributes.stream().map(ProductAttribute::getId).toList());
                    throw new BadRequestException(Constants.ERROR_CODE.PRODUCT_ATTRIBUTE_NOT_FOUND, idAttributes);
                }
                Map<Long, ProductAttribute> productAttributeMap = productAttributes.stream()
                        .collect(Collectors.toMap(ProductAttribute::getId, Function.identity()));
                for(ProductAttributeTemplatePostVm attributeTemplatePostVm: ProductAttributeTemplates){
                    attributeTemplateList.add(ProductAttributeTemplate
                            .builder()
                                    .productAttribute(productAttributeMap.get(attributeTemplatePostVm.ProductAttributeId()))
                                    .productTemplate(productTemplate)
                                    .displayOrder(attributeTemplatePostVm.displayOrder())
                            .build()
                    );
                }
            }
        }
        return attributeTemplateList;
    }


    public void validateExistedName(String name,Long id){
        if(checkExistedName(name,id)){
            throw new DuplicatedException(Constants.ERROR_CODE.NAME_ALREADY_EXITED,name);
        }
    }
    public boolean checkExistedName(String name, Long id){
        return productTemplateRepository.findExistedName(name,id) != null;
    }

}
