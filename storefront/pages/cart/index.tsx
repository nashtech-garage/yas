import { NextPage } from "next";
import Link from "next/link";
import { Button } from "react-bootstrap";


const Cart:NextPage = () => {    
    return (
            <section className="shop-cart spad">
                <div className="container">
                    <div className="row">
                        <div className="col-lg-12">
                            <div className="shop__cart__table">
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
                                                <img src="https://www.duchuymobile.com/images/variant_image/47/iphone-13-pro-max-den.jpeg" alt=""
                                                    style={{ "width": "85px", "height": "85px" }} />
                                                <div className="cart__product__item__title">
                                                    <h6>Chain bucket bag</h6>
                                                </div>
                                            </td>
                                            <td className="cart__price">$ 150.0</td>
                                            <td className="cart__quantity">
                                                <div className="pro-qty">
                                                    <div className="quantity buttons_added">
                                                        <input type="button" value="-" className="minus" />
                                                        <input type="number" step="1" min="1" max="" name="quantity" value="1" title="Qty" className="input-text qty text"  />
                                                        <input type="button" value="+" className="plus" />
                                                    </div>
                                                </div>
                                            </td>
                                            <td className="cart__total">$ 300.0</td>
                                            <td className="cart__close"> <button className="remove_product"><i className="bi bi-x-lg"></i></button> </td>
                                        </tr>

                                    </tbody>
                                </table>
                            </div>
                        </div>
                    </div>
                    <div className="row">
                        <div className="col-lg-6 col-md-6 col-sm-6">
                            <div>
                                <Link href={"/"}>
                                    <Button className="cart__btn2"><i className="bi bi-house-fill"></i> CONTINUE SHOPPING</Button>
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
                                    <li>Subtotal <span>$ 750.0</span></li>
                                    <li>Total <span>$ 750.0</span></li>
                                </ul>
                                <a href="#" className="primary-btn">Proceed to checkout</a>
                            </div>
                        </div>
                    </div>
                </div>
            </section>
    )
}

export default Cart;