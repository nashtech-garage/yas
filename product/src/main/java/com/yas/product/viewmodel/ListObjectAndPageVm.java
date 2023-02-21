package com.yas.product.viewmodel;

import com.yas.product.model.Product;
import com.yas.product.model.ProductRating;

import java.util.Date;

public record ListObjectAndPageVm<T>(T listObject, int totalPage) {
}