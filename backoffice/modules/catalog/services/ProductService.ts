import { Product } from '../models/Product';
import { ProductPut } from '../models/ProductPut';
import { Products } from '../models/Products';

export async function getProducts(
  pageNo: number,
  productName: string,
  brandName: string
): Promise<Products> {
  const url = `/api/product/backoffice/products?pageNo=${pageNo}&product-name=${productName}&brand-name=${brandName}`;
  const response = await fetch(url);
  return await response.json();
}

export async function getProduct(id: number) {
  const response = await fetch('/api/product/backoffice/products/' + id);
  return await response.json();
}

export async function createProduct(
  product: any,
  thumbnail?: File,
  productImage?: FileList
): Promise<Product> {
  let body = new FormData();

  body.append('productDetails', new Blob([JSON.stringify(product)], { type: 'application/json' }));
  thumbnail && body.append('files', thumbnail);
  productImage && Array.from(productImage).forEach((file) => body.append('files', file));

  const response = await fetch('/api/product/backoffice/products', {
    method: 'POST',
    body: body,
  });
  return response.json();
}

export async function updateProduct(id: number, product: ProductPut) {
  const response = await fetch('/api/product/backoffice/products/' + id, {
    method: 'PUT',
    body: JSON.stringify(product),
    headers: { 'Content-Type': 'application/json' },
  });
  return response;
}
