import { ProductThumbnail } from '../../catalog/models/ProductThumbnail';
import { AddToCartModel } from '../models/AddToCartModel';
import { Cart } from '../models/Cart';
import { UpdateCartModel } from '../models/UpdateCartModel';
import apiClientService from '@/common/services/ApiClientService';

const baseUrl = '/api/cart';

export async function addToCart(addToCart: AddToCartModel[]): Promise<Cart> {
  const response = await apiClientService.post(
    `${baseUrl}/storefront/carts`,
    JSON.stringify(addToCart)
  );
  if (response.status >= 200 && response.status < 300) return await response.json();
  throw response;
}

export async function getCart(): Promise<Cart> {
  const response = await apiClientService.get(`${baseUrl}/storefront/carts`);
  if (response.status >= 200 && response.status < 300) return await response.json();
  throw response;
}

export async function getCartProductThumbnail(ids: number[]): Promise<ProductThumbnail[]> {
  const response = await apiClientService.get(
    `/api/product/storefront/products/list-featured?productId=${ids}`
  );
  return await response.json();
}

export async function removeProductInCart(productId: number) {
  const response = await apiClientService.delete(
    `${baseUrl}/storefront/cart-item?productId=${productId}`
  );
  if (response.status === 204) return response;
  else return await response.json();
}

export async function updateCart(updateCartBody: AddToCartModel): Promise<UpdateCartModel> {
  const response = await apiClientService.put(
    `${baseUrl}/cart-item`,
    JSON.stringify(updateCartBody)
  );

  if (response.status >= 200 && response.status < 300) return await response.json();

  throw response;
}

export async function getNumberCartItems(): Promise<number> {
  const response = await apiClientService.get(`${baseUrl}/storefront/count-cart-items`);
  if (response.status >= 200 && response.status < 300) return await response.json();
  throw response;
}
