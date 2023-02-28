import { GetServerSideProps } from 'next';
import Head from 'next/head';
import Link from 'next/link';
import { useEffect, useState } from 'react';
import { Carousel, Modal, Table } from 'react-bootstrap';
import Figure from 'react-bootstrap/Figure';
import { toast, ToastContainer } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import BreadcrumbComponent from '../../common/components/BreadcrumbComponent';
import { BreadcrumbModel } from '../../modules/breadcrumb/model/BreadcrumbModel';
import { AddToCartModel } from '../../modules/cart/models/AddToCartModel';
import { addToCart } from '../../modules/cart/services/CartService';
import { ProductDetail } from '../../modules/catalog/models/ProductDetail';
import { ProductOptionValueGet } from '../../modules/catalog/models/ProductOptionValueGet';
import { ProductVariations } from '../../modules/catalog/models/ProductVariations';
import Tab from 'react-bootstrap/Tab';
import Tabs from 'react-bootstrap/Tabs';
import {
  getProductDetail,
  getProductVariations,
} from '../../modules/catalog/services/ProductService';
import { formatPrice } from '../../utils/formatPrice';
import { Rating } from '../../modules/catalog/models/Rating';
import { getRatingsByProductId } from '../../modules/catalog/services/RatingService';

type Props = {
  product: ProductDetail;
  productVariations?: ProductVariations[];
  ratingOject: {};
};

export const getServerSideProps: GetServerSideProps = async (context: any) => {
  const { slug } = context.query;
  let product = await getProductDetail(slug);

  let productOptionValue: ProductOptionValueGet[] = (await getProductVariations(product.id)) || [];

  const productVariations: ProductVariations[] = [];

  if (Array.isArray(productOptionValue)) {
    for (const option of productOptionValue) {
      let index = productVariations.findIndex(
        (productVariation) => productVariation.name === option.productOptionName
      );
      if (index > -1) {
        productVariations.at(index)?.value.push(option.productOptionValue);
      } else {
        let newVariation: ProductVariations = {
          name: option.productOptionName,
          value: [option.productOptionValue],
        };

        productVariations.push(newVariation);
      }
    }
  }

  //getRating
  let ratingObject = await getRatingsByProductId(product.id);

  return { props: { product, productVariations, ratingObject } };
};

const handleAddToCart = async (event: any) => {
  event.preventDefault();
  let addToCartModel: AddToCartModel[] = [
    {
      productId: event.target.productId.value,
      quantity: event.target.quantity.value,
    },
  ];
  await addToCart(addToCartModel)
    .then(() => {
      toast.success(' Add to cart success', {
        position: 'top-right',
        autoClose: 1000,
        closeOnClick: true,
        pauseOnHover: false,
        theme: 'colored',
      });
    })
    .catch(() => {
      toast.error('Add to cart failed. Try again', {
        position: 'top-right',
        autoClose: 1000,
        closeOnClick: true,
        pauseOnHover: false,
        theme: 'colored',
      });
    });
};

