import { AddToCartModel } from '../models/AddToCartModel';
import { Cart } from '../models/Cart';

export async function addToCart(addToCart: AddToCartModel[]): Promise<Cart> {
  const response = await fetch('/api/cart/storefront/carts', {
    method: 'POST',
    body: JSON.stringify(addToCart),
    headers: { 'Content-type': 'application/json; charset=UTF-8' },
  });
  return await response.json();
}
