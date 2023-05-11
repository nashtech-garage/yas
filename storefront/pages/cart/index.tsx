import Link from 'next/link';
import { useEffect, useState } from 'react';
import { Button } from 'react-bootstrap';
import { toast } from 'react-toastify';

import ImageWithFallBack from '@/common/components/ImageWithFallback';
import ConfirmationDialog from '@/common/components/dialog/ConfirmationDialog';
import { useCartContext } from '@/context/CartContext';
import { Cart as CartModel } from '@/modules/cart/models/Cart';
import {
  addToCart,
  getCart,
  getCartProductThumbnail,
  removeProductInCart,
  updateCart,
} from '@/modules/cart/services/CartService';
import { formatPrice } from 'utils/formatPrice';
import { CheckoutItem } from '@/modules/order/models/CheckoutItem';
import { createCheckout } from '@/modules/order/services/OrderService';
import { Checkout } from '@/modules/order/models/Checkout';
import { useUserInfoContext } from '@/context/UserInfoContext';
import { useRouter } from 'next/router';

const Cart = () => {
  const router = useRouter();
  type Item = {
    productId: number;
    quantity: number;
    productName: string;
    slug: string;
    thumbnailUrl: string;
    price: number;
  };

  const [items, setItems] = useState<Item[]>([]);

  const [loaded, setLoaded] = useState(false);

  const [productIdRemove, setProductIdRemove] = useState<number>(0);

  const [isOpenRemoveDialog, setIsOpenRemoveDialog] = useState(false);

  const [totalPrice, setTotalPrice] = useState(0);

  const [checkoutItems, setCheckoutItems] = useState<CheckoutItem[]>([]);
  // const { email } = useUserInfoContext();

  const [cart, setCart] = useState<CartModel>({
    id: 0,
    customerId: '',
    cartDetails: [
      {
        id: 0,
        productId: 0,
        quantity: 0,
      },
    ],
  });
  const { fetchNumberCartItems } = useCartContext();

  const calculateProductPrice = (item: Item) => {
    return formatPrice(item.price * item.quantity);
  };

  const getProductThumbnails = (productIds: number[]) => {
    return getCartProductThumbnail(productIds);
  };

  const loadCart = () => {
    getCart().then((data) => {
      setCart(data);
      fetchNumberCartItems();
      const cartDetails = data.cartDetails;
      const productIds = cartDetails.map((item) => item.productId);
      getProductThumbnails(productIds).then((results) => {
        const newItems: Item[] = [];
        results.forEach((result) => {
          newItems.push({
            productId: result.id,
            quantity: cartDetails.find((detail) => detail.productId === result.id)?.quantity!,
            productName: result.name,
            slug: result.slug,
            thumbnailUrl: result.thumbnailUrl,
            price: result.price,
          });
        });
        setItems(newItems);
      });
    });
  };

  useEffect(() => {
    const totalPrice = items
      .map((item) => item.price * item.quantity)
      .reduce((accumulator, currentValue) => accumulator + currentValue, 0);
    setTotalPrice(totalPrice);
  }, [items]);

  const removeProduct = (productId: number) => {
    removeProductInCart(productId).then(() => loadCart());
    setIsOpenRemoveDialog(false);
  };

  const handlePlus = (productId: number) => {
    addToCart([
      {
        productId: productId,
        quantity: 1,
      },
    ]).then(() => loadCart());
  };

  const handleMinus = (productId: number, productQuantity: number) => {
    if (productQuantity === 1) {
      removeProductInCart(productId).then(() => loadCart());
    } else {
      updateCart({
        productId: productId,
        quantity: productQuantity - 1,
      }).then(() => loadCart());
    }
  };

  const handleQuantityOnChange = (productId: number, quanity: number) => {
    items.find((item) => item.productId === productId)!.quantity = quanity;
  };

  const handleQuantityKeyDown = (productId: number, key: string) => {
    if (key === 'Enter') {
      updateCart({
        productId: productId,
        quantity: items.find((item) => item.productId === productId)!.quantity,
      })
        .then(() => loadCart())
        .catch((err) =>
          toast.error(err, {
            position: 'top-right',
            autoClose: 2000,
            closeOnClick: true,
            pauseOnHover: false,
            theme: 'colored',
          })
        );
    }
  };

  const handleCartQuantityInputOnBlur = (productId: number, newQuantity: number) => {
    updateCart({
      productId: productId,
      quantity: newQuantity,
    })
      .then(() => loadCart())
      .catch(() =>
        toast.error("Couldn't change product quantity in cart!", {
          position: 'top-right',
          autoClose: 2000,
          closeOnClick: true,
          pauseOnHover: false,
          theme: 'colored',
        })
      );
  };

  const openRemoveConfirmDialog = (productId: number) => {
    setProductIdRemove(productId);
    setIsOpenRemoveDialog(true);
  };

  useEffect(() => {
    if (!loaded) {
      loadCart();
      setLoaded(true);
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [loaded]);

  const handleCheckout = () => {
    const checkoutItems = convertItemsToCheckoutItems(items);
    setCheckoutItems(checkoutItems);

    let checkout: Checkout = {
      email: '',
      note: '',
      couponCode: '',
      checkoutItemPostVms: checkoutItems,
    };
    // console.log(checkout);

    createCheckout(checkout).then((res) => {
      router.push(`/checkout/${res?.id}`);
    });
  };

  const convertItemToCheckoutItem = (item: Item): CheckoutItem => {
    return {
      productId: item.productId,
      productName: item.productName,
      quantity: item.quantity,
      productPrice: item.price,
    };
  };

  const convertItemsToCheckoutItems = (items: Item[]): CheckoutItem[] => {
    return items.map(convertItemToCheckoutItem);
  };

  return (
    <section className="shop-cart spad">
      <div className="container">
        <div className="row">
          <div className="col-lg-12">
            <div className="shop__cart__table">
              {!cart.id ? (
                <h4>There are no items in this cart.</h4>
              ) : (
                <table>
                  <thead>
                    <tr>
                      <th>Product</th>
                      <th>Price</th>
                      <th>Quantity</th>
                      <th>Total</th>
                      <th></th>
                    </tr>
                  </thead>
                  <tbody>
                    {items.map((item) => {
                      return (
                        <tr key={item.quantity.toString() + item.productId.toString()}>
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
                          <td className="cart__price">{formatPrice(item.price)}</td>
                          <td className="cart__quantity">
                            <div className="pro-qty">
                              <div className="quantity buttons_added">
                                <input
                                  id="minus-button"
                                  type="button"
                                  value="-"
                                  className="minus"
                                  onClick={() => handleMinus(item.productId, item.quantity)}
                                />
                                <input
                                  id="quanity"
                                  type="number"
                                  step="1"
                                  min="1"
                                  max=""
                                  name="quantity"
                                  defaultValue={item.quantity}
                                  onBlur={(e) =>
                                    handleCartQuantityInputOnBlur(
                                      item.productId,
                                      parseInt(e.target.value)
                                    )
                                  }
                                  onChange={(e) =>
                                    handleQuantityOnChange(item.productId, parseInt(e.target.value))
                                  }
                                  onKeyDown={(e) => handleQuantityKeyDown(item.productId, e.key)}
                                  title="Qty"
                                  className="input-text qty text"
                                />
                                <input
                                  id="plus-button"
                                  type="button"
                                  value="+"
                                  className="plus"
                                  onClick={() => handlePlus(item.productId)}
                                />
                              </div>
                            </div>
                          </td>
                          <td className="cart__total">{calculateProductPrice(item)}</td>
                          <td className="cart__close">
                            {' '}
                            <button
                              className="remove_product"
                              onClick={() => {
                                openRemoveConfirmDialog(item.productId);
                              }}
                            >
                              <i className="bi bi-x-lg"></i>
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
                <input type="text" placeholder="Enter your coupon code" />
                <button className="site-btn">Apply</button>
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

              <a className="primary-btn" onClick={handleCheckout} style={{ cursor: 'pointer' }}>
                Proceed to checkout
              </a>
            </div>
          </div>
        </div>
        <ConfirmationDialog
          isOpen={isOpenRemoveDialog}
          okText="Remove"
          cancelText="Cancel"
          cancel={() => setIsOpenRemoveDialog(false)}
          ok={() => removeProduct(productIdRemove)}
        >
          <p>Do you want to remove this Product from the cart ?</p>
        </ConfirmationDialog>
      </div>
    </section>
  );
};

export default Cart;
