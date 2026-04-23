import AddressCard from '@/modules/address/components/AddressCard';
import { Address } from '@/modules/address/models/AddressModel';
import { getUserAddress } from '@/modules/customer/services/CustomerService';
import React, { useEffect, useState } from 'react';
import Modal from 'react-bootstrap/Modal';

type Props = {
  showModal: boolean;
  handleModalClose: (isSelectionMade?: boolean) => void;
  handleSelectAddress: (address: Address) => any;
  defaultUserAddress?: Address;
  selectedAddressId?: number;
};

const ModalAddressList = ({
  showModal,
  handleModalClose,
  handleSelectAddress,
  defaultUserAddress,
  selectedAddressId,
}: Props) => {
  const [addresses, setAddresses] = useState<Address[]>([]);

  useEffect(() => {
    if (!showModal) {
      return;
    }
    getUserAddress()
      .then((res) => {
        setAddresses(res);
      })
      .catch((err) => {
        console.log('Load address fail: ', err.message);
      });
  }, [showModal]);

  const handleAddressClick = (address: Address) => {
    handleSelectAddress(address);
    handleModalClose(true);
  };

  const isAddressSelected = (address: Address) => {
    if (selectedAddressId) {
      return selectedAddressId == address.id;
    }
    if (defaultUserAddress?.id) {
      return defaultUserAddress.id == address.id;
    }
    return false;
  };

  return (
    <Modal show={showModal} onHide={() => handleModalClose()} size="lg" centered>
      <Modal.Header closeButton>
        <Modal.Title className="text-dark fw-bold">Select address</Modal.Title>
      </Modal.Header>
      <Modal.Body style={{ minHeight: '500px' }}>
        <div className="body">
          <div className="row">
            {addresses.length == 0 ? (
              <div className="mx-2">Please add your address in your profile</div>
            ) : (
              addresses.map((address) => (
                <div
                  className="col-lg-6 mb-2"
                  onClick={() => {
                    handleAddressClick(address);
                  }}
                  key={address.id}
                >
                  <AddressCard address={address} isSelected={isAddressSelected(address)} />
                </div>
              ))
            )}
          </div>
        </div>
      </Modal.Body>
    </Modal>
  );
};

export default ModalAddressList;
