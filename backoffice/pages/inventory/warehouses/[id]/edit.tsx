import { NextPage } from 'next';
import Link from 'next/link';
import { useRouter } from 'next/router';
import { useForm } from 'react-hook-form';
import { WarehouseDetail } from '@inventoryModels/WarehouseDetail';
import { editWarehouse, getWarehouse } from '@inventoryServices/WarehouseService';
import WarehouseGeneralInformation from '@inventoryComponents/WarehouseGeneralInformation';
import { useEffect, useState } from 'react';
import { WAREHOUSE_URL } from 'constants/Common';
import { toastError } from '@commonServices/ToastService';
import { handleUpdatingResponse } from '@commonServices/ResponseStatusHandlingService';

const WarehouseEdit: NextPage = () => {
  const router = useRouter();
  const {
    register,
    handleSubmit,
    formState: { errors },
    setValue,
    trigger,
  } = useForm<WarehouseDetail>();
  const [warehouseDetail, setWarehouseDetail] = useState<WarehouseDetail>();
  const [isLoading, setLoading] = useState(false);
  const { id } = router.query;
  const handleSubmitEdit = async (event: WarehouseDetail) => {
    if (id) {
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

      editWarehouse(+id, warehouseDetail).then((response) => {
        handleUpdatingResponse(response);
        router.replace(WAREHOUSE_URL);
      });
    }
  };

  useEffect(() => {
    if (id) {
      setLoading(true);
      getWarehouse(+id).then((data) => {
        if (data.id) {
          setWarehouseDetail(data);
          setLoading(false);
        } else {
          toastError(data?.detail);
          setLoading(false);
          router.push(WAREHOUSE_URL);
        }
      });
    }
  }, [id]);

  if (isLoading) return <p>Loading...</p>;
  if (!warehouseDetail) return <></>;
  return (
    <>
      <div className="row mt-5">
        <div className="col-md-8">
          <h2>Edit warehouse: {id}</h2>
          <form onSubmit={handleSubmit(handleSubmitEdit)}>
            <WarehouseGeneralInformation
              register={register}
              errors={errors}
              setValue={setValue}
              trigger={trigger}
              warehouseDetail={warehouseDetail}
            />
            <button className="btn btn-primary" type="submit">
              Save
            </button>
            <Link href={`${WAREHOUSE_URL}`}>
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

export default WarehouseEdit;
