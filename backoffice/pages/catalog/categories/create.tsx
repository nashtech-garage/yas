import type { NextPage } from 'next'
import { Category } from '../../../modules/catalog/models/Category';
import { createCategory } from '../../../modules/catalog/services/CategoryService';

const CategoryCreate: NextPage = () => {
  const handleSubmit = async (event:any) => {
    event.preventDefault()

    let category : Category = {
      id: 0,
      name: event.target.name.value,
      slug: event.target.slug.value,
      description: event.target.description.value
    }

    category = await createCategory(category);
    location.replace("/catalog/categories");
  }

  return (
    <>
    <div className='row mt-5'>
      <div className='col-md-8'>
        <h2>Create category</h2>
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
          <button className="btn btn-primary" type="submit">Submit</button>
        </form>
      </div>
    </div>
    </>
  )
};

export default CategoryCreate