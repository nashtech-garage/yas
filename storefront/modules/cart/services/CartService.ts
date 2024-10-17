import { CartItemGetVm, CartItemGetDetailsVm } from '../models/CartItemGetVm';
import { ProductThumbnail } from '../../catalog/models/ProductThumbnail';
import apiClientService from '@/common/services/ApiClientService';
import { CartItemPostVm } from '../models/CartItemPostVm';
import { YasError } from '@/common/services/errors/YasError';
import { getProductsByIds } from '@/modules/catalog/services/ProductService';
import { CartItemPutVm } from '../models/CartItemPutVm';
import { CartItemDeleteVm } from '../models/CartItemDeleteVm';

const CART_BASE_URL = `/api/cart/storefront/cart/items`;

export async function addCartItem(payload: CartItemPostVm): Promise<CartItemGetVm> {
  const response = await apiClientService.post(CART_BASE_URL, JSON.stringify(payload));
  if (!response.ok) {
    await throwDetailedError(response);
  }
  return await response.json();
}

export async function getDetailedCartItems(): Promise<CartItemGetDetailsVm[]> {
  const cartItems = await getCartItems();
  const cartItemProductIds = cartItems.map((item) => item.productId);
  const products = await getProductsByIds(cartItemProductIds);
  return mapCartItemsToDetailedItems(cartItems, products);
}

async function getCartItems(): Promise<CartItemGetVm[]> {
  const response = await apiClientService.get(CART_BASE_URL);
  if (!response.ok) {
    await throwDetailedError(response);
  }
  return await response.json();
}

function mapCartItemsToDetailedItems(
  cartItemGetVms: CartItemGetVm[],
  products: ProductThumbnail[]
): CartItemGetDetailsVm[] {
  const detailedCartItems: CartItemGetDetailsVm[] = [];

  const productMap = new Map(products.map((product) => [product.id, product]));

  for (const cartItem of cartItemGetVms) {
    const product = productMap.get(cartItem.productId);
    if (!product) {
      continue;
    }
    detailedCartItems.push({
      ...cartItem,
      productName: product.name,
      slug: product.slug,
      thumbnailUrl: product.thumbnailUrl,
      price: product.price,
    });
  }
  return detailedCartItems;
}

export async function getNumberCartItems(): Promise<number> {
  const response = await apiClientService.get(CART_BASE_URL);
  if (!response.ok) {
    await throwDetailedError(response);
  }
  const cartItems = await response.json();
  const numberCartItems = cartItems.reduce(
    (currentTotal: number, item: CartItemGetVm) => currentTotal + item.quantity,
    0
  );
  return numberCartItems;
}

export async function deleteCartItem(productId: number): Promise<void> {
  const response = await apiClientService.delete(`${CART_BASE_URL}/${productId}`);
  if (!response.ok) {
    await throwDetailedError(response);
  }
}

export async function bulkDeleteCartItems(payload: CartItemDeleteVm[]): Promise<CartItemGetVm[]> {
  const response = await apiClientService.post(`${CART_BASE_URL}/remove`, JSON.stringify(payload));
  if (!response.ok) {
    await throwDetailedError(response);
  }
  return await response.json();
}

export async function updateCartItem(
  productId: number,
  payload: CartItemPutVm
): Promise<CartItemGetVm> {
  const response = await apiClientService.put(
    `${CART_BASE_URL}/${productId}`,
    JSON.stringify(payload)
  );
  if (!response.ok) {
    await throwDetailedError(response);
  }
  return await response.json();
}

async function throwDetailedError(response: Response) {
  const errorResponse = await response.json();
  throw new YasError(errorResponse);
}
