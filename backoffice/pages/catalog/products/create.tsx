import { NextPage } from 'next';
import Link from 'next/link';
import { Tab, Tabs } from 'react-bootstrap';
import { SubmitHandler, useForm } from 'react-hook-form';
import { toast } from 'react-toastify';
import { handleCreatingResponse } from '../../../common/services/ResponseStatusHandlingService';
import { PRODUCT_URL } from '../../../constants/Common';
import {
  CrossSellProduct,
  ProductAttributes,
  ProductCategoryMapping,
  ProductGeneralInformation,
  ProductImage,
  ProductSEO,
  ProductVariation,
  RelatedProduct,
} from '../../../modules/catalog/components';
import { FormProduct } from '../../../modules/catalog/models/FormProduct';
import { ProductAttributeValuePost } from '../../../modules/catalog/models/ProductAttributeValuePost';
import { mapFormProductToProductPayload } from '../../../modules/catalog/models/ProductPayload';
import { createProductAttributeValueOfProduct } from '../../../modules/catalog/services/ProductAttributeValueService';
import { createProduct } from '../../../modules/catalog/services/ProductService';
import { useRouter } from 'next/router';
import { useEffect, useState } from 'react';

const ProductCreate: NextPage = () => {
  const router = useRouter();
  const {
    register,
    setValue,
    handleSubmit,
    getValues,
    watch,
    formState: { errors },
  } = useForm<FormProduct>({
    defaultValues: {
      isVisibleIndividually: true,
      isPublished: true,
      isAllowedToOrder: true,
    },
  });
  const [tabKey, setTabKey] = useState('general');

  const onSubmitForm: SubmitHandler<FormProduct> = async (data) => {
    try {
      // create product
      const payload = mapFormProductToProductPayload(data);
      const productResponse = await createProduct(payload);

      // upload product attribute
      if (productResponse.status === 201) {
        const responseBody = await productResponse.json();
        for (const att of data.productAttributes || []) {
          let productAtt: ProductAttributeValuePost = {
            productId: responseBody.id,
            productAttributeId: att.id,
            value: att.value,
          };
          await createProductAttributeValueOfProduct(productAtt);
        }
        await router.push(PRODUCT_URL);
      }

      handleCreatingResponse(productResponse);
    } catch (error) {
      toast.error('Create product failed');
    }
  };

  useEffect(() => {
    if (Object.keys(errors).length) {
      setTabKey('general');
      setTimeout(() => {
        document.getElementById(Object.keys(errors)[0])?.scrollIntoView();
      }, 0);
    }
  }, [errors]);

  return (
    <div className="create-product">
      <h2>Create Product</h2>

      <form onSubmit={handleSubmit(onSubmitForm)}>
        <Tabs className="mb-3" activeKey={tabKey} onSelect={(e: any) => setTabKey(e)}>
          <Tab eventKey={'general'} title="General Information">
            <ProductGeneralInformation
              register={register}
              errors={errors}
              setValue={setValue}
              watch={watch}
            />
          </Tab>
          <Tab eventKey={'image'} title="Product Images">
            <ProductImage setValue={setValue} />
          </Tab>
          <Tab eventKey={'variation'} title="Product Variations">
            <ProductVariation getValue={getValues} setValue={setValue} />
          </Tab>

          <Tab eventKey={'attribute'} title="Product Attributes">
            <ProductAttributes setValue={setValue} getValue={getValues} />
          </Tab>
          <Tab eventKey={'category'} title="Category Mapping">
            <ProductCategoryMapping setValue={setValue} getValue={getValues} />
          </Tab>
          <Tab eventKey={'related'} title="Related Products">
            <RelatedProduct setValue={setValue} getValue={getValues} />
          </Tab>
          <Tab eventKey={'cross-sell'} title="Cross-sell Product">
            <CrossSellProduct setValue={setValue} getValue={getValues} />
          </Tab>
          <Tab eventKey={'seo'} title="SEO">
            <ProductSEO register={register} errors={errors} />
          </Tab>
        </Tabs>
        <div className="text-center">
          <button className="btn btn-primary" type="submit">
            Create
          </button>
          <Link href="/catalog/products">
            <button className="btn btn-secondary m-3">Cancel</button>
          </Link>
        </div>
      </form>
    </div>
  );
};

export default ProductCreate;
