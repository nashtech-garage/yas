import clsx from 'clsx';
import { useState } from 'react';
import { Image } from 'react-bootstrap';
import { toast } from 'react-toastify';
import { UseFormSetValue } from 'react-hook-form';

import { uploadMedia } from '../services/MediaService';
import { FormProduct } from '../models/FormProduct';

import styles from '../../../styles/ChooseImage.module.css';
import { ProductVariation } from '../models/ProductVariation';

type ChooseThumbnailProps = {
  id: string;
  image: Image | null;
  setValue: UseFormSetValue<FormProduct>;
  name: 'thumbnailMedia' | 'productVariations';
  productVariation?: ProductVariation[];
  index?: number;
};

type Image = {
  id: number;
  url: string;
};

export const isValidFile = (file: File, validTypes: string[]) => {
  return validTypes.indexOf(file.type) !== -1;
};

export const validTypes = ['image/jpeg', 'image/png'];

export default function ChooseThumbnail({
  id,
  image,
  setValue,
  name,
  productVariation,
  index,
}: ChooseThumbnailProps) {
  const [thumbnailURL, setThumbnailURL] = useState<Image | null>(image);

  const onChangeImage = async (event: React.ChangeEvent<HTMLInputElement>) => {
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

      const response = await uploadMedia(filesList[0]);

      if (name === 'thumbnailMedia') {
        setValue('thumbnailMedia', {
          id: response.id,
          url: URL.createObjectURL(file),
        });
      } else if (name === 'productVariations' && productVariation && index) {
        const productVariations = [...productVariation];

        productVariations[index].optionThumbnail = {
          id: response.id,
          url: URL.createObjectURL(file),
        };

        setValue('productVariations', productVariations);
      }

      setThumbnailURL({
        id: response.id,
        url: URL.createObjectURL(file),
      });
    } catch (error) {
      toast.error('Upload image failed');
    }
  };

  const onDeleteImage = () => {
    setThumbnailURL(null);
    if (name === 'thumbnailMedia') {
      setValue('thumbnailMedia', undefined);
    } else if (name === 'productVariations' && productVariation && index) {
      const productVariations = [...productVariation];

      productVariations[index].optionThumbnail = undefined;

      setValue('productVariations', productVariations);
    }
  };

  return (
    <>
      {!thumbnailURL && (
        <label className={styles['image-label']} htmlFor={id}>
          Choose an image
        </label>
      )}

      <input hidden type="file" id={id} onChange={(event) => onChangeImage(event)} />

      {thumbnailURL && (
        <div className={styles['product-image']}>
          <div className={styles['actions']}>
            <label className={styles['icon']} htmlFor={id}>
              <i className="bi bi-arrow-repeat"></i>
            </label>

            <div onClick={onDeleteImage} className={clsx(styles['icon'], styles['delete'])}>
              <i className="bi bi-x-lg"></i>
            </div>
          </div>
          <Image width={'100%'} src={thumbnailURL.url} alt="Thumbnail" />
        </div>
      )}
    </>
  );
}
