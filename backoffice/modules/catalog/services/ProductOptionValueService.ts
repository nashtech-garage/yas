import { ProductOptionValuePost } from './../models/ProductOptionValuePost';

export async function createProductOptionValue(data: ProductOptionValuePost): Promise<any> {
  const res = await fetch('/api/product/backoffice/product-option-values', {
    method: 'POST',
    body: JSON.stringify(data),
    headers: { 'Content-type': 'application/json; charset=UTF-8' },
  });
  return res;
}
