import { Warehouse } from '@inventoryModels/Warehouse';
import { getWarehouses } from '@inventoryService/WarehouseService';
import type { NextPage } from 'next';
import { useEffect, useState } from 'react';
import Form from 'react-bootstrap/Form';
import { Button, Table } from 'react-bootstrap';
import {
  fetchStocksInWarehouseByProductNameAndProductSku,
  updateProductQuantityInStock,
} from '@inventoryService/StockService';
import { StockInfo } from '@inventoryModels/StockInfo';
import { toastError } from '@commonServices/ToastService';
import { ProductQuantityInStock } from '@inventoryModels/ProductQuantityInStock';

const warehouseStocks: NextPage = () => {
  const [warehouseIdSelected, setWarehouseIdSelected] = useState<number>(0);
  const [warehouses, setWarehouses] = useState<Warehouse[]>([]);
  const [warehouseStocks, setWarehouseStocks] = useState<StockInfo[]>([]);
  const [productName, setProductName] = useState<string>('');
  const [productSku, setProductSku] = useState<string>('');
  const [productAdjustedQuantity, setProductAdjustedQuantity] = useState<Map<number, number>>(
    new Map()
  );
  const [productAdjustedNote, setProductAdjustedNote] = useState<Map<number, string>>(new Map());

  useEffect(() => {
    fetchWarehouses();
  }, []);

  useEffect(() => {
    if (warehouseIdSelected) {
      fetchStocksInWarehouse();
    }
  }, [warehouseIdSelected, productName, productSku]);

  const fetchWarehouses = () => {
    getWarehouses().then((results) => setWarehouses(results));
  };

  const fetchStocksInWarehouse = () => {
    fetchStocksInWarehouseByProductNameAndProductSku(warehouseIdSelected, productName, productSku)
      .then(async (result) => {
        if (result.status !== 200) {
          toastError('Something wrong has just happened');
        } else {
          let rs: StockInfo[] = await result.json();
          let productQuantityMap: Map<number, number> = new Map();
          setWarehouseStocks(rs);
          rs.forEach((stock) => productQuantityMap.set(stock.id, 0));
          setProductAdjustedQuantity(productQuantityMap);
        }
      })
      .catch(() => {
        toastError('Something wrong has just happened');
      });
  };

  // const selectProductIntoWarehouse = (event: any, index: number) => {
  //   const shallowProducts = warehouseStocks.map((product, i) => {
  //     if (i === index) {
  //       product.isSelected = event.target.checked;
  //     }
  //     return product;
  //   });
  //   setwarehouseStocks(shallowProducts);
  // };

  const updateProductQuantityInStockOnClick = () => {
    let requestBody: ProductQuantityInStock[] = [];

    warehouseStocks.forEach((stock) => {
      if (productAdjustedQuantity.has(stock.id)) {
        requestBody.push({
          stockId: stock.id,
          quantity: productAdjustedQuantity.get(stock.id) ?? 0,
          note: productAdjustedNote.get(stock.id) ?? '',
        });
      }
    });

    updateProductQuantityInStock(requestBody)
      .then((rs) => {
        if (rs.ok) {
          updateProductQuantityInStockAfterSaved();
        }
      })
      .catch((err) => toastError('Something went wrong'));
  };

  const updateProductQuantityInStockAfterSaved = () => {
    let newStocks = [...warehouseStocks];
    let newMap = new Map();

    newStocks.forEach((stock) => {
      if (productAdjustedQuantity.has(stock.id)) {
        stock.quantity += productAdjustedQuantity.get(stock.id) ?? 0;
        productAdjustedQuantity.set(stock.id, 0);
      }
      newMap.set(stock.id, 0);
    });
    setProductAdjustedQuantity(newMap);
    setWarehouseStocks(newStocks);
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
        <Table striped bordered hover className="mt-2">
          <thead>
            <tr>
              <th>Name</th>
              <th>SKU</th>
              <th>Current Quantity</th>
              <th>(+/-)Adjusted Quantity</th>
              <th>Note</th>
              <th>Stock History</th>
            </tr>
          </thead>
          <tbody>
            {warehouseStocks.map((stockInfo, index) => (
              <tr key={stockInfo.id}>
                <td>{stockInfo.productName}</td>
                <td>{stockInfo.productSku}</td>
                <td>{stockInfo.quantity}</td>
                <td>
                  <form>
                    <Form.Control
                      type="number"
                      id="product-adjusted-quantity"
                      placeholder="Adjusted quantity"
                      defaultValue={0}
                      onChange={(event) => {
                        var newMap = new Map(productAdjustedQuantity);
                        newMap.set(stockInfo.id, Number(event.target.value));
                        setProductAdjustedQuantity(newMap);
                      }}
                    />
                  </form>
                </td>
                <td>
                  <form>
                    <Form.Control
                      id="product-adjusted-note"
                      placeholder="Adjusted note"
                      onChange={(event) => {
                        var newMap = new Map(productAdjustedNote);
                        newMap.set(stockInfo.id, event.target.value);
                        setProductAdjustedNote(newMap);
                      }}
                    />
                  </form>
                </td>
                <td>put a link here</td>
              </tr>
            ))}
          </tbody>
        </Table>
        <div style={{ display: 'flex', justifyContent: 'end' }}>
          <Button variant="primary" onClick={updateProductQuantityInStockOnClick}>
            Save
          </Button>
        </div>
      </div>
    </>
  );
};

export default warehouseStocks;
