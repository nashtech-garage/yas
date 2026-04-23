package com.yas.recommendation.vector.product.document;

import static com.yas.recommendation.vector.product.document.ProductDocument.CONTENT_FORMAT;
import static com.yas.recommendation.vector.product.document.ProductDocument.PREFIX_PRODUCT;

import com.yas.recommendation.vector.common.document.BaseDocument;
import com.yas.recommendation.vector.common.document.DocumentMetadata;
import com.yas.recommendation.vector.product.formatter.ProductDocumentFormatter;

/**
 * Represents a document that contains product-related information.
 * The content of this document is formatted using a custom content formatter.
 */
@DocumentMetadata(
        docIdPrefix = PREFIX_PRODUCT,
        contentFormat = CONTENT_FORMAT,
        documentFormatter = ProductDocumentFormatter.class
)
public class ProductDocument extends BaseDocument {

    public static final String PREFIX_PRODUCT = "PRODUCT";
    public static final String CONTENT_FORMAT =
            "{name}| {shortDescription}| {specification}| Price: {price}| {brandName}| {categories}| {metaTitle}"
                    + "| {metaKeyword}| {metaDescription}| {attributeValues}";
}