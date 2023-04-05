import ConfirmationDialog from 'common/components/ConfirmationDialog';
import styles from '../../../styles/ProductVariant.module.css';

import { ProductVariation } from '../models/ProductVariation';
import ChooseImages from './ChooseImages';
import ChooseThumbnail from './ChooseThumbnail';
import { useState } from 'react';
import { deleteProduct } from '@catalogServices/ProductService';

type ProductVariantProps = {
  index: number;
  variant: ProductVariation;
  onDelete: (variant: ProductVariation) => void;
};

export default function ProductVariant({ variant, index, onDelete }: ProductVariantProps) {
  const [isOpenRemoveDialog, setIsOpenRemoveDialog] = useState<boolean>(false);

  const deleteVariation = () => {
    if (variant.id) {
      setIsOpenRemoveDialog(true);
    } else {
      onDelete(variant);
    }
  };

  const deleteExistingVariation = () => {
    deleteProduct(variant.id!).then(() => onDelete(variant));
  };

  const onChangeValue = (event: React.ChangeEvent<HTMLInputElement>) => {
    const value = event.target.value;

    switch (event.target.name) {
      case 'optionSku':
        variant.optionSku = value;
        break;
      case 'optionGtin':
        variant.optionGTin = value;
        break;
      case 'optionPrice':
        variant.optionPrice = +value;
        break;
      default:
        return;
    }
  };

  return (
    <div className={styles['product-variant']}>
      <span className={styles['delete-button']} onClick={() => deleteVariation()}>
        <i className="bi bi-x"></i>
      </span>
      <div className={styles['main']}>
        <div className="pe-3">
          <ChooseThumbnail
            id={`sub-thumbnail-${index}`}
            image={
              variant.optionThumbnail
                ? { id: variant.optionThumbnail.id, url: variant.optionThumbnail.url }
                : null
            }
            name="productVariations"
            variation={variant}
            wrapperStyle={{
              width: '120px',
              height: '150px',
            }}
            iconStyle={{
              fontSize: '12px',
              width: '20px',
              height: '20px',
            }}
            actionStyle={{
              top: '-10px',
              right: '-8px',
            }}
          />
        </div>
        <div className="flex-grow-1 d-flex flex-column gap-2">
          <div className="d-flex gap-4">
            <div className="w-50">
              <label className="form-label fw-bold">Name</label>
              <input
                type="text"
                className="form-control w-100"
                readOnly
                value={variant.optionName}
              />
            </div>
            <div className="w-50">
              <label className="form-label fw-bold">
                Price (<span className="text-danger">*</span>)
              </label>
              <input
                type="number"
                min={1}
                className="form-control w-100"
                name="optionPrice"
                defaultValue={variant.optionPrice}
                onChange={onChangeValue}
              />
            </div>
          </div>
          <div className="d-flex gap-4">
            <div className="w-50">
              <label className="form-label fw-bold">GTIN</label>
              <input
                type="text"
                className="form-control w-100"
                name="optionGtin"
                defaultValue={variant.optionGTin}
                onChange={onChangeValue}
              />
            </div>
            <div className="w-50">
              <label className="form-label fw-bold">SKU</label>
              <input
                type="text"
                className="form-control w-100"
                name="optionSku"
                defaultValue={variant.optionSku}
                onChange={onChangeValue}
              />
            </div>
          </div>
        </div>
      </div>
      <div className="d-flex flex-wrap gap-3 mt-3">
        <ChooseImages
          id={`sub-images-${index}`}
          images={variant.optionImages || []}
          name="productVariations"
          variation={variant}
          wrapperStyle={{
            width: '100px',
            height: '100px',
            fontSize: '12px',
          }}
          iconStyle={{
            fontSize: '12px',
            width: '20px',
            height: '20px',
          }}
          actionStyle={{
            top: '-12px',
            right: '-8px',
          }}
        />
      </div>
      <ConfirmationDialog
        isOpen={isOpenRemoveDialog}
        okText="Yes"
        cancelText="No"
        cancel={() => setIsOpenRemoveDialog(false)}
        ok={() => deleteExistingVariation()}
      >
        <p>Do you want to remove variant {variant.optionName} ?</p>
      </ConfirmationDialog>
    </div>
  );
}
