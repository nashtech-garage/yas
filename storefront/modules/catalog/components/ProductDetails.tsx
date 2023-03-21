import Link from 'next/link';

import { formatPrice } from '../../../utils/formatPrice';
import { AddToCartModel } from '../../cart/models/AddToCartModel';
import { addToCart } from '../../cart/services/CartService';
import { ProductDetail } from '../models/ProductDetail';
import { ProductOptions } from '../models/ProductOptions';
import { ProductVariation } from '../models/ProductVariation';
import { toastError, toastSuccess } from '../services/ToastService';

export interface ProductDetailProps {
  product: ProductDetail;
  productOptions?: ProductOptions[];
  productVariations?: ProductVariation[];
}

export default function ProductDetails({
  product,
  productOptions,
  productVariations: _tmp,
}: ProductDetailProps) {
  const handleAddToCart = async () => {
    let payload: AddToCartModel[] = [
      {
        productId: product.id,
        quantity: 1,
      },
    ];
    await addToCart(payload)
      .then((_response) => {
        toastSuccess('Add to cart success');
      })
      .catch((error) => {
        if (error.status === 403) toastError('You need to log in before add to cart');
        else toastError('Add to cart failed. Try again');
      });
  };

  return (
    <>
      <div className="d-flex gap-2 align-items-center mb-2">
        <h5 className="m-0 fs-6">Brand: </h5>
        <Link href="#" className="fs-6">
          {product.brandName}
        </Link>
      </div>

      {/* product options */}
      {(productOptions || []).map((productOption, index) => (
        <div className="mb-3" key={index}>
          <h5 className="mb-2 fs-6">{productOption.name}:</h5>
          {(productOption.value || []).map((productOptionValue, index) => (
            <button key={index} className="btn btn-outline-primary me-2 py-1 px-2">
              {productOptionValue}
            </button>
          ))}
        </div>
      ))}
      <h4 className="fs-3" style={{ color: 'red' }}>
        {formatPrice(product.price)}
      </h4>
      <p className="py-4">{product.description}</p>
      <div>
        <button
          type="submit"
          className="btn btn-dark d-flex align-items-center justify-content-center gap-2 w-100 fs-6 fw-bold"
          style={{ height: '56px' }}
          disabled={!product.isAllowedToOrder}
          onClick={handleAddToCart}
        >
          <span>Add to cart</span>
        </button>
      </div>
      <div className="d-flex gap-2 mt-2">
        <button className="btn btn-primary w-100">
          <div style={{ fontSize: '14px' }}>Installment purchase</div>
          <div style={{ fontSize: '14px' }}>Browse profiles in 5 minutes</div>
        </button>
        <button className="btn btn-primary w-100">
          <div style={{ fontSize: '14px' }}>0% installment payment via card</div>
          <div style={{ fontSize: '14px' }}>Visa, Mastercard, JSB, Amex</div>
        </button>
      </div>
      <p className="text-center my-4">
        Call to order{' '}
        <a className="fw-bold" href="tel:18009999">
          1800.9999
        </a>{' '}
        (7:30 - 22:00)
      </p>
    </>
  );
}
