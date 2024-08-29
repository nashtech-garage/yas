import { GetServerSideProps, GetServerSidePropsContext } from 'next';
import Head from 'next/head';
import { useEffect, useState } from 'react';
import { Container, Table } from 'react-bootstrap';
import Tab from 'react-bootstrap/Tab';
import Tabs from 'react-bootstrap/Tabs';

import BreadcrumbComponent from '../../common/components/BreadcrumbComponent';

import { BreadcrumbModel } from '../../modules/breadcrumb/model/BreadcrumbModel';
import { ProductDetails, RelatedProduct } from '../../modules/catalog/components';
import { ProductDetail } from '../../modules/catalog/models/ProductDetail';
import { ProductOptions } from '../../modules/catalog/models/ProductOptions';
import { ProductVariation } from '../../modules/catalog/models/ProductVariation';
import {
  getProductDetail,
  getProductOptionValues,
  getProductVariationsByParentId,
} from '../../modules/catalog/services/ProductService';
import { toastError, toastSuccess } from '../../modules/catalog/services/ToastService';
import { PostRatingForm, RatingList } from '../../modules/rating/components';
import { Rating } from '../../modules/rating/models/Rating';
import { RatingPost } from '../../modules/rating/models/RatingPost';
import {
  createRating,
  getAverageStarByProductId,
  getRatingsByProductId,
} from '../../modules/rating/services/RatingService';

type Props = {
  product: ProductDetail;
  productOptions?: ProductOptions[];
  productVariations?: ProductVariation[];
  pvid: string | null;
};

export const getServerSideProps: GetServerSideProps = async (
  context: GetServerSidePropsContext
) => {
  const { slug, pvid } = context.query;

  // fetch product by slug
  const product = await getProductDetail(slug as string);
  if (!product.id) return { notFound: true };

  const productOptions: ProductOptions[] = [];
  let productVariations: ProductVariation[] = [];

  if (product.hasOptions) {
    // fetch product options
    try {
      const productOptionValues = await getProductOptionValues(product.id);

      for (const optionValue of productOptionValues) {
        const index = productOptions.findIndex(
          (productOption) => productOption.name === optionValue.productOptionName
        );
        if (index > -1) {
          productOptions.at(index)?.value.push(optionValue.productOptionValue);
        } else {
          const newProductOption: ProductOptions = {
            id: optionValue.productOptionId,
            name: optionValue.productOptionName,
            value: [optionValue.productOptionValue],
          };

          productOptions.push(newProductOption);
        }
      }
    } catch (error) {
      console.error(error);
    }

    // fetch product variations
    try {
      productVariations = await getProductVariationsByParentId(product.id);
    } catch (error) {
      console.error(error);
    }
  }

  return {
    props: {
      product,
      productOptions,
      productVariations,
      pvid: pvid !== undefined ? (pvid as string) : null,
    },
  };
};

const ProductDetailsPage = ({ product, productOptions, productVariations, pvid }: Props) => {
  const [pageNo, setPageNo] = useState<number>(0);
  const [ratingList, setRatingList] = useState<Rating[]>();
  const [totalPages, setTotalPages] = useState<number>(0);
  const [totalElements, setTotalElements] = useState<number>(0);
  const pageSize = 5;

  const [ratingStar, setRatingStar] = useState<number>(5);
  const [contentRating, setContentRating] = useState<string>('');
  const [isPost, setIsPost] = useState<boolean>(false);

  const [averageStar, setAverageStar] = useState<number>(0);

  useEffect(() => {
    getAverageStarByProductId(product.id).then((res) => {
      setAverageStar(res);
    });
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

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
      productName: product.name,
    };
    createRating(ratingPost)
      .then(() => {
        setContentRating('');
        setIsPost(!isPost);
        toastSuccess('Post a review succesfully');
      })
      .catch((err) => {
        if (err == 403)
          toastError(
            'You are not authorized to rate this product since you are not logged in or have not purchased it'
          );
        else if (err == 409) {
          toastError('You have rated this product before');
        } else {
          toastError('Some thing went wrong. Try again after a few seconds');
        }
      });
  };

  const category: BreadcrumbModel = {
    pageName: product.productCategories.toString(),
    url: '#',
  };

  const crumb: BreadcrumbModel[] = [
    {
      pageName: 'Home',
      url: '/',
    },
    {
      pageName: product.name,
      url: '',
    },
  ];

  if (product.productCategories.toString()) {
    crumb.splice(1, 0, category);
  }

  return (
    <Container>
      <Head>
        <title>{product.name}</title>
      </Head>
      <BreadcrumbComponent props={crumb} />

      <ProductDetails
        product={product}
        productOptions={productOptions}
        productVariations={productVariations}
        pvid={pvid}
        averageStar={averageStar}
        totalRating={totalElements}
      />

      {/* Product Attributes */}
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

      {/* Specification and Rating */}
      <Tabs defaultActiveKey="Specification" id="product-detail-tab" className="mb-3 " fill>
        <Tab eventKey="Specification" title="Specification" style={{ minHeight: '200px' }}>
          <div className="tabs" dangerouslySetInnerHTML={{ __html: product.specification }}></div>
        </Tab>
        <Tab eventKey="Reviews" title={`Reviews (${totalElements})`} style={{ minHeight: '200px' }}>
          <div>
            <div
              style={{
                borderBottom: '1px solid lightgray',
                marginBottom: 30,
              }}
            >
              <PostRatingForm
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

      {/* Related products */}
      <RelatedProduct productId={product.id} />
    </Container>
  );
};

export default ProductDetailsPage;
