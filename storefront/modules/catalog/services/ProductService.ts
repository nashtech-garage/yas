import { Product } from "../models/Product";
import { ProductThumbnail } from "../models/ProductThumbnail";

export async function getFeaturedProducts(): Promise<ProductThumbnail[]> {
  const response = await fetch(
    "http://storefront/api/product/storefront/products/featured"
  );
  return await response.json();
}

export async function getProduct(id: number): Promise<Product> {
  const response = await fetch("/api/product/storefront/products/" + id);
  return await response.json();
}
