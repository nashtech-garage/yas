import { useEffect, useRef } from 'react';
import { CSVLink } from 'react-csv';
import ReactDOM from 'react-dom';
import moment from 'moment';
import {
  FORMAT_DATE_YYYY_MM_DD_HH_MM,
  mappingExportingOrderColumnNames,
} from '../../../constants/Common';
import { exportOrders } from '../../order/services/OrderService';

const CSVDownload = (props) => {
  const btnRef = useRef(null);
  useEffect(() => btnRef.current?.click(), []);
  return (
    <CSVLink {...props}>
      <span ref={btnRef} />
    </CSVLink>
  );
};

const ExportOrders = ({ params = '', getFilter }) => {
  const downloadRef = useRef();

  const getExportingOrders = () => {
    console.log('hehe' + params);
    exportOrders(params).then((data) => {
      console.log(data);
      const fileName = moment(Date.now()).format(FORMAT_DATE_YYYY_MM_DD_HH_MM) + '_orders.csv';
      const headers =
        data?.[0] &&
        Object.keys(data?.[0]).map((key) => ({
          label: mappingExportingOrderColumnNames[key] || '',
          key,
        }));

      ReactDOM.render(
        <CSVDownload filename={fileName} headers={headers} data={data} />,
        downloadRef.current
      );
    });
  };
  const handleClick = () => {
    getFilter();
    getExportingOrders();
  };

  return (
    <>
      <button type="button" className="btn btn-success me-2" onClick={handleClick}>
        <i className="fa fa-download me-2" aria-hidden="true"></i> Export
      </button>
      <div
        className="btn btn-success me-2"
        style={{ backgroundColor: 'white', borderColor: 'white', padding: '0' }}
        ref={downloadRef}
      />
    </>
  );
};

export default ExportOrders;
