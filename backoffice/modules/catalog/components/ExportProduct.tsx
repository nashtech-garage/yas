import { format as formatDate } from 'date-fns';
import { useEffect, useRef, useState } from 'react';
import { CSVLink } from 'react-csv';
import ReactDOM from 'react-dom';
import {
  FORMAT_DATE_YYYY_MM_DD_HH_MM,
  mappingExportingProductColumnNames,
} from '../../../constants/Common';
import styles from '../../../styles/ExportCSV.module.css';
import { exportProducts } from '../services/ProductService';
import { toastError } from '../services/ToastService';

const CSVDownload = (props: any) => {
  const btnRef = useRef(null);
  useEffect(() => btnRef.current?.click(), []);
  return (
    <CSVLink {...props}>
      <span ref={btnRef} />
    </CSVLink>
  );
};

const ExportProduct = ({ productName = '', brandName = '' }) => {
  const downloadRef = useRef();
  const [products, setProducts] = useState<Product[]>([]);

  const getData = () => {
    try {
      exportProducts(productName, brandName).then((data) => {
        const fileName = formatDate(Date.now(), FORMAT_DATE_YYYY_MM_DD_HH_MM) + '_products.csv';
        const headers =
          data?.[0] &&
          Object.keys(data?.[0]).map((key) => ({
            label: mappingExportingProductColumnNames[key] || '',
            key,
          }));

        ReactDOM.render(
          <CSVDownload key={Date.now()} filename={fileName} headers={headers} data={data} />,
          downloadRef.current
        );
      });
    } catch (errors) {
      const errorMessages = errors.response?.data?.errors;
      const errorMessage = errorMessages ? errorMessages[0]?.errorMessage : errors?.message;
      toastError(errorMessage);
    }
  };

  return (
    <div>
      <button className="btn btn-primary" onClick={getData}>
        Export
      </button>
      <div ref={downloadRef} />
    </div>
  );
};

export default ExportProduct;
