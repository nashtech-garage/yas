import Link from 'next/link';
import { useEffect, useState } from 'react';
import { Button } from 'react-bootstrap';

import ImageWithFallBack from '@/common/components/ImageWithFallback';
import ConfirmationDialog from '@/common/components/dialog/ConfirmationDialog';
import * as CartService from '@/modules/cart/services/CartService';
import { formatPrice } from 'utils/formatPrice';
import { toastError } from '@/modules/catalog/services/ToastService';
import { CartItemGetDetailsVm } from '@/modules/cart/models/CartItemGetVm';
import { Checkout } from '@/modules/order/models/Checkout';
import { useUserInfoContext } from '@/context/UserInfoContext';
import { createCheckout } from '@/modules/order/services/OrderService';
import { useRouter } from 'next/router';
import { CheckoutItem } from '@/modules/order/models/CheckoutItem';
import { useCartContext } from '@/context/CartContext';
import { PromotionVerifyResult } from '@/modules/promotion/model/Promotion';
import { verifyPromotion } from '@/modules/promotion/service/PromotionService';
import { CartItemPutVm } from '@/modules/cart/models/CartItemPutVm';

const Cart = () => {
  const router = useRouter();

  const [cartItems, setCartItems] = useState<CartItemGetDetailsVm[]>([]);

  const [selectedProductIds, setSelectedProductIds] = useState<Set<number>>(new Set());

  const [loadingItems, setLoadingItems] = useState<Set<number>>(new Set());

  const [totalPrice, setTotalPrice] = useState(0);

  const [isDeleteConfirmationModalOpened, setIsDeleteConfirmationModalOpened] = useState(false);

  const [productIdToRemove, setProductIdToRemove] = useState<number>(0);

  const { email } = useUserInfoContext();

  const { fetchNumberCartItems } = useCartContext();

  const [couponCode, setCouponCode] = useState<string>('');

  const [promotionApply, setPromotionApply] = useState<PromotionVerifyResult>();

  useEffect(() => {
    loadCartItems();
  }, []);

  useEffect(() => {
    const selectedItems = getSelectedCartItems();
    const newTotalPrice = selectedItems
      .map((item) => item.price * item.quantity)
      .reduce((accumulator, currentValue) => accumulator + currentValue, 0);
    setTotalPrice(newTotalPrice);
  }, [cartItems, selectedProductIds]);

  const loadCartItems = async () => {
    try {
      const newCartItems = await CartService.getDetailedCartItems();
      setCartItems(newCartItems);
      fetchNumberCartItems();
    } catch (error) {
      return [];
    }
  };

  const getSelectedCartItems = () => {
    return cartItems.filter((cartItem) => selectedProductIds.has(cartItem.productId));
  };

  const handleSelectAllCartItemsChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (e.target.checked) {
      const allProductIds = cartItems.map((cartItem) => cartItem.productId);
      setSelectedProductIds(new Set(allProductIds));
    } else {
      setSelectedProductIds(new Set());
    }
  };

  const handleSelectCartItemChange = (productId: number) => {
    setSelectedProductIds((prevSelectedProductIds) => {
      const newSelectedProductIds = new Set(prevSelectedProductIds);
      if (newSelectedProductIds.has(productId)) {
        newSelectedProductIds.delete(productId);
      } else {
        newSelectedProductIds.add(productId);
      }
      return newSelectedProductIds;
    });
  };

  const calculateProductPrice = (item: CartItemGetDetailsVm) => {
    return formatPrice(item.price * item.quantity - (promotionApply?.discountValue ?? 0));
  };

  const handleCartItemQuantityOnBlur = async (
    productId: number,
    event: React.FocusEvent<HTMLInputElement>
  ) => {
    const newQuantity = parseInt(event.target.value);
    const product = cartItems.find((item) => item.productId === productId);
    if (!product || newQuantity === product.quantity) {
      return;
    }
    await handleUpdateCartItemQuantity(productId, newQuantity);
  };

  const handleCartItemQuantityKeyDown = async (
    productId: number,
    event: React.KeyboardEvent<HTMLInputElement>
  ) => {
    const allowedKeys = ['Backspace', 'ArrowLeft', 'ArrowRight', 'Delete', 'Tab', 'Enter'];
    const digitKeyPattern = /^\d$/;

    if (!allowedKeys.includes(event.key) && digitKeyPattern.test(event.key)) {
      event.preventDefault();
      return;
    }

    if (event.key === 'Enter') {
      const newQuantity = parseInt(event.currentTarget.value);
      await handleUpdateCartItemQuantity(productId, newQuantity);
    }
  };

  const handleIncreaseQuantity = async (productId: number) => {
    const cartItem = cartItems.find((item) => item.productId === productId);
    if (!cartItem) {
      return;
    }
    const newQuantity = cartItem.quantity + 1;
    await handleUpdateCartItemQuantity(productId, newQuantity);
  };

  const handleDecreaseQuantity = async (productId: number) => {
    const cartItem = cartItems.find((item) => item.productId === productId);
    if (!cartItem) {
      return;
    }
    const newQuantity = cartItem.quantity - 1;
    if (newQuantity < 1) {
      handleOpenDeleteConfirmationModal(productId);
    } else {
      await handleUpdateCartItemQuantity(productId, newQuantity);
    }
  };

  const handleUpdateCartItemQuantity = async (productId: number, quantity: number) => {
    setLoadingItems((prevLoadingItems) => new Set(prevLoadingItems).add(productId));
    try {
      const payload: CartItemPutVm = {
        quantity: quantity,
      };
      await CartService.updateCartItem(productId, payload);
      loadCartItems();
    } catch (error) {
      toastErrorWithDetails('Failed to update cart item quantity', error);
    } finally {
      setLoadingItems((prevLoadingItems) => {
        const newLoadingItems = new Set(prevLoadingItems);
        newLoadingItems.delete(productId);
        return newLoadingItems;
      });
    }
  };

  const handleOpenDeleteConfirmationModal = (productId: number) => {
    setProductIdToRemove(productId);
    setIsDeleteConfirmationModalOpened(true);
  };

  const handleDeleteCartItem = async (productId: number) => {
    try {
      await CartService.deleteCartItem(productId);
    } catch (error) {
      toastError('Failed to delete cart item');
    }
    loadCartItems();
    setIsDeleteConfirmationModalOpened(false);
    setProductIdToRemove(0);
  };

  const handleCheckout = () => {
    const selectedItems = getSelectedCartItems();
    const checkoutItems = selectedItems.map((item) => convertItemToCheckoutItem(item));

    let checkout: Checkout = {
      email: email,
      note: '',
      couponCode: '',
      checkoutItemPostVms: checkoutItems,
    };

    createCheckout(checkout)
      .then((res) => {
        router.push(`/checkout/${res?.id}`); //NOSONAR
      })
      .catch((err) => {
        if (err == 403) toastError('Please login to checkout!');
      });
  };

  const convertItemToCheckoutItem = (item: CartItemGetDetailsVm): CheckoutItem => {
    return {
      productId: item.productId,
      productName: item.productName,
      quantity: item.quantity,
      productPrice: item.price,
    };
  };

  const applyCopounCode = () => {
    verifyPromotion({
      couponCode: couponCode,
      orderPrice: totalPrice,
      productIds: Array.from(selectedProductIds.values()),
    }).then((result) => {
      setPromotionApply(result);
    });
  };

  const removeCouponCode = () => {
    setPromotionApply(undefined);
  };

  const toastErrorWithDetails = (message: string, error: any) => {
    if (error instanceof Error) {
      const reason = error.message;
      toastError(`${message}: ${reason}`);
    } else {
      toastError(message);
    }
  };

  return (
    <section className="shop-cart spad">
      <div className="container">
        <div className="row">
          <div className="col-lg-12">
            <div className="shop__cart__table">
              {cartItems.length === 0 ? (
                <h4>There are no items in this cart.</h4>
              ) : (
                <table>
                  <thead>
                    <tr>
                      <th>
                        <label className="item-checkbox-label" htmlFor="select-all-checkbox">
                          {''}
                          <input
                            id="select-all-checkbox"
                            type="checkbox"
                            className="form-check-input item-checkbox"
                            onChange={handleSelectAllCartItemsChange}
                            checked={selectedProductIds.size === cartItems.length}
                          />
                        </label>
                      </th>
                      <th>Product</th>
                      <th>Price</th>
                      <th>Quantity</th>
                      <th>Total</th>
                      <th></th>
                    </tr>
                  </thead>
                  <tbody>
                    {cartItems.map((item) => {
                      const isLoading = loadingItems.has(item.productId);
                      return (
                        <tr key={item.quantity.toString() + item.productId.toString()}>
                          <td>
                            <label className="item-checkbox-label" htmlFor="select-item-checkbox">
                              {''}
                              <input
                                className="form-check-input item-checkbox"
                                type="checkbox"
                                checked={selectedProductIds.has(item.productId)}
                                onChange={() => handleSelectCartItemChange(item.productId)}
                              />
                            </label>
                          </td>
                          <td className="cart__product__item d-flex align-items-center">
                            <div className="h-100">
                              <Link
                                href={{
                                  pathname: '/redirect',
                                  query: { productId: item.productId },
                                }}
                              >
                                <ImageWithFallBack
                                  src={item.thumbnailUrl}
                                  alt={item.productName}
                                  style={{ width: '120px', height: '120px', cursor: 'pointer' }}
                                />
                              </Link>
                            </div>
                            <div className="cart__product__item__title pt-0">
                              <Link
                                href={{
                                  pathname: '/redirect',
                                  query: { productId: item.productId },
                                }}
                              >
                                <h6 className="product-link">{item.productName}</h6>
                              </Link>
                            </div>
                          </td>
                          <td className="cart__price">
                            {promotionApply?.productId === item.productId && (
                              <div style={{ textDecorationLine: 'line-through' }}>
                                {formatPrice(item.price)}
                              </div>
                            )}
                            <div>
                              {formatPrice(item.price - (promotionApply?.discountValue ?? 0))}
                            </div>
                          </td>
                          <td className="cart__quantity">
                            <div className="pro-qty">
                              <div
                                className={`quantity buttons_added ${isLoading ? 'disabled' : ''}`}
                              >
                                <button
                                  id="minus-button"
                                  type="button"
                                  className="minus"
                                  onClick={() => handleDecreaseQuantity(item.productId)}
                                  disabled={isLoading}
                                >
                                  -
                                </button>

                                <input
                                  id="quanity"
                                  type="number"
                                  step="1"
                                  min="1"
                                  max=""
                                  name="quantity"
                                  defaultValue={item.quantity}
                                  onBlur={(e) => handleCartItemQuantityOnBlur(item.productId, e)}
                                  onKeyDown={(e) =>
                                    handleCartItemQuantityKeyDown(item.productId, e)
                                  }
                                  title="Qty"
                                  className="input-text qty text"
                                  disabled={isLoading}
                                />
                                <button
                                  id="plus-button"
                                  type="button"
                                  className="plus"
                                  onClick={() => {
                                    handleIncreaseQuantity(item.productId);
                                  }}
                                  disabled={isLoading}
                                >
                                  +
                                </button>
                              </div>
                            </div>
                          </td>
                          <td className="cart__total">{calculateProductPrice(item)}</td>
                          <td className="cart__close">
                            {' '}
                            <button
                              className="remove_product"
                              onClick={() => {
                                handleOpenDeleteConfirmationModal(item.productId);
                              }}
                            >
                              <i className="bi bi-x-lg fs-5"></i>
                            </button>{' '}
                          </td>
                        </tr>
                      );
                    })}
                  </tbody>
                </table>
              )}
            </div>
          </div>
        </div>
        {promotionApply && (
          <div className="mt-5 mb-5">
            <div className="row">
              <div style={{ width: '188px' }}>
                <i className="bi bi-receipt"></i> Coupon code applied:
              </div>
              <div className="col">
                <span className="coupon-code-apply" aria-hidden="true" onClick={removeCouponCode}>
                  {promotionApply.couponCode}
                </span>
              </div>
            </div>
          </div>
        )}
        <div className="row">
          <div className="col-lg-6 col-md-6 col-sm-6">
            <div>
              <Link href={'/'}>
                <Button className="cart__btn2">
                  <i className="bi bi-house-fill"></i> CONTINUE SHOPPING
                </Button>
              </Link>
            </div>
          </div>
        </div>
        <div className="row">
          <div className="col-lg-6">
            <div className="discount__content">
              <h6>Discount codes</h6>
              <form action="#">
                <input
                  type="text"
                  placeholder="Enter your coupon code"
                  onChange={(e) => setCouponCode(e.target.value)}
                />
                <button
                  className="site-btn primary-btn btn btn-primary"
                  disabled={selectedProductIds.size === 0}
                  onClick={applyCopounCode}
                >
                  Apply
                </button>
              </form>
            </div>
          </div>
          <div className="col-lg-4 offset-lg-2">
            <div className="cart__total__procced">
              <h6>Cart total</h6>
              <ul>
                <li>
                  Subtotal <span>{formatPrice(totalPrice)}</span>
                </li>
                <li>
                  Total <span>{formatPrice(totalPrice)}</span>
                </li>
              </ul>

              <Button
                className="primary-btn"
                onClick={handleCheckout}
                disabled={selectedProductIds?.size === 0}
              >
                Proceed to checkout
              </Button>
            </div>
          </div>
        </div>
        <ConfirmationDialog
          isOpen={isDeleteConfirmationModalOpened}
          okText="Remove"
          cancelText="Cancel"
          cancel={() => setIsDeleteConfirmationModalOpened(false)}
          ok={() => handleDeleteCartItem(productIdToRemove)}
        >
          <p>Do you want to remove this Product from the cart ?</p>
        </ConfirmationDialog>
      </div>
    </section>
  );
};

export default Cart;
