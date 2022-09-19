import type { NextPage } from 'next'
import { useState } from "react";
import { Product } from '../../../modules/catalog/models/Product';
import { createProduct } from '../../../modules/catalog/services/ProductService';

const ProductCreate: NextPage = () => {
  const [thumbnail, setThumbnail] = useState<File>();
  const [thumbnailURL, setThumbnailURL] = useState<string>();

  const onThumbnailSelected = (event: React.ChangeEvent<HTMLInputElement>) => {
    if (event.target.files && event.target.files[0]) {
      const i = event.target.files[0];

      setThumbnail(i);
      setThumbnailURL(URL.createObjectURL(i));
    }
  };

  const handleSubmit = async (event:any) => {
    event.preventDefault()

    let product : Product = {
      id: 0,
      name: event.target.name.value,
      slug: event.target.slug.value,
      description: event.target.description.value,
      shortDescription: '',
      specification: '',
      sku: '',
      gtin: '',
      metaKeyword: '',
      metaDescription: ''
    }
    if(!thumbnail){
      alert("A thumbnail is required.")
    }
    else {
      product = await createProduct(product, thumbnail);
      location.replace("/catalog/products");
    }
  }
  
  return (
    <>
    <div className='row mt-5'>
      <div className='col-md-8'>
        <h2>Create product</h2>
        <form onSubmit={handleSubmit}>
        <div className="mb-3">
          <label className='form-label' htmlFor="name">Name</label>
          <input className="form-control" type="text" id="name" name="name" required />
        </div>
        <div className="mb-3">
          <label className='form-label' htmlFor="slug">Slug</label>
          <input className="form-control" type="text" id="slug" name="slug" required />
        </div>
        <div className="mb-3">
          <label className='form-label' htmlFor="description">Description</label>
          <textarea className="form-control" id="description" name="description" />
        </div>
        <div className='mb-3'>
          <label className='form-label' htmlFor="thumbnail">Description</label>
          <input className="form-control" type="file" name="thumbnail" onChange={onThumbnailSelected} />
          <img style={{width: '150px'}} src={thumbnailURL} />
        </div>
          <button className="btn btn-primary" type="submit">Submit</button>
        </form>
      </div>
    </div>
    </>
  )
}
  
export default ProductCreate