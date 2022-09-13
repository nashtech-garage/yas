import type { NextPage } from "next";
import React, { useState } from "react";
import { Product } from "../../../modules/catalog/models/Product";
import { createProduct } from "../../../modules/catalog/services/ProductService";
import { useForm, SubmitHandler } from "react-hook-form";
import { yupResolver } from "@hookform/resolvers/yup";
import * as yup from "yup";

/**
 * TODO: Get all branch/if not, create new branch
 * TODO: Get all category/if not, create new category
 * TODO: handle enter multi specification 
 */

/**
 * !ERROR: validate file upload not work correctly
 */

const schema = yup
  .object({
    name: yup.string().required("Product name is required"),
    slug: yup.string().required(),
    description: yup.string().required("Description is required"),
    shortDescription: yup.string().required("Description is required"),
    specification: yup.string().required("Fill at least one specification"),
    price: yup
      .number()
      .typeError("Price must me a number")
      .default(0.0)
      .positive("Price must be positive number")
      .required("Product price is required"), 
    thumbnail: yup.mixed().test("required","Thumbnail is required", (value) => {
      return  value && value.size;
    }),
    brand: yup.string().required("Product brand is required"),
    category: yup.array().required("Fill at least one category"),
  })
  .required();

const ProductCreate: NextPage = () => {
  const [thumbnailURL, setThumbnailURL] = useState<string>();
  const [productImageURL, setProductImageURL] = useState<string[]>();
  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<Product>({ resolver: yupResolver(schema) });

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

  const onSubmit: SubmitHandler<Product> = (data) => {
    console.log(data);
  };
  return (
    <>
      <div className="row mt-5">
        <div className="col-md-8">
          <h2>Create product</h2>
          <form onSubmit={handleSubmit(onSubmit)}>
            <div className="mb-3">
              <label className="form-label" htmlFor="name">
                Name
              </label>
              <input
                className={`form-control ${errors.name ? "border-danger" : ""}`}
                {...register("name")}
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
                className={`form-control ${errors.slug ? "border-danger" : ""}`}
                id="slug"
                {...register("slug")}
              />
              <sup className="text-danger fst-italic">
                {errors.slug?.message}
              </sup>
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
              <input
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
                Sku
              </label>
              <input className="form-control" id="sku" {...register("sku")} />
            </div>
            <div className="mb-3">
              <label className="form-label" htmlFor="gtin">
                Gtin
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
                className={`form-control ${errors.thumbnail ? "border-danger" : ""}`}
                type="file"
                id="thumbnail"
                {...register("thumbnail")}
                onChange={onThumbnailSelected}
              />
              <sup className="text-danger fst-italic">
                {errors.thumbnail?.message}
              </sup>

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
            <button className="btn btn-primary" type="submit">
              Submit
            </button>
          </form>
        </div>
      </div>
    </>
  );
};

export default ProductCreate;
