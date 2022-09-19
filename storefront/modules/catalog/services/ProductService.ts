import { Product } from "../models/Product";
import { ProductThumbnail } from "../models/ProductThumbnail";

export async function getFeaturedProducts(): Promise<ProductThumbnail[]> {
  const response = await fetch(process.env.NEXT_PUBLIC_API_BASE_PATH + "/product/storefront/products/featured");
  return await response.json();
}

export async function getProduct(slug: string): Promise<Product> {
  const response = await fetch(process.env.NEXT_PUBLIC_API_BASE_PATH + "/product/storefront/products/" + slug);
  return response.json();
}
