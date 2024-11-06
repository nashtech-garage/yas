import { getProductOptionValueByProductId, getVariationsByProductId } from '@catalogServices/ProductService';
import { useRouter } from 'next/router';
import React, { useEffect, useMemo, useState } from 'react';
import { UseFormGetValues, UseFormSetValue } from 'react-hook-form';
import Select, { SingleValue } from 'react-select';
import { toast } from 'react-toastify';

import { FormProduct } from '../models/FormProduct';
import { ProductOption } from '../models/ProductOption';
import { ProductVariation } from '../models/ProductVariation';
import { getProductOptions } from '../services/ProductOptionService';
import ProductVariant from './ProductVariant';
import { ProductOptionValuePost } from '@catalogModels/ProductOptionValuePost';
import { Button } from 'react-bootstrap';
import DisplayTypeModal from './DisplayTypeModel';
import CustomInput from './CustomInput';

type Props = {
  getValue: UseFormGetValues<FormProduct>;
  setValue: UseFormSetValue<FormProduct>;
};

type SelectedOptionValue = {
  id: number;
  name: string;
  value?: string;
}

const ProductVariations = ({ getValue, setValue }: Props) => {
  const router = useRouter();
  const { id } = router.query;

  const [currentOption, setCurrentOption] = useState<SingleValue<ProductOption>>(null);
  const [productOptions, setProductOptions] = useState<ProductOption[]>([]);
  const [selectedOptions, setSelectedOptions] = useState<SelectedOptionValue[]>([]);
  const [optionCombines, setOptionCombines] = useState<string[]>([]);
  const [productOptionValuePost, setProductOptionValuePost] = useState<ProductOptionValuePost[]>([]);
  const [currentModelOption, setCurrentModelOption] = useState<SingleValue<ProductOption>>(null);
  const [customInputValues, setCustomInputValues] = useState<Record<string, string[]>>({});
  const [showDisplayStyleModel, setShowDisplayStyleModel] = useState(false);

  const handleCloseDisplayModel = () => setShowDisplayStyleModel(false);

  const handleCustomInputChange = (optionName: string, value: string[]) => {
    setCustomInputValues((prevValues) => ({
      ...prevValues,
      [optionName]: value,
    }));
    updateProductOptionValue(optionName, value);
  };

  const handleChangeDisplayType = (type: string) => {
    setProductOptionValuePost((prevProductOptionValuePost) => {
      const updatedPosts = prevProductOptionValuePost.map((post) => {
        if (post.productOptionId === currentModelOption?.id) {
          return { ...post, displayType: type };
        }
        return post;
      });
      setValue('productOptionValuePost', updatedPosts);
      return updatedPosts;
    });
  }

  const handleColorChange = (event: React.ChangeEvent<HTMLInputElement>, optionValueKey: string) => {
    event.preventDefault();
    const displayColor = event.target.value;
    setProductOptionValuePost((preProductOptionValuePost) => {
      let updatedProductOptionValuePost;
      const existingProductOptionValuePost = preProductOptionValuePost.find((t) => (t.productOptionId === currentModelOption?.id));
      if (existingProductOptionValuePost) {
        updatedProductOptionValuePost = preProductOptionValuePost.map((post) =>
          post.productOptionId === currentModelOption?.id
            ? {
              ...post,
              value: { ...post.value, [optionValueKey]: displayColor },
            }
            : post
        );
      } else {
        updatedProductOptionValuePost = [...preProductOptionValuePost, {
          productOptionId: currentModelOption?.id || 0,
          displayType: "text",
          displayOrder: 1,
          value: { [optionValueKey]: displayColor },
        }];
      }
      setValue('productOptionValuePost', updatedProductOptionValuePost);
      return updatedProductOptionValuePost;
    });
  }

  const openSelectOptionModel = (event: React.MouseEvent<HTMLElement>, option: string) => {
    event.preventDefault();
    const optionValues = customInputValues[option]
    if (!optionValues || optionValues.length === 0) {
      return toast.warning('Please insert option value');
    }
    const productOption = productOptions.find((productOption) => productOption.name === option);
    updateProductOptionValue(option, optionValues);
    setCurrentModelOption(productOption || null);
    setShowDisplayStyleModel(true);
  };

  const updateProductOptionValue = (optionName: string, optionValues: string[]) => {
    const productOption = productOptions.find((productOption) => productOption.name === optionName);
    setProductOptionValuePost((productOptions) => {
      const currentOptions = [...productOptions];
      const index = currentOptions.findIndex((t) => t.productOptionId === productOption?.id);

      if (index !== -1) {
        // Update the existing entry
        const existingValue = currentOptions[index].value;
        const updatedValue: Record<string, string> = {};
        // Loop through option values to create a new value object
        optionValues.forEach(optionValue => {
          updatedValue[optionValue] = existingValue[optionValue] || '#000000';
        });
        currentOptions[index] = { ...currentOptions[index], value: updatedValue, };
      } else {
        // Create a new entry if it doesn't exist
        const newProductOptionValuePost: ProductOptionValuePost = {
          productOptionId: productOption?.id || 0,
          displayOrder: 1,
          displayType: "text",
          value: optionValues.reduce((acc, optionValue) => {
            acc[optionValue] = '#000000';
            return acc;
          }, {} as Record<string, string>),
        };
        currentOptions.push(newProductOptionValuePost);
      }
      setValue('productOptionValuePost', currentOptions);
      return currentOptions;
    });
  }

  useEffect(() => {
    if (id) {
      loadExistingVariant(+id);
      loadExistingProductOptionValue(+id);
    }
  }, [id]);

  const loadExistingVariant = (id: number) => {
    getVariationsByProductId(id).then((results) => {
      if (results) {
        const listOptionCombine: string[] = [];
        const productVariants: ProductVariation[] = [];
        results.forEach((item) => {
          listOptionCombine.push(item.name || '');
          productVariants.push({
            id: item.id,
            optionName: item.name || '',
            optionGTin: item.gtin || '',
            optionSku: item.sku || '',
            optionPrice: item.price || 0,
            optionThumbnail: item.thumbnail,
            optionImages: item.productImages,
            optionValuesByOptionId: item.options,
          });
        });

        setOptionCombines(listOptionCombine);
        setValue('productVariations', productVariants);
      }
    });
  };

  const loadExistingProductOptionValue = (id: number) => {
    getProductOptionValueByProductId(id).then((results) => {
      if (results) {
        const productOptionValuePosts: ProductOptionValuePost[] = [];
        results.forEach((item) => {
          productOptionValuePosts.push({
            productOptionId: item.productOptionId,
            displayType: item.displayType || 'text',
            displayOrder: item.displayOrder || 0,
            value: item.productOptionValue ? JSON.parse(item.productOptionValue) : {},
          });

          setSelectedOptions((prevSelectedOptions) => {
            const option: SelectedOptionValue = {
              id: item.productOptionId ? item.productOptionId : 0,
              name: item.productOptionName ? item.productOptionName : "",
              value: item ? Object.keys(JSON.parse(item.productOptionValue)).join(',') : '',
            }

            if (!prevSelectedOptions.find((t) => t.name === option.name)) {
              return [...prevSelectedOptions, option];
            }
            return prevSelectedOptions;
          });

          setCustomInputValues((prevInputValues) => {
            const newValues = item.productOptionValue ? Object.keys(JSON.parse(item.productOptionValue))
              .join(',').split(',').map(v => v.trim()) : [];
            return {
              ...prevInputValues,
              [item.productOptionName ?? '']: [...(prevInputValues[item.productOptionName ?? ''] || []),
              ...newValues],
            };
          });

        });
        setValue('productOptionValuePost', productOptionValuePosts);
        setProductOptionValuePost(productOptionValuePosts);
      }
    })
  }

  const options = useMemo(() => {
    return productOptions.map((option) => ({ value: option.name, label: option.name }));
  }, [productOptions]);

  let listVariant = useMemo(() => {
    return getValue('productVariations') || [];
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [optionCombines]);

  useEffect(() => {
    getProductOptions().then((data) => {
      setProductOptions(data);
    });
  }, []);

  const onAddOption = (event: React.MouseEvent<HTMLElement>) => {
    event.preventDefault();
    if (!currentOption) {
      toast.info('Select options first');
    } else {
      const index = selectedOptions.find((t) => t.name === currentOption.name);
      if (!index) {
        let newOption: SelectedOptionValue = {
          id: currentOption.id,
          name: currentOption.name,
        }
        setSelectedOptions([...selectedOptions, newOption]);
      } else {
        toast.info(`${currentOption.name} is selected. Select Other`);
      }
    }
  };

  const onDeleteOption = (event: React.MouseEvent<HTMLElement>, option: string) => {
    event.preventDefault();
    setOptionCombines([]);
    const result = selectedOptions.filter((_option) => _option.name !== option);
    const productOption = productOptions.find((productOption) => productOption.name === option);
    const productOptionPosts = productOptionValuePost.filter((_option) => _option.productOptionId != productOption?.id)
    setCurrentModelOption(null);
    setProductOptionValuePost([...productOptionPosts]);
    setSelectedOptions([...result]);
  };

  const onGenerate = (event: React.MouseEvent<HTMLElement>) => {
    event.preventDefault();
    const formProductVariations = getValue('productVariations') || [];
    const optionValuesByOptionIds = generateDistinctProductOptionCombinations();
    const productOptionValues = generateProductOptionValue();

    if (optionValuesByOptionIds.length === 0) {
      return toast.warn('Please Input Values Option');
    }
    optionValuesByOptionIds.forEach((optionValuesByOptionId, index) => {
      const productName = getValue('name');
      const variationName = [productName, ...Array.from(optionValuesByOptionId.values())]
        .join(' ')
        .trim();

      const checkVariationName = formProductVariations.some(
        (variation) => variation.optionName == variationName
      );

      if (checkVariationName) {
        return toast.warning('Combined Option Values are Duplicated');
      }

      const newVariation: ProductVariation = {
        optionName: variationName,
        optionGTin: getValue('gtin') ?? '',
        optionSku: getValue('sku') ?? '',
        optionPrice: getValue('price') ?? 0,
        optionValuesByOptionId: Object.fromEntries(optionValuesByOptionId),
      };
      setOptionCombines([variationName]);
      setValue('productVariations', [...formProductVariations, newVariation]);
      formProductVariations.push(newVariation);
    });
    setValue('productOptionValuePost', productOptionValues)
  };

  const generateProductOptionValue = (): ProductOptionValuePost[] => {
    const result = productOptionValuePost;
    const optionValuesByOptionId = new Map<number, string>();
    let isEmptyOptions = false;
    selectedOptions.forEach((option) => {
      if (isEmptyOptions) return;
      const optionValues = customInputValues[option.name];
      if (optionValues.length === 0) {
        isEmptyOptions = true;
        return;
      }
      optionValues.forEach((value) => {
        const productOption = productOptions.find((productOption) => productOption.name === option.name);
        const productOptionId = productOption?.id ?? -1;
        optionValuesByOptionId.set(productOptionId, value);
        const productOptionValue = productOptionValuePost.find((t) => t.productOptionId === productOptionId);

        if (!productOptionValue) {
          result.push({
            productOptionId,
            value: { [`${value}`]: '#000000' },
            displayType: 'text',
            displayOrder: 1,
          });
        } else {
          let keyExists = false;
          for (const key in productOptionValue.value) {
            if (key === value) {
              keyExists = true;
              break;
            }
          }
          if (!keyExists) {
            productOptionValue.value = {
              ...productOptionValue.value,
              [`${value}`]: '#000000'
            };
          }
        }
      })
    });
    return result;
  }

  const generateProductOptionCombinations = (): Array<Map<number, string>> => {
    const optionValuesArray: Array<Map<number, string>> = [];
    let isEmptyOptions = false;

    selectedOptions.forEach((option) => {
      if (isEmptyOptions) return;
      const optionValues = customInputValues[option.name];
      if (optionValues.length === 0) {
        isEmptyOptions = true;
        return;
      }
      optionValues.forEach((value) => {
        const productOption = productOptions.find((productOption) => productOption.name === option.name);
        const productOptionId = productOption?.id ?? -1;

        const optionMap = new Map<number, string>();
        optionMap.set(productOptionId, value);
        optionValuesArray.push(optionMap);
      });
    });

    return isEmptyOptions ? [] : optionValuesArray;
  };

  const generateDistinctProductOptionCombinations = (): Array<Map<number, string>> => {
    const optionValuesArray = generateProductOptionCombinations();
    if (optionValuesArray.length === 0) {
      return [];
    }

    // Group values by option ID
    const groupedByOptionId: Map<number, string[]> = new Map();

    optionValuesArray.forEach((optionMap) => {
      optionMap.forEach((value, key) => {
        if (!groupedByOptionId.has(key)) {
          groupedByOptionId.set(key, []);
        }
        const existingValues = groupedByOptionId.get(key)!;
        if (!existingValues.includes(value)) {
          existingValues.push(value);
        }
      });
    });

    // Get all unique keys (option IDs)
    const optionIds = Array.from(groupedByOptionId.keys());

    // Generate all combinations using recursive helper function
    const combine = (index: number, currentCombination: Map<number, string>): Array<Map<number, string>> => {
      if (index === optionIds.length) {
        return [new Map(currentCombination)];
      }

      const id = optionIds[index];
      const values = groupedByOptionId.get(id)!;
      const combinations: Array<Map<number, string>> = [];

      values.forEach((value) => {
        currentCombination.set(id, value);
        combinations.push(...combine(index + 1, currentCombination));
        currentCombination.delete(id);
      });

      return combinations;
    };

    return combine(0, new Map());
  };

  const onDeleteVariation = (variant: ProductVariation) => {
    const result = optionCombines.filter((optionName) => optionName !== variant.optionName);
    setOptionCombines(result);

    let productVar = getValue('productVariations') || [];
    productVar = productVar.filter((item) => item.optionName !== variant.optionName);
    setValue('productVariations', productVar);
  };

  return (
    <>
      {/* Selection */}
      <div className="mb-3 d-flex justify-content-evenly">
        <label className="form-label m-0" htmlFor="option">
          Available Options
        </label>
        <Select
          className="w-50"
          options={options}
          isClearable
          isOptionDisabled={(option) => (selectedOptions.find((t) => t.name === option.value.toString()) != null)}
          onChange={(option) => {
            if (option?.label) {
              setCurrentOption({
                id: +option.value,
                name: option.label,
              });
            }
          }}
        />
        <button className="btn btn-success" onClick={(event) => onAddOption(event)}>
          Add Option
        </button>
      </div>

      {/* Value options */}
      {selectedOptions.length > 0 && (
        <div className="mb-3">
          <h5 className="mb-3">Value Options</h5>
          <div className="mb-3">
            {(selectedOptions || []).map((option) => (
              <div className="mb-3 d-flex gap-4" key={option.name}>
                <label className="form-label flex-grow-1 d-flex flex-col align-items-center" htmlFor={option.name}>
                  {option.name}
                </label>
                <span className="form-control w-75 border-none p-0">
                  <CustomInput
                    defaultValue={option.value}
                    onChange={(value) => handleCustomInputChange(option.name, value)}
                    productVariations={listVariant}
                  />
                </span>
                <Button className='py-0' variant="primary" onClick={(event) => { openSelectOptionModel(event, option.name); }}>
                  Display Style
                </Button>
                <DisplayTypeModal
                  show={showDisplayStyleModel}
                  handleCloseModel={handleCloseDisplayModel}
                  handleColorChange={handleColorChange}
                  currentModelOption={currentModelOption}
                  productOptionValuePost={productOptionValuePost}
                  setDisplayType={handleChangeDisplayType}
                />
                <button
                  className="btn btn-danger"
                  onClick={(event) => onDeleteOption(event, option.name)}
                >
                  <i className="bi bi-x"></i>
                </button>
              </div>
            ))}
          </div>
          <div className="text-center">
            <button className="btn btn-info" onClick={onGenerate}>
              Generate Combine
            </button>
          </div>
        </div>
      )}

      {/* Product variations */}
      {listVariant.length > 0 && (
        <div className="mb-3">
          <h5 className="mb-3">Product Variations</h5>

          {listVariant.map((variant, index) => (
            <ProductVariant
              key={variant.optionName}
              index={index}
              variant={variant}
              onDelete={onDeleteVariation}
            />
          ))}
        </div>
      )}
    </>
  );
};

export default ProductVariations;
