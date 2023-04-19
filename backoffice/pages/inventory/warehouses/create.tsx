import type { NextPage } from 'next';
import { WarehouseDetail } from '@inventoryModels/WarehouseDetail';
import { createWarehouse } from '@inventoryServices/WarehouseService';
import React from 'react';
import { useForm } from 'react-hook-form';
import Link from 'next/link';
import { useRouter } from 'next/router';
import WarehouseGeneralInformation from '@inventoryComponents/WarehouseGeneralInformation';
import { WAREHOUSE_URL } from 'constants/Common';
import { handleCreatingResponse } from '@commonServices/ResponseStatusHandlingService';

const WarehouseCreate: NextPage = () => {
  const router = useRouter();
  const {
    register,
    handleSubmit,
    formState: { errors },
    setValue,
    trigger,
  } = useForm<WarehouseDetail>();
  const handleSubmitWarehouse = async (event: WarehouseDetail) => {
    let warehouseDetail: WarehouseDetail = {
      id: 0,
      name: event.name,
      contactName: event.contactName,
      phone: event.phone,
      addressLine1: event.addressLine1,
      addressLine2: event.addressLine2,
      city: event.city,
      zipCode: event.zipCode,
      districtId: event.districtId,
      stateOrProvinceId: event.stateOrProvinceId,
      countryId: event.countryId,
    };
    let response = await createWarehouse(warehouseDetail);
    handleCreatingResponse(response);
    router.replace(WAREHOUSE_URL);
  };

  return (
    <>
      <div className="row mt-5">
        <div className="col-md-8">
          <h2>Create Warehouse</h2>
          <form onSubmit={handleSubmit(handleSubmitWarehouse)}>
            <WarehouseGeneralInformation
              register={register}
              errors={errors}
              setValue={setValue}
              trigger={trigger}
            />
            <button className="btn btn-primary" type="submit">
              Save
            </button>
            <Link href="/inventory/warehouses">
              <button className="btn btn-primary" style={{ background: 'red', marginLeft: '30px' }}>
                Cancel
              </button>
            </Link>
          </form>
        </div>
      </div>
    </>
  );
};

export default WarehouseCreate;
