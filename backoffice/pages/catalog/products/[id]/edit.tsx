import type { NextPage } from 'next'
import { useRouter } from 'next/router'
import { useEffect, useState } from 'react'
import { Product } from '../../../../modules/catalog/models/Product'
import { ProductThumbnail } from '../../../../modules/catalog/models/ProductThumbnail'
import { getProduct, updateProduct } from '../../../../modules/catalog/services/ProductService'
import { useForm } from "react-hook-form";
import slugify from "slugify";

const ProductEdit: NextPage = () => {
  //Get ID
  const router = useRouter()
  const { id } = router.query;
  //Variables
  const [thumbnail, setThumbnail] = useState<File>();
  const [thumbnailURL, setThumbnailURL] = useState<string>();
  const [generateSlug, setGenerateSlug] = useState<string>();

  //Get product detail
  const [product, setProduct] = useState<ProductThumbnail>();
  const [isLoading, setLoading] = useState(false);
  const [error, setError] = useState();
  //Form validate
  const { register, formState: { errors }, handleSubmit } = useForm();

  useEffect(() => {
    setLoading(true);
    if (id) {
      getProduct(+id)
        .then((data) => {
          setProduct(data);
          setLoading(false);
        });
    }
  }, []);

  //Handle
  const onNameChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    setGenerateSlug(slugify(event.target.value.replace(/(^\s+|\s+$)/g, '').toLowerCase()));
  };
  const onSlugChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    setGenerateSlug(event.target.value.replace(/(^\s+|\s+$)/g, '').toLowerCase());
  };

  const onThumbnailSelected = (event: React.ChangeEvent<HTMLInputElement>) => {
    if (event.target.files && event.target.files[0]) {
      const i = event.target.files[0];
      setThumbnail(i);
      setThumbnailURL(URL.createObjectURL(i));
    }
  };
  const onSubmit = (data: any) => {
    const slug = generateSlug ? generateSlug : data.slug;
    let product: Product = {
      id: 0,
      name: data.name.replace(/(^\s+|\s+$)/g, ''),
      slug: slug.replace(/(^\s+|\s+$)/g, ''),
      description: data.description?.replace(/(^\s+|\s+$)/g, ''),
      shortDescription: data.shortDescription.replace(/(^\s+|\s+$)/g, ''),
      specification: data.specification.replace(/(^\s+|\s+$)/g, ''),
      sku: data.sku.replace(/(^\s+|\s+$)/g, ''),
      gtin: data.gtin.replace(/(^\s+|\s+$)/g, ''),
      metaKeyword: data.metaKeyword.replace(/(^\s+|\s+$)/g, ''),
      metaDescription: data.metaDescription?.replace(/(^\s+|\s+$)/g, ''),
    }
    if (!thumbnail) {
      alert("A thumbnail is required.")
    }
    else {
      if (id) {
        updateProduct(+id, product, thumbnail);
        location.replace("/catalog/products");
      }
    }
  }
  if (isLoading) return <p>Loading...</p>;
  if (!product) { return <p>No product</p>; }
  else {
    return (
      <>
        <div className='row mt-5'>
          <div className='col-md-8'>
            <h2>Update product: #{product.id}</h2>
            <form onSubmit={handleSubmit(onSubmit)}>
              <div className="mb-3">
                <label className='form-label' htmlFor="name">Name <span style={{ 'color': 'red' }}>*</span></label>
                <input
                  defaultValue={product.name}
                  {...register("name", { required: "Name is required", onChange: onNameChange })}
                  className={`form-control ${errors.name ? "border-danger" : ""}`}
                  type="text" id="name" name="name"
                />
                <p className='error-field'><>{errors.name?.message}</></p>
              </div>
              <div className="mb-3">
                <label className='form-label' htmlFor="slug">Slug <span style={{ 'color': 'red' }}>*</span></label>
                <input
                  value={generateSlug ? generateSlug : product.slug}
                  {...register("slug", { required: "Slug is required", onChange: onSlugChange })}
                  className={`form-control ${errors.slug ? "border-danger" : ""}`}
                  type="text" id="slug" name="slug"
                />
              </div>
              <div className="mb-3">
                <label className='form-label' htmlFor="short-description">Short Description <span style={{ 'color': 'red' }}>*</span></label>
                <input
                  defaultValue={product.shortDescription}
                  {...register("shortDescription", { required: "Short Description is required" })}
                  className={`form-control ${errors.shortDescription ? "border-danger" : ""}`}
                  type="text" id="short-description" name="shortDescription" />
              </div>
              <div className="mb-3">
                <label className='form-label' htmlFor="description">Description</label>
                <textarea defaultValue={product.description} className="form-control" id="description" name="description" />
              </div>
              <div className="mb-3">
                <label className='form-label' htmlFor="specification">Specification <span style={{ 'color': 'red' }}>*</span></label>
                <textarea
                  defaultValue={product.specification}
                  {...register("specification", { required: "Specification is required" })}
                  className={`form-control ${errors.specification ? "border-danger" : ""}`}
                  id="specification" name="specification" />
              </div>
              <div className="mb-3">
                <label className='form-label' htmlFor="sku">SKU <span style={{ 'color': 'red' }}>*</span></label>
                <input
                  defaultValue={product.sku}
                  {...register("sku", { required: "SKU is required" })}
                  className={`form-control ${errors.sku ? "border-danger" : ""}`}
                  type="text" id="sku" name="sku" />
              </div>
              <div className="mb-3">
                <label className='form-label' htmlFor="gtin">GTIN <span style={{ 'color': 'red' }}>*</span></label>
                <input
                  defaultValue={product.gtin}
                  {...register("gtin", { required: "GTIN is required" })}
                  className={`form-control ${errors.gtin ? "border-danger" : ""}`}
                  type="text" id="gtin" name="gtin" />
              </div>
              <div className="mb-3">
                <label className='form-label' htmlFor="meta-keyword">Meta Keyword <span style={{ 'color': 'red' }}>*</span></label>
                <input
                  defaultValue={product.metaKeyword}
                  {...register("metaKeyword", { required: "Meta Keyword is required" })}
                  className={`form-control ${errors.metaKeyword ? "border-danger" : ""}`}
                  type="text" id="meta-keyword" name="metaKeyword" />
              </div>
              <div className="mb-3">
                <label className='form-label' htmlFor="meta-description">Meta Description</label>
                <input defaultValue={product.descriptionMetaKeyword} type="text" className="form-control" id="meta-description" name="metaDescription" />
              </div>
              <div className='mb-3'>
                <label className='form-label' htmlFor="thumbnail">Thumbnail <span style={{ 'color': 'red' }}>*</span></label>
                <input className="form-control" type="file" name="thumbnail" onChange={onThumbnailSelected} />
                <img style={{ width: '150px' }} src={thumbnailURL ? thumbnailURL : product.thumbnailUrl} />
              </div>
              <button className="btn btn-primary" type="submit">Submit</button>
            </form>
          </div>
        </div>
      </>
    )
  }
}

export default ProductEdit