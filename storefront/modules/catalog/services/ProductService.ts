import { Product } from "../models/Product";
import { ProductThumbnail } from "../models/ProductThumbnail";

export async function getFeaturedProducts(): Promise<ProductThumbnail[]> {
  const response = await fetch(
    "http://storefront/api/product/storefront/products/featured"
  );
  return await response.json();
}

export async function getProduct(slug: string): Promise<Product> {
  const response = await fetch("http://storefront/api/product/storefront/products/" + slug);
  return response.json();
}
