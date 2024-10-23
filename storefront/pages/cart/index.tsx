import Link from 'next/link';
import { useEffect, useState } from 'react';
import { Button } from 'react-bootstrap';

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
import CartItem from '@/modules/cart/components/CartItem';

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

  const [discountMoney, setDiscountMoney] = useState<number>(0);
  const [subTotalPrice, setSubTotalPrice] = useState<number>(0);

  useEffect(() => {
    loadCartItems();
  }, []);

  useEffect(() => {
    const selectedItems = getSelectedCartItems();
    // Calculate sub total price
    const newTotalPrice = selectedItems.reduce((accumulator, item) => {
      return accumulator + item.price * item.quantity;
    }, 0);

    setSubTotalPrice(newTotalPrice);
    // Calculate total discount
    const newDiscountMoney = selectedItems.reduce((total, item) => {
      const discount =
        promotionApply?.discountType === 'PERCENTAGE'
          ? item.price * (promotionApply.discountValue / 100)
          : promotionApply?.discountValue ?? 0;

      return total + discount;
    }, 0);
    setDiscountMoney(newDiscountMoney);
    console.log('discountMoney: ' + newDiscountMoney);

    // Calculate total price
    const totalPriceLast = newTotalPrice - newDiscountMoney;
    setTotalPrice(totalPriceLast);
  }, [cartItems, selectedProductIds, promotionApply]);

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

    if (!allowedKeys.includes(event.key) && !digitKeyPattern.test(event.key)) {
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
    console.log('Total Price:', totalPrice); // Log the totalPrice

    verifyPromotion({
      couponCode: couponCode,
      orderPrice: totalPrice,
      productIds: Array.from(selectedProductIds.values()),
    }).then((result) => {
      console.log('Promotion Result:', result); // Log the result
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
                      return (
                        <CartItem
                          key={item.productId}
                          item={item}
                          isLoading={loadingItems.has(item.productId)}
                          isSelected={selectedProductIds.has(item.productId)}
                          promotionApply={promotionApply}
                          handleSelectCartItemChange={handleSelectCartItemChange}
                          handleDecreaseQuantity={handleDecreaseQuantity}
                          handleIncreaseQuantity={handleIncreaseQuantity}
                          handleCartItemQuantityOnBlur={handleCartItemQuantityOnBlur}
                          handleCartItemQuantityKeyDown={handleCartItemQuantityKeyDown}
                          handleOpenDeleteConfirmationModal={handleOpenDeleteConfirmationModal}
                        />
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
              <div className="col">
                {promotionApply.couponCode ? (
                  <span className="coupon-code-apply" aria-hidden="true" onClick={removeCouponCode}>
                    <i className="bi bi-receipt"></i>
                    {promotionApply?.discountType === 'PERCENTAGE' ? (
                      <> You got a {promotionApply.discountValue}% discount on one product!</>
                    ) : (
                      <> You got a {promotionApply.discountValue}$ discount on one product!</>
                    )}
                  </span>
                ) : (
                  <span
                    className="invalid-coupon-code"
                    style={{ color: 'red', fontWeight: 'bold' }}
                    aria-hidden="true"
                    onClick={removeCouponCode}
                  >
                    <i className="bi bi-receipt"></i> Coupon code not valid!
                  </span>
                )}
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
                  Subtotal <span>{formatPrice(subTotalPrice)}</span>
                </li>
                <li>
                  Discount <span>{formatPrice(discountMoney)}</span>
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
