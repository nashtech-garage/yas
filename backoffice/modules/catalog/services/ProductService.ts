import { ProductPost } from "./../models/ProductPost";
import { Product } from "../models/Product";
import { ProductPut } from "../models/ProductPut";
import { Products } from '../models/Products'


export async function getProducts(pageNo:number): Promise<Products> {
  const url = `/api/product/backoffice/products?pageNo=${pageNo}`
  const response = await fetch(url);
  return await response.json();
}

export async function getProduct(id: number): Promise<Product> {
  const response = await fetch("/api/product/backoffice/products/" + id);
  return await response.json();
}

export async function createProduct(product: ProductPost, thumbnail?: File, productImage?: FileList): Promise<Product> {
  let body = new FormData();

  body.append("productDetails", new Blob([JSON.stringify(product)], { type: "application/json" }));
  thumbnail && body.append("files", thumbnail);
  productImage && Array.from(productImage).forEach((file) => body.append("files", file))

  const response = await fetch("/api/product/backoffice/products", {
    method: "POST",
    body: body,
  });
  return await response.json();
}

export async function updateProduct(
  id: number,
  product: ProductPut,
  thumbnail: File,
  productImages: FileList
): Promise<Number> {
  const body = new FormData();
  body.append("name", product.name);
  body.append("slug", product.slug);
  body.append("price", product.price+"");
  body.append("shortDescription", product.shortDescription);
  product.description && body.append("description", product.description);
  body.append("specification", product.specification);
  body.append("sku", product.sku);
  body.append("gtin", product.gtin);
  body.append("metaKeyword", product.metaKeyword);
  product.metaDescription && body.append("metaDescription", product.metaDescription);
  product.isAllowedToOrder && body.append("isAllowedToOrder", product.isAllowedToOrder+"");
  product.isFeatured && body.append("isFeatured", product.isFeatured+"");
  product.isPublished && body.append("isPublished", product.isPublished+"");
  body.append("thumbnail", thumbnail);
  Array.from(productImages).forEach((image) => body.append("productImages", image))
  body.append("brandId", product.brandId+"");
  Array.from(product.categoryIds).forEach((category) => body.append("categoryIds", category+""))
  const res = await fetch("/api/product/backoffice/products/" + id, {
    method: "PUT",
    body: body
  });
  return res.status;
}
