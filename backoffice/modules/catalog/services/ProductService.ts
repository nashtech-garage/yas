import { Product } from '../models/Product'
import { ProductThumbnail } from '../models/ProductThumbnail';

export async function getProducts(): Promise<Product[]> {
  const response = await fetch('/api/product/backoffice/products');
  return await response.json();
}

export async function getProduct(id: number): Promise<ProductThumbnail> {
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

export async function updateProduct(id: number, product: Product, thumbnail?: File): Promise<Number> {
  const body = new FormData()
  body.append('name', product.name);
  body.append('slug', product.slug);
  body.append('shortDescription', product.shortDescription);
  product.description && body.append('description', product.description);
  body.append('specification', product.specification);
  body.append('sku', product.sku);
  body.append('gtin', product.gtin);
  body.append('metaKeyword', product.metaKeyword);
  product.metaDescription && body.append('metaDescription', product.metaDescription);
  thumbnail && body.append('thumbnail', thumbnail);
  const res = await fetch('/api/product/backoffice/products/'+id, {
    method: 'PUT',
    body: body
  })
  console.log(res);
  return res.status;
}