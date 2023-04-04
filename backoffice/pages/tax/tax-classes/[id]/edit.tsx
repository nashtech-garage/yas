import { NextPage } from 'next';
import Link from 'next/link';
import { useRouter } from 'next/router';
import { useForm } from 'react-hook-form';
import { TaxClass } from '@taxModels/TaxClass';
import { editTaxClass, getTaxClass } from '@taxServices/TaxClassService';
import TaxClassGeneralInformation from '@taxComponents/TaxClassGeneralInformation';
import { useEffect, useState } from 'react';
import { TAX_CLASS_URL } from 'constants/Common';
import { toastError } from '@commonServices/ToastService';
import { handleUpdatingResponse } from '@commonServices/ResponseStatusHandlingService';

const TaxClassEdit: NextPage = () => {
  const router = useRouter();
  const {
    register,
    handleSubmit,
    formState: { errors },
    setValue,
    trigger,
  } = useForm<TaxClass>();
  const [taxClass, setTaxClass] = useState<TaxClass>();
  const [isLoading, setLoading] = useState(false);
  const { id } = router.query;
  const handleSubmitEdit = async (event: TaxClass) => {
    if (id) {
      let taxClass: TaxClass = {
        id: 0,
        name: event.name,
      };

      editTaxClass(+id, taxClass).then((response) => {
        handleUpdatingResponse(response);
        router.replace(TAX_CLASS_URL);
      });
    }
  };

  useEffect(() => {
    if (id) {
      setLoading(true);
      getTaxClass(+id).then((data) => {
        if (data.id) {
          setTaxClass(data);
          setLoading(false);
        } else {
          toastError(data?.detail);
          setLoading(false);
          router.push(TAX_CLASS_URL);
        }
      });
    }
  }, [id]);

  if (isLoading) return <p>Loading...</p>;
  if (!taxClass) return <></>;
  return (
    <>
      <div className="row mt-5">
        <div className="col-md-8">
          <h2>Edit Tax Class: {id}</h2>
          <form onSubmit={handleSubmit(handleSubmitEdit)}>
            <TaxClassGeneralInformation
              register={register}
              errors={errors}
              setValue={setValue}
              trigger={trigger}
              taxClass={taxClass}
            />

            <button className="btn btn-primary" type="submit">
              Save
            </button>
            <Link href="tax/tax-classes">
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

export default TaxClassEdit;
