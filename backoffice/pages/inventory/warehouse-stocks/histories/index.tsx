import { Warehouse } from '@inventoryModels/Warehouse';
import type { NextPage } from 'next';
import { useEffect, useState } from 'react';
import Form from 'react-bootstrap/Form';
import { Button, Table } from 'react-bootstrap';
import { StockInfo } from '@inventoryModels/StockInfo';
import { toastError } from '@commonServices/ToastService';
import { ProductQuantityInStock } from '@inventoryModels/ProductQuantityInStock';
import path from 'path';
import { useRouter } from 'next/router';
import { string } from 'yup';
import { StockHistory } from '@inventoryModels/StockHistory';
import { StockHistoryList } from '@inventoryModels/StockHistoryList';
import { getStockHistories } from '@inventoryServices/StockHistoryService';

const WarehouseStocksHistories: NextPage = () => {
  const router = useRouter();
  const { warehouseId, productId } = router.query;
  const [stockHistories, setStockHistories] = useState<StockHistory[]>([]);

  useEffect(() => {
    fetchHistories();
  }, [warehouseId, productId]);

  const fetchHistories = () => {
    console.log(warehouseId + ' ' + productId);
    if (warehouseId && productId) {
      getStockHistories(+warehouseId, +productId)
        .then(async (response) => {
          if (!response.ok) {
            toastError('Something wrong has just happened');
          } else {
            let data: StockHistoryList = await response.json();
            setStockHistories(data.data);
          }
        })
        .catch(() => toastError('Something wrong has just happened'));
    }

    return;
  };

  return (
    <>
      <div className="row mt-5">
        <div className="col-md-5">
          <h2 className="text-danger font-weight-bold mb-3">Review Warehouse Stock History</h2>
        </div>
      </div>

      <div className="mt-3">
        <Table striped bordered hover className="mt-2">
          <thead>
            <tr>
              <th>Name</th>
              <th>Created By</th>
              <th>Created On</th>
              <th>(+/-)Adjusted Quantity</th>
              <th>Note</th>
            </tr>
          </thead>
          <tbody>
            {stockHistories.map((stockHistory, index) => (
              <tr key={stockHistory.id}>
                <td>{stockHistory.productName}</td>
                <td>{stockHistory.createdBy}</td>
                <td>{stockHistory.createdOn}</td>
                <td>{stockHistory.adjustedQuantity}</td>
                <td>{stockHistory.note}</td>
              </tr>
            ))}
          </tbody>
        </Table>
      </div>
    </>
  );
};

export default WarehouseStocksHistories;
