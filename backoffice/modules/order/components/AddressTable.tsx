import { Table } from 'react-bootstrap';
import { OrderAddress } from '../models/OrderAddress';
type Props = {
  address: OrderAddress;
  isShowOnGoogleMap: boolean;
};

const AddressTable = ({ address, isShowOnGoogleMap }: Props) => {
  if (!address) return <>No address found</>;
  return (
    <>
      <Table hover>
        <thead>
          <tr>
            <th className="d-flex justify-content-center">Billing address</th>
            <th className="w-50"></th>
          </tr>
        </thead>
        <tbody>
          <tr>
            <td className="d-flex justify-content-center">Full name</td>
            <td>{address.contactName}</td>
          </tr>
          <tr>
            <td className="d-flex justify-content-center">Phone</td>
            <td>{address.phone}</td>
          </tr>
          <tr>
            <td className="d-flex justify-content-center">Address line 1</td>
            <td>{address.addressLine1}</td>
          </tr>
          <tr>
            <td className="d-flex justify-content-center">Address line 2</td>
            <td>{address.addressLine2}</td>
          </tr>

          <tr>
            <td className="d-flex justify-content-center">Zip code</td>
            <td>{address.zipCode}</td>
          </tr>
          <tr>
            <td className="d-flex justify-content-center">District name</td>
            <td>{address.districtName}</td>
          </tr>
          <tr>
            <td className="d-flex justify-content-center">City</td>
            <td>{address.city}</td>
          </tr>
          <tr>
            <td className="d-flex justify-content-center">State or province name</td>
            <td>{address.stateOrProvinceName}</td>
          </tr>
          <tr>
            <td className="d-flex justify-content-center">Country name</td>
            <td>{address.countryName}</td>
          </tr>
          {isShowOnGoogleMap ? (
            <tr>
              <td style={{ cursor: 'pointer' }}>
                <i className="fa fa-map-marker me-3 mt-2" aria-hidden="true"></i>View on Google Maps
              </td>
            </tr>
          ) : (
            <tr>
              <td style={{ height: '40px' }}></td>
            </tr>
          )}
          <tr className="mt-5">
            <td>
              <button className="mt-2 btn btn-dark">Edit</button>
            </td>
          </tr>
        </tbody>
      </Table>
    </>
  );
};

export default AddressTable;
