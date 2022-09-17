import type { NextPage } from "next";
import React, { useEffect, useState } from "react";
import { Product } from "../../../modules/catalog/models/Product";
import { createProduct } from "../../../modules/catalog/services/ProductService";
import { useForm, SubmitHandler } from "react-hook-form";
import { yupResolver } from "@hookform/resolvers/yup";
import * as yup from "yup";
import slugify from "slugify";
import { Category } from "../../../modules/catalog/models/Category";
import { Brand } from "../../../modules/catalog/models/Brand";
import { getCategories } from "../../../modules/catalog/services/CategoryService";
import { getBrands } from "../../../modules/catalog/services/BrandService";

const schema = yup
  .object({
    name: yup.string().required("Product name is required"),
    slug: yup.string().required("Slug is required"),
    description: yup.string().required("Description is required"),
    shortDescription: yup.string().required("Short description is required"),
    specification: yup.string().required("Specification is required"),
    price: yup
      .number()
      .typeError("Price must me a number")
      .default(0.0)
      .positive("Price must be positive number")
      .required("Product price is required"),
    // brand: yup.number().min(1, "Select Branch").required("Select Brand"),
  })
  .required();

const ProductCreate: NextPage = () => {
  const [thumbnailURL, setThumbnailURL] = useState<string>();
  const [productImageURL, setProductImageURL] = useState<string[]>();
  const [categoriesId, setCategoriesId] = useState<number[]>([]);
  const [selectedCategories, setSelectedCategories] = useState<string[]>([]);
  const [categories, setCategories] = useState<Category[]>([]);
  const [brands, setBrands] = useState<Brand[]>([]);

  const {
    register,
    handleSubmit,
    setValue,
    formState: { errors },
  } = useForm<Product>({ resolver: yupResolver(schema) });

  useEffect(() => {
    getCategories()
      .then((data) => {
        setCategories(data);
      })
      .catch((err) => console.log(err));

    getBrands()
      .then((data) => {
        setBrands(data);
      })
      .catch((err) => console.log(err));
  }, []);

  const onThumbnailSelected = (event: React.ChangeEvent<HTMLInputElement>) => {
    if (event.target.files && event.target.files[0]) {
      const i = event.target.files[0];
      setThumbnailURL(URL.createObjectURL(i));
    }
  };

  const onProductImageSelected = (
    event: React.ChangeEvent<HTMLInputElement>
  ) => {
    if (event.target.files) {
      const files = event.target.files;
      let length = files.length;

      let urls: string[] = [];

      for (let i = 0; i < length; i++) {
        const file = files[i];
        urls.push(URL.createObjectURL(file));
      }
      setProductImageURL([...urls]);
    }
  };

  const onSubmitForm: SubmitHandler<Product> = async (data) => {
    data.categoriesId = categoriesId;
    console.log(data)

    await createProduct(data).then((res) => {
      location.replace("/catalog/products");
    }).catch((err) => {
      alert("Cannot Create Product. Try Later!")
      console.log(err)
    })  
  };

  const onCategoryChange = (event: React.ChangeEvent<HTMLSelectElement>) => {
    let category = event.target.value;
    if (selectedCategories.indexOf(category) === -1) {
      let id = categories.find((item) => item.name === category)?.id;
      if (id) {
        setCategoriesId([...categoriesId, id]);
      }
      setSelectedCategories([category, ...selectedCategories]);
    }
  };

  return (
    <>
      <div className="row mt-5">
        <div className="col-md-8">
          <h2>Create product</h2>
          <form onSubmit={handleSubmit(onSubmitForm)}>
            <div className="mb-3">
              <label className="form-label" htmlFor="name">
                Name
              </label>
              <input
                className={`form-control ${errors.name ? "border-danger" : ""}`}
                {...register("name", {
                  onChange: (event) =>
                    setValue("slug", slugify(event.target.value)),
                })}
              />
              <sup className="text-danger fst-italic">
                {errors.name?.message}
              </sup>
            </div>
            <div className="mb-3">
              <label className="form-label" htmlFor="slug">
                Slug
              </label>
              <input
                className={`form-control ${
                  errors.slug ? "border-danger" : ""
                } `}
                id="slug"
                {...register("slug", {
                  onChange: (e) => setValue("slug", e.target.value),
                  onBlur: (e) => setValue("slug", slugify(e.target.value)),
                })}
              />
              <sup className="text-danger fst-italic">
                {errors.slug?.message}
              </sup>
            </div>
            <div className="mb-3">
              <label className="form-label" htmlFor="brand">
                Brand
              </label>
              <select
                className={`form-select ${errors.brand ? "border-danger" : ""}`}
                id="brand"
                {...register("brand")}
                defaultValue={0}
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
              <sup className="text-danger fst-italic">
                {errors.brand?.message}
              </sup>
            </div>

            <div className="mb-3">
              <label className="form-label" htmlFor="category">
                Category
              </label>
              <select
                className={`form-select ${
                  errors.categoriesId ? "border-danger" : ""
                }`}
                id="category"
                {...register("categoriesId")}
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
              <sup className="text-danger fst-italic">
                {errors.categoriesId?.message}
              </sup>
              <div className="d-flex flex-start mt-2">
                {selectedCategories.map((category, index) => (
                  <span
                    className="border border-primary rounded fst-italic px-2"
                    key={index}
                  >
                    {category}
                  </span>
                ))}
              </div>
            </div>

            <div className="mb-3">
              <label className="form-label" htmlFor="short-description">
                Short Description
              </label>
              <textarea
                className={`form-control ${
                  errors.shortDescription ? "border-danger" : ""
                }`}
                id="short-description"
                {...register("shortDescription")}
              />
              <sup className="text-danger fst-italic">
                {errors.shortDescription?.message}
              </sup>
            </div>
            <div className="mb-3">
              <label className="form-label" htmlFor="description">
                Description
              </label>
              <textarea
                className={`form-control ${
                  errors.description ? "border-danger" : ""
                }`}
                id="description"
                {...register("description")}
              />
              <sup className="text-danger fst-italic">
                {errors.description?.message}
              </sup>
            </div>
            <div className="mb-3">
              <label className="form-label" htmlFor="specification">
                Specification
              </label>
              <textarea
                className={`form-control ${
                  errors.specification ? "border-danger" : ""
                }`}
                id="specification"
                {...register("specification")}
              />
              <sup className="text-danger fst-italic">
                {errors.specification?.message}
              </sup>
            </div>
            <div className="mb-3">
              <label className="form-label" htmlFor="sku">
                SKU
              </label>
              <input className="form-control" id="sku" {...register("sku")} />
            </div>
            <div className="mb-3">
              <label className="form-label" htmlFor="gtin">
                GTIN
              </label>
              <input className="form-control" id="gtin" {...register("gtin")} />
            </div>
            <div className="mb-3">
              <label className="form-label" htmlFor="price">
                Price
              </label>
              <input
                className={`form-control ${
                  errors.price ? "border-danger" : ""
                }`}
                id="price"
                type="number"
                {...register("price")}
              />
              <sup className="text-danger fst-italic">
                {errors.price?.message}
              </sup>
            </div>
            <div className="d-flex justify-content-between">
              <div className="mb-3">
                <label
                  className="form-label me-3"
                  htmlFor="is-allowed-to-order"
                >
                  Is Allowed To Order
                </label>
                <input
                  id="is-allowed-to-order"
                  type="checkbox"
                  {...register("isAllowedToOrder")}
                />
              </div>

              <div className="mb-3">
                <label className="form-label me-3" htmlFor="is-published">
                  Is Published
                </label>
                <input
                  type="checkbox"
                  id="is-published"
                  {...register("isPublished")}
                />
              </div>
              <div className="mb-3">
                <label className="form-label me-3" htmlFor="is-featured">
                  Is Featured
                </label>
                <input
                  id="is-featured"
                  type="checkbox"
                  {...register("isFeatured")}
                />
              </div>
            </div>

            <div className="mb-3">
              <label className="form-label" htmlFor="thumbnail">
                Thumbnail
              </label>
              <input
                className={`form-control`}
                type="file"
                id="thumbnail"
                {...register("thumbnail", {onChange: onThumbnailSelected})}
              />

              <img style={{ width: "150px" }} src={thumbnailURL} />
            </div>
            <div className="mb-3">
              <label className="form-label" htmlFor="product-image">
                Product Image
              </label>
              <input
                className="form-control"
                type="file"
                id="product-image"
                {...register("productImages")}
                onChange={onProductImageSelected}
                multiple
              />
              {productImageURL?.map((productImageUrl, index) => (
                <img
                  style={{ width: "150px" }}
                  src={productImageUrl}
                  key={index}
                  alt="Image"
                />
              ))}
            </div>
            <button
              className="btn btn-primary"
              type="submit"
              disabled={thumbnailURL == null}
            >
              Submit
            </button>
          </form>
        </div>
      </div>
    </>
  );
};

export default ProductCreate;
