import { format as formatDate } from 'date-fns';
import { useEffect, useRef } from 'react';
import { CSVLink } from 'react-csv';
import ReactDOM from 'react-dom';
import moment from 'moment';
import {
  FORMAT_DATE_YYYY_MM_DD_HH_MM,
  mappingExportingProductColumnNames,
  EXPORT_FAILED,
} from '../../../constants/Common';
import { exportProducts } from '../services/ProductService';

const CSVDownload = (props) => {
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

  //Get list of product need export
  const getExportingProducts = () => {
    exportProducts(productName, brandName).then((data) => {
      const fileName = moment(Date.now()).format(FORMAT_DATE_YYYY_MM_DD_HH_MM) + '_products.csv';
      const headers =
        data?.[0] &&
        Object.keys(data?.[0]).map((key) => ({
          label: mappingExportingProductColumnNames[key] || '',
          key,
        }));

      ReactDOM.render(
        <CSVDownload filename={fileName} headers={headers} data={data} />,
        downloadRef.current
      );
    });
  };

  return (
    <div>
      <button className="btn btn-primary" onClick={getExportingProducts}>
        Export Product
      </button>
      <div ref={downloadRef} />
    </div>
  );
};

export default ExportProduct;
