import { GetServerSideProps } from 'next';
import Head from 'next/head';
import { useEffect, useState } from 'react';
import { Table } from 'react-bootstrap';
import Tab from 'react-bootstrap/Tab';
import Tabs from 'react-bootstrap/Tabs';
import { toast, ToastContainer } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import StarRatings from 'react-star-ratings';
import BreadcrumbComponent from '../../common/components/BreadcrumbComponent';
import { ProductImageGallery } from '../../common/components/ProductImageGallery';
import { BreadcrumbModel } from '../../modules/breadcrumb/model/BreadcrumbModel';
import { DetailHeader, ProductDetails } from '../../modules/catalog/components';
import { ProductDetail } from '../../modules/catalog/models/ProductDetail';
import { ProductOptionValueGet } from '../../modules/catalog/models/ProductOptionValueGet';
import { ProductVariations } from '../../modules/catalog/models/ProductVariations';
import { Rating } from '../../modules/catalog/models/Rating';
import { RatingPost } from '../../modules/catalog/models/RatingPost';
import {
  getProductDetail,
  getProductVariations,
} from '../../modules/catalog/services/ProductService';
import { getRatingsByProductId, createRating } from '../../modules/catalog/services/RatingService';

import RatingList from '../../modules/catalog/components/RatingList';
import PostRating from '../../modules/catalog/components/PostRating';

type Props = {
  product: ProductDetail;
  productVariations?: ProductVariations[];
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

  return { props: { product, productVariations } };
};

const ProductDetailsPage = ({ product, productVariations }: Props) => {
  const [pageNo, setPageNo] = useState<number>(0);
  const [ratingList, setRatingList] = useState<Rating[]>();
  const [totalPages, setTotalPages] = useState<number>(0);
  const [totalElements, setTotalElements] = useState<number>(0);
  const pageSize = 5;

  const [ratingStar, setRatingStar] = useState<number>(5);
  const [contentRating, setContentRating] = useState<string>('');
  const [isPost, setIsPost] = useState<boolean>(false);

  useEffect(() => {
    getRatingsByProductId(product.id, pageNo, pageSize).then((res) => {
      setRatingList(res.ratingList);
      setTotalPages(res.totalPages);
      setTotalElements(res.totalElements);
    });
  }, [pageNo, pageSize, product.id, isPost]);

  const handlePageChange = ({ selected }: any) => {
    setPageNo(selected);
  };
  const handleChangeRating = (ratingStar: number) => {
    setRatingStar(ratingStar);
  };

  const handleCreateRating = () => {
    const ratingPost: RatingPost = {
      content: contentRating,
      star: ratingStar,
      productId: product.id,
    };
    createRating(ratingPost)
      .then(() => {
        setContentRating('');
        setIsPost(!isPost);
        toast.success('Post a review succesfully', {
          position: 'top-right',
          autoClose: 1000,
          closeOnClick: true,
          pauseOnHover: false,
          theme: 'colored',
        });
      })
      .catch((err) => {
        if (err == 403)
          toast.error('Please login to post a review', {
            position: 'top-right',
            autoClose: 2000,
            closeOnClick: true,
            pauseOnHover: false,
            theme: 'colored',
          });
        else {
          toast.error('Some thing went wrong. Try again after a', {
            position: 'top-right',
            autoClose: 1000,
            closeOnClick: true,
            pauseOnHover: false,
            theme: 'colored',
          });
        }
      });
  };

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

  return (
    <>
      <Head>
        <title>{product.name}</title>
      </Head>
      <ToastContainer style={{ marginTop: '70px' }} />
      <BreadcrumbComponent props={crumb} />

      <DetailHeader
        productName={product.name}
        averageStar={product.averageStar}
        ratingCount={totalElements}
      />

      <div className="row justify-content-center">
        <div className="col-6">
          <ProductImageGallery listImages={product.productImageMediaUrls} />
        </div>

        <div className="col-6">
          <ProductDetails product={product} productVariations={productVariations} />
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

      {/* specification  and Rating */}
      <Tabs defaultActiveKey="Specification" id="product-detail-tab" className="mb-3 " fill>
        <Tab eventKey="Specification" title="Specification" style={{ minHeight: '200px' }}>
          <div className="tabs"> {product.specification}</div>
        </Tab>
        <Tab eventKey="Reviews" title={`Reviews (${totalElements})`} style={{ minHeight: '200px' }}>
          <div>
            <div
              style={{
                borderBottom: '1px solid lightgray',
                marginBottom: 30,
              }}
            >
              <PostRating
                ratingStar={ratingStar}
                handleChangeRating={handleChangeRating}
                contentRating={contentRating}
                setContentRating={setContentRating}
                handleCreateRating={handleCreateRating}
              />
            </div>
            <div>
              <RatingList
                ratingList={ratingList ? ratingList : null}
                pageNo={pageNo}
                totalElements={totalElements}
                totalPages={totalPages}
                handlePageChange={handlePageChange}
              />
            </div>
          </div>
        </Tab>
      </Tabs>
    </>
  );
};

export default ProductDetailsPage;
