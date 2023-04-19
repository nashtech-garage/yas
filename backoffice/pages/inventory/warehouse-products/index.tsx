import { ProductInfoVm } from '@inventoryModels/ProductInfoVm';
import { Warehouse } from '@inventoryModels/Warehouse';
import {
  FilterExistInWHSelection,
  getProductInWarehouse,
  getWarehouses,
} from '@inventoryServices/WarehouseService';
import type { NextPage } from 'next';
import { useEffect, useState } from 'react';
import Form from 'react-bootstrap/Form';
import { Button, Table } from 'react-bootstrap';
import { StockPostVM } from '@inventoryModels/Stock';
import { addProductIntoWarehouse } from '@inventoryServices/StockService';
import { Console } from 'console';

const WarehouseProducts: NextPage = () => {
  const [warehouseIdSelected, setWarehouseIdSelected] = useState<number>(0);
  const [warehouses, setWarehouses] = useState<Warehouse[]>([]);
  const [warehouseProducts, setWarehouseProducts] = useState<ProductInfoVm[]>([]);
  const [productName, setProductName] = useState<string>('');
  const [productSku, setProductSku] = useState<string>('');
  const [existStatusSelected, setExistStatusSelected] = useState<string>(
    FilterExistInWHSelection.ALL
  );
  const existSelections = Object.keys(FilterExistInWHSelection);

  useEffect(() => {
    fetchWarehouses();
  }, []);

  useEffect(() => {
    if (warehouseIdSelected) {
      fetchProductsInWarehouse();
    }
  }, [warehouseIdSelected, productName, productSku, existStatusSelected]);

  const fetchWarehouses = () => {
    getWarehouses().then((results) => setWarehouses(results));
  };

  const fetchProductsInWarehouse = () => {
    getProductInWarehouse(warehouseIdSelected, productName, productSku, existStatusSelected).then(
      (results) => {
        setWarehouseProducts(() => results.map((result) => ({ ...result, isSelected: false })));
      }
    );
  };

  const selectProductIntoWarehouse = (event: any, index: number) => {
    const shallowProducts = warehouseProducts.map((product, i) => {
      if (i === index) {
        product.isSelected = event.target.checked;
      }
      return product;
    });
    setWarehouseProducts(shallowProducts);
  };

  const addIntoWarehouse = () => {
    if (warehouseIdSelected && warehouseProducts.length) {
      const stockPostVms: StockPostVM[] = warehouseProducts
        .filter((productInfo) => productInfo.isSelected)
        .map((productInfo) => {
          return {
            productId: productInfo.id,
            warehouseId: warehouseIdSelected,
          };
        });

      addProductIntoWarehouse(stockPostVms).then(() => fetchProductsInWarehouse());
    }
  };

  return (
    <>
      <div className="row mt-5">
        <div className="col-md-5">
          <h2 className="text-danger font-weight-bold mb-3">Manage Warehouse Products</h2>
        </div>
      </div>

      <div>
        <Form>
          <div className="row col-md-12">
            <div className="col-md-6">
              <Form.Select
                id="warehouse-selection"
                onChange={(e) => {
                  setWarehouseIdSelected(Number(e.target.value));
                }}
                style={!warehouseIdSelected ? { color: '#838d8d' } : {}}
                placeholder="Select Warehouse..."
              >
                <option key="all" style={{ color: '#838d8d' }} value={undefined}>
                  Select Warehouse...
                </option>
                {warehouses.map((item) => (
                  <option key={item.id} style={{ color: 'black' }} value={item.id}>
                    {item.name}
                  </option>
                ))}
              </Form.Select>
            </div>
            <div className="row col-md-6">
              <label className="col mt-1">Exist in Warehouse</label>
              <Form.Select
                id="exist-selection"
                onChange={(e) => {
                  setExistStatusSelected(e.target.value);
                }}
                className="col"
                disabled={!warehouseIdSelected}
              >
                {existSelections.map((item) => (
                  <option key={item} value={item}>
                    {item}
                  </option>
                ))}
              </Form.Select>
            </div>
          </div>
          <div className="row col-md-12 mt-3">
            <div className="col-md">
              <Form.Control
                id="product-name"
                placeholder="Search product name ..."
                defaultValue={productName}
                onChange={(event) => setProductName(event.target.value)}
                disabled={!warehouseIdSelected}
              />
            </div>
            <div className="col-md">
              <Form.Control
                id="product-sku"
                placeholder="Search product SKU ..."
                defaultValue={productSku}
                onChange={(event) => setProductSku(event.target.value)}
                disabled={!warehouseIdSelected}
              />
            </div>
          </div>
        </Form>
      </div>
      <div className="mt-3">
        <div>
          <Button variant="primary" onClick={addIntoWarehouse}>
            Add Into Warehouse
          </Button>
        </div>
        <Table striped bordered hover className="mt-2">
          <thead>
            <tr>
              <th>Name</th>
              <th>SKU</th>
              <th>Exist In Warehouse</th>
            </tr>
          </thead>
          <tbody>
            {warehouseProducts.map((productInfo, index) => (
              <tr key={productInfo.id}>
                <td>{productInfo.name}</td>
                <td>{productInfo.sku}</td>
                <td>
                  <Form.Check
                    type="checkbox"
                    id={'checkbox-product-' + productInfo.id + '-warehouse-' + warehouseIdSelected}
                    className="mx-4 my-2 font-weight-bold"
                    checked={productInfo.existInWH || productInfo.isSelected}
                    disabled={productInfo.existInWH}
                    onChange={(event) => selectProductIntoWarehouse(event, index)}
                  />
                </td>
              </tr>
            ))}
          </tbody>
        </Table>
      </div>
    </>
  );
};

export default WarehouseProducts;
