import type { NextPage } from 'next'
import Link from 'next/link'
import { useRouter } from 'next/router'
import { useEffect, useState } from 'react'
import { Category } from '../../../../modules/catalog/models/Category'
import { getCategories, getCategory, updateCategory } from '../../../../modules/catalog/services/CategoryService'

const CategoryEdit: NextPage = () => {
  const router = useRouter()
  const { id } = router.query
  var slugify = require('slugify') 
  const [categories, setCategories] = useState<Category[]>([]);
  const [category, setCategory] = useState<Category>();
  const [slug, setSlug] = useState<string>();
  const handleSubmitEdit = async (event:any) => {
    event.preventDefault()
    if(event.target.parentCategory.value==0) event.target.parentCategory.value = null;
    let category : Category = {
      id: 0,
      name: event.target.name.value,
      slug: event.target.slug.value,
      metaKeywords: event.target.metaKeywords.value,
      metaDescription: event.target.metaDescription.value,
      parentId: event.target.parentCategory.value,
      parentName: "",
      displayOrder: event.target.displayOrder.value,
      description: event.target.description.value,
    }
    
    if(id){
    updateCategory(+id, category)
    .then((response)=>{
      if(response.status===204){
        alert("Update successfully")
        location.replace("/catalog/categories");
      }
      else if(response.title==='Not found'){
        alert(response.detail)
        location.replace("/catalog/categories");
      }
      else if(response.title==='Bad request'){
        alert(response.detail)
      }
      else{
        alert("Update failed")
        location.replace("/catalog/categories");
      }
    })
    }
  }
  useEffect(()=>{
    if(id)
      getCategory(+id)
      .then((data)=>{
        setCategory(data);
        setSlug(data.slug)
      })
   },[id])

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
          <h2>Edit Category</h2>
          <form onSubmit={handleSubmitEdit} name='form'>
        <div className="mb-3">
          <label className='form-label' htmlFor="name">Name</label>
          <input className="form-control" type="text" id="name" name="name" defaultValue={category?.name} required 
          onChange={(e)=>{
            let generate = (slugify(e.target.value, {
              replacement: '-',  
              remove: undefined, 
              lower: true, 
              strict: false,  
              locale: 'vi', 
              trim: true    
            }))
            setSlug(generate);
            console.log(slug)
          }}/>
        </div>
        <div className="mb-3">
          <label className='form-label' htmlFor="slug">Slug</label>
          <input className="form-control" type="text" id="slug" name="slug" defaultValue={slug} required />
        </div>
        <div className="mb-3">
          <label className='form-label' htmlFor="parentCategory">Parent category</label>
          <select className="form-control" id="parentCategory" name="parentCategory" >
            <option value={0}>
                    Top
                  </option>
                  {categories.map((c) => {
                    if(c.id == category?.parentId){
                        return(<option selected value={c.id} key={c.id}>
                                {c.name}
                                </option>)
                    }
                    else{
                        return(<option value={c.id} key={c.id}>
                                {c.name}
                                </option>)
                    }
                  }
                  )}
          </select>
        </div>
        <div className="mb-3">
          <label className='form-label' htmlFor="description" >Description</label>
          <textarea className="form-control" id="description" defaultValue={category?.description} name="description" />
        </div>
        <div className="mb-3">
          <label className='form-label' htmlFor="metaKeywords">Meta Keywords</label>
          <input className="form-control" type="text" id="metaKeywords" name="metaKeywords" defaultValue={category?.metaKeywords} />
        </div>
        <div className="mb-3">
          <label className='form-label' htmlFor="metaDescription">Meta Description</label>
          <textarea className="form-control" id="metaDescription" name="metaDescription" defaultValue={category?.metaDescription}/>
        </div>
        <div className="mb-3">
          <label className='form-label' htmlFor="displayOrder" >Display Order</label>
          <input className="form-control" type="number" defaultValue={category?.displayOrder} min = {0} id="displayOrder" name="displayOrder" />
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
  }
  export default CategoryEdit