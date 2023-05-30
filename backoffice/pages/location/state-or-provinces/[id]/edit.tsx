import { NextPage } from 'next';
import Link from 'next/link';
import { useRouter } from 'next/router';
import { useEffect, useState } from 'react';
import { useForm } from 'react-hook-form';

import { handleUpdatingResponse } from '@commonServices/ResponseStatusHandlingService';
import { toastError } from '@commonServices/ToastService';
import StateOrProvinceGeneralInformation from '@locationComponents/StateOrProvinceGeneralInformation';
import { StateOrProvince } from '@locationModels/StateOrProvince';
import { editStateOrProvince, getStateOrProvince } from '@locationServices/StateOrProvinceService';
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

      editStateOrProvince(+id, stateOrProvince)
        .then((response) => {
          handleUpdatingResponse(response);
          router.replace(STATE_OR_PROVINCE_URL).catch((error) => console.log(error));
        })
        .catch((error) => console.log(error));
    }
  };

  useEffect(() => {
    if (id) {
      setLoading(true);
      getStateOrProvince(+id)
        .then((data) => {
          if (data.id) {
            setStateOrProvince(data);
            setLoading(false);
          } else {
            toastError(data?.detail);
            setLoading(false);
            router.push(STATE_OR_PROVINCE_URL).catch((error) => console.log(error));
          }
        })
        .catch((error) => console.log(error));
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
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
