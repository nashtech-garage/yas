import type { NextPage } from 'next';
import Link from 'next/link';
import { useRouter } from 'next/router';
import { useEffect, useState } from 'react';
import { Tab, Tabs } from 'react-bootstrap';
import { SubmitHandler, useForm } from 'react-hook-form';
import { toast } from 'react-toastify';

import {
  CrossSellProduct,
  ProductCategoryMapping,
  ProductImage,
  ProductSEO,
  ProductUpdateInformation,
  RelatedProduct,
} from '../../../../modules/catalog/components';
import { Product } from '../../../../modules/catalog/models/Product';
import { ProductPost } from '../../../../modules/catalog/models/ProductPost';
import { getProduct, updateProduct } from '../../../../modules/catalog/services/ProductService';
import ProductAttributes from '../[id]/productAttributes';

const EditProduct: NextPage = () => {
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
  } = useForm<ProductPost>();

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
          router.push('/catalog/products');
        }
      });
    }
  }, [id]);

  //Form validate
  const onSubmit: SubmitHandler<ProductPost> = (data) => {
    if (id) {
      updateProduct(+id, data).then(async (res) => {
        if (res.ok) {
          router.push('/catalog/products');
        } else {
          res.json().then((error) => {
            toast(error.detail);
          });
        }
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
              <ProductUpdateInformation register={register} errors={errors} setValue={setValue} />
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
