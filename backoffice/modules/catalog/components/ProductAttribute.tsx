import React, { useEffect, useState } from 'react';
import { Table } from 'react-bootstrap';
import { UseFormGetValues, UseFormSetValue } from 'react-hook-form/dist/types';
import { toast } from 'react-toastify';

import { FormProduct } from '../models/FormProduct';
import { ProductAttribute } from '../models/ProductAttribute';
import { ProductAttributeValue } from '../models/ProductAttributeValue';
import { getProductAttributes } from '../services/ProductAttributeService';
import { getProductTemplates, getProductTemplate } from '@catalogServices/ProductTemplateService';
import { ProductTemplate } from '@catalogModels/ProductTemplate';

type Props = {
  setValue: UseFormSetValue<FormProduct>;
  getValue: UseFormGetValues<FormProduct>;
};

const ProductAttributes = ({ setValue, getValue }: Props) => {
  const [productAttributes, setProductAttributes] = useState<ProductAttribute[]>([]);
  const [selectedAttributes, setSelectedAttributes] = useState<string[]>([]);
  const [productTemplates, setProductTemplates] = useState<ProductTemplate[]>();
  const [checkHidden, setCheckHidden] = useState<boolean>(false);

  useEffect(() => {
    getProductAttributes().then((data) => setProductAttributes(data));
    getProductTemplates().then((data) => setProductTemplates(data));
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

  const onProductTemplate = (event: React.MouseEvent<HTMLElement>) => {
    event.preventDefault();
    let ptId = (document.getElementById('product-template') as HTMLSelectElement).value;
    setValue('productAttributes', []);
    getProductTemplate(parseInt(ptId)).then((data) => {
      let attributes = [];
      let attNames = [];
      for (const element of data.productAttributeTemplates) {
        let att = productAttributes.find(
          (attribute) => attribute.id === element.productAttribute.id
        );
        if (att) {
          let newAttr: ProductAttributeValue = {
            id: att.id,
            nameProductAttribute: att.name,
            value: '',
          };
          attNames.push(att?.name);
          attributes.push(newAttr);
          setValue('productAttributes', attributes);
          setSelectedAttributes(attNames);
        }
      }
    });
  };
  const handleSelectProductTemplate = (event: React.ChangeEvent<HTMLSelectElement>) => {
    event.preventDefault();
    let ptName = (document.getElementById('product-template') as HTMLSelectElement).value;
    if (ptName !== '0') {
      setCheckHidden(true);
    }
  };
  return (
    <>
      {!productTemplates ? (
        <></>
      ) : (
        <div className="row mb-3">
          <div className="col-2">
            <label className="form-label" htmlFor="brand">
              <span>Product Templates</span>
            </label>
          </div>
          <div className="col-6">
            <select
              className="form-control"
              id="product-template"
              defaultValue="0"
              onChange={handleSelectProductTemplate}
            >
              <option value="0" disabled hidden>
                Select Product Templates
              </option>
              {(productTemplates || []).map((obj) => (
                <option value={obj.id} key={obj.id}>
                  {obj.name}
                </option>
              ))}
            </select>
          </div>
          <div className="col-4">
            {checkHidden ? (
              <button className="btn btn-primary" onClick={onProductTemplate}>
                Apply
              </button>
            ) : (
              <button className="btn btn btn-secondary">Apply</button>
            )}
          </div>
        </div>
      )}

      <div className="row mb-3">
        <div className="col-2">
          <label className="form-label" htmlFor="brand">
            <span style={{ flex: '1' }}>Available Attribute</span>
          </label>
        </div>
        <div className="col-6">
          <select className="form-control" id="attribute" defaultValue="0">
            <option value="0" disabled hidden>
              Select Attribute
            </option>
            {(productAttributes || []).map((att) => (
              <option value={att.name} key={att.id}>
                {att.name}
              </option>
            ))}
          </select>
        </div>
        <div className="col-4">
          <button className="btn btn-primary" onClick={onAddAttribute}>
            Add Attribute
          </button>
        </div>
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
