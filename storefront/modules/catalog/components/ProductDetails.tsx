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
  const [listImages, setListImages] = useState<string[]>([]);

  const getColorId = (options: ProductOptions[]) => {
    return options.find((attr) => attr.name === 'COLOR')?.id;
  };

  const initCurrentSelectedOption = useMemo(() => {
    if (!productOptions?.length || !productVariations?.length) {
      setListImages([
        ...(product.thumbnailMediaUrl ? [product.thumbnailMediaUrl] : []),
        ...product.productImageMediaUrls,
      ]);
      return {};
    }

    const productVariation = pvid && productVariations.find((item) => item.id.toString() === pvid);
    if (productVariation) {
      return productVariation.options;
    }

    const colorId = getColorId(productOptions);
    const productWithColor = productVariations.find(
      (variant) => String(colorId) in variant.options
    );

    setListImages(
      productWithColor
        ? [
            ...(productWithColor.thumbnail?.url ? [productWithColor.thumbnail.url] : []),
            ...productWithColor.productImages.map((image) => image.url),
          ]
        : [
            ...(product.thumbnailMediaUrl ? [product.thumbnailMediaUrl] : []),
            ...product.productImageMediaUrls,
          ]
    );
    return productWithColor ? productWithColor.options : productVariations[0].options;
  }, [productOptions, productVariations, pvid]);

  const router = useRouter();
  const [currentSelectedOption, setCurrentSelectedOption] =
    useState<CurrentSelectedOption>(initCurrentSelectedOption);
  const [optionSelected, setOptionSelected] = useState<CurrentSelectedOption>({});
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
    const isOptionSelected = (
      key: string,
      currentSelectedOption: CurrentSelectedOption,
      item: ProductVariation
    ) => {
      return currentSelectedOption[+key] === item.options[+key];
    };

    const areAllOptionsSelected = (
      optionKeys: string[],
      currentSelectedOption: CurrentSelectedOption,
      item: ProductVariation
    ) => {
      return optionKeys.every((key: string) => isOptionSelected(key, currentSelectedOption, item));
    };

    const findProductVariation = () => {
      return productVariations?.find((item) => {
        const optionKeys = Object.keys(item.options);
        return (
          optionKeys.length === Object.keys(currentSelectedOption).length &&
          areAllOptionsSelected(optionKeys, currentSelectedOption, item)
        );
      });
    };

    const updateListImages = (variation: ProductVariation) => {
      const urls = [
        ...(variation.thumbnail?.url ? [variation.thumbnail.url] : []),
        ...variation.productImages.map((image) => image.url),
      ];
      setListImages(urls);
      setCurrentProduct(variation);
    };

    const updateListImagesBySelectedProduct = (
      productVariations: ProductVariation[],
      productOptions: ProductOptions[]
    ) => {
      const productSelected = productVariations.find((item) => {
        return item.options[+Object.keys(optionSelected)[0]] == Object.values(optionSelected)[0];
      });

      if (productSelected) {
        const colorId = getColorId(productOptions);
        if (colorId === +Object.keys(optionSelected)[0]) {
          const urlList = productSelected.productImages.map((image) => image.url);
          setListImages([
            ...(productSelected.thumbnail?.url ? [productSelected.thumbnail.url] : []),
            ...urlList,
          ]);
        }
        setCurrentProduct(productSelected);
      }
    };

    if (productOptions?.length && productVariations?.length) {
      const productVariation = findProductVariation();
      if (productVariation) {
        updateListImages(productVariation);
      } else {
        updateListImagesBySelectedProduct(productVariations, productOptions);
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
      setOptionSelected({ [optionId]: optionValue });
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
          <ProductImageGallery listImages={listImages} />
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
