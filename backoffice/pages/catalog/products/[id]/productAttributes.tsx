import type { NextPage } from 'next';
import Link from 'next/link';
import { useRouter } from 'next/router';
import React, { useEffect, useState } from 'react';
import { Table } from 'react-bootstrap';

import { ProductAttribute } from '@catalogModels/ProductAttribute';
import { ProductAttributeValue } from '@catalogModels/ProductAttributeValue';
import { ProductAttributeValuePost } from '@catalogModels/ProductAttributeValuePost';
import { getProductAttributes } from '@catalogServices/ProductAttributeService';
import {
  createProductAttributeValueOfProduct,
  deleteProductAttributeValueOfProductById,
  getAttributeValueOfProduct,
  updateProductAttributeValueOfProduct,
} from '@catalogServices/ProductAttributeValueService';

const ProductAttributes: NextPage = () => {
  //Get ID
  const router = useRouter();
  const { id } = router.query;

  const [attributeOfCurrentProducts, setAttributeOfCurrentProducts] = useState<
    ProductAttributeValue[]
  >([]);
  const [attributeOfProducts, setAttributeOfProducts] = useState<ProductAttributeValue[]>([]);

  const [productAttributes, setProductAttributes] = useState<ProductAttribute[]>([]);
  const [array] = useState<ProductAttribute[]>([]);

  const [nameAttribute, setNameAttribute] = useState(String);
  const [editProductAttributeId, setEditProductAttributeId] = useState(String);
  const [productAttributeId, setProductAttributeId] = useState(String);
  const [attributeName, setAttributeName] = useState(String);

  const [listDeleteProductAttributeId, setListDeleteProductAttributeId] = useState<string[]>([]);
  const [listUpdateProductAttributeId, setListUpdateProductAttributeId] = useState<string[]>([]);
  const [listCreateProductAttributeId, setListCreateProductAttributeId] = useState<string[]>([]);

  let arrayDeleteProductAttributeId: string[] = [];
  let arrayCreateProductAttributeId: string[] = [];
  let arrayAttributeOfProducts: ProductAttributeValue[] = [];

  useEffect(() => {
    if (id) {
      let checkIdValid = true;
      getAttributeValueOfProduct(+id)
        .then((data) => {
          setAttributeOfProducts(data);
          setAttributeOfCurrentProducts(data);
          getProductAttributes()
            .then((data1) => {
              if (array.length === 0) {
                data1.forEach((obj1) => {
                  data.forEach((obj) => {
                    if (obj1.name === obj.nameProductAttribute) {
                      checkIdValid = false;
                    }
                  });
                  if (checkIdValid) array.push(obj1);
                  checkIdValid = true;
                });
                setProductAttributes(array);
              }
            })
            .catch((err) => console.log(err));
        })
        .catch((err) => console.log(err));
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [id]);

  const editValueAttribute = (event: any) => {
    event.preventDefault();
    arrayAttributeOfProducts = attributeOfProducts;
    arrayAttributeOfProducts.forEach((obj) => {
      if (obj.id.toString() === editProductAttributeId.toString()) {
        obj.value = event.target.value;
      }
    });
    attributeOfCurrentProducts.forEach((obj) => {
      if (obj.id.toString() === editProductAttributeId.toString()) {
        setListUpdateProductAttributeId((prevState) => [...prevState, obj.id.toString()]);
      }
    });
    setAttributeOfProducts(arrayAttributeOfProducts);
  };
  const addNewAttributeOfProduct = (event: React.MouseEvent<HTMLElement>) => {
    event.preventDefault();
    let productAttributeValue: ProductAttributeValue = {
      id: +productAttributeId,
      nameProductAttribute: nameAttribute,
      value: '',
    };
    arrayCreateProductAttributeId = listCreateProductAttributeId;
    arrayCreateProductAttributeId.push(productAttributeId.toString());
    setListCreateProductAttributeId(arrayCreateProductAttributeId);
    setListDeleteProductAttributeId(
      listDeleteProductAttributeId.filter(
        (item) => item.valueOf().toString() !== productAttributeId.toString()
      )
    );
    setProductAttributes(productAttributes.filter((item) => item.name !== nameAttribute));
    setAttributeOfProducts([...attributeOfProducts, productAttributeValue]);
    setNameAttribute('');
  };
  const deleteAttributeOfProduct = (event: any) => {
    event.preventDefault();
    let productAttribute: ProductAttribute = {
      id: parseInt(editProductAttributeId),
      name: attributeName,
      productAttributeGroup: '',
    };
    arrayDeleteProductAttributeId = listDeleteProductAttributeId;
    arrayDeleteProductAttributeId.push(editProductAttributeId.toString());
    setListDeleteProductAttributeId(arrayDeleteProductAttributeId);
    setListCreateProductAttributeId(
      listCreateProductAttributeId.filter(
        (item) => item.valueOf().toString() !== editProductAttributeId.toString()
      )
    );
    setAttributeOfProducts(
      attributeOfProducts.filter((item) => item.nameProductAttribute !== attributeName)
    );
    setProductAttributes([...productAttributes, productAttribute]);
  };
  const saveProductAttributeOfProduct = async (event: any) => {
    event.preventDefault();
    for (const productAttributes1 of attributeOfProducts) {
      for (const createProductAttribute of listCreateProductAttributeId) {
        if (productAttributes1.id === parseInt(createProductAttribute.valueOf())) {
          if (id) {
            let productAttributeValuePost: ProductAttributeValuePost = {
              ProductId: +id,
              productAttributeId: productAttributes1.id,
              value: productAttributes1.value,
            };
            await createProductAttributeValueOfProduct(productAttributeValuePost);
          }
        }
      }
    }
    for (const productAttributes1 of attributeOfProducts) {
      for (const list of listUpdateProductAttributeId) {
        if (parseInt(list.valueOf()) === productAttributes1.id) {
          if (id) {
            let productAttributeValuePost: ProductAttributeValuePost = {
              ProductId: 0,
              productAttributeId: 0,
              value: productAttributes1.value,
            };
            await updateProductAttributeValueOfProduct(
              productAttributes1.id,
              productAttributeValuePost
            );
          }
        }
      }
    }
    for (const currentAttributes of attributeOfCurrentProducts) {
      for (const list of listDeleteProductAttributeId) {
        if (parseInt(list.valueOf()) === currentAttributes.id) {
          await deleteProductAttributeValueOfProductById(currentAttributes.id);
        }
      }
    }
    if (id) {
      router.push('/catalog/products').catch((err) => console.log(err));
    }
  };

  return (
    <>
      <div className="mb-3">
        <div>
          <div className="mb-3">
            <label className="form-label" htmlFor="productAG">
              Attribute Name
            </label>
            <select
              className="form-control"
              name="attribute-name"
              id="attribute-name"
              onChange={(event) => {
                setNameAttribute(event.target.value.split('/')[0]);
                setProductAttributeId(event.target.value.split('/')[1]);
              }}
            >
              <option value={'Select'}>Select</option>
              {productAttributes?.map((obj) => (
                <option value={obj.name + '/' + obj.id} key={obj.id}>
                  {obj.name}
                </option>
              ))}
            </select>
          </div>
          {nameAttribute === '' || nameAttribute === 'Select' ? (
            <button className="btn btn-primary" type="submit" disabled>
              Add Attribute
            </button>
          ) : (
            <button className="btn btn-primary" onClick={addNewAttributeOfProduct}>
              Add Attribute
            </button>
          )}
        </div>
      </div>
      <form>
        <div className="mb-3">
          <div className="mb-3">
            <label className="form-label" htmlFor="name">
              Product Attributes
            </label>
          </div>
          <Table>
            <thead>
              <tr>
                <th>Attribute Name</th>
                <th>Value</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {attributeOfProducts?.map((productValue) => (
                <React.Fragment key={productValue.id}>
                  <tr
                    onClickCapture={() => {
                      setAttributeName(productValue.nameProductAttribute);
                      setEditProductAttributeId(productValue.id + '');
                    }}
                    key={productValue.id}
                  >
                    <td>{productValue.nameProductAttribute}</td>
                    <td>
                      <input
                        className="form-control"
                        type="text"
                        id="value"
                        name="value"
                        defaultValue={productValue.value}
                        onChange={editValueAttribute}
                      />
                    </td>
                    <td>
                      <button
                        className="btn btn-outline-danger"
                        type="submit"
                        onClick={deleteAttributeOfProduct}
                      >
                        Delete
                      </button>
                    </td>
                  </tr>
                </React.Fragment>
              ))}
            </tbody>
          </Table>
        </div>
        <div className="text-center">
          <button className="btn btn-primary" type="submit" onClick={saveProductAttributeOfProduct}>
            Save
          </button>
          <Link href="/catalog/products">
            <button className="btn btn-secondary m-3">Cancel</button>
          </Link>
        </div>
      </form>
    </>
  );
};
export default ProductAttributes;
