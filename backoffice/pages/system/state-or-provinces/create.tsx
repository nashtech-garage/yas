import type { NextPage } from 'next';
import { StateOrProvince } from '@systemModels/StateOrProvince';
import { createStateOrProvince } from '@systemServices/StateOrProvinceService';
import React from 'react';
import { useForm } from 'react-hook-form';
import Link from 'next/link';
import { useRouter } from 'next/router';
import StateOrProvinceGeneralInformation from '@systemComponents/StateOrProvinceGeneralInformation';
import { STATE_OR_PROVINCE_URL } from 'constants/Common';
import { handleCreatingResponse } from '@commonServices/ResponseStatusHandlingService';

const StateOrProvinceCreate: NextPage = () => {
  const router = useRouter();
  const { countryId } = router.query;
  const {
    register,
    handleSubmit,
    formState: { errors },
    setValue,
    trigger,
  } = useForm<StateOrProvince>();
  const handleSubmitStateOrProvince = async (event: StateOrProvince) => {
    if (countryId) {
      let stateOrProvince: StateOrProvince = {
        id: 0,
        name: event.name,
        code: event.code,
        type: event.type,
        countryId: parseInt(countryId.toString()),
      };
      let response = await createStateOrProvince(stateOrProvince);
      handleCreatingResponse(response);
      router.replace(STATE_OR_PROVINCE_URL);
    }
  };

  return (
    <>
      <div className="row mt-5">
        <div className="col-md-8">
          <h2>Create state Or Province</h2>
          <form onSubmit={handleSubmit(handleSubmitStateOrProvince)}>
            <StateOrProvinceGeneralInformation
              register={register}
              errors={errors}
              setValue={setValue}
              trigger={trigger}
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

export default StateOrProvinceCreate;
