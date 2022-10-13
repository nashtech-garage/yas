import React, { useEffect, useState } from 'react';
import { Table } from 'react-bootstrap';
import { UseFormGetValues, UseFormSetValue } from 'react-hook-form/dist/types';
import { toast } from 'react-toastify';
import { ProductAttribute } from '../models/ProductAttribute';
import { ProductAttributeValue } from '../models/ProductAttributeValue';
import { ProductPost } from '../models/ProductPost';
import { getProductAttributes } from '../services/ProductAttributeService';

type Props = {
  setValue: UseFormSetValue<ProductPost>;
  getValue: UseFormGetValues<ProductPost>;
};

const ProductAttributes = ({ setValue, getValue }: Props) => {
  const [productAttributes, setProductAttributes] = useState<ProductAttribute[]>([]);
  const [selectedAttributes, setSelectedAttributes] = useState<string[]>([]);

  useEffect(() => {
    getProductAttributes().then((data) => setProductAttributes(data));
  }, []);

  const onAddAttribute = (event: React.MouseEvent<HTMLElement>) => {
    event.preventDefault();
    let attName = (document.getElementById('attribute') as HTMLSelectElement).value;
    if (attName === '0') {
      toast.info('No attribute has been selected yet');
    } else {
      let productAtt = getValue('productAttributes') || [];

      if (selectedAttributes.indexOf(attName) === -1) {
        setSelectedAttributes([...selectedAttributes, attName]);
        let att = productAttributes.find((att) => att.name === attName);
        if (att) {
          let newAttr: ProductAttributeValue = {
            id: att.id,
            nameProductAttribute: attName,
            value: '',
          };
          productAtt.push(newAttr);
          setValue('productAttributes', productAtt);
        }
      } else {
        toast.info(`${attName} is selected`);
      }
    }
  };

  const onDeleteSelectedAttribute = (event: React.MouseEvent<HTMLElement>, attName: string) => {
    event.preventDefault();
    let productAtt = getValue('productAttributes') || [];
    let filter = selectedAttributes.filter((item) => item !== attName);
    let attFilter = productAtt.filter((_att) => _att.nameProductAttribute !== attName);
    setSelectedAttributes(filter);
    setValue('productAttributes', attFilter);
  };

  const onAttributeValueChange = (event: React.ChangeEvent<HTMLInputElement>, attName: string) => {
    let productAtt = getValue('productAttributes') || [];
    let index = productAtt.findIndex((att) => att.nameProductAttribute === attName);
    productAtt[index].value = event.target.value;
    setValue('productAttributes', productAtt);
  };

  return (
    <>
      <div className="mb-3 d-flex justify-content-evenly">
        <label className="form-label" htmlFor="brand">
          Available Attribute
        </label>
        <select className="form-control w-50" id="attribute" defaultValue="0">
          <option value="0" disabled hidden>
            Select Attribute
          </option>
          {(productAttributes || []).map((att) => (
            <option value={att.name} key={att.id}>
              {att.name}
            </option>
          ))}
        </select>
        <button className="btn btn-primary" onClick={onAddAttribute}>
          Add Attribute
        </button>
      </div>
      {selectedAttributes.length > 0 && (
        <>
          <h4>Product Attribute</h4>
          <Table>
            <thead>
              <tr>
                <th>Attribute name</th>
                <th>Value</th>
                <th>Action</th>
              </tr>
            </thead>
            <tbody>
              {(selectedAttributes || []).map((item) => (
                <tr key={item}>
                  <th>{item}</th>
                  <th>
                    <input
                      type="text"
                      className="w-75"
                      onChange={(e) => onAttributeValueChange(e, item)}
                    />
                  </th>
                  <th>
                    <button
                      className="btn btn-danger"
                      onClick={(event) => onDeleteSelectedAttribute(event, item)}
                    >
                      <i className="bi bi-x"></i>
                    </button>
                  </th>
                </tr>
              ))}
            </tbody>
          </Table>
        </>
      )}
    </>
  );
};

export default ProductAttributes;
