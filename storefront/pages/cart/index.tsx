import { NextPage } from "next";
import Link from "next/link";
import { Button } from "react-bootstrap";


const Cart: NextPage = () => {
    const products = [
        {
            id: 2,
            name: "iphone 14",
            price: 1500,
            url: "https://www.duchuymobile.com/images/variant_image/47/iphone-13-pro-max-den.jpeg",
            quantity: 1

        },
        {
            id: 3,
            name: "iphone 13",
            price: 1000,
            url: "https://www.duchuymobile.com/images/variant_image/47/iphone-13-pro-max-den.jpeg",
            quantity: 2

        },
        {
            id: 1,
            name: "iphone 12",
            price: 500,
            url: "https://www.duchuymobile.com/images/variant_image/47/iphone-13-pro-max-den.jpeg",
            quantity: 3

        }
    ]

    const handleMinus = (e: any) => {
        console.log(e.target.id.slice(-1));
        let productId = e.target.id.slice(-1);
        const input = document.getElementById(`quantity-${productId}`) as HTMLInputElement | null;
        const oldValue: number = Number(input?.value)
        if (input != null) input.value = String(oldValue - 1);
        if (input != null && oldValue != null && oldValue == 0) input.value = String(0)
    }

    const handlePlus = (e: any) => {
        console.log(e.target.id.slice(-1));
        let productId = e.target.id.slice(-1);
        const input = document.getElementById(`quantity-${productId}`) as HTMLInputElement | null;
        const oldValue: number = Number(input?.value)
        if (input != null) input.value = String(oldValue + 1);
    }

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
                                    {
                                        products.map(product =>
                                        (
                                            <tr key={product.id}>
                                                <td className="cart__product__item">
                                                    <img src={product.url} alt="img" style={{ "width": "85px", "height": "85px" }} />
                                                    <div className="cart__product__item__title">
                                                        <h6>{product.name}</h6>
                                                    </div>
                                                </td>
                                                <td className="cart__price">{product.price}</td>
                                                <td className="cart__quantity">
                                                    <div className="pro-qty">
                                                        <div className="quantity buttons_added">
                                                            <input id={`minus-${product.id}`} type="button" value="-" className="minus"
                                                                onClick={(e) => handleMinus(e)} />
                                                            <input id={`quantity-${product.id}`} type="number" step="1" min="1" max="" name="quantity" defaultValue={product.quantity} title="Qty" className="input-text qty text" />
                                                            <input id={`plus-${product.id}`} type="button" value="+" className="plus"
                                                                onClick={(e) => handlePlus(e)} />
                                                        </div>
                                                    </div>
                                                </td>
                                                <td className="cart__total">{product.price * product.quantity} </td>
                                                <td className="cart__close"> <button className="remove_product"><i className="bi bi-x-lg"></i></button> </td>
                                            </tr>
                                        ))
                                    }


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