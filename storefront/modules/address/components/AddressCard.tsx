import React from 'react';
import { Address } from '../models/AddressModel';

type Props = {
  address: Address;
};

const AddressCard = ({ address, isSelected }: { address: Address; isSelected: boolean }) => {
  return (
    <div
      className={`address-card position-relative mb-4 ps-3 d-flex flex-column border-bottom border-secondary p-2 text-dark ${
        isSelected ? 'border border-primary rounded' : ''
      }`}
    >
      {isSelected && (
        <span className="badge bg-primary position-absolute top-0 end-0 m-2">Selected</span>
      )}
      <span className="fw-bold mb-1">Name: {address.contactName} </span>
      <span>Phone: {address.phone} </span>
      <span>
        Address: {address.addressLine1} {address.districtName} {address.city}{' '}
        {address.stateOrProvinceName} {address.countryName}
      </span>
    </div>
  );
};

export default AddressCard;
