import { NextPage } from 'next';
import Link from 'next/link';
import { useRouter } from 'next/router';
import { useEffect, useState } from 'react';
import { useForm } from 'react-hook-form';

import BrandGeneralInformation from '@catalogComponents/BrandGeneralInformation';
import { Brand } from '@catalogModels/Brand';
import { editBrand, getBrand } from '@catalogServices/BrandService';
import { handleUpdatingResponse } from '@commonServices/ResponseStatusHandlingService';
import { toastError } from '@commonServices/ToastService';
import { BRAND_URL, ResponseStatus } from '@constants/Common';

const BrandEdit: NextPage = () => {
  const router = useRouter();
  const {
    register,
    handleSubmit,
    formState: { errors },
    setValue,
    trigger,
  } = useForm<Brand>();
  const [brand, setBrand] = useState<Brand>();
  const [isLoading, setLoading] = useState(false);
  const { id } = router.query;

  const handleSubmitEdit = async (event: Brand) => {
    if (id) {
      let brand: Brand = {
        id: 0,
        name: event.name,
        slug: event.slug,
        isPublish: event.isPublish,
      };

      const brandResponse = await editBrand(+id, brand);
      if (brandResponse.status === ResponseStatus.SUCCESS) {
        router.replace(BRAND_URL).catch((error) => console.log(error));
      }
      handleUpdatingResponse(brandResponse);
    }
  };

  useEffect(() => {
    if (id) {
      setLoading(true);
      getBrand(+id)
        .then((data) => {
          if (data.id) {
            setBrand(data);
            setLoading(false);
          } else {
            toastError(data?.detail);
            setLoading(false);
            router.push(BRAND_URL).catch((error) => console.log(error));
          }
        })
        .catch((error) => console.log(error));
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [id]);

  if (isLoading) return <p>Loading...</p>;
  if (!brand) return <></>;
  return (
    <>
      <div className="row mt-5">
        <div className="col-md-8">
          <h2>Edit brand: {id}</h2>
          <form onSubmit={handleSubmit(handleSubmitEdit)}>
            <BrandGeneralInformation
              register={register}
              errors={errors}
              setValue={setValue}
              trigger={trigger}
              brand={brand}
            />

            <button className="btn btn-primary" type="submit">
              Save
            </button>
            <Link href="/catalog/brands">
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

export default BrandEdit;
