import styles from '../../../styles/ProductVariant.module.css';

import { ProductVariation } from '../models/ProductVariation';
import ChooseImages from './ChooseImages';
import ChooseThumbnail from './ChooseThumbnail';

type ProductVariantProps = {
  index: number;
  variant: ProductVariation;
  onDelete: (variant: ProductVariation) => void;
};

export default function ProductVariant({ variant, index, onDelete }: ProductVariantProps) {
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
      <span className={styles['delete-button']} onClick={() => onDelete(variant)}>
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
                onChange={onChangeValue}
              />
            </div>
          </div>
          <div className="d-flex gap-4">
            <div className="w-50">
              <label className="form-label fw-bold">
                GTIN (<span className="text-danger">*</span>)
              </label>
              <input
                type="text"
                className="form-control w-100"
                name="optionGtin"
                onChange={onChangeValue}
              />
            </div>
            <div className="w-50">
              <label className="form-label fw-bold">
                SKU (<span className="text-danger">*</span>)
              </label>
              <input
                type="text"
                className="form-control w-100"
                name="optionSku"
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
    </div>
  );
}
