import type {NextPage} from 'next'
import { ProductAttribute } from '../../../modules/catalog/models/ProductAttribute';
import {useForm} from "react-hook-form";
import React, {useEffect, useState} from "react";
import Link from "next/link";
import {getProductAttributeGroups} from "../../../modules/catalog/services/ProductAttributeGroupService";
import {ProductAttributeGroup} from "../../../modules/catalog/models/ProductAttributeGroup";
import {createProductAttribute} from "../../../modules/catalog/services/ProductAttributeService";

interface ProductAttributeId  {
    name: string
    productAttributeGroupId: string
}
const ProductAttributeCreate: NextPage = () => {
    const { formState , register, handleSubmit } = useForm();
    const [productAttributeGroup, setProductAttributeGroup] = useState<ProductAttributeGroup[]>([]);
    const [idGroup, setIdGroup] = useState(String);

    const { errors } = formState;
    useEffect(()=>{
        getProductAttributeGroups()
            .then((data) => {
                setProductAttributeGroup(data);
            });
    }, [])
    const handleSubmitProductAttribute = async (event:any) => {
        if(idGroup === "0") setIdGroup("")
        let productAttribute : ProductAttributeId = {
            name: event.name,
            productAttributeGroupId: idGroup,
        }
        console.log(productAttribute)
        await createProductAttribute(productAttribute);
        location.replace("/catalog/product-attributes");
    }
    return (
        <>
            <div className='row mt-5'>
                <div className='col-md-8'>
                    <h2>Create Product Attribute</h2>
                    <form onSubmit={handleSubmit(handleSubmitProductAttribute)}>
                        <div className="mb-3">
                            <div className="mb-3">
                                <label className='form-label' htmlFor="name">Name</label>
                                <input className="form-control"
                                       {...register('name', { required : true })}
                                       type="text"
                                       id="name"
                                       name="name"
                                />
                                {errors.name && errors.name.type == "required" && <p className='text-danger'>Please enter the name</p>}
                            </div>
                        </div>
                        <div className="mb-3">
                            <label className='form-label' htmlFor="productAG">Group</label>
                            <select className="form-control" name="groupId" id="groupId"
                                    onChange={(event) => setIdGroup((event.target.value))}
                            >
                                <option value={0}>
                                </option>
                                {productAttributeGroup.map((productAttributeGroup) => (
                                    <option value={productAttributeGroup.id}
                                            key={productAttributeGroup.id}
                                    >
                                        {productAttributeGroup.name}
                                    </option>
                                ))}
                            </select>
                        </div>
                        <button className="btn btn-primary" type="submit" >Save</button>
                        <Link href="/catalog/product-attributes">
                            <button className="btn btn-primary" style={{  marginLeft:"30px"}}>Cancel</button>
                        </Link>
                    </form>
                </div>
            </div>
        </>
    )
};

export default ProductAttributeCreate