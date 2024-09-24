import { useEffect, useState } from 'react';
import { FieldErrorsImpl, UseFormRegister, UseFormSetValue, UseFormTrigger } from 'react-hook-form';
import { DatePicker, Input, Select, Switch, TextArea } from '../../../common/items/Input';
import { PromotionDetail, PromotionDto } from '../models/Promotion';
import { searchBrands, searchCategories, searchProducts } from '../services/ProductService';
import MultipleAutoComplete from './MultipleAutoComplete';

type Props = {
  register: UseFormRegister<PromotionDto>;
  errors: FieldErrorsImpl<PromotionDto>;
  setValue: UseFormSetValue<PromotionDto>;
  trigger: UseFormTrigger<PromotionDto>;
  promotion?: PromotionDetail;
  isSubmitting: boolean;
};

const PromotionGeneralInformation = ({
  register,
  errors,
  setValue,
  trigger,
  promotion,
  isSubmitting,
}: Props) => {
  const [discountType, setDiscountType] = useState(promotion?.discountType);
  const [usageType, setUsageType] = useState(promotion?.usageType);
  const [applyTo, setApplyTo] = useState(promotion?.applyTo);
  const [brands, setBrands] = useState(promotion?.brands?.map((brand) => brand.id) ?? []);
  const [categories, setCategories] = useState(
    promotion?.categories?.map((category) => category.id) ?? []
  );
  const [products, setProducts] = useState(promotion?.products?.map((product) => product.id) ?? []);
  const [productVms, setProductVms] = useState(promotion?.products ?? []);
  const [brandVms, setBrandVms] = useState(promotion?.brands ?? []);
  const [categoryVms, setCategoryVms] = useState(promotion?.categories ?? []);

  useEffect(() => {
    if (promotion) {
      setDiscountType(promotion.discountType);
      setUsageType(promotion.usageType);
      setApplyTo(promotion.applyTo);

      setProducts(promotion.products?.map((product) => product.id) ?? []);
      setProductVms(promotion.products ?? []);

      setBrands(promotion.brands?.map((brand) => brand.id) ?? []);
      setBrandVms(promotion.brands ?? []);

      setCategories(promotion.categories?.map((category) => category.id) ?? []);
      setCategoryVms(promotion.categories ?? []);
    }
  }, [promotion]);

  useEffect(() => {
    if (applyTo === 'PRODUCT') {
      searchProducts('').then((data) => {
        setProductVms(data.productContent);
      });
    }
  }, [applyTo]);

  const convertDateToString = (date?: Date | string) => {
    if (date) {
      if (typeof date === 'string') {
        date = new Date(date);
      }
      const month = date.getMonth() + 1;
      const dateInMonth = date.getDate();
      return `${date.getFullYear()}-${month > 9 ? month : '0' + month}-${
        dateInMonth > 9 ? dateInMonth : '0' + dateInMonth
      }`;
    }
    return '';
  };

  const onSelectProducts = (id: number) => {
    setValue('productIds', [...(products ?? []), id]);
    setProducts([...(products ?? []), id]);
  };

  const removeProduct = (id: number) => {
    const productFilters = products?.filter((product) => product !== id);
    setProducts(productFilters);
    setValue('productIds', productFilters);
  };

  const fetchProducts = (name: string) => {
    searchProducts(name).then((data) => {
      setProductVms(data.productContent);
    });
  };

  const fetchCategories = (name: string) => {
    searchCategories(name).then((data) => {
      setCategoryVms(data);
    });
  };

  const removeCategory = (id: number) => {
    const categoryFilters = categories?.filter((category) => category !== id);
    setCategories(categoryFilters);
    setValue('categoryIds', categoryFilters);
  };

  const onSelectCategory = (id: number) => {
    setValue('categoryIds', [...(categories ?? []), id]);
    setCategories([...(categories ?? []), id]);
  };

  const fetchBrands = (name: string) => {
    searchBrands(name).then((data) => {
      setBrandVms(data);
    });
  };

  const removeBrand = (id: number) => {
    const brandFilters = brands?.filter((brand) => brand !== id);
    setBrands(brandFilters);
    setValue('brandIds', brandFilters);
  };

  const onSelectBrand = (id: number) => {
    setValue('brandIds', [...(brands ?? []), id]);
    setBrands([...(brands ?? []), id]);
  };

  return (
    <>
      <Input
        labelText="Name"
        field="name"
        defaultValue={promotion?.name}
        register={register}
        registerOptions={{
          required: { value: true, message: 'Name is required' },
        }}
        error={errors.name?.message}
      />
      <Input
        labelText="Slug"
        field="slug"
        defaultValue={promotion?.slug}
        register={register}
        registerOptions={{
          required: { value: true, message: 'Slug is required' },
        }}
        error={errors.slug?.message}
      />
      <Input
        labelText="Coupon code"
        field="couponCode"
        defaultValue={promotion?.couponCode}
        register={register}
        registerOptions={{
          required: { value: true, message: 'Coupon code is required' },
        }}
        error={errors.couponCode?.message}
      />
      <TextArea
        labelText="Description"
        field="description"
        defaultValue={promotion?.description}
        register={register}
        error={errors.description?.message}
      />
      <Select
        labelText="Discount type"
        field="discountType"
        placeholder="Select discount type"
        defaultValue={promotion?.discountType}
        register={register}
        registerOptions={{
          required: { value: true, message: 'Discount type is required' },
        }}
        error={errors.discountType?.message}
        options={[
          { value: 'PERCENTAGE', label: 'Percentage' },
          { value: 'FIXED', label: 'Amount' },
        ]}
        onChange={(event) => {
          setDiscountType(event.target.value);
        }}
      />
      {discountType === 'PERCENTAGE' && (
        <Input
          labelText="Discount percentage"
          field="discountPercentage"
          defaultValue={promotion?.discountPercentage}
          register={register}
          registerOptions={{
            required: { value: true, message: 'Discount percentage is required' },
          }}
          error={errors.discountPercentage?.message}
        />
      )}
      {discountType === 'FIXED' && (
        <Input
          labelText="Discount amount"
          field="discountAmount"
          defaultValue={promotion?.discountAmount}
          register={register}
          registerOptions={{
            required: { value: true, message: 'Discount amount is required' },
          }}
          error={errors.discountAmount?.message}
        />
      )}
      <Select
        labelText="Usage type"
        field="usageType"
        defaultValue={promotion?.usageType}
        register={register}
        registerOptions={{
          required: { value: true, message: 'Usage type is required' },
        }}
        placeholder="Select usage type"
        error={errors.usageType?.message}
        options={[
          { value: 'LIMITED', label: 'Limited' },
          { value: 'UNLIMITED', label: 'Unlimited' },
        ]}
        onChange={(event) => {
          setUsageType(event.target.value);
        }}
      />
      {usageType === 'LIMITED' && (
        <Input
          labelText="Usage limit"
          field="usageLimit"
          defaultValue={promotion?.usageLimit}
          register={register}
          registerOptions={{
            required: { value: true, message: 'Usage limit is required' },
          }}
          error={errors.usageLimit?.message}
        />
      )}
      <Switch
        labelText="Active"
        field="isActive"
        defaultChecked={promotion?.isActive}
        register={register}
      />
      <DatePicker
        labelText="Start date"
        field="startDate"
        defaultValue={convertDateToString(promotion?.startDate)}
        register={register}
        registerOptions={{
          required: { value: true, message: 'Start date is required' },
        }}
        error={errors.startDate?.message}
      />
      <DatePicker
        labelText="End date"
        field="endDate"
        defaultValue={convertDateToString(promotion?.endDate)}
        register={register}
        registerOptions={{
          required: { value: true, message: 'End date is required' },
        }}
        error={errors.endDate?.message}
      />
      <Select
        labelText="Apply to"
        field="applyTo"
        defaultValue={promotion?.applyTo}
        register={register}
        registerOptions={{
          required: { value: true, message: 'Apply to is required' },
        }}
        placeholder="Select apply to"
        error={errors.applyTo?.message}
        options={[
          { value: 'PRODUCT', label: 'Products' },
          { value: 'CATEGORY', label: 'Categories' },
          { value: 'BRAND', label: 'Brands' },
        ]}
        onChange={(event) => {
          setApplyTo(event.target.value);
        }}
      />
      {!isSubmitting && applyTo === 'PRODUCT' && (
        <MultipleAutoComplete
          labelText="Product"
          field="product"
          register={register}
          registerOptions={{
            required: { value: true, message: 'Product is required' },
          }}
          options={productVms}
          fetchOptions={fetchProducts}
          onSelect={(id) => onSelectProducts(id)}
          optionSelectedIds={products}
          isSubmitting={isSubmitting}
          onRemoveElement={(id) => removeProduct(id)}
          addedOptions={promotion?.products}
        />
      )}
      {!isSubmitting && applyTo === 'CATEGORY' && (
        <MultipleAutoComplete
          labelText="Category"
          field="category"
          register={register}
          registerOptions={{
            required: { value: true, message: 'Category is required' },
          }}
          options={categoryVms}
          fetchOptions={fetchCategories}
          onSelect={(id) => onSelectCategory(id)}
          optionSelectedIds={categories}
          isSubmitting={isSubmitting}
          onRemoveElement={(id) => removeCategory(id)}
          addedOptions={promotion?.categories}
        />
      )}
      {!isSubmitting && applyTo === 'BRAND' && (
        <MultipleAutoComplete
          labelText="Brand"
          field="brand"
          register={register}
          registerOptions={{
            required: { value: true, message: 'Brand is required' },
          }}
          options={brandVms}
          fetchOptions={fetchBrands}
          onSelect={(id) => onSelectBrand(id)}
          optionSelectedIds={brands}
          isSubmitting={isSubmitting}
          onRemoveElement={(id) => removeBrand(id)}
          addedOptions={promotion?.brands}
        />
      )}
    </>
  );
};

export default PromotionGeneralInformation;
