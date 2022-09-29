import type { NextPage } from 'next'
import { useRouter } from 'next/router'
import React, { useEffect, useState} from 'react'
import Link from "next/link";
import { Table} from "react-bootstrap";
import {
    createProductAttributeValueOfProduct, deleteProductAttributeValueOfProductById,
    getAttributeValueOfProduct, updateProductAttributeValueOfProduct
} from "../../../../modules/catalog/services/ProductAttributeValueService";
import {ProductAttributeValue} from "../../../../modules/catalog/models/ProductAttributeValue";
import {
    getProductAttributes
} from "../../../../modules/catalog/services/ProductAttributeService";
import {ProductAttribute} from "../../../../modules/catalog/models/ProductAttribute";
import {ProductAttributeValuePost} from "../../../../modules/catalog/models/ProductAttributeValuePost";

const ProductAttributes: NextPage = () => {
    //Get ID
    const router = useRouter()
    const { id } = router.query

    const [attributeOfCurrentProducts, setAttributeOfCurrentProducts] = useState<ProductAttributeValue[]>([]);
    const [attributeOfProducts, setAttributeOfProducts] = useState<ProductAttributeValue[]>([]);
    const [productAttributes, setProductAttributes] = useState<ProductAttribute[]>([]);
    const [array] = useState<ProductAttribute[]>([]);

    const [nameAttribute, setNameAttribute] = useState(String);
    const [editProductAttributeId , setEditProductAttributeId] = useState(String)
    const [productAttributeId, setProductAttributeId] = useState(String);
    const [attributeName, setAttributeName] = useState(String);

    const [listDeleteProductAttributeId, setListDeleteProductAttributeId] = useState<String[]>([])
    const [listUpdateProductAttributeId, setListUpdateProductAttributeId] = useState<String[]>([])
    const [listCreateProductAttributeId, setListCreateProductAttributeId] = useState<String[]>([])

    const [arrayDeleteProductAttributeId] = useState<String[]>([])
    const [arrayUpdateProductAttributeId] = useState<String[]>([])
    const [arrayCreateProductAttributeId] = useState<String[]>([])

    let checkProductAttributeIdValid: Boolean;

    useEffect(() =>{
        if (id) {
            let checkIdValid: Boolean
            getAttributeValueOfProduct(+id)
                .then((data) => {
                    setAttributeOfProducts(data);
                    setAttributeOfCurrentProducts(data);
                    getProductAttributes()
                        .then((data1) => {
                            if(array.length===0) {
                                data1.forEach((obj1) => {
                                    data.forEach((obj) => {
                                        if (obj1.name === obj.nameProductAttribute) {
                                            checkIdValid = false;
                                        }
                                    })
                                    if (checkIdValid)
                                        array.push(obj1)
                                    checkIdValid = true;
                                })
                                setProductAttributes(array);
                            }
                        });
                });
        }

    }, [id]);

    const editValueAttribute = (event:any) =>{
        event.preventDefault()
        attributeOfProducts.forEach((obj)=>{
            if(obj.id.toString() === editProductAttributeId.toString()){
                obj.value = event.target.value
            }
        })
        attributeOfCurrentProducts.forEach((obj)=>{
            if(obj.id.toString() === editProductAttributeId.toString())
                arrayUpdateProductAttributeId.push(obj.id.toString())
        })
        setListUpdateProductAttributeId(arrayUpdateProductAttributeId)
        setAttributeOfProducts(attributeOfProducts);
    }
    const addNewAttributeOfProduct = (event:any) => {
        event.preventDefault()
        let productAttributeValue : ProductAttributeValue = {
            id: productAttributeId ,
            nameProductAttribute: nameAttribute,
            value: "",
        }
        arrayCreateProductAttributeId.push(productAttributeId)
        setListCreateProductAttributeId(arrayCreateProductAttributeId);
        setProductAttributes( productAttributes.filter(item =>item.name !== nameAttribute));
        setAttributeOfProducts([ ...attributeOfProducts,productAttributeValue]);
        setNameAttribute("")
    };
    const deleteAttributeOfProduct = (event: any) => {
        event.preventDefault()
        let productAttribute : ProductAttribute = {
            id: parseInt(editProductAttributeId),
            name: attributeName,
            productAttributeGroup: ""
        }
        arrayDeleteProductAttributeId.push(editProductAttributeId)
        setListDeleteProductAttributeId(arrayDeleteProductAttributeId);
        setAttributeOfProducts( attributeOfProducts.filter(item =>item.nameProductAttribute !== attributeName ));
        setProductAttributes([ ...productAttributes,productAttribute]);
    };
    const saveProductAttributeOfProduct = async  (event: any) => {
        event.preventDefault()
        checkProductAttributeIdValid = true;
        for (const productAttributes1 of attributeOfProducts) {
            attributeOfCurrentProducts.forEach((currentAttributes)=>{
                if( parseInt(productAttributes1.id) === parseInt(currentAttributes.id)){
                    checkProductAttributeIdValid = false;
                    return;
                }
            })
            if(checkProductAttributeIdValid){
                if (id) {
                    let productAttributeValuePost: ProductAttributeValuePost = {
                        ProductId: (+id).toString(),
                        productAttributeId: productAttributes1.id,
                        value: productAttributes1.value
                    }
                    await createProductAttributeValueOfProduct(productAttributeValuePost);
                }
            }
            checkProductAttributeIdValid=true;
        }
        for (const currentAttributes of attributeOfCurrentProducts) {
            for (const list of listDeleteProductAttributeId) {
                if( parseInt(list.valueOf()) === parseInt(currentAttributes.id)){
                    for (const listCreate of listCreateProductAttributeId) {
                        if(parseInt(currentAttributes.id) === parseInt(listCreate.valueOf()) ){
                            checkProductAttributeIdValid = false;
                            break;
                        }
                    }
                    if(checkProductAttributeIdValid) {
                        await deleteProductAttributeValueOfProductById(parseInt(currentAttributes.id));
                    }
                    checkProductAttributeIdValid = true;
                }
            }
        }
        for (const productAttributes1 of attributeOfProducts) {
            for (const list of listUpdateProductAttributeId) {
                if( parseInt(list.valueOf()) === parseInt(productAttributes1.id)){
                    if(id){
                        let productAttributeValuePost: ProductAttributeValuePost = {
                            ProductId: "",
                            productAttributeId: "",
                            value: productAttributes1.value
                        }
                        await updateProductAttributeValueOfProduct(parseInt(productAttributes1.id),productAttributeValuePost);
                    }
                }
            }
        }
        if(id) {
            location.replace("/catalog/products")
        }
    };


    return (
        <>
            <div className='row mt-5'>
                <div className='col-md-8'>
                    <h2>Update product: #{id}</h2>
                </div>
            </div>
            <div className="mb-3">
                <div className="mb-3">
                    <label className='form-label' htmlFor="available-attributes">Available Attributes</label>
                </div>

                <form onSubmit={addNewAttributeOfProduct}>
                    <div className="mb-3">
                        <label className='form-label' htmlFor="productAG">Attribute Name</label>
                        <select className="form-control" name="attribute-name" id="attribute-name"
                                onChange={(event) => {setNameAttribute(event.target.value.split("/")[0]) ; setProductAttributeId(event.target.value.split("/")[1])}}
                        >
                            <option value={"Select"}>
                                Select
                            </option>
                            {productAttributes?.map((obj) => (
                                <option value={obj.name +"/"+ obj.id}
                                        key={obj.id}
                                >
                                    {obj.name}
                                </option>
                            ))}
                        </select>
                    </div>
                    {
                        ( nameAttribute === "" || nameAttribute === "Select")  ?
                            <button  className="btn btn-primary" type="submit" disabled >Add Attribute</button>
                            :
                            <button  className="btn btn-primary" type="submit" >Add Attribute</button>
                    }
                </form>
            </div>
            <form>
                <div className="mb-3">
                    <div className="mb-3">
                        <label className='form-label' htmlFor="name">Product Attributes</label>
                    </div>
                    <Table>
                        <thead>
                        <tr>
                            <th>Attribute Name</th>
                            <th>Value</th>
                            <th>Actions</th>
                        </tr>
                        </thead>
                        <tbody>
                        {
                            attributeOfProducts?.map((productValue)=>(
                                <React.Fragment key={productValue.id} >
                                    <tr
                                        onClickCapture={() =>{ setAttributeName(productValue.nameProductAttribute) ; setEditProductAttributeId(productValue.id)}}
                                        key={productValue.id}
                                    >
                                        <td>{productValue.nameProductAttribute}</td>
                                        <td>
                                            <input
                                                className="form-control"
                                                type="text"
                                                id="value"
                                                name="value"
                                                defaultValue={productValue.value}
                                                onChange={editValueAttribute}
                                            />
                                        </td>
                                        <td>
                                            <button className="btn btn-outline-danger" type="submit" onClick={deleteAttributeOfProduct}>Delete</button>
                                        </td>
                                    </tr>
                                </React.Fragment>
                            ))
                        }
                        </tbody>
                    </Table>
                </div>
                <button className="btn btn-primary" type="submit" onClick={saveProductAttributeOfProduct}>Save</button>

                <Link href={`/catalog/products/${id}/edit`}>
                    <button className="btn btn-outline-secondary" style={{marginLeft: "30px"}} type="button">Cancel</button>
                </Link>

            </form>
        </>
    )
}
export default ProductAttributes