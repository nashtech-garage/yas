import clsx from 'clsx';
import { useState } from 'react';
import { Image } from 'react-bootstrap';
import { UseFormSetValue } from 'react-hook-form';
import { toast } from 'react-toastify';

import { FormProduct } from '../models/FormProduct';
import { ProductVariation } from '../models/ProductVariation';
import { uploadMedia } from '../services/MediaService';
import { isValidFile, validTypes } from './ChooseThumbnail';

import styles from '../../../styles/ChooseImage.module.css';

type ChooseImagesProps = {
  id: string;
  images: Image[];
  name: 'productImageMedias' | 'productVariations';
  setValue?: UseFormSetValue<FormProduct>;
  variation?: ProductVariation;
  wrapperStyle?: React.CSSProperties;
  actionStyle?: React.CSSProperties;
  iconStyle?: React.CSSProperties;
};

type Image = {
  id: number;
  url: string;
};

export default function ChooseImages({
  id,
  images,
  setValue,
  name,
  variation,
  wrapperStyle,
  actionStyle,
  iconStyle,
}: ChooseImagesProps) {
  const [productImageURL, setProductImageURL] = useState<Image[]>(
    images.map((image) => ({
      id: image.id,
      url: image.url,
    }))
  );

  const onUploadImage = async (event: React.ChangeEvent<HTMLInputElement>) => {
    if (!event) {
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
      const images: Image[] = [];

      const uploadPromises = Array.from(filesList).map(async (file) => {
        const response = await uploadMedia(file);

        images.push({
          id: response.id,
          url: URL.createObjectURL(file),
        });
      });

      await Promise.all(uploadPromises);

      if (name === 'productImageMedias') {
        setValue?.(
          name,
          [...productImageURL, ...images].map((image) => ({
            id: image.id,
            url: image.url,
          }))
        );
      } else if (name === 'productVariations' && variation !== undefined) {
        if (variation.optionImages) variation.optionImages = [...variation.optionImages, ...images];
        else {
          variation.optionImages = [...images];
        }
      }

      setProductImageURL((prev) => [...prev, ...images]);
    } catch (error) {
      toast.error('Upload image failed');
    }
  };

  const onChangeImage = async (event: React.ChangeEvent<HTMLInputElement>, index: number) => {
    if (!event) {
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
      const file = filesList[0];
      const response = await uploadMedia(file);

      const productImageTmp = [...productImageURL];
      productImageTmp[index] = {
        id: response.id,
        url: URL.createObjectURL(file),
      };

      if (name === 'productImageMedias') {
        setValue?.(name, productImageTmp);
      } else if (name === 'productVariations' && variation !== undefined) {
        variation.optionImages = [...productImageTmp];
      }

      setProductImageURL(productImageTmp);
    } catch (error) {
      toast.error('Upload image failed');
    }
  };

  const onDeleteImage = (imageId?: number) => {
    if (name === 'productImageMedias') {
      setValue?.(
        name,
        productImageURL.filter((image) => image.id !== imageId)
      );
    } else if (name === 'productVariations' && variation !== undefined && variation.optionImages) {
      variation.optionImages = variation.optionImages.filter((image) => image.id !== imageId);
    }

    setProductImageURL((prev) => prev.filter((image) => image.id !== imageId));
  };

  return (
    <>
      <input hidden type="file" id={id} onChange={(event) => onUploadImage(event)} multiple />

      <div className="d-flex gap-2 flex-wrap">
        {productImageURL.map((productImageUrl, index) => (
          <div key={productImageUrl.id} className={styles['product-image']} style={wrapperStyle}>
            <div className={styles['actions']} style={actionStyle}>
              <label
                className={styles['icon']}
                htmlFor={`${id}-${productImageUrl.id}`}
                style={iconStyle}
              >
                <i className="bi bi-arrow-repeat"></i>
              </label>

              <div
                onClick={() => onDeleteImage(productImageUrl.id)}
                className={clsx(styles['icon'], styles['delete'])}
                style={iconStyle}
              >
                <i className="bi bi-x-lg"></i>
              </div>
            </div>

            <input
              hidden
              type="file"
              id={`${id}-${productImageUrl.id}`}
              onChange={(event) => onChangeImage(event, index)}
              multiple
            />
            <Image width={'100%'} src={productImageUrl.url} alt="Image" />
          </div>
        ))}

        <label className={styles['image-label']} htmlFor={id} style={wrapperStyle}>
          Choose product images
        </label>
      </div>
    </>
  );
}
