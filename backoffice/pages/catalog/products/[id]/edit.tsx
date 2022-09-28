import type { NextPage } from 'next'
import { useRouter } from 'next/router'
import { useEffect, useState } from 'react'
import { Product } from '../../../../modules/catalog/models/Product'
import { getProduct, updateProduct } from '../../../../modules/catalog/services/ProductService'
import { useForm } from "react-hook-form";
import slugify from "slugify";
import { ProductPut } from '../../../../modules/catalog/models/ProductPut'
import { Category } from "../../../../modules/catalog/models/Category";
import { Brand } from "../../../../modules/catalog/models/Brand";
import { getCategories } from '../../../../modules/catalog/services/CategoryService'
import { getBrands } from '../../../../modules/catalog/services/BrandService'
const ProductEdit: NextPage = () => {
  //Get ID
  const router = useRouter()
  const { id } = router.query;
  //Variables
  const [thumbnail, setThumbnail] = useState<File>();
  const [thumbnailURL, setThumbnailURL] = useState<string>();
  const [generateSlug, setGenerateSlug] = useState<string>();
  const [productImages, setProductImages] = useState<FileList>();
  const [productImageMediaUrls, setproductImageMediaUrls] = useState<string[]>();
  const [categoryIds, setCategoryIds] = useState<number[]>([]);
  const [selectedCategories, setSelectedCategories] = useState<string[]>([]);
  const [categories, setCategories] = useState<Category[]>([]);
  const [brands, setBrands] = useState<Brand[]>([]);
  //Get product detail
  const [product, setProduct] = useState<Product>();
  const [isLoading, setLoading] = useState(false);
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
    getCategories().then((data) => {
      setCategories(data);
    });
    getBrands().then((data) => {
      setBrands(data);
    });
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

  const onProductImageSelected = (
    event: React.ChangeEvent<HTMLInputElement>
  ) => {
    if (event.target.files) {
      const files = event.target.files;
      setProductImages(files);
      let length = files.length;

      let urls: string[] = [];

      for (let i = 0; i < length; i++) {
        const file = files[i];
        urls.push(URL.createObjectURL(file));
      }
      setproductImageMediaUrls([...urls]);
    }
  };

  const onCategoryChange = (event: React.ChangeEvent<HTMLSelectElement>) => {
    let category = event.target.value;
    if (selectedCategories.indexOf(category) === -1) {
      let id = categories.find((item) => item.name === category)?.id;
      if (id) {
        setCategoryIds([...categoryIds, id]);
      }
      setSelectedCategories([category, ...selectedCategories]);
    }
  };

  const onSubmit = (data: any) => {
    const slug = generateSlug ? generateSlug : data.slug;
    let defaultCategoryIds: number[] = [];
    if(product){
      Array.from(product.categories).forEach((category) => {
        defaultCategoryIds = [...defaultCategoryIds, category.id]
      })
    }
    let productPut: ProductPut = {
      name: data.name.replace(/(^\s+|\s+$)/g, ''),
      slug: slug.replace(/(^\s+|\s+$)/g, ''),
      price: data.price,
      isAllowedToOrder: data.isAllowedToOrder,
      isPublished: data.isPublished,
      isFeatured: data.isFeatured,
      description: data.description?.replace(/(^\s+|\s+$)/g, ''),
      shortDescription: data.shortDescription.replace(/(^\s+|\s+$)/g, ''),
      specification: data.specification.replace(/(^\s+|\s+$)/g, ''),
      sku: data.sku.replace(/(^\s+|\s+$)/g, ''),
      gtin: data.gtin.replace(/(^\s+|\s+$)/g, ''),
      metaKeyword: data.metaKeyword.replace(/(^\s+|\s+$)/g, ''),
      metaDescription: data.metaDescription?.replace(/(^\s+|\s+$)/g, ''),
      brandId: data.brandId,
      categoryIds: categoryIds.length > 0 ? categoryIds : defaultCategoryIds
    }
    if (id) {
      if (thumbnail && productImages) {
        updateProduct(+id, productPut, thumbnail, productImages).then((res) => {
          if(res === 204){
          location.replace("/catalog/products");
          }
          else if(res === 400){
            alert("Please fill out again! Bad Request!");
          }
        }).catch((error) => {
          alert("Cannot update product. Try later!");
        })
      }
      else {
        alert("Thumbnail and Product Images is required!");
      }
    }
    else {
      alert("Please try later!");
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
                <p className='error-field'><>{errors.slug?.message}</></p>
              </div>
              <div className="mb-3">
              <label className="form-label" htmlFor="brand">
                Brand <span style={{ 'color': 'red' }}>*</span>
              </label>
              <select
                className={`form-select ${
                  errors.brandId ? "border-danger" : ""
                }`}
                id="brand"
                {...register("brandId")}
                defaultValue={product.brandId}
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
              <p className='error-field'><>{errors.brandId?.message}</></p>
            </div>

            <div className="mb-3">
              <label className="form-label" htmlFor="category">
                Category <span style={{ 'color': 'red' }}>*</span>
              </label>
              <select
                className={`form-select ${
                  errors.categoryIds ? "border-danger" : ""
                }`}
                id="category"
                {...register("categoryIds")}
                onChange={onCategoryChange}
                defaultValue={0}
              >
                <option disabled hidden value={0}>
                  Select Category
                </option>
                {Array.from(categories).map((category) => (
                  <option value={category?.name} key={category?.id}>
                    {category?.name}
                  </option>
                ))}
              </select>
              <p className='error-field'><>{errors.categoryIds?.message}</></p>
              <div className="d-flex flex-start mt-2">
                {selectedCategories.length > 0 ? selectedCategories.map((category, index) => (
                  <span
                    className="border border-primary rounded fst-italic px-2"
                    key={index}
                  >
                    {category}
                  </span>
                )) : (
                  product.categories && product.categories.map((category, index) => (
                    <span
                      className="border border-primary rounded fst-italic px-2"
                      key={index}
                    >
                      {category.name}
                    </span>
                  ))
                )}
              </div>
            </div>
              <div className="mb-3">
                <label className='form-label' htmlFor="short-description">Short Description <span style={{ 'color': 'red' }}>*</span></label>
                <input
                  defaultValue={product.shortDescription}
                  {...register("shortDescription", { required: "Short Description is required" })}
                  className={`form-control ${errors.shortDescription ? "border-danger" : ""}`}
                  type="text" id="short-description" name="shortDescription" />
                <p className='error-field'><>{errors.shortDescription?.message}</></p>
              </div>
              <div className="mb-3">
                <label className='form-label' htmlFor="description">Description</label>
                <textarea
                  defaultValue={product.description}
                  {...register("description")}
                  className="form-control"
                  id="description" name="description" />
                <p className='error-field'><>{errors.description?.message}</></p>
              </div>
              <div className="mb-3">
                <label className='form-label' htmlFor="specification">Specification <span style={{ 'color': 'red' }}>*</span></label>
                <textarea
                  defaultValue={product.specification}
                  {...register("specification", { required: "Specification is required" })}
                  className={`form-control ${errors.specification ? "border-danger" : ""}`}
                  id="specification" name="specification" />
                <p className='error-field'><>{errors.specification?.message}</></p>
              </div>
              <div className="mb-3">
                <label className='form-label' htmlFor="sku">SKU <span style={{ 'color': 'red' }}>*</span></label>
                <input
                  defaultValue={product.sku}
                  {...register("sku", { required: "SKU is required" })}
                  className={`form-control ${errors.sku ? "border-danger" : ""}`}
                  type="text" id="sku" name="sku" />
                <p className='error-field'><>{errors.sku?.message}</></p>
              </div>
              <div className="mb-3">
                <label className='form-label' htmlFor="gtin">GTIN <span style={{ 'color': 'red' }}>*</span></label>
                <input
                  defaultValue={product.gtin}
                  {...register("gtin", { required: "GTIN is required" })}
                  className={`form-control ${errors.gtin ? "border-danger" : ""}`}
                  type="text" id="gtin" name="gtin" />
                <p className='error-field'><>{errors.gtin?.message}</></p>
              </div>
              <div className="mb-3">
                <label className="form-label" htmlFor="price">Price <span style={{ 'color': 'red' }}>*</span></label>
                <input
                  defaultValue={product.price}
                  {...register("price", { required: "Price is required", min: 0 })}
                  className={`form-control ${errors.price ? "border-danger" : ""}`}
                  type="number" id="price"
                />
                {errors.price && errors.price.type === "min" ?
                  (
                    <p className='error-field'>Price must be at least 0</p>
                  ) :
                  (
                    <p className='error-field'><>{errors.price?.message}</></p>
                  )
                }
              </div>
              <div className="d-flex justify-content-between">
                <div className="mb-3">
                  <label className="form-label me-3" htmlFor="is-allowed-to-order">Is Allowed To Order</label>
                  <input
                    defaultChecked={product.isAllowedToOrder}
                    {...register("isAllowedToOrder")}
                    type="checkbox" id="is-allowed-to-order"
                  />
                </div>

                <div className="mb-3">
                  <label className="form-label me-3" htmlFor="is-published">Is Published</label>
                  <input
                    defaultChecked={product.isPublished}
                    {...register("isPublished")}
                    type="checkbox" id="is-published"
                  />
                </div>
                <div className="mb-3">
                  <label className="form-label me-3" htmlFor="is-featured">Is Featured</label>
                  <input
                    defaultChecked={product.isFeatured}
                    {...register("isFeatured")}
                    type="checkbox" id="is-featured"
                  />
                </div>
              </div>
              <div className="mb-3">
                <label className='form-label' htmlFor="meta-keyword">Meta Keyword <span style={{ 'color': 'red' }}>*</span></label>
                <input
                  defaultValue={product.metaKeyword}
                  {...register("metaKeyword", { required: "Meta Keyword is required" })}
                  className={`form-control ${errors.metaKeyword ? "border-danger" : ""}`}
                  type="text" id="meta-keyword" name="metaKeyword" />
                <p className='error-field'><>{errors.metaKeyword?.message}</></p>
              </div>
              <div className="mb-3">
                <label className='form-label' htmlFor="meta-description">Meta Description</label>
                <input
                  defaultValue={product.metaDescription}
                  {...register("metaDescription")}
                  type="text" className="form-control" id="meta-description" name="metaDescription" />
                <p className='error-field'><>{errors.metaDescription?.message}</></p>
              </div>
              <div className='mb-3'>
                <label className='form-label' htmlFor="thumbnail">Thumbnail <span style={{ 'color': 'red' }}>*</span></label>
                <input className="form-control" type="file" name="thumbnail" onChange={onThumbnailSelected} />
                <img style={{ width: '150px' }} src={thumbnailURL ? thumbnailURL : product.thumbnailMediaUrl} />
              </div>
              <div className="mb-3">
                <label className="form-label" htmlFor="product-image">Product Images <span style={{ 'color': 'red' }}>*</span>
                </label>
                <input
                  className="form-control"
                  type="file"
                  id="product-images"
                  onChange={onProductImageSelected}
                  multiple
                />
                {productImageMediaUrls ? productImageMediaUrls.map((imageUrl, index) => (
                    <img style={{ width: "150px" }} src={imageUrl} key={index} alt="Product Image"/>
                )) : (
                  product.productImageMediaUrls && product.productImageMediaUrls.map((imageUrl, index) => (
                    <img style={{ width: "150px" }} src={imageUrl} key={index} alt="Product Image"/>
                  ))
                )}
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
