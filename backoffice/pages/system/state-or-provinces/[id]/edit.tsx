import { NextPage } from 'next';
import Link from 'next/link';
import { useRouter } from 'next/router';
import { useForm } from 'react-hook-form';
import { StateOrProvince } from '../../../../modules/catalog/models/StateOrProvince';
import { editStateOrProvince, getStateOrProvince } from '../../../../modules/system/services/StateOrProvinceService';
import StateOrProvinceGeneralInformation from '../../../../modules/system/components/StateOrProvinceGeneralInformation';
import { useEffect, useState } from 'react';
import { BRAND_URL } from '../../../../constants/Common';
import { toastError } from '../../../../modules/catalog/services/ToastService';

import { handleUpdatingResponse } from '../../../../modules/catalog/services/ResponseStatusHandlingService';

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
  const { id } = router.query.get('countryId');
  const handleSubmitEdit = async (event: StateOrProvince) => {
    if (id) {
      let stateOrProvince: StateOrProvince = {
        id: 0,
        name: event.name,
        code: event.code,
        type: event.type,
      };

      editStateOrProvince(+id, stateOrProvince).then((response) => {
        handleUpdatingResponse(response);
        router.replace('/system/state-or-provinces');
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
          router.push('/system/state-or-provinces');
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
            <Link href="/system/state-or-provinces">
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
