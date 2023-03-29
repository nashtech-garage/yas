import { NextPage } from 'next';
import Link from 'next/link';
import { useRouter } from 'next/router';
import { useForm } from 'react-hook-form';
import { StateOrProvince } from '@systemModels/StateOrProvince';
import {
  editStateOrProvince,
  getStateOrProvince,
} from '@systemServices/StateOrProvinceService';
import StateOrProvinceGeneralInformation from '@systemComponents/StateOrProvinceGeneralInformation';
import { useEffect, useState } from 'react';
import { toastError } from '@commonServices/ToastService';
import { handleUpdatingResponse } from '@commonServices/ResponseStatusHandlingService';
import { STATE_OR_PROVINCE_URL } from 'constants/Common';

const StateOrProvinceEdit: NextPage = () => {
  const router = useRouter();
  const {
    register,
    handleSubmit,
    formState: { errors },
    setValue,
    trigger,
  } = useForm<StateOrProvince>();
  const [stateOrProvince, setStateOrProvince] = useState<StateOrProvince>();
  const [isLoading, setLoading] = useState(false);
  const { id } = router.query;
  const handleSubmitEdit = async (event: StateOrProvince) => {
    if (id) {
      let stateOrProvince: StateOrProvince = {
        id: 0,
        name: event.name,
        code: event.code,
        type: event.type,
        countryId: 0,
      };

      editStateOrProvince(+id, stateOrProvince).then((response) => {
        handleUpdatingResponse(response);
        router.replace(STATE_OR_PROVINCE_URL);
      });
    }
  };

  useEffect(() => {
    if (id) {
      setLoading(true);
      getStateOrProvince(+id).then((data) => {
        if (data.id) {
          setStateOrProvince(data);
          setLoading(false);
        } else {
          toastError(data?.detail);
          setLoading(false);
          router.push(STATE_OR_PROVINCE_URL);
        }
      });
    }
  }, [id]);

  if (isLoading) return <p>Loading...</p>;
  if (!stateOrProvince) return <></>;
  return (
    <>
      <div className="row mt-5">
        <div className="col-md-8">
          <h2>Edit stateOrProvince: {id}</h2>
          <form onSubmit={handleSubmit(handleSubmitEdit)}>
            <StateOrProvinceGeneralInformation
              register={register}
              errors={errors}
              setValue={setValue}
              trigger={trigger}
              stateOrProvince={stateOrProvince}
            />

            <button className="btn btn-primary" type="submit">
              Save
            </button>
            <Link href={`${STATE_OR_PROVINCE_URL}`}>
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

export default StateOrProvinceEdit;
