import React, { useState } from 'react';
import { Table } from 'react-bootstrap';
import { toast } from 'react-toastify';
import { UseFormGetValues } from 'react-hook-form';
import { ProductPost } from '../models/ProductPost';

const options = ['Color', 'Size', 'SSD', 'Chip'];
const headers = ['Option Combinations', 'SKU', 'GTIN', 'Price', 'Thumbnail', 'Images', 'Action'];

interface IProductVariation {
  name: string;
  sku?: string;
  gtin?: string;
  price?: number;
  thumbnail?: File;
  images?: FileList;
}

type Props = {
  getValue: UseFormGetValues<ProductPost>;
};

const ProductVariation = ({ getValue }: Props) => {
  const [selectedOptions, setSelectedOptions] = useState<string[]>([]);
  const [productVariations, setProductVariations] = useState<IProductVariation[]>([]);

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
    setProductVariations([]);
    let result = selectedOptions.filter((_option) => _option !== option);
    setSelectedOptions([...result]);
  };

  const onGenerate = (event: React.MouseEvent<HTMLElement>) => {
    event.preventDefault();
    let result: string[] = [];
    selectedOptions.forEach((option) => {
      let combines = (document.getElementById(option) as HTMLInputElement).value.split(',');
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
    let productVariation: IProductVariation[] = [];
    result.forEach((item) => {
      productVariation.push({ name: item, sku: '', price: 0, gtin: '' });
    });
    setProductVariations(productVariation);
  };

  const onDeleteVariation = (event: React.MouseEvent<HTMLElement>, index: number) => {
    event.preventDefault();
    let item = productVariations.at(index);
    const result = productVariations.filter((pro) => pro !== item);
    setProductVariations(result);
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
          {(options || []).map((option) => (
            <option value={option} key={option}>
              {option}
            </option>
          ))}
        </select>
        <button className="btn btn-primary" onClick={onAddOption}>
          Add Option
        </button>
      </div>
      <div className="mb-3">
        <h5>Value Options</h5>
        <div className="mb-3">
          {(selectedOptions || []).map((option) => (
            <div className="mb-3 d-flex justify-content-evenly" key={option}>
              <label className="form-label" htmlFor={option}>
                {option}
              </label>
              <input type="text" id={option} className={`form-control w-75`} />
              <button className="btn btn-danger" onClick={(event) => onDeleteOption(event, option)}>
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

      {productVariations.length > 0 && (
        <div className="mb-3">
          <h5>Product Variations</h5>
          <Table>
            <thead className="mb-3">
              {headers.map((title, index) => (
                <th key={index}>{title}</th>
              ))}
            </thead>
            <tbody>
              {(productVariations || []).map((ele, index) => (
                <tr key={ele.name}>
                  <th>{getValue('name').concat(' ', ele.name)}</th>
                  <th>
                    <input type="text" className="w-50" />
                  </th>
                  <th>
                    <input type="text" className="w-50" />
                  </th>
                  <th>
                    <input type="number" className="w-50" placeholder={ele.price?.toString()} />
                  </th>
                  <th>
                    <input type="file" />
                  </th>
                  <th>
                    <input type="file" multiple />
                  </th>
                  <th>
                    <button
                      className="btn btn-danger"
                      onClick={(event) => onDeleteVariation(event, index)}
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
