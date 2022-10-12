import { useState } from 'react';

// TODO: Give this component a `setValues` or register field
const ProductImage = () => {
  const [thumbnailURL, setThumbnailURL] = useState<string>();
  const [productImageURL, setProductImageURL] = useState<string[]>();
  const [thumbnail, setThumbnail] = useState<File>();
  const [productImages, setProductImages] = useState<FileList>();

  const onProductImageSelected = (event: React.ChangeEvent<HTMLInputElement>) => {
    if (event.target.files) {
      const files = event.target.files;
      setProductImages(files);
      let length = files.length;

      let urls: string[] = [];

      for (let i = 0; i < length; i++) {
        const file = files[i];
        urls.push(URL.createObjectURL(file));
      }
      setProductImageURL([...urls]);
    }
  };

  const onThumbnailSelected = (event: React.ChangeEvent<HTMLInputElement>) => {
    if (event.target.files && event.target.files[0]) {
      const i = event.target.files[0];
      setThumbnail(i);
      setThumbnailURL(URL.createObjectURL(i));
    }
  };
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
};

export default ProductImage;
