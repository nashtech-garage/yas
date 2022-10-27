import Link from 'next/link';
import { useEffect, useState } from 'react';
import { Button } from 'react-bootstrap';
import { Cart } from '../../modules/cart/models/Cart';
import { getCart, getCartProductThumbnail } from '../../modules/cart/services/CartService';

let loaded = false;

const Cart = () => {
  type Item = {
    productId: number;
    quantity: number;
    productName: string;
    slug: string;
    thumbnailUrl: string;
  }
  
  const [items, setItems] = useState<Item[]>([])

  const [cart, setCart] = useState<Cart>({
    id: 0,
    customerId: '',
    cartDetails: [{
      id: 0,
      productId: 0,
      quantity: 0,
    }]
  })

  useEffect(() => {
    if(!loaded)
    getCart().then(data => {
      setCart(data);
      data.cartDetails.map(async item => {
        const product = await getCartProductThumbnail(item.productId);
        setItems(preitems => [
          ...preitems,
          {
            productId: product.id,
            quantity: item.quantity,
            productName: product.name,
            slug: product.slug,
            thumbnailUrl: product.thumbnailUrl
          }
        ])
      })
    })
    loaded = true;
  }, []);


  return (
    <section className="shop-cart spad">
      <div className="container">
        <div className="row">
          <div className="col-lg-12">
            <div className="shop__cart__table">
              {!cart.id ? <h4>There are no items in this cart.</h4> :
                items.map((item) => {
                  return (
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
                        <tr>
                          <td className="cart__product__item">
                            <img
                              src={item.thumbnailUrl}
                              alt="img"
                              style={{ width: '85px', height: '85px' }}
                            />
                            <div className="cart__product__item__title">
                              <h6>{item.productName}</h6>
                            </div>
                          </td>
                          <td className="cart__price">34.500.000</td>
                          <td className="cart__quantity">
                            <div className="pro-qty">
                              <div className="quantity buttons_added">
                                <input
                                  id="minus-button"
                                  type="button"
                                  value="-"
                                  className="minus"
                                // onClick={(e) => handleMinus(e)}
                                />
                                <input
                                  id="quanity"
                                  type="number"
                                  step="1"
                                  min="1"
                                  max=""
                                  name="quantity"
                                  defaultValue={item.quantity}
                                  title="Qty"
                                  className="input-text qty text"
                                />
                                <input
                                  id="plus-button"
                                  type="button"
                                  value="+"
                                  className="plus"
                                // onClick={(e) => handlePlus(e)}
                                />
                              </div>
                            </div>
                          </td>
                          <td className="cart__total">34500000 </td>
                          <td className="cart__close">
                            {' '}
                            <button className="remove_product">
                              <i className="bi bi-x-lg"></i>
                            </button>{' '}
                          </td>
                        </tr>
                      </tbody>
                    </table>
                  )
                })
              }
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
                  Subtotal <span>$ 750.0</span>
                </li>
                <li>
                  Total <span>$ 750.0</span>
                </li>
              </ul>
              <a href="#" className="primary-btn">
                Proceed to checkout
              </a>
            </div>
          </div>
        </div>
      </div>
    </section>
  );
};

export default Cart;
