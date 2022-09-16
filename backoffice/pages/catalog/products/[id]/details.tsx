import type { NextPage } from "next";
import { useRouter } from "next/router";

const ProductDetails: NextPage = () => {
  const router = useRouter();
  const { id } = router.query;

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
            src="https://mdbootstrap.com/img/new/standard/city/041.webp"
            className="img-thumbnail"
            alt="..."
            style={{ width: "600px", height: "600px" }}
          />
          <span className="text-center fst-italic">hkdfskfkdslf</span>
        </div>

        <div>
          <h2 className="mb-2">hfkdfkdsnk</h2>
          <div className="mb-4 d-flex gap-3">
            <button type="button" className="btn btn-outline-primary">
             Brand: djfj
            </button>
            <button type="button" className="btn btn-outline-success">Category: djfj</button>
              
          </div>
          <p className="mb-4 text-muted">
            Lorem ipsum dolor sit, amet consectetur adipisicing elit. Reiciendis
            vero voluptas voluptates. Expedita magnam veniam ipsa nesciunt hic,
            eaque commodi impedit tenetur, eveniet alias quia, laudantium
            explicabo. Rem, officiis ipsam.
          </p>
          <p className="mb-4">specification</p>
          <div className="d-flex flex-column gap-1">
            <div
              className="alert alert-primary d-flex justify-content-between"
              role="alert"
            >
              A simple primary alert—check it out!
              <span className="badge bg-primary text-uppercase">sku</span>
            </div>
            <div
              className="alert alert-secondary d-flex justify-content-between"
              role="alert"
            >
              A simple secondary alert—check it out!
              <span className="badge bg-secondary text-uppercase">gtin</span>
            </div>
            <div
              className="alert alert-success d-flex justify-content-between"
              role="alert"
            >
              A simple success alert—check it out!
              <span className="badge bg-success text-uppercase">slug</span>
            </div>
            <div
              className="alert alert-danger d-flex justify-content-between"
              role="alert"
            >
              A simple success alert—check it out!
              <span className="badge bg-danger text-uppercase">
                metaKeyword
              </span>
            </div>
            <div
              className="alert alert-warning d-flex justify-content-between"
              role="alert"
            >
              A simple success alert—check it out!
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
