import type { NextPage } from 'next'
import { useRouter } from 'next/router'
import { useEffect, useState } from 'react'
import { Product } from '../../../../modules/catalog/models/Product'
import { ProductThumbnail } from '../../../../modules/catalog/models/ProductThumbnail'
import { getProduct, updateProduct } from '../../../../modules/catalog/services/ProductService'

const ProductEdit: NextPage = () => {
  const router = useRouter()
  const { id } = router.query;
  //Variables
  const [thumbnail, setThumbnail] = useState<File>();
  const [thumbnailURL, setThumbnailURL] = useState<string>();
  //Get product detail
  const [product, setProduct] = useState<ProductThumbnail>();
  const [isLoading, setLoading] = useState(false);

  useEffect(() => {
    setLoading(true);
    if(id){
      getProduct(+id)
      .then((data) => {
        setProduct(data);
        setLoading(false);
      });
    }
  }, []);

  //Handle
  const onThumbnailSelected = (event: React.ChangeEvent<HTMLInputElement>) => {
    if (event.target.files && event.target.files[0]) {
      const i = event.target.files[0];
      setThumbnail(i);
      setThumbnailURL(URL.createObjectURL(i));
    }
  };
  const handleSubmit = async (event: any) => {
    event.preventDefault()
    let product: Product = {
      id: 0,
      name: event.target.name.value,
      slug: event.target.slug.value,
      description: event.target.description.value,
      shortDescription: event.target.shortDescription.value,
      specification: event.target.specification.value,
      sku: event.target.sku.value,
      gtin: event.target.gtin.value,
      metaKeyword: event.target.metaKeyword.value,
      descriptionMetaKeyword: event.target.descriptionMetaKeyword.value,
    }
    if (!thumbnail) {
      alert("A thumbnail is required.")
    }
    else {
      if(id){
        updateProduct(+id, product, thumbnail);
        location.replace("/catalog/products");
      }
    }
  }

  console.log(product);
  if (isLoading) return <p>Loading...</p>;
  if (!product) return <p>No product</p>;
  return (
    <>
      <div className='row mt-5'>
        <div className='col-md-8'>
          <h2>Update product</h2>
          <form onSubmit={handleSubmit}>
            <div className="mb-3">
              <label className='form-label' htmlFor="name">Name <span style={{'color': 'red'}}>*</span></label>
              <input defaultValue={product.name} className="form-control" type="text" id="name" name="name" required />
            </div>
            <div className="mb-3">
              <label className='form-label' htmlFor="slug">Slug <span style={{'color': 'red'}}>*</span></label>
              <input defaultValue={product.slug} className="form-control" type="text" id="slug" name="slug" required />
            </div>
            <div className="mb-3">
              <label className='form-label' htmlFor="short-description">Short Description <span style={{'color': 'red'}}>*</span></label>
              <input defaultValue={product.shortDescription} type="text" className="form-control" id="short-description" name="shortDescription" />
            </div>
            <div className="mb-3">
              <label className='form-label' htmlFor="description">Description</label>
              <textarea defaultValue={product.description} className="form-control" id="description" name="description" />
            </div>
            <div className="mb-3">
              <label className='form-label' htmlFor="specification">Specification <span style={{'color': 'red'}}>*</span></label>
              <input defaultValue={product.specification} type="text" className="form-control" id="specification" name="specification" />
            </div>
            <div className="mb-3">
              <label className='form-label' htmlFor="sku">SKU <span style={{'color': 'red'}}>*</span></label>
              <input defaultValue={product.sku} type="text" className="form-control" id="sku" name="sku" />
            </div>
            <div className="mb-3">
              <label className='form-label' htmlFor="gtin">GTIN <span style={{'color': 'red'}}>*</span></label>
              <input defaultValue={product.gtin} type="text" className="form-control" id="gtin" name="gtin" />
            </div>
            <div className="mb-3">
              <label className='form-label' htmlFor="meta-keyword">Meta Keyword <span style={{'color': 'red'}}>*</span></label>
              <input defaultValue={product.metaKeyword} type="text" className="form-control" id="meta-keyword" name="metakeyword" />
            </div>
            <div className="mb-3">
              <label className='form-label' htmlFor="meta-keyword-description">Meta Keyword Description</label>
              <input defaultValue={product.descriptionMetaKeyword} type="text" className="form-control" id="meta-keyword-description" name="metaKeywordDescription" />
            </div>
            <div className='mb-3'>
              <label className='form-label' htmlFor="thumbnail">Thumbnail <span style={{'color': 'red'}}>*</span></label>
              <input className="form-control" type="file" name="thumbnail" onChange={onThumbnailSelected} />
              <img style={{ width: '150px' }} src={thumbnailURL ? thumbnailURL : product.thumbnailUrl} />
            </div>
            <button className="btn btn-primary" type="submit">Submit</button>
          </form>
        </div>
      </div>
    </>
  )
}

export default ProductEdit