const ProductDetails = ({ product, productVariations, ratingObject }: Props) => {
  const crumb: BreadcrumbModel[] = [
    {
      pageName: 'Home',
      url: '/',
    },
    {
      pageName: product.productCategories.toString(),
      url: '#',
    },
    {
      pageName: product.name,
      url: '',
    },
  ];
  useEffect(() => {
    console.log(ratingObject.ratingList);
  }, []);

  const [currentShowUrl, setCurrentShowUrl] = useState<string>(product.thumbnailMediaUrl);
  const [modalShow, setModalShow] = useState<boolean>(false);
  const [index, setIndex] = useState<number>(0);
  return (
    <>
      <Head>
        <title>{product.name}</title>
      </Head>
      <ToastContainer style={{ marginTop: '70px' }} />
      <BreadcrumbComponent props={crumb} />
      <div className="row justify-content-center">
        <div className="product-item col-5">
          <Figure className="main-thumbnail">
            <Figure.Image
              width={500}
              height={500}
              alt="photo"
              src={currentShowUrl}
              onClick={() => setModalShow(true)}
            ></Figure.Image>
          </Figure>
          <div className="product-images">
            <Figure className="images">
              {(product.productImageMediaUrls || []).map((item, index) => (
                <Figure.Image
                  width={100}
                  height={100}
                  alt="photo"
                  src={item}
                  key={index}
                  onClick={() => setCurrentShowUrl(item)}
                ></Figure.Image>
              ))}
            </Figure>
          </div>
          <span className="caption">{product.shortDescription}</span>
        </div>

        <div className="col-7" style={{ marginTop: '20px' }}>
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
          <Link href="#" style={{ color: '#89b5fa', fontSize: '20px' }}>
            {product.brandName}
          </Link>
          <hr />
          {/* product variation */}

          {(productVariations || []).map((productVariation, index) => (
            <div key={index}>
              <h5>{productVariation.name}</h5>
              {(productVariation.value || []).map((productVariationValue, index) => (
                <button key={index} className="btn btn-outline-primary me-3">
                  {productVariationValue}
                </button>
              ))}
            </div>
          ))}
          <h4 style={{ color: 'red' }}>{formatPrice(product.price)}</h4>

          <div id="full-stars-example">
            <div className="rating-group">
              <label aria-label="1 star" className="rating__label" htmlFor="rating-1">
                <i className="rating__icon rating__icon--star fa fa-star"></i>
              </label>
              <input className="rating__input" name="rating" id="rating-1" value="1" type="radio" />
              <label aria-label="2 stars" className="rating__label" htmlFor="rating-2">
                <i className="rating__icon rating__icon--star fa fa-star"></i>
              </label>
              <input className="rating__input" name="rating" id="rating-2" value="2" type="radio" />
              <label aria-label="3 stars" className="rating__label" htmlFor="rating-3">
                <i className="rating__icon rating__icon--star fa fa-star"></i>
              </label>
              <input className="rating__input" name="rating" id="rating-3" value="3" type="radio" />
              <label aria-label="4 stars" className="rating__label" htmlFor="rating-4">
                <i className="rating__icon rating__icon--star fa fa-star"></i>
              </label>
              <input className="rating__input" name="rating" id="rating-4" value="4" type="radio" />
              <label aria-label="5 stars" className="rating__label" htmlFor="rating-5">
                <i className="rating__icon rating__icon--star fa fa-star"></i>
              </label>
              <input className="rating__input" name="rating" id="rating-5" value="5" type="radio" />
            </div>
          </div>
          <p>{product.description}</p>
          <div className="d-flex flex-column gap-1">
            <div className="d-flex justify-content-between">
              <form onSubmit={handleAddToCart}>
                <input type={'hidden'} name={'productId'} value={product.id} />
                <input type={'hidden'} name={'quantity'} value={1} />
                <button
                  type="submit"
                  className="btn btn-primary"
                  disabled={product.isAllowedToOrder ? false : true}
                >
                  {' '}
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
              </form>
            </div>
          </div>
        </div>
      </div>
      <div className="container" style={{ marginTop: '70px' }}>
        <Table>
          {product.productAttributeGroups.map((attributeGroup) => (
            <>
              <thead key={attributeGroup.name}>
                <tr className="product_detail_tr">
                  <th className="product_detail_th">{attributeGroup.name} :</th>
                  <th></th>
                </tr>
              </thead>

              <tbody>
                {attributeGroup.productAttributeValues.map((productAttribute) => (
                  <tr key={productAttribute.name}>
                    <th className="product_attribute_name_th">{productAttribute.name}</th>
                    <th className="product_attribute_value_th">{productAttribute.value}</th>
                  </tr>
                ))}
              </tbody>
            </>
          ))}
        </Table>
      </div>

      {/* Show modal image */}
      <Modal
        show={modalShow}
        onHide={() => setModalShow(false)}
        size="lg"
        aria-labelledby="contained-modal-title-vcenter"
        centered
      >
        <Modal.Header closeButton>
          <Modal.Title id="contained-modal-title-vcenter">{product.name}</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <Carousel activeIndex={index} onSelect={(selectedIndex, e) => setIndex(selectedIndex)}>
            {(product.productImageMediaUrls || []).map((item, index) => (
              <Carousel.Item key={index}>
                <img src={item} alt="" className="d-block w-100" />
              </Carousel.Item>
            ))}
          </Carousel>
        </Modal.Body>
      </Modal>

      {/* Description and Rating */}
      <Tabs defaultActiveKey="Specification" id="product-detail-tab" className="mb-3 " justify>
        <Tab eventKey="Specification" title="Specification" style={{ minHeight: '200px' }}>
          <div className="tabs"> {product.specification}</div>
        </Tab>
        <Tab eventKey="Reviews" title="Reviews" style={{ minHeight: '200px' }}>
          <div>
            <p>Anonymous: </p>
            {Array.isArray(ratingObject.ratingList) &&
              ratingObject.ratingList?.map((rating: Rating) => (
                <p key={rating.id}>{rating.content}</p>
              ))}
          </div>
        </Tab>
      </Tabs>
    </>
  );
};

export default ProductDetails;
