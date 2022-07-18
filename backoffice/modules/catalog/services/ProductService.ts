import { Product } from '../models/Product'

export async function getProducts(): Promise<Product[]> {
  const response = await fetch('/api/product/backoffice/products');
  return await response.json();
}

export async function getProduct(id: number): Promise<Product> {
  const response = await fetch('/api/product/backoffice/products/' + id);
  return await response.json();
}

export async function createProduct(category: Product): Promise<Product> {
  const response = await fetch('/api/product/backoffice/products', {
    method: 'POST',
    body: JSON.stringify(category),
    headers: {"Content-type": "application/json; charset=UTF-8"}
  })
  return await response.json();
}