import React from 'react';
import { Address } from '../models/AddressModel';

type Props = {
  address: Address;
};

const AddressCard = ({ address }: Props) => {
  return (
    <div className="address-card mb-4 ps-3 d-flex flex-column border-bottom border-secondary p-2 text-dark">
      <span className="fw-bold mb-1">Name: {address.contactName} </span>
      <span>Phone: {address.phone} </span>
      <span>
        Address: {address.addressLine1} {address.districtId} {address?.city}{' '}
        {address.stateOrProvinceId} {address.countryId}
      </span>
    </div>
  );
};

export default AddressCard;
