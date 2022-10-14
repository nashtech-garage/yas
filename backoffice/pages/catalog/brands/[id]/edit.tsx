import { NextPage } from 'next';
import Link from 'next/link';
import { useRouter } from 'next/router';
import { useEffect, useState } from 'react';
import { useForm } from 'react-hook-form';
import { Brand } from '../../../../modules/catalog/models/Brand';
import { toast } from 'react-toastify';
import { editBrand, getBrand } from '../../../../modules/catalog/services/BrandService';

const BrandEdit: NextPage = () => {
  const router = useRouter();
  const { register, handleSubmit, formState } = useForm();
  const { errors } = formState;
  const [generateSlug, setGenerateSlug] = useState<string>('');
  const slugify = require('slugify');
  const [brand, setBrand] = useState<Brand>();
  const [slug, setSlug] = useState<string>('');
  const { id } = router.query;

  const onNameChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    setSlug(slugify(event.target.value));
  };
  const onSlugChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    setSlug(event.target.value);
  };

  const handleSubmitEdit = async (event: any) => {
    event.preventDefault();

    if (id) {
      let brand: Brand = {
        id: 0,
        name: event.target.name.value,
        slug: slug,
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
      getBrand(+id).then((res) => {
        setBrand(res);
        setSlug(res?.slug);
      });
    }
  }, [id]);

  return (
    <>
      <div className="row mt-5">
        <div className="col-md-8">
          <h2>Edit brand</h2>
          <form onSubmit={handleSubmitEdit}>
            <div className="mb-3">
              <div className="mb-3">
                <label className="form-label" htmlFor="slug">
                  Name
                </label>
                <input
                  defaultValue={brand?.name}
                  className="form-control"
                  required
                  type="text"
                  id="name"
                  name="name"
                  onChange={onNameChange}
                />
              </div>
            </div>
            <div className="mb-3">
              <label className="form-label" htmlFor="slug">
                Slug
              </label>
              <input
                required
                defaultValue={slug}
                className="form-control"
                type="text"
                id="slug"
                name="slug"
                onChange={onSlugChange}
              />
            </div>
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
