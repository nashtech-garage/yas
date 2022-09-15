import { Product } from '../models/Product'

export async function getProducts(): Promise<Product[]> {
  const response = await fetch('/api/product/backoffice/products');
  return await response.json();
}

export async function getProduct(id: number): Promise<Product> {
  const response = await fetch('/api/product/backoffice/products/' + id);
  return await response.json();
}

export async function createProduct(product:any): Promise<Product> {
  const response = await fetch('/api/product/backoffice/products', {
    method: 'POST',
    body: product
  })
  return await response.json();
}