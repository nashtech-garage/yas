import type { NextPage } from "next";
import { useRouter } from "next/router";
import { useEffect, useState } from "react";
import { number } from "yup";
import { Product } from "../../../../modules/catalog/models/Product";
import { getProduct } from "../../../../modules/catalog/services/ProductService";

const ProductDetails: NextPage = () => {
  const router = useRouter();
  const { id } = router.query;
  const [product, setProduct] = useState<Product>();
  const [isLoading, setLoading] = useState(false);

  useEffect(() => {
    setLoading(true);
    getProduct(Number(id)).then((data) => {
      setProduct(data);
      setLoading(false);
    });
  }, []);

  if (isLoading) return <p>Loading...</p>;
  if (!product) return <p>No product</p>;
  return (
    <>
      <div className="row mt-5">
        <div className="col-md-8 text-center">
          <h2>Product details {id}</h2>
        </div>
      </div>

      <div className="d-flex flex-row mt-5 gap-5">
        <div className="d-flex flex-column gap-3">
          <img
            src={product.thumbnailMediaUrl}
            className="img-thumbnail"
            alt="..."
            style={{ width: "500px", height: "500px" }}
          />
          <span className="text-center fst-italic">
            {product.shortDescription}
          </span>
        </div>

        <div>
          <h2 className="mb-2">{product.name}</h2>
          <div className="mb-4 d-flex gap-3">
            <button type="button" className="btn btn-outline-primary">
              Brand: Kafka
            </button>
            <button type="button" className="btn btn-outline-success">
              Category: Zelo
            </button>
          </div>
          <p className="mb-4 text-muted">{product.description}</p>
          <p className="mb-4">{product.specification}</p>
          <div className="d-flex flex-column gap-1">
            <div
              className="alert alert-primary d-flex justify-content-between"
              role="alert"
            >
              {product.sku}
              <span className="badge bg-primary text-uppercase">sku</span>
            </div>
            <div
              className="alert alert-secondary d-flex justify-content-between"
              role="alert"
            >
              {product.gtin}
              <span className="badge bg-secondary text-uppercase">gtin</span>
            </div>
            <div
              className="alert alert-success d-flex justify-content-between"
              role="alert"
            >
              {product.slug}
              <span className="badge bg-success text-uppercase">slug</span>
            </div>
            <div
              className="alert alert-danger d-flex justify-content-between"
              role="alert"
            >
              {product.metaKeyword}
              <span className="badge bg-danger text-uppercase">
                metaKeyword
              </span>
            </div>
            <div
              className="alert alert-warning d-flex justify-content-between"
              role="alert"
            >
              {product.metaDescription}
              <span className="badge bg-warning text-uppercase">
                metaDescription
              </span>
            </div>
          </div>

          <div></div>
        </div>
      </div>
    </>
  );
};

export default ProductDetails;
