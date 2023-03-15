import clsx from 'clsx';
import { useEffect, useState } from 'react';
import { Image } from 'react-bootstrap';
import { UseFormSetValue } from 'react-hook-form';
import { toast } from 'react-toastify';

import { Product } from '../models/Product';
import { ProductPost } from '../models/ProductPost';
import { uploadMedia } from '../services/MediaService';

import styles from '../../../styles/ProductImage.module.css';

type Props = {
  product?: Product;
  setValue: UseFormSetValue<ProductPost>;
};

type EventType = 'thumbnail' | 'productImages';

type Image = {
  id: number;
  url: string;
};

const isValidFile = (file: File, validTypes: string[]) => {
  return validTypes.indexOf(file.type) !== -1;
};

const ProductImage = ({ product, setValue }: Props) => {
  const validTypes = ['image/jpeg', 'image/png'];

  const [thumbnailURL, setThumbnailURL] = useState<Image | null>(
    product?.thumbnailMedia
      ? { id: +product.thumbnailMedia.id, url: product.thumbnailMedia.url }
      : null
  );
  const [productImageURL, setProductImageURL] = useState<Image[]>(
    product?.productImageMedias
      ? product.productImageMedias.map((image) => ({
          id: image.id,
          url: image.url,
        }))
      : []
  );

  useEffect(() => {
    setValue('thumbnailMedia', product?.thumbnailMedia);
    setValue('productImageMedias', product?.productImageMedias);
  }, []);

  const onChangeImage = async (
    event: React.ChangeEvent<HTMLInputElement>,
    type: EventType,
    index: number = -1
  ) => {
    if (!event || !type) {
      return;
    }

    const filesList = event.target.files;
    const isAllValidImages =
      filesList && Array.from(filesList).every((file) => isValidFile(file, validTypes));

    if (!isAllValidImages) {
      toast.error('Please select an image file (jpg or png)');
      return;
    }

    try {
      if (type === 'thumbnail') {
        const file = filesList[0];

        const response = await uploadMedia(filesList[0]);

        setValue('thumbnailMedia', {
          id: response.id,
          url: URL.createObjectURL(file),
        });
        setThumbnailURL({
          id: response.id,
          url: URL.createObjectURL(file),
        });
      } else if (type === 'productImages') {
        if (index > -1) {
          const file = filesList[0];

          const response = await uploadMedia(file);

          const productImageTmp = [...productImageURL];
          productImageTmp[index] = {
            id: response.id,
            url: URL.createObjectURL(file),
          };

          setValue(
            'productImageMedias',
            productImageTmp.map((image) => ({
              id: image.id,
              url: image.url,
            }))
          );
          setProductImageURL(productImageTmp);
        } else {
          const images: Image[] = [];

          const uploadPromises = Array.from(filesList).map(async (file) => {
            const response = await uploadMedia(file);

            images.push({
              id: response.id,
              url: URL.createObjectURL(file),
            });
          });

          await Promise.all(uploadPromises);

          setValue(
            'productImageMedias',
            [...productImageURL, ...images].map((image) => ({
              id: image.id,
              url: image.url,
            }))
          );
          setProductImageURL((prev) => [...prev, ...images]);
        }
      }
    } catch (error) {
      toast.error('Upload image failed');
    }
  };

  const onDeleteImage = (type: EventType, imageId?: number) => {
    if (type === 'thumbnail') {
      setThumbnailURL(null);
    } else {
      setProductImageURL((prev) => prev.filter((image) => image.id !== imageId));
    }
  };

  return (
    <>
      <div className="mb-3">
        <h4 className="mb-3">Thumbnail</h4>
        {!thumbnailURL && (
          <label className={styles['image-label']} htmlFor="product-thumbnail">
            Choose an image
          </label>
        )}

        <input
          hidden
          type="file"
          id="product-thumbnail"
          onChange={(event) => onChangeImage(event, 'thumbnail')}
        />

        {thumbnailURL && (
          <div className={styles['product-image']}>
            <div className={styles['actions']}>
              <label className={styles['icon']} htmlFor="product-thumbnail">
                <i className="bi bi-arrow-repeat"></i>
              </label>

              <div
                onClick={() => onDeleteImage('thumbnail')}
                className={clsx(styles['icon'], styles['delete'])}
              >
                <i className="bi bi-x-lg"></i>
              </div>
            </div>
            <Image width={'100%'} src={thumbnailURL.url} alt="Thumbnail" />
          </div>
        )}
      </div>
      <div className="mb-3">
        <h4 className="mb-3">Product Image</h4>

        <input
          hidden
          type="file"
          id="product-images"
          onChange={(event) => onChangeImage(event, 'productImages')}
          multiple
        />

        <div className="d-flex gap-2 flex-wrap">
          {productImageURL.map((productImageUrl, index) => (
            <div key={productImageUrl.id} className={styles['product-image']}>
              <div className={styles['actions']}>
                <label className={styles['icon']} htmlFor={`product-images-${productImageUrl.id}`}>
                  <i className="bi bi-arrow-repeat"></i>
                </label>

                <div
                  onClick={() => onDeleteImage('productImages', productImageUrl.id)}
                  className={clsx(styles['icon'], styles['delete'])}
                >
                  <i className="bi bi-x-lg"></i>
                </div>
              </div>

              <input
                hidden
                type="file"
                id={`product-images-${productImageUrl.id}`}
                onChange={(event) => onChangeImage(event, 'productImages', index)}
                multiple
              />
              <Image width={'100%'} src={productImageUrl.url} alt="Image" />
            </div>
          ))}

          <label className={styles['image-label']} htmlFor="product-images">
            Choose multi images
          </label>
        </div>
      </div>
    </>
  );
};

export default ProductImage;
