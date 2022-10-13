import React, { useEffect, useState } from 'react';
import { Table } from 'react-bootstrap';
import { toast } from 'react-toastify';
import { UseFormGetValues, UseFormRegister, UseFormSetValue } from 'react-hook-form';
import { ProductPost } from '../models/ProductPost';
import { ProductVariation } from '../models/ProductVariation';
import { ProductOption } from '../models/ProductOption';
import { getProductOptions } from '../services/ProductOptionService';

const headers = ['Option Combinations', 'SKU', 'GTIN', 'Price', 'Thumbnail', 'Images', 'Action'];

type Props = {
  getValue: UseFormGetValues<ProductPost>;
  setValue: UseFormSetValue<ProductPost>;
};

const ProductVariation = ({ getValue, setValue }: Props) => {
  const [selectedOptions, setSelectedOptions] = useState<string[]>([]);
  const [optionCombines, setOptionCombines] = useState<string[]>([]);
  const [productOptions, setProductOptions] = useState<ProductOption[]>([]);

  useEffect(() => {
    getProductOptions().then((data) => setProductOptions(data));
  }, []);

  const onAddOption = (event: React.MouseEvent<HTMLElement>) => {
    event.preventDefault();
    let option = (document.getElementById('option') as HTMLSelectElement).value;

    if (option === '0') {
      toast.info('Select options first');
    } else {
      let index = selectedOptions.indexOf(option);
      if (index === -1) {
        setSelectedOptions([...selectedOptions, option]);
      } else {
        toast.info(`${option} is selected. Select Other`);
      }
    }
  };

  const onDeleteOption = (event: React.MouseEvent<HTMLElement>, option: string) => {
    event.preventDefault();
    setOptionCombines([]);
    let result = selectedOptions.filter((_option) => _option !== option);
    setSelectedOptions([...result]);
  };

  const onGenerate = (event: React.MouseEvent<HTMLElement>) => {
    event.preventDefault();
    let result: string[] = [];
    let productOp = getValue('productOptions') || [];
    selectedOptions.forEach((option) => {
      let combines = (document.getElementById(option) as HTMLInputElement).value.split(',');
      let item = productOptions.find((_option) => _option.name === option);
      productOp.push({ ProductOptionId: item?.id, value: combines });
      if (result.length === 0) {
        combines.forEach((item) => {
          result.push(item);
        });
      } else {
        let index = 0;
        const old = [...result];
        for (const oldItem of old) {
          for (const newItem of combines) {
            result[index] = oldItem.concat(' ', newItem);
            index++;
          }
        }
      }
    });
    setValue('productOptions', productOp);
    let options: string[] = [];
    let productVar: ProductVariation[] = [];
    result.forEach((item) => {
      options.push(item);
      productVar.push({
        optionName: getValue('name').concat(' ', item),
        optionGTin: getValue('gtin'),
        optionSku: getValue('sku'),
        optionPrice: getValue('price'),
      });
    });
    setOptionCombines(options);
    setValue('productVariations', productVar);
  };

  const onDeleteVariation = (event: React.MouseEvent<HTMLElement>, optionName: string) => {
    event.preventDefault();
    const result = optionCombines.filter((_optionName) => _optionName !== optionName);
    setOptionCombines(result);

    let productVar = getValue('productVariations') || [];
    productVar = productVar.filter((item) => item.optionName !== optionName);
    setValue('productVariations', productVar);
  };

  const onChangeVariationValue = (
    event: React.ChangeEvent<HTMLInputElement>,
    optionName: string
  ) => {
    let value = event.target.value;
    let productVar = getValue('productVariations') || [];
    let item = productVar.find((item) => item.optionName === optionName);
    if (item) {
      switch (event.target.name) {
        case 'optionSku':
          item.optionSku = value;
          break;
        case 'optionGTin':
          item.optionGTin = value;
          break;
        case 'optionPrice':
          item.optionPrice = +value;
          break;
        case 'optionThumbnail':
          if (event.target.files) {
            item.optionThumbnail = event.target.files[0];
          }
          break;
        case 'optionImages':
          if (event.target.files) {
            item.optionImages = event.target.files;
          }
        default:
          break;
      }
      setValue('productVariations', productVar);
    }
  };

  return (
    <>
      <div className="mb-3 d-flex justify-content-evenly">
        <label className="form-label" htmlFor="brand">
          Available Options
        </label>
        <select className="form-control w-50" id="option" defaultValue="0">
          <option value="0" disabled hidden>
            Select Options
          </option>
          {(productOptions || []).map((option) => (
            <option value={option.name} key={option.id}>
              {option.name}
            </option>
          ))}
        </select>
        <button className="btn btn-primary" onClick={onAddOption}>
          Add Option
        </button>
      </div>
      {selectedOptions.length > 0 && (
        <div className="mb-3">
          <h5>Value Options</h5>
          <div className="mb-3">
            {(selectedOptions || []).map((option) => (
              <div className="mb-3 d-flex justify-content-evenly" key={option}>
                <label className="form-label" htmlFor={option}>
                  {option}
                </label>
                <input type="text" id={option} className={`form-control w-75`} />
                <button
                  className="btn btn-danger"
                  onClick={(event) => onDeleteOption(event, option)}
                >
                  <i className="bi bi-x"></i>
                </button>
              </div>
            ))}
          </div>
          <div className="text-center">
            <button className="btn btn-primary" onClick={onGenerate}>
              Generate Combine
            </button>
          </div>
        </div>
      )}

      {optionCombines.length > 0 && (
        <div className="mb-3">
          <h5>Product Variations</h5>
          <Table>
            <thead className="mb-3">
              {headers.map((title, index) => (
                <th key={index}>{title}</th>
              ))}
            </thead>
            <tbody>
              {(optionCombines || []).map((ele) => (
                <tr key={ele}>
                  <th>{getValue('name').concat(' ', ele)}</th>
                  <th>
                    <input
                      type="text"
                      className="w-50"
                      name="optionSku"
                      onChange={(e) => onChangeVariationValue(e, ele)}
                    />
                  </th>
                  <th>
                    <input
                      type="text"
                      className="w-50"
                      name="optionGTin"
                      onChange={(e) => onChangeVariationValue(e, ele)}
                    />
                  </th>
                  <th>
                    <input
                      type="number"
                      className="w-50"
                      name="optionPrice"
                      onChange={(e) => onChangeVariationValue(e, ele)}
                    />
                  </th>
                  <th>
                    <input
                      type="file"
                      name="optionThumbnail"
                      onChange={(e) => onChangeVariationValue(e, ele)}
                    />
                  </th>
                  <th>
                    <input
                      type="file"
                      multiple
                      name="optionImages"
                      onChange={(e) => onChangeVariationValue(e, ele)}
                    />
                  </th>
                  <th>
                    <button
                      className="btn btn-danger"
                      onClick={(event) => onDeleteVariation(event, ele)}
                    >
                      <i className="bi bi-x"></i>
                    </button>
                  </th>
                </tr>
              ))}
            </tbody>
          </Table>
        </div>
      )}
    </>
  );
};

export default ProductVariation;
