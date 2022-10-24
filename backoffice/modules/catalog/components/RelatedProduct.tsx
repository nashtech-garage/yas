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

const RelatedProduct = ({ setValue, getValue }: Props) => {
  const [modalShow, setModalShow] = useState<boolean>(false);
  const [selectedRelatedProduct, setSelectedRelatedProduct] = useState<Product[]>([]);

  const onProductSelected = (event: React.MouseEvent<HTMLElement>, product: Product) => {
    event.preventDefault();
    let relatedProduct = getValue('relateProduct') || [];
    let index = relatedProduct.indexOf(product.id);
    if (index === -1) {
      relatedProduct.push(product.id);
      setSelectedRelatedProduct([...selectedRelatedProduct, product]);
    } else {
      relatedProduct = relatedProduct.filter((item) => item !== product.id);
      let filterRelated = selectedRelatedProduct.filter((_product) => _product.id !== product.id);
      setSelectedRelatedProduct([...filterRelated]);
    }
    setValue('relateProduct', relatedProduct);
  };
  return (
    <>
      <Button variant="primary" onClick={() => setModalShow(true)}>
        Manage Related Product
      </Button>

      <ShowProductModel
        show={modalShow}
        onHide={() => setModalShow(false)}
        label="Add Related Product"
        onSelected={onProductSelected}
      />
      {selectedRelatedProduct.length > 0 && (
        <Table>
          <thead>
            <th>Selected</th>
            <th>Product Name</th>
          </thead>
          <tbody>
            {(selectedRelatedProduct || []).map((product) => (
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

export default RelatedProduct;
