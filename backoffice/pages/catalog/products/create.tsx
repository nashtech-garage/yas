import { NextPage } from 'next';
import Link from 'next/link';
import Router from 'next/router';
import { Tab, Tabs } from 'react-bootstrap';
import { SubmitHandler, useForm } from 'react-hook-form';
import slugify from 'slugify';
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
import { ProductAttributeValuePost } from '../../../modules/catalog/models/ProductAttributeValuePost';
import { ProductPost } from '../../../modules/catalog/models/ProductPost.js';
import { createProductAttributeValueOfProduct } from '../../../modules/catalog/services/ProductAttributeValueService';
import { createProductOptionValue } from '../../../modules/catalog/services/ProductOptionValueService';
import { createProduct } from '../../../modules/catalog/services/ProductService';

const ProductCreate: NextPage = () => {
  const {
    register,
    setValue,
    handleSubmit,
    getValues,
    formState: { errors },
  } = useForm<ProductPost>({
    defaultValues: {
      isVisibleIndividually: true,
      isPublished: true,
      isAllowedToOrder: true,
    },
  });

  const onSubmitForm: SubmitHandler<ProductPost> = async (data) => {
    data.brandId = data.brandId == 0 ? undefined : data.brandId;
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
    };
    const res = await createProduct(product, data.thumbnail, data.productImages);

    // upload variation
    let variations = data.productVariations || [];
    for (const option of variations) {
      let _product = {
        name: option.optionName,
        slug: slugify(option.optionName),
        sku: option.optionSku,
        gtin: option.optionGTin,
        price: option.optionPrice,
        parentId: res.id,
      };
      await createProduct(_product, option.optionThumbnail, option.optionImages);
    }

    // upload product attribute
    for (const att of data.productAttributes || []) {
      let productAtt: ProductAttributeValuePost = {
        ProductId: res.id,
        productAttributeId: att.id,
        value: att.value,
      };
      await createProductAttributeValueOfProduct(productAtt);
    }

    // upload Option Value
    for (const ele of data.productOptions || []) {
      ele.ProductId = res.id;
      ele.displayOrder = 1;
      await createProductOptionValue(ele);
    }

    Router.replace('/catalog/products');
  };

  return (
    <div className="create-product">
      <h2>Create Product</h2>

      <form onSubmit={handleSubmit(onSubmitForm)}>
        <Tabs defaultActiveKey={'general'} className="mb-3">
          <Tab eventKey={'general'} title="General Information">
            <ProductGeneralInformation register={register} errors={errors} setValue={setValue} />
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
