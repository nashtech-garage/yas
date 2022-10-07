import { GetServerSideProps } from "next";
import { Product } from "../../modules/catalog/models/Product";
import { getProduct } from "../../modules/catalog/services/ProductService";

type Props = {product: Product}

export const getServerSideProps: GetServerSideProps = async (context: any) => {
  const { slug } = context.query;
  let product = await getProduct(slug);
  return { props: { product }};
};

const ProductDetails = ({product} : Props) => {
  return (
    <>
      <div className="row justify-content-center">
        <div className="product-item col-4">
          <img
            src={product.thumbnailMediaUrl}
            className="img-thumbnail"
            alt="photo"
          />
          <span className="caption">
            {product.shortDescription}
          </span>
        </div>

        <div className="col-8">
          <div className="d-flex justify-content-between align-items-center">
            <h2 className="mb-2">{product.name}</h2>
            <span className="text-danger">
              <svg
                xmlns="http://www.w3.org/2000/svg"
                width="16"
                height="16"
                fill="currentColor"
                className="bi bi-bag-heart"
                viewBox="0 0 16 16"
              >
                <path
                  fillRule="evenodd"
                  d="M10.5 3.5a2.5 2.5 0 0 0-5 0V4h5v-.5Zm1 0V4H15v10a2 2 0 0 1-2 2H3a2 2 0 0 1-2-2V4h3.5v-.5a3.5 3.5 0 1 1 7 0ZM14 14V5H2v9a1 1 0 0 0 1 1h10a1 1 0 0 0 1-1ZM8 7.993c1.664-1.711 5.825 1.283 0 5.132-5.825-3.85-1.664-6.843 0-5.132Z"
                />
              </svg>
            </span>
          </div>
          <div className="mb-4 d-flex gap-3">
            <button type="button" className="btn btn-outline-primary">
              Brand: Sonic
            </button>
            <button type="button" className="btn btn-outline-success">
              Category: Zelo
            </button>
          </div>
          <p className="mb-4 text-muted">
            Description:{" "}
            {!product.description ? "No Description" : product.description}
          </p>
          <p className="mb-4 fw-bold fst-italic">
            Specification:{" "}
            {!product.specification ? "No Specification" : product.specification}
          </p>
          <div className="d-flex flex-column gap-1">
            <div
              className="alert alert-primary d-flex justify-content-between"
              role="alert"
            >
              {!product.sku ? "No Sku" : product.sku}
              <span className="badge bg-primary text-uppercase">sku</span>
            </div>
            <div
              className="alert alert-secondary d-flex justify-content-between"
              role="alert"
            >
              {!product.gtin ? "No Gtin" : product.gtin}
              <span className="badge bg-secondary text-uppercase">gtin</span>
            </div>
            <div
              className="alert alert-success d-flex justify-content-between"
              role="alert"
            >
              {!product.slug ? "No Slug" : product.slug}
              <span className="badge bg-success text-uppercase">slug</span>
            </div>
            <div
              className="alert alert-danger d-flex justify-content-between"
              role="alert"
            >
              {!product.metaKeyword ? "No Meta Keyword" : product.metaKeyword}
              <span className="badge bg-danger text-uppercase">
                metaKeyword
              </span>
            </div>
            <div
              className="alert alert-warning d-flex justify-content-between"
              role="alert"
            >
              {!product.metaDescription ? "No Meta Description" : product.metaDescription}
              <span className="badge bg-warning text-uppercase">
                metaDescription
              </span>
            </div>
            <div className="d-flex justify-content-between">
              <button type="button" className="btn btn-danger">
                {" "}
                <span>
                  <svg
                    xmlns="http://www.w3.org/2000/svg"
                    width="16"
                    height="16"
                    fill="currentColor"
                    className="bi bi-heart me-3"
                    viewBox="0 0 16 16"
                  >
                    <path d="m8 2.748-.717-.737C5.6.281 2.514.878 1.4 3.053c-.523 1.023-.641 2.5.314 4.385.92 1.815 2.834 3.989 6.286 6.357 3.452-2.368 5.365-4.542 6.286-6.357.955-1.886.838-3.362.314-4.385C13.486.878 10.4.28 8.717 2.01L8 2.748zM8 15C-7.333 4.868 3.279-3.04 7.824 1.143c.06.055.119.112.176.171a3.12 3.12 0 0 1 .176-.17C12.72-3.042 23.333 4.867 8 15z" />
                  </svg>
                </span>
                Add to favourite
              </button>
              <button type="button" className="btn btn-primary">
                {" "}
                <span>
                  <svg
                    xmlns="http://www.w3.org/2000/svg"
                    width="16"
                    height="16"
                    fill="currentColor"
                    className="bi bi-cart me-3"
                    viewBox="0 0 16 16"
                  >
                    <path d="M0 1.5A.5.5 0 0 1 .5 1H2a.5.5 0 0 1 .485.379L2.89 3H14.5a.5.5 0 0 1 .491.592l-1.5 8A.5.5 0 0 1 13 12H4a.5.5 0 0 1-.491-.408L2.01 3.607 1.61 2H.5a.5.5 0 0 1-.5-.5zM3.102 4l1.313 7h8.17l1.313-7H3.102zM5 12a2 2 0 1 0 0 4 2 2 0 0 0 0-4zm7 0a2 2 0 1 0 0 4 2 2 0 0 0 0-4zm-7 1a1 1 0 1 1 0 2 1 1 0 0 1 0-2zm7 0a1 1 0 1 1 0 2 1 1 0 0 1 0-2z" />
                  </svg>
                </span>
                Add to cart
              </button>
            </div>
          </div>
        </div>
      </div>
    </>
  );
};

export default ProductDetails;
