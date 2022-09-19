import { Product } from '../models/Product'

export async function getProducts(): Promise<Product[]> {
  const response = await fetch('/api/product/backoffice/products');
  return await response.json();
}

export async function getProduct(id: number): Promise<Product> {
  const response = await fetch('/api/product/backoffice/products/' + id);
  return await response.json();
}

export async function createProduct(product: Product): Promise<Product> {
  const payload = new FormData();
  const productDetails = {
    name: product.name,
    slug: product.slug,
    brandId: product.brand == 0 ? null : product.brand,
    categoryIds: product.categoriesId,
    shortDescription: product.shortDescription, 
    description: product.description,
    specification: product.specification,
    sku: product.sku,
    gtin: product.gtin,
    price: product.price,
    isAllowedToOrder: product.isAllowedToOrder,
    isPublished: product.isPublished,
    isFeature: product.isFeatured,
    metaKeyword: "",
    metaDescription: "",
  };

  payload.append(
    "productDetails",
    new Blob([JSON.stringify(productDetails)], { type: "application/json" })
  );
  
  const files = [product.thumbnail[0], ...product.productImages];
  console.log(files)
  Array.from(files).forEach((file) =>
    payload.append("files", file)
  );

  const response = await fetch("/api/product/backoffice/products", {
    method: "POST",
    body: payload,
  });
  return response.json();

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
  return res.status;
}