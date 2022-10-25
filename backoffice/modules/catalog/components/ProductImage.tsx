import { useRouter } from 'next/router';
import { UseFormSetValue } from 'react-hook-form';
import { ProductPost } from '../models/ProductPost';
import { Product } from '../models/Product';
import { useEffect, useState } from 'react';
import { getProduct } from '../services/ProductService';
import { uploadMedia } from '../services/MediaService';
import { toast } from 'react-toastify';

type Props = {
  setValue: UseFormSetValue<ProductPost>;
};

const ProductImage = ({ setValue }: Props) => {
  const [thumbnailURL, setThumbnailURL] = useState<string>();
  const [productImageURL, setProductImageURL] = useState<string[]>();
  const [product, setProduct] = useState<Product>();
  const [productImageMediaUrls, setproductImageMediaUrls] = useState<string[]>();

  const router = useRouter();
  const { id } = router.query;

  //Product Create
  const onProductImageSelected = (event: React.ChangeEvent<HTMLInputElement>) => {
    if (event.target.files) {
      const files = event.target.files;
      let length = files.length;
      let urls: string[] = [];
      for (let i = 0; i < length; i++) {
        const file = files[i];
        urls.push(URL.createObjectURL(file));
      }
      setValue('productImages', files);
      setProductImageURL([...urls]);
    }
  };

  const onThumbnailSelected = (event: React.ChangeEvent<HTMLInputElement>) => {
    if (event.target.files && event.target.files[0]) {
      const i = event.target.files[0];
      setValue('thumbnail', i);
      setThumbnailURL(URL.createObjectURL(i));
    }
  };
  ////////

  /// Product Update

  useEffect(() => {
    if (id) {
      getProduct(+id).then((data) => {
        setProduct(data);
      });
    }
  }, []);

  const onProductUpdateImageSelected = (event: React.ChangeEvent<HTMLInputElement>) => {
    if (event.target.files) {
      const files = event.target.files;
      let length = files.length;
      let urls: string[] = [];
      let ids: number[] = [];
      for (let i = 0; i < length; i++) {
        const file = files[i];
        urls.push(URL.createObjectURL(file));
        uploadMedia(file)
          .then((res) => {
            ids = [...ids, res.id];
            setValue('productImageIds', ids);
          })
          .catch(() => {
            toast('Upload failed. Please try again!');
          });
      }
      setproductImageMediaUrls([...urls]);
    }
  };

  const onThumbnailUpdateSelected = (event: React.ChangeEvent<HTMLInputElement>) => {
    if (event.target.files && event.target.files[0]) {
      const i = event.target.files[0];
      setThumbnailURL(URL.createObjectURL(i));
      uploadMedia(i)
        .then((res) => {
          setValue('thumbnailMediaId', res.id);
        })
        .catch(() => {
          toast('Upload failed. Please try again!');
        });
    }
  };

  if (id) {
    if (!product) {
      return <p>No product</p>;
    } else {
      return (
        <>
          <div className="mb-3">
            <label className="form-label" htmlFor="thumbnail">
              Thumbnail
            </label>
            <input
              className="form-control"
              type="file"
              name="thumbnail"
              onChange={onThumbnailUpdateSelected}
            />
            <img
              style={{ width: '150px' }}
              src={thumbnailURL ? thumbnailURL : product.thumbnailMediaUrl}
            />
          </div>
          <div className="mb-3">
            <label className="form-label" htmlFor="product-image">
              Product Images
            </label>
            <input
              className="form-control"
              type="file"
              id="product-images"
              onChange={onProductUpdateImageSelected}
              multiple
            />
            {productImageMediaUrls
              ? productImageMediaUrls.map((imageUrl, index) => (
                  <img style={{ width: '150px' }} src={imageUrl} key={index} alt="Product Image" />
                ))
              : product.productImageMediaUrls &&
                product.productImageMediaUrls.map((imageUrl, index) => (
                  <img style={{ width: '150px' }} src={imageUrl} key={index} alt="Product Image" />
                ))}
          </div>
        </>
      );
    }
  } else {
    return (
      <>
        <div className="mb-3">
          <label className="form-label" htmlFor="thumbnail">
            Thumbnail
          </label>
          <input
            className={`form-control`}
            type="file"
            id="thumbnail"
            onChange={onThumbnailSelected}
          />

          <img style={{ width: '150px' }} src={thumbnailURL} />
        </div>
        <div className="mb-3">
          <label className="form-label" htmlFor="product-image">
            Product Image
          </label>
          <input
            className="form-control"
            type="file"
            id="product-image"
            onChange={onProductImageSelected}
            multiple
          />
          {productImageURL?.map((productImageUrl, index) => (
            <img style={{ width: '150px' }} src={productImageUrl} key={index} alt="Image" />
          ))}
        </div>
      </>
    );
  }
};

export default ProductImage;
