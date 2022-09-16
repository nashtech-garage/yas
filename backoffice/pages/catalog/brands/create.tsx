import type { NextPage } from 'next'
import { Brand } from '../../../modules/catalog/models/Brand';
import { createBrand } from '../../../modules/catalog/services/BrandService';
import React, {useState} from "react";
import { useForm } from 'react-hook-form'

const BrandCreate: NextPage = () => {
    const { register, handleSubmit, formState } = useForm();
    const { errors } = formState;

    const [generateSlug, setGenerateSlug] = useState<string>();


    function submitButton(event:any){
        console.log("submit")

    }
    const handleSubmitBrand = async (event:any) => {
        let brand : Brand = {
            id: 0,
            name: event.name,
            slug: generateSlug === undefined ? "" : generateSlug,
        }
        console.log(brand)
        brand = await createBrand(brand);
        location.replace("/catalog/brands");
    }
    const slug = require('slug');
    const onNameChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        setGenerateSlug(slug(event.target.value));
    };
    const onSlugChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        setGenerateSlug((event.target.value));
    };
    return (
        <>
            <div className='row mt-5'>
                <div className='col-md-8'>
                    <h2>Create brand</h2>
                    <form onSubmit={handleSubmit(handleSubmitBrand)}>
                        <div className="mb-3">
                            <div className="mb-3">
                                <label className='form-label' htmlFor="slug">Slug</label>
                                <input className="form-control"
                                       {...register('name', { required : true })}
                                       type="text"
                                       id="name"
                                       onChange={onNameChange}
                                       name="name"
                                />
                                {errors.name && errors.name.type == "required" && <p className='text-danger'>Please enter the name brand</p>}

                            </div>
                        </div>
                        <div className="mb-3">
                            <label className='form-label' htmlFor="slug">Slug</label>
                            <input className="form-control"
                                   type="text"
                                   id="slug"
                                   defaultValue={generateSlug}
                                   name="slug"
                                   onChange={onSlugChange}
                            />
                        </div>
                        <button className="btn btn-primary" type="submit" onClick={submitButton}>Submit</button>
                        <a className="btn btn-primary" href="/catalog/brands" style={{background:"red", marginLeft:"30px"}}>Cancel</a>
                    </form>
                </div>
            </div>
        </>
    )
};

export default BrandCreate
