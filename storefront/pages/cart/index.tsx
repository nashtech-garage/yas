import Link from 'next/link';
import { useEffect, useState } from 'react';
import { Button } from 'react-bootstrap';
import { toast } from 'react-toastify';

import ImageWithFallBack from '@/common/components/ImageWithFallback';
import ConfirmationDialog from '@/common/components/dialog/ConfirmationDialog';
import { useCartContext } from '@/context/CartContext';
import { Cart as CartModel } from '@/modules/cart/models/Cart';
import {
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
import { toastError } from '@/modules/catalog/services/ToastService';

const Cart = () => {
  const router = useRouter();
  type Item = {
    productId: number;
    quantity: number;
    productName: string;
    slug: string;
    thumbnailUrl: string;
    price: number;
    stock: number;
  };

  const LOW_STOCK_THRESHOLD = 5;

  const [items, setItems] = useState<Item[]>([]);

  const [selectedProductIds, setSelectedProductIds] = useState<Set<number>>(new Set());

  const [loaded, setLoaded] = useState(false);

  const [productIdRemove, setProductIdRemove] = useState<number>(0);

  const [isOpenRemoveDialog, setIsOpenRemoveDialog] = useState(false);

  const [totalPrice, setTotalPrice] = useState(0);

  const { email } = useUserInfoContext();

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

  useEffect(() => {
    const selectedItems = getSelectedItems();
    const newTotalPrice = selectedItems
      .map((item) => item.price * item.quantity)
      .reduce((accumulator, currentValue) => accumulator + currentValue, 0);
    setTotalPrice(newTotalPrice);
  }, [items, selectedProductIds]);

  const getSelectedItems = () => {
    return items.filter((item) => selectedProductIds.has(item.productId));
  };

  const calculateProductPrice = (item: Item) => {
    return formatPrice(item.price * item.quantity);
  };

  const getProductThumbnails = (productIds: number[]) => {
    return getCartProductThumbnail(productIds);
  };

  const loadCart = () => {
    getCart()
      .then((data) => {
        setCart(data);
        fetchNumberCartItems();
        const cartDetails = data.cartDetails;
        const productIds = cartDetails.map((item) => item.productId);
        getProductThumbnails(productIds)
          .then((results) => {
            const newItems: Item[] = [];
            results.forEach((result) => {
              const quantity = cartDetails.find((detail) => detail.productId === result.id)
                ?.quantity!;
              newItems.push({
                productId: result.id,
                quantity: quantity,
                productName: result.name,
                slug: result.slug,
                thumbnailUrl: result.thumbnailUrl,
                price: result.price,
                stock: result.stock,
              });
            });
            setItems(newItems);
          })
          .catch((err) => {
            console.log('Load product thumbnails fail: ' + err.message);
          });
      })
      .catch((err) => {
        console.log('Load cart failed: ' + err.message);
      });
  };

  const removeProduct = (productId: number) => {
    removeProductInCart(productId)
      .then(() => {
        loadCart();
        setSelectedProductIds((prevSelectedProductIds) => {
          const newSelectedItems = new Set(prevSelectedProductIds);
          newSelectedItems.delete(productId);
          return newSelectedItems;
        });
      })
      .catch((err) => {
        console.log('remove product in cart fail: ' + err.message);
      });
    setIsOpenRemoveDialog(false);
  };

  const handlePlus = (productId: number, productQuantity: number) => {
    updateCart({
      productId: productId,
      quantity: productQuantity + 1,
    })
      .then(() => loadCart())
      .catch((err) => {
        console.log('Plus product to cart fail: ' + err.message);
      });
  };

  const handleMinus = (productId: number, productQuantity: number) => {
    if (productQuantity === 1) {
      removeProductInCart(productId)
        .then(() => loadCart())
        .catch((err) => {
          console.log('remove product in cart fail: ' + err.message);
        });
    } else {
      updateCart({
        productId: productId,
        quantity: productQuantity - 1,
      })
        .then(() => loadCart())
        .catch((err) => {
          console.log('update product in cart fail: ' + err.message);
        });
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
    const selectedItems = getSelectedItems();
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

  const convertItemToCheckoutItem = (item: Item): CheckoutItem => {
    return {
      productId: item.productId,
      productName: item.productName,
      quantity: item.quantity,
      productPrice: item.price,
    };
  };

  const handleSelectAllChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (e.target.checked) {
      const allProductIds = items.map((item) => item.productId);
      setSelectedProductIds(new Set(allProductIds));
    } else {
      setSelectedProductIds(new Set());
    }
  };

  const handleSelectItemChange = (productId: number) => {
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
                      <th>
                        <label className="item-checkbox-label" htmlFor="select-all-checkbox">
                          {''}
                          <input
                            id="select-all-checkbox"
                            type="checkbox"
                            className="form-check-input item-checkbox"
                            onChange={handleSelectAllChange}
                            checked={selectedProductIds.size === items.length}
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
                    {items.map((item) => {
                      const isOutOfStock = item.stock === 0;
                      const isLowStock = item.stock > 0 && item.stock <= LOW_STOCK_THRESHOLD;
                      return (
                        <tr key={item.quantity.toString() + item.productId.toString()}>
                          <td>
                            <label className="item-checkbox-label" htmlFor="select-item-checkbox">
                              <input
                                className="form-check-input item-checkbox"
                                type="checkbox"
                                checked={selectedProductIds.has(item.productId)}
                                onChange={() => handleSelectItemChange(item.productId)}
                                disabled={isOutOfStock}
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
                              {isOutOfStock && (
                                <p className="text-danger">Out of stock</p>
                              )}
                              {isLowStock && (
                                <p className="text-warning">Low stock: only {item.stock} left!</p>
                              )}
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
                                  disabled={isOutOfStock}
                                />
                                <input
                                  id="quantity"
                                  type="number"
                                  step="1"
                                  min="1"
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
                                  disabled={isOutOfStock}
                                />
                                <input
                                  id="plus-button"
                                  type="button"
                                  value="+"
                                  className="plus"
                                  onClick={() => handlePlus(item.productId, item.quantity)}
                                  disabled={isOutOfStock}
                                />
                              </div>
                            </div>
                          </td>
                          <td className="cart__total">{calculateProductPrice(item)}</td>
                          <td className="cart__close">
                            <button
                              className="remove_product"
                              onClick={() => openRemoveConfirmDialog(item.productId)}
                              disabled={isOutOfStock}
                            >
                              <i className="bi bi-x-lg fs-5"></i>
                            </button>
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
