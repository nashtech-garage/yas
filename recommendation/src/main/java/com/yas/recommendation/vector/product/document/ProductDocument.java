package com.yas.recommendation.vector.product.document;

import static com.yas.recommendation.vector.product.document.ProductDocument.CONTENT_FORMAT;

import com.yas.recommendation.vector.common.document.BaseDocument;
import com.yas.recommendation.vector.common.document.DocumentMetadata;
import com.yas.recommendation.vector.common.formatter.DefaultDocumentFormatter;

/**
 * Represents a document that contains product-related information.
 * The content of this document is formatted using a custom content formatter.
 */
@DocumentMetadata(
    docIdPrefix = ProductDocument.PREFIX_PRODUCT,
    contentFormat = CONTENT_FORMAT,
    documentFormatter = DefaultDocumentFormatter.class
)
public class ProductDocument extends BaseDocument {

    public static final String PREFIX_PRODUCT = "PRODUCT";
    public static final String CONTENT_FORMAT =
        "{name}| {shortDescription}| {specification}| {price}| {brand}| {categories}| {metaTitle}| {metaKeyword}| {metaDescription}";
}