import { ProductThumbnail } from '../../catalog/models/ProductThumbnail';
import { AddToCartModel } from '../models/AddToCartModel';
import { Cart } from '../models/Cart';
import { UpdateCartModel } from '../models/UpdateCartModel';

export async function addToCart(addToCart: AddToCartModel[]): Promise<Cart> {
  const response = await fetch(`/api/cart/storefront/carts`, {
    method: 'POST',
    body: JSON.stringify(addToCart),
    headers: { 'Content-type': 'application/json; charset=UTF-8' },
  });
  if (response.status >= 200 && response.status < 300) return await response.json();
  return Promise.reject(response);
}

export async function getCart(): Promise<Cart> {
  const response = await fetch('/api/cart/storefront/carts', {
    headers: { 'Content-type': 'application/json; charset=UTF-8' },
  });
  return await response.json();
}

export async function getCartProductThumbnail(ids: number[]): Promise<ProductThumbnail[]> {
  const response = await fetch(`api/product/storefront/products/list-featured?productId=` + ids, {
    headers: { 'Content-type': 'application/json; charset=UTF-8' },
  });
  return await response.json();
}

export async function removeProductInCart(productId: number) {
  const response = await fetch('/api/cart/storefront/cart-item?productId=' + productId, {
    method: 'DELETE',
    headers: { 'Content-type': 'application/json; charset=UTF-8' },
  });
  if (response.status === 204) return await response;
  else return await response.json();
}

export async function updateCart(updateCartBody: AddToCartModel): Promise<UpdateCartModel> {
  const response = await fetch('/api/cart/cart-item', {
    method: 'PUT',
    body: JSON.stringify(updateCartBody),
    headers: { 'Content-type': 'application/json; charset=UTF-8' },
  });

  if (response.status >= 200 && response.status < 300) return await response.json();

  return Promise.reject(response);
}

export function cartEventSource(
  onRecievedData: (numberItem: number) => void,
  connect?: () => void,
  handleError?: () => void
): void {
  const eventSource = new EventSource(
    `${process.env.NEXT_PUBLIC_API_BASE_PATH}/cart/storefront/count-cart-items`
  );

  eventSource.onopen = (event) => {
    if (connect) {
      connect();
    }
  };

  eventSource.onmessage = (event) => {
    onRecievedData(event.data);
  };

  eventSource.onerror = (event) => {
    if (handleError) {
      handleError();
    }
    eventSource.close();
  };
}
