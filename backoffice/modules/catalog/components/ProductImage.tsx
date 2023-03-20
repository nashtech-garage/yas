import { useEffect } from 'react';
import { UseFormSetValue } from 'react-hook-form';

import { Product } from '../models/Product';

import { FormProduct } from '../models/FormProduct';
import ChooseImages from './ChooseImages';
import ChooseThumbnail from './ChooseThumbnail';

type Props = {
  product?: Product;
  setValue: UseFormSetValue<FormProduct>;
};

const ProductImage = ({ product, setValue }: Props) => {
  useEffect(() => {
    setValue('thumbnailMedia', product?.thumbnailMedia);
    setValue('productImageMedias', product?.productImageMedias);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  return (
    <>
      <div className="mb-3">
        <h4 className="mb-3">Thumbnail</h4>

        <ChooseThumbnail
          id="main-thumbnail"
          image={
            product?.thumbnailMedia
              ? { id: +product.thumbnailMedia.id, url: product.thumbnailMedia.url }
              : null
          }
          setValue={setValue}
          name="thumbnailMedia"
        />
      </div>
      <div className="mb-3">
        <h4 className="mb-3">Product Image</h4>

        <ChooseImages
          id="main-product-images"
          images={
            product?.productImageMedias
              ? product.productImageMedias.map((image) => ({
                  id: image.id,
                  url: image.url,
                }))
              : []
          }
          setValue={setValue}
          name="productImageMedias"
        />
      </div>
    </>
  );
};

export default ProductImage;
