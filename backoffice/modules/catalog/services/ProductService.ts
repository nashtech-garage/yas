import { Product } from '../models/Product'

export async function getProducts(): Promise<Product[]> {
  const response = await fetch('/api/product/backoffice/products');
  return await response.json();
}

export async function getProduct(id: number): Promise<Product> {
  const response = await fetch('/api/product/backoffice/products/' + id);
  return await response.json();
}

export async function createProduct(product: Product, thumbnail: File): Promise<Product> {
  const body = new FormData()
  body.append('name', product.name);
  body.append('slug', product.slug);
  body.append('description', product.description);
  body.append('thumbnail', thumbnail);

  const response = await fetch('/api/product/backoffice/products', {
    method: 'POST',
    body: body
  })
  return await response.json();
}