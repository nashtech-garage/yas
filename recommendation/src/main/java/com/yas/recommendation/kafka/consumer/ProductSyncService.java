package com.yas.recommendation.kafka.consumer;

import com.yas.recommendation.constant.Action;
import com.yas.recommendation.vector.product.service.ProductVectorSyncService;
import org.springframework.stereotype.Service;

@Service
public class ProductSyncService {

    private final ProductVectorSyncService productVectorSyncService;

    public ProductSyncService(ProductVectorSyncService productVectorSyncService) {
        this.productVectorSyncService = productVectorSyncService;
    }

//    public void sync() {
//        boolean isPublished = productObject != null && productObject.get("is_published").getAsBoolean();
//
//        switch (action) {
//            case Action.CREATE, Action.READ:
//                productVectorSyncService.createProductVector(id, isPublished);
//                break;
//            case Action.UPDATE:
//                productVectorSyncService.updateProductVector(id, isPublished);
//                break;
//            case Action.DELETE:
//                productVectorSyncService.deleteProductVector(id);
//                break;
//            default:
//                break;
//        }
//    }

}
