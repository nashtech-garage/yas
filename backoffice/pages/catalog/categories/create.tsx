import type { NextPage } from 'next'
import Link from 'next/link';
import { useEffect, useState } from 'react';
import { useAccordionButton } from 'react-bootstrap';
import { isNull } from 'util';
import { Category } from '../../../modules/catalog/models/Category';
import { createCategory, getCategories } from '../../../modules/catalog/services/CategoryService';

const CategoryCreate: NextPage = () => {
  var slugify = require('slugify')
  const [categories, setCategories] = useState<Category[]>([]);
  const handleSubmit = async (event:any) => {
    event.preventDefault()
    if(event.target.parentCategory.value==0) event.target.parentCategory.value=null;
    let category : Category = {
      id: 0,
      name: event.target.name.value,
      slug: event.target.slug.value,
      description: event.target.description.value,
      parentId: event.target.parentCategory.value,
      parentName: "",
      metaKeywords: event.target.metaKeywords.value,
      metaDescription: event.target.metaDescription.value,
      displayOrder: event.target.displayOrder.value,
    }
    category = await createCategory(category);
    location.replace("/catalog/categories");
  }
 useEffect(()=>{
  getCategories()
      .then((data) => {
        setCategories(data);
      });
 },[])
  return (
    <>
    <div className='row mt-5'>
      <div className='col-md-8'>
        <h2>Create category</h2>
        <form onSubmit={handleSubmit} name='form'>
        <div className="mb-3">
          <label className='form-label' htmlFor="name">Name</label>
          <input className="form-control" type="text" id="name" name="name" required 
          onChange={(e)=>{
            let slug = document.getElementById("slug")
            slug?.setAttribute('value', slugify(e.target.value, {
              replacement: '-',  // replace spaces with replacement character, defaults to `-`
              remove: undefined, // remove characters that match regex, defaults to `undefined`
              lower: true,      // convert to lower case, defaults to `false`
              strict: false,     // strip special characters except replacement, defaults to `false`
              locale: 'vi',      // language code of the locale to use
              trim: true         // trim leading and trailing replacement chars, defaults to `true`
            }))
          }}/>
        </div>
        <div className="mb-3">
          <label className='form-label' htmlFor="slug">Slug</label>
          <input className="form-control" type="text" id="slug" name="slug" required />
        </div>
        <div className="mb-3">
          <label className='form-label' htmlFor="parentCategory">Parent category</label>
          <select className="form-control" id="parentCategory" name="parentCategory">
            <option value={0}>
                    Top
                  </option>
                  {categories.map((category) => (
                    <option value={category.id} key={category.id}>
                      {category.name}
                    </option>
                  ))}
          </select>
        </div>
        <div className="mb-3">
          <label className='form-label' htmlFor="description">Description</label>
          <textarea className="form-control" id="description" name="description" />
        </div>
        <div className="mb-3">
          <label className='form-label' htmlFor="metaKeywords">Meta Keywords</label>
          <input className="form-control" type="text" id="metaKeywords" name="metaKeywords"  />
        </div>
        <div className="mb-3">
          <label className='form-label' htmlFor="metaDescription">Meta Description</label>
          <textarea className="form-control" id="metaDescription" name="metaDescription" />
        </div>
        <div className="mb-3">
          <label className='form-label' htmlFor="displayOrder">Display Order</label>
          <input className="form-control" type="number" defaultValue={0} id="displayOrder" name="displayOrder" />
        </div>
        <button className="btn btn-primary" type="submit">Save</button>
          &emsp;
          <Link href="/catalog/categories">
          <button className="btn btn-outline-secondary" type="button">Cancel</button>
          </Link>
        </form>
      </div>
    </div>
    </>
  )
};

export default CategoryCreate