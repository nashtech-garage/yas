import React, { useState } from 'react';
import Button from 'react-bootstrap/Button';
import { Product } from '../models/Product';
import { Table } from 'react-bootstrap';
import { UseFormSetValue, UseFormGetValues } from 'react-hook-form';
import { ProductPost } from '../models/ProductPost';
import ShowProductModel from '../../../common/items/ProductModal';

type Props = {
  setValue: UseFormSetValue<ProductPost>;
  getValue: UseFormGetValues<ProductPost>;
};

const CrossSellProduct = ({ setValue, getValue }: Props) => {
  const [modalShow, setModalShow] = useState<boolean>(false);
  const [selectedProduct, setSelectedProduct] = useState<Product[]>([]);

  const onProductSelected = (event: React.MouseEvent<HTMLElement>, product: Product) => {
    let temp = getValue('crossSell') || [];
    let index = temp.indexOf(product.id);
    if (index === -1) {
      temp.push(product.id);
      setSelectedProduct([...selectedProduct, product]);
    } else {
      temp = temp.filter((item) => item !== product.id);
      let filterProduct = selectedProduct.filter((_product) => _product.id !== product.id);
      setSelectedProduct([...filterProduct]);
    }
    setValue('crossSell', temp);
  };
  return (
    <>
      <Button variant="primary" onClick={() => setModalShow(true)}>
        Manage Cross-Sell Product
      </Button>

      <ShowProductModel
        show={modalShow}
        onHide={() => setModalShow(false)}
        onSelected={onProductSelected}
        label="Add Cross - Sell Product"
      />
      {selectedProduct.length > 0 && (
        <Table>
          <thead>
            <th>Selected</th>
            <th>Product Name</th>
          </thead>
          <tbody>
            {(selectedProduct || []).map((product) => (
              <tr className="mb-3" key={product.id}>
                <th>{product.id}</th>
                <th>{product.name}</th>
              </tr>
            ))}
          </tbody>
        </Table>
      )}
    </>
  );
};

export default CrossSellProduct;
