import { useEffect, useState } from 'react';
import { Brand } from '../models/Brand';
import { getBrands } from '../services/BrandService';
import { UseFormRegister, FieldErrorsImpl, UseFormSetValue } from 'react-hook-form';
import { ProductPost } from '../models/ProductPost';
import { Product } from '../models/Product';
import { useRouter } from 'next/router';
import { getProduct } from '../../../modules/catalog/services/ProductService';
import { toast } from 'react-toastify';
import slugify from 'slugify';

type Props = {
  register: UseFormRegister<ProductPost>;
  errors: FieldErrorsImpl<ProductPost>;
  setValue: UseFormSetValue<ProductPost>;
};

const ProductUpdateInformation = ({ register, errors, setValue }: Props) => {
  //Get ID
  const router = useRouter();
  const { id } = router.query;

  const [brands, setBrands] = useState<Brand[]>([]);
  const [generateSlug, setGenerateSlug] = useState<string>();

  //Get product detail
  const [product, setProduct] = useState<Product>();
  const [isLoading, setLoading] = useState(false);

  useEffect(() => {
    setLoading(true);
    if (id) {
      getProduct(+id).then((data) => {
        if (data.id) {
          setProduct(data);
          setLoading(false);
        } else {
          //Show error
          toast(data.detail);
          location.replace('/catalog/products');
        }
      });
    }
    getBrands().then((data) => {
      setBrands(data);
    });
  }, [id]);
  //Handle
  const onNameChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    setGenerateSlug(slugify(event.target.value.toLowerCase()));
  };

  const onSlugChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    setGenerateSlug(event.target.value.toLowerCase());
  };
  if (isLoading) return <p>Loading...</p>;
  if (!product) {
    return <p>No product</p>;
  }

  return (
    <>
      <div className="mb-3">
        <label className="form-label" htmlFor="name">
          Product Name <span style={{ color: 'red' }}>*</span>
        </label>
        <input
          defaultValue={product.name}
          {...register('name', { required: 'Name is required', onChange: onNameChange })}
          className={`form-control ${errors.name ? 'border-danger' : ''}`}
          type="text"
          id="name"
          name="name"
        />
        <p className="error-field">
          <>{errors.name?.message}</>
        </p>
      </div>
      <div className="mb-3">
        <label className="form-label" htmlFor="slug">
          Slug
        </label>
        <input
          value={generateSlug !== undefined ? generateSlug : product.slug}
          {...register('slug', { onChange: onSlugChange })}
          className={`form-control ${errors.slug ? 'border-danger' : ''}`}
          type="text"
          id="slug"
          name="slug"
        />
        <p className="error-field">
          <>{errors.slug?.message}</>
        </p>
      </div>
      <div className="mb-3">
        <label className="form-label" htmlFor="sku">
          SKU
        </label>
        <input
          defaultValue={product.sku}
          {...register('sku')}
          className={`form-control ${errors.sku ? 'border-danger' : ''}`}
          type="text"
          id="sku"
          name="sku"
        />
        <p className="error-field">
          <>{errors.sku?.message}</>
        </p>
      </div>
      <div className="mb-3">
        <label className="form-label" htmlFor="gtin">
          GTIN
        </label>
        <input
          defaultValue={product.gtin}
          {...register('gtin')}
          className={`form-control ${errors.gtin ? 'border-danger' : ''}`}
          type="text"
          id="gtin"
          name="gtin"
        />
        <p className="error-field">
          <>{errors.gtin?.message}</>
        </p>
      </div>
      <div className="mb-3">
        <label className="form-label" htmlFor="description">
          Description
        </label>
        <textarea
          defaultValue={product.description}
          {...register('description')}
          className="form-control"
          id="description"
          name="description"
        />
        <p className="error-field">
          <>{errors.description?.message}</>
        </p>
      </div>
      <div className="mb-3">
        <label className="form-label" htmlFor="short-description">
          Short Description
        </label>
        <input
          defaultValue={product.shortDescription}
          {...register('shortDescription')}
          className={`form-control ${errors.shortDescription ? 'border-danger' : ''}`}
          type="text"
          id="short-description"
          name="shortDescription"
        />
        <p className="error-field">
          <>{errors.shortDescription?.message}</>
        </p>
      </div>
      <div className="mb-3">
        <label className="form-label" htmlFor="specification">
          Specification
        </label>
        <textarea
          defaultValue={product.specification}
          {...register('specification')}
          className={`form-control ${errors.specification ? 'border-danger' : ''}`}
          id="specification"
          name="specification"
        />
        <p className="error-field">
          <>{errors.specification?.message}</>
        </p>
      </div>
      <div className="mb-3">
        <label className="form-label" htmlFor="price">
          Price
        </label>
        <input
          defaultValue={product.price}
          {...register('price', { min: 0 })}
          className={`form-control ${errors.price ? 'border-danger' : ''}`}
          type="number"
          id="price"
        />
        {errors.price && errors.price.type === 'min' ? (
          <p className="error-field">Price must be at least 0</p>
        ) : (
          <p className="error-field">
            <>{errors.price?.message}</>
          </p>
        )}
      </div>
      <div className="mb-3">
        <label className="form-label" htmlFor="brand">
          Brand
        </label>
        <select
          className={`form-select ${errors.brandId ? 'border-danger' : ''}`}
          id="brand"
          {...register('brandId')}
          defaultValue={product.brandId ? product.brandId : 0}
        >
          <option disabled hidden value={0}>
            Select Brand
          </option>
          {Array.from(brands).map((brand) => (
            <option value={brand.id} key={brand.id}>
              {brand.name}
            </option>
          ))}
        </select>
        <p className="error-field">
          <>{errors.brandId?.message}</>
        </p>
      </div>
      <div className="mb-3">
        <label className="form-label me-3" htmlFor="is-allowed-to-order">
          Is Allowed To Order
        </label>
        <input
          defaultChecked={product.isAllowedToOrder}
          {...register('isAllowedToOrder')}
          type="checkbox"
          id="is-allowed-to-order"
        />
      </div>
      <div className="mb-3">
        <label className="form-label me-3" htmlFor="is-published">
          Is Published
        </label>
        <input
          defaultChecked={product.isPublished}
          {...register('isPublished')}
          type="checkbox"
          id="is-published"
        />
      </div>
      <div className="mb-3">
        <label className="form-label me-3" htmlFor="is-featured">
          Is Featured
        </label>
        <input
          defaultChecked={product.isFeatured}
          {...register('isFeatured')}
          type="checkbox"
          id="is-featured"
        />
      </div>
    </>
  );
};

export default ProductUpdateInformation;