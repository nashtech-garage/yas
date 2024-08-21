import Link from 'next/link';
import { useRouter } from 'next/router';
import { useEffect, useMemo, useState } from 'react';

import { ProductImageGallery } from '@/common/components/ProductImageGallery';
import { useCartContext } from '@/context/CartContext';
import { AddToCartModel } from '@/modules/cart/models/AddToCartModel';
import { addToCart } from '@/modules/cart/services/CartService';
import { formatPrice } from '@/utils/formatPrice';
import { ProductDetail } from '../models/ProductDetail';
import { ProductOptions } from '../models/ProductOptions';
import { ProductVariation } from '../models/ProductVariation';
import { toastError, toastSuccess } from '../services/ToastService';
import DetailHeader from './DetailHeader';

type ProductDetailProps = {
  product: ProductDetail;
  productOptions?: ProductOptions[];
  productVariations?: ProductVariation[];
  pvid: string | null;
  averageStar: number;
  totalRating: number;
};

type CurrentSelectedOption = {
  [key: string]: string;
};

export default function ProductDetails({
  product,
  productOptions,
  productVariations,
  pvid,
  averageStar,
  totalRating,
}: ProductDetailProps) {
  const initCurrentSelectedOption = useMemo(() => {
    if (
      productOptions &&
      productOptions.length > 0 &&
      productVariations &&
      productVariations.length > 0
    ) {
      if (pvid) {
        const productVariation = productVariations?.find((item) => item.id.toString() === pvid);
        if (productVariation) {
          return Object.keys(productVariation.options).reduce((acc, cur) => {
            return {
              ...acc,
              [cur]: productVariation.options[+cur],
            };
          }, {});
        }
      }
      return productVariations[0].options;
    } else {
      return {};
    }
  }, [productOptions, productVariations, pvid]);

  const router = useRouter();
  const [currentSelectedOption, setCurrentSelectedOption] =
    useState<CurrentSelectedOption>(initCurrentSelectedOption);
  const [currentProduct, setCurrentProduct] = useState<ProductDetail | ProductVariation>(product);
  const { fetchNumberCartItems } = useCartContext();
  useEffect(() => {
    if (
      productOptions &&
      productOptions.length > 0 &&
      productVariations &&
      productVariations.length > 0
    ) {
      router.query.pvid = currentProduct.id.toString();
      router.push(router, undefined, { shallow: true });
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [productOptions, productVariations, pvid, currentProduct.id]);

  useEffect(() => {
    if (
      productOptions &&
      productOptions.length > 0 &&
      productVariations &&
      productVariations.length > 0
    ) {
      const productVariation = productVariations.find((item) => {
        return (
          Object.keys(item.options).length === Object.keys(currentSelectedOption).length &&
          Object.keys(item.options).every((key) => {
            return currentSelectedOption[+key] === item.options[+key];
          })
        );
      });
      if (productVariation) {
        setCurrentProduct(productVariation);
      }
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [JSON.stringify(currentSelectedOption)]);

  const handleAddToCart = async () => {
    let payload: AddToCartModel[] = [
      {
        productId: currentProduct.id,
        quantity: 1,
        parentProductId: product.hasOptions ? product.id : null,
      },
    ];
    await addToCart(payload)
      .then((_response) => {
        toastSuccess('Add to cart success');
        fetchNumberCartItems();
      })
      .catch((error) => {
        if (error.status === 403) toastError('You need to log in before add to cart');
        else toastError('Add to cart failed. Try again');
      });
  };

  const handleSelectOption = (optionId: number, optionValue: string) => {
    if (
      productOptions &&
      productOptions.length > 0 &&
      productVariations &&
      productVariations.length > 0
    ) {
      setCurrentSelectedOption({ ...currentSelectedOption, [optionId]: optionValue });
    }
  };
  return (
    <>
      <DetailHeader
        productName={currentProduct.name}
        averageStar={averageStar}
        ratingCount={totalRating}
      />

      <div className="row justify-content-center">
        <div className="col-6">
          <ProductImageGallery listImages={product.productImageMediaUrls} />
        </div>

        <div className="col-6">
          <div className="d-flex gap-2 align-items-center mb-2">
            <h5 className="m-0 fs-6">Brand: </h5>
            <Link href="#" className="fs-6">
              {product.brandName}
            </Link>
          </div>

          {/* product options */}
          {(productOptions || []).map((productOption) => (
            <div className="mb-3" key={productOption.name}>
              <h5 className="mb-2 fs-6">{productOption.name}:</h5>
              {(productOption.value || []).map((productOptionValue) => (
                <button
                  key={productOptionValue}
                  className={`btn me-2 py-1 px-2 ${
                    currentSelectedOption[productOption.id] === productOptionValue
                      ? 'btn-primary'
                      : 'btn-outline-primary'
                  }`}
                  onClick={() => handleSelectOption(productOption.id, productOptionValue)}
                >
                  {productOptionValue}
                </button>
              ))}
            </div>
          ))}
          <h4 className="fs-3" style={{ color: 'red' }}>
            {formatPrice(currentProduct.price)}
          </h4>
          <div className="py-4" dangerouslySetInnerHTML={{ __html: product.description }}></div>
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
        </div>
      </div>
    </>
  );
}
