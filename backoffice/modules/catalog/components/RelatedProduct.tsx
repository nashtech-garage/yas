import { useRouter } from 'next/router';
import React, { useEffect, useState } from 'react';
import { Table } from 'react-bootstrap';
import Button from 'react-bootstrap/Button';
import { UseFormGetValues, UseFormSetValue } from 'react-hook-form';

import { FormProduct } from '@catalogModels/FormProduct';
import { Product } from '@catalogModels/Product';
import { getRelatedProductByProductId } from '@catalogServices/ProductService';
import ShowProductModel from '@commonItems/ProductModal';

type Props = {
  setValue: UseFormSetValue<FormProduct>;
  getValue: UseFormGetValues<FormProduct>;
};

const RelatedProduct = ({ setValue, getValue }: Props) => {
  const router = useRouter();
  const { id } = router.query;

  const [modalShow, setModalShow] = useState<boolean>(false);
  const [selectedRelatedProduct, setSelectedRelatedProduct] = useState<Product[]>([]);

  useEffect(() => {
    if (id) {
      fetchRelatedProduct(+id);
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [id]);

  const fetchRelatedProduct = (productId: number) => {
    getRelatedProductByProductId(productId)
      .then((results) => {
        setSelectedRelatedProduct(results);
        setValue(
          'relateProduct',
          results.map((product) => product.id)
        );
      })
      .catch((error) => {
        console.log(error);
      });
  };

  const onProductSelected = (product: Product) => {
    let relatedProduct = getValue('relateProduct') || [];
    const index = relatedProduct.indexOf(product.id);
    if (index === -1) {
      relatedProduct.push(product.id);
      setSelectedRelatedProduct([...selectedRelatedProduct, product]);
    } else {
      relatedProduct = relatedProduct.filter((item) => item !== product.id);
      const filterRelated = selectedRelatedProduct.filter((_product) => _product.id !== product.id);
      setSelectedRelatedProduct([...filterRelated]);
    }
    setValue('relateProduct', relatedProduct);
  };

  const onProductDeleted = (product: Product) => {
    const relatedProduct = getValue('relateProduct') || [];
    const index = relatedProduct.indexOf(product.id);
    if (index !== -1) {
      relatedProduct.splice(index, 1);
      const filterRelated = selectedRelatedProduct.filter((_product) => _product.id !== product.id);
      setSelectedRelatedProduct([...filterRelated]);
    }
    setValue('relateProduct', relatedProduct);
  };

  return (
    <>
      <Button variant="secondary" onClick={() => setModalShow(true)}>
        Add Related Product
      </Button>

      <ShowProductModel
        show={modalShow}
        onHide={() => setModalShow(false)}
        label="Add Related Product"
        onSelected={onProductSelected}
        selectedProduct={selectedRelatedProduct}
      />

      {selectedRelatedProduct.length > 0 && (
        <Table striped bordered hover className="my-4">
          <thead>
            <tr>
              <th style={{ width: '20%' }}>Selected product id</th>
              <th>Product Name</th>
              <th>Action</th>
            </tr>
          </thead>
          <tbody>
            {(selectedRelatedProduct || []).map((product) => (
              <tr key={product.id}>
                <td>{product.id}</td>
                <td>{product.name}</td>
                <td>
                  <Button variant="danger" onClick={() => onProductDeleted(product)}>
                    Delete
                  </Button>
                </td>
              </tr>
            ))}
          </tbody>
        </Table>
      )}
    </>
  );
};

export default RelatedProduct;
