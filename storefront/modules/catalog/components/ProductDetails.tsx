import Link from 'next/link';
import { useRouter } from 'next/router';
import { useEffect, useMemo, useState } from 'react';

import { ProductImageGallery } from '@/common/components/ProductImageGallery';
import { useCartContext } from '@/context/CartContext';
import { formatPrice } from '@/utils/formatPrice';
import { ProductDetail } from '../models/ProductDetail';
import { ProductOptions } from '../models/ProductOptions';
import { ProductVariation } from '../models/ProductVariation';
import { toastError, toastSuccess } from '../services/ToastService';
import DetailHeader from './DetailHeader';
import { CartItemPostVm } from '@/modules/cart/models/CartItemPostVm';
import { YasError } from '@/common/services/errors/YasError';
import { addCartItem } from '@/modules/cart/services/CartService';
import { ProductOptionValueDisplay } from '../models/ProductOptionValueGet';
import { Button } from 'react-bootstrap';

type ProductDetailProps = {
  product: ProductDetail;
  productOptions?: ProductOptions[];
  productVariations?: ProductVariation[];
  productOptionValueGet?: ProductOptionValueDisplay[];
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
  productOptionValueGet,
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
  const [isUnchecking, setIsUnchecking] = useState<boolean>(false);
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

    const findProductVariationMatchAllOptions = () => {
      return productVariations?.find((item) => {
        const optionKeys = Object.keys(item.options);
        return (
          optionKeys.length === Object.keys(currentSelectedOption).length &&
          areAllOptionsSelected(optionKeys, currentSelectedOption, item)
        );
      });
    };

    const updateListImagesByProductVariationMatchAllOptions = (variation: ProductVariation) => {
      const urls = [
        ...(variation.thumbnail?.url ? [variation.thumbnail.url] : []),
        ...variation.productImages.map((image) => image.url),
      ];
      setListImages(urls);
      setCurrentProduct(variation);
      setCurrentSelectedOption(variation.options);
    };

    const updateListImagesBySelectedOption = (productVariations: ProductVariation[]) => {
      const productSelected = productVariations.find((item) => {
        return item.options[+Object.keys(optionSelected)[0]] == Object.values(optionSelected)[0];
      });

      if (productSelected) {
        const urlList = productSelected.productImages.map((image) => image.url);
        setListImages([
          ...(productSelected.thumbnail?.url ? [productSelected.thumbnail.url] : []),
          ...urlList,
        ]);
        setCurrentSelectedOption(productSelected.options);
        setCurrentProduct(productSelected);
      }
    };

    if (productOptions?.length && productVariations?.length) {
      const productVariationMatchAllOptions = findProductVariationMatchAllOptions();
      if (productVariationMatchAllOptions) {
        updateListImagesByProductVariationMatchAllOptions(productVariationMatchAllOptions);
      } else if (!isUnchecking) {
        updateListImagesBySelectedOption(productVariations);
      }
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [JSON.stringify(currentSelectedOption)]);

  const handleAddToCart = async () => {
    const payload: CartItemPostVm = {
      productId: currentProduct.id,
      quantity: 1,
    };
    try {
      await addCartItem(payload);
    } catch (error) {
      if (error instanceof YasError && error.status === 403) {
        toastError('You need to login first before adding to cart');
      } else {
        toastError('Add to cart failed. Try again');
      }
      return;
    }
    toastSuccess('Add to cart success');
    fetchNumberCartItems();
  };

  const handleSelectOption = (optionId: number, optionValue: string) => {
    if (
      productOptions &&
      productOptions.length > 0 &&
      productVariations &&
      productVariations.length > 0
    ) {
      if (currentSelectedOption[+optionId] === optionValue) {
        if (Object.keys(currentSelectedOption).length > 1) {
          setCurrentSelectedOption((prev) => {
            const newOption = { ...prev };
            delete newOption[+optionId];
            return newOption;
          });
          setIsUnchecking(true);
        }
      } else {
        setCurrentSelectedOption({ ...currentSelectedOption, [optionId]: optionValue });
        setOptionSelected({ [optionId]: optionValue });
        setIsUnchecking(false);
      }
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
          {(productOptions || []).map((productOption) => {
            const productOptionPost = productOptionValueGet?.find(
              (productOptionPost) => productOptionPost.productOptionId === productOption.id
            );
            const parsedValue = productOptionPost?.productOptionValue ? JSON.parse(productOptionPost.productOptionValue) : [];
            return productOptionPost ? (
              <div className="mb-3" key={productOption.name}>
                <h5 className="mb-2 fs-6">{productOption.name}:</h5>
                {productOptionPost.displayType === 'color' ? (
                  <div className="d-flex">
                    {Object.entries(parsedValue).map(([key, value]: any, id: any) => (
                      <Button
                        key={key}
                        className={`color-swatch me-2 py-1 px-2 ${currentSelectedOption[productOptionPost.productOptionId] === key
                          ? 'border border-2 border-primary opacity-100'
                          : 'btn-outline-primary opacity-25'}`}
                        style={{ backgroundColor: value, width: '30px', height: '30px', borderRadius: '50%' }}
                        onClick={() => handleSelectOption(productOptionPost.productOptionId, key)}
                        aria-label={`Color option ${value}`}
                      ></Button>
                    ))}
                  </div>
                ) : (
                  <div className="d-flex gap-2">
                    {Object.entries(parsedValue).map(([key, value]: any, id: any) => (
                      <Button
                        key={key}
                        className={`${currentSelectedOption[productOptionPost.productOptionId] === key
                          ? 'btn btn-primary text-white'
                          : 'text-dark btn-outline-primary'}`}
                        onClick={() => handleSelectOption(productOptionPost.productOptionId, key)}
                        aria-label={`Color option ${value}`}
                        variant='outline-secondary'
                      >{key}</Button>
                    ))}
                  </div>
                )}
              </div>
            ) : productOption.name;
          })}

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
