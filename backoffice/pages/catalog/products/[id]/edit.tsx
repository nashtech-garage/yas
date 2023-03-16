import type { NextPage } from 'next';
import Link from 'next/link';
import { useRouter } from 'next/router';
import { useEffect, useState } from 'react';
import { Tab, Tabs } from 'react-bootstrap';
import { SubmitHandler, useForm } from 'react-hook-form';
import { toast } from 'react-toastify';
import slugify from 'slugify';

import { useUpdatingContext } from '../../../../common/hooks/UseToastContext';
import { PRODUCT_URL } from '../../../../constants/Common';
import {
  CrossSellProduct,
  ProductCategoryMapping,
  ProductGeneralInformation,
  ProductImage,
  ProductSEO,
  RelatedProduct,
} from '../../../../modules/catalog/components';
import { FormProduct } from '../../../../modules/catalog/models/FormProduct';
import { Product } from '../../../../modules/catalog/models/Product';
import { getProduct, updateProduct } from '../../../../modules/catalog/services/ProductService';
import ProductAttributes from '../[id]/productAttributes';

const EditProduct: NextPage = () => {
  const { handleUpdatingResponse } = useUpdatingContext();
  //Get ID
  const router = useRouter();
  const { id } = router.query;

  const [product, setProduct] = useState<Product>();
  const [isLoading, setLoading] = useState(false);
  const [tabKey, setTabKey] = useState('general');

  const {
    register,
    setValue,
    handleSubmit,
    getValues,
    formState: { errors },
  } = useForm<FormProduct>();

  useEffect(() => {
    setLoading(true);
    if (id) {
      getProduct(+id).then((data) => {
        if (data.id) {
          setProduct(data);
          setLoading(false);
        } else {
          //Show error
          toast(data.detail);
          router.push(PRODUCT_URL);
        }
      });
    }
  }, [id]);

  //Form validate
  const onSubmit: SubmitHandler<FormProduct> = (data) => {
    if (id) {
      const product = {
        name: data.name,
        slug: data.slug,
        brandId: data.brandId,
        categoryIds: data.categoryIds,
        shortDescription: data.shortDescription,
        description: data.description,
        specification: data.specification,
        sku: data.sku,
        gtin: data.gtin,
        price: data.price,
        isAllowedToOrder: data.isAllowedToOrder,
        isPublished: data.isPublished,
        isFeatured: data.isFeatured,
        isVisibleIndividually: data.isVisibleIndividually,
        metaTitle: data.metaTitle,
        metaKeyword: data.metaKeyword,
        metaDescription: data.metaDescription,
        thumbnailMediaId: data.thumbnailMedia?.id,
        productImageIds: data.productImageMedias?.map((image) => image.id),
        variations: data.productVariations
          ? data.productVariations.map((variant) => {
              return {
                name: variant.optionName,
                slug: slugify(variant.optionName),
                sku: variant.optionSku,
                gtin: variant.optionGTin,
                price: variant.optionPrice,
                thumbnailMediaId: variant.optionThumbnail?.id,
                productImageIds: variant.optionImages?.map((image) => image.id),
              };
            })
          : [],
      };
      updateProduct(+id, product).then(async (res) => {
        handleUpdatingResponse(res, PRODUCT_URL);
      });
    }
  };
  if (isLoading) return <p>Loading...</p>;
  if (!product) {
    return <p>No product</p>;
  } else {
    return (
      <div className="create-product">
        <h2>Update Product: {product.name}</h2>

        <form onSubmit={handleSubmit(onSubmit)}>
          <Tabs className="mb-3" activeKey={tabKey} onSelect={(e: any) => setTabKey(e)}>
            <Tab eventKey={'general'} title="General Information">
              <ProductGeneralInformation register={register} errors={errors} setValue={setValue} />
            </Tab>
            <Tab eventKey={'image'} title="Product Images">
              <ProductImage product={product} setValue={setValue} />
            </Tab>
            <Tab eventKey={'variation'} title="Product Variations"></Tab>
            <Tab eventKey={'attribute'} title="Product Attributes">
              <ProductAttributes />
            </Tab>
            <Tab eventKey={'category'} title="Category Mapping">
              <ProductCategoryMapping product={product} setValue={setValue} getValue={getValues} />
            </Tab>
            <Tab eventKey={'related'} title="Related Products">
              <RelatedProduct setValue={setValue} getValue={getValues} />
            </Tab>
            <Tab eventKey={'cross-sell'} title="Cross-sell Product">
              <CrossSellProduct setValue={setValue} getValue={getValues} />
            </Tab>
            <Tab eventKey={'seo'} title="SEO">
              <ProductSEO product={product} register={register} errors={errors} />
            </Tab>
          </Tabs>

          {tabKey === 'attribute' ? (
            <div className="text-center"></div>
          ) : (
            <div className="text-center">
              <button className="btn btn-primary" type="submit">
                Save
              </button>
              <Link href="/catalog/products">
                <button className="btn btn-secondary m-3">Cancel</button>
              </Link>
            </div>
          )}
        </form>
      </div>
    );
  }
};

export default EditProduct;
