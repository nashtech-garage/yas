import { NextPage } from 'next';
import Link from 'next/link';
import { useRouter } from 'next/router';
import { useForm } from 'react-hook-form';
import { Brand } from '../../../../modules/catalog/models/Brand';
import { toast } from 'react-toastify';
import { editBrand, getBrand } from '../../../../modules/catalog/services/BrandService';
import BrandGeneralInformation from '../../../../modules/catalog/components/BrandGeneralInformation';
import { useEffect, useState } from 'react';
import { ClipLoader } from 'react-spinners';

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
      };

      editBrand(+id, brand).then((response) => {
        if (response.status === 204) {
          toast.success('Update successfully');
          location.replace('/catalog/brands');
        } else if (response.title === 'Not found') {
          toast.error(response.detail);
          location.replace('/catalog/brands');
        } else if (response.title === 'Bad request') {
          toast.error(response.detail);
        } else {
          toast.error('Update failed');
          location.replace('/catalog/brands');
        }
      });
    }
  };

  useEffect(() => {
    if (id) {
      setLoading(true);
      getBrand(+id).then((data) => {
        if (data.id) {
          setBrand(data);
          setLoading(false);
        } else {
          toast(data?.detail);
          setLoading(false);
          router.push('/catalog/brands');
        }
      });
    }
  }, [id]);

  if (isLoading) return <p>Loading...</p>;
  return (
    <div className="row mt-5">
      <div className="col-md-8">
        <h2>Edit brand: {id}</h2>
        <form onSubmit={handleSubmit(handleSubmitEdit)}>
          <BrandGeneralInformation
            register={register}
            errors={errors}
            setValue={setValue}
            trigger={trigger}
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
  );
};

export default BrandEdit;
