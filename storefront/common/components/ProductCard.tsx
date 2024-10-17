import { useLayoutEffect, useState } from 'react';
import { getMediaById } from '@/modules/media/services/MediaService';
import { ProductThumbnail } from 'modules/catalog/models/ProductThumbnail';
import ProductCardBase from './ProductCardBase';

export interface Props {
  product: ProductThumbnail;
  thumbnailId?: number;
  className?: string[];
}

export default function ProductCard({ product, className, thumbnailId }: Props) {
  const [thumbnailUrl, setThumbnailUrl] = useState<string>(thumbnailId ? '' : product.thumbnailUrl);

  useLayoutEffect(() => {
    if (thumbnailId) {
      fetchMediaUrl(thumbnailId);
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [thumbnailId]);

  const fetchMediaUrl = (mediaId: number) => {
    getMediaById(mediaId)
      .then((response) => {
        setThumbnailUrl(response.url);
      })
      .catch((error) => {
        console.log(error);
      });
  };

  return <ProductCardBase product={product} thumbnailUrl={thumbnailUrl} className={className} />;
}
