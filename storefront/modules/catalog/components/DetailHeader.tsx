import { Star } from '../../rating/components';

export interface Props {
  productName: string;
  averageStar: number;
  ratingCount: number;
}

export default function DetailHeader({ productName, averageStar, ratingCount }: Props) {
  return (
    <div className="product-detail-header">
      <div className="left">
        <h4 className="title">{productName}</h4>
        <div className="rating-star">
          <Star star={averageStar} />
        </div>
        <span className="rating-count">({ratingCount} ratings)</span>
      </div>

      <span>
        <i className="bi bi-bag-heart fs-3 text-danger"></i>
      </span>
    </div>
  );
}
