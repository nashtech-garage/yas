import React, { useEffect, useState } from 'react';
import { useForm } from 'react-hook-form';
import Link from 'next/link';
import { useRouter } from 'next/router';
import { Table } from 'react-bootstrap';
import { ProductAttribute } from '@catalogModels/ProductAttribute';
import { getProductAttributes } from '@catalogServices/ProductAttributeService';
import { toast } from 'react-toastify';
import {
  FromProductTemplate,
  ProductAttributeOfTemplate,
} from '@catalogModels/FormProductTemplate';
import { createProductTemplate } from '@catalogServices/ProductTemplateService';
import { handleCreatingResponse } from '../../../common/services/ResponseStatusHandlingService';
import { PRODUCT_TEMPLATE_URL } from '../../../constants/Common';

const ProductTemplateCreate = () => {
  const { setValue, getValues } = useForm<FromProductTemplate>();
  const [productAttributes, setProductAttributes] = useState<ProductAttribute[]>([]);
  const [selectedAttributes, setSelectedAttributes] = useState<string[]>([]);
  const [checkHidden, setCheckHidden] = useState<boolean>(false);
  const { formState, register, handleSubmit } = useForm<FromProductTemplate>();
  const { errors } = formState;
  const router = useRouter();

  useEffect(() => {
    getProductAttributes().then((data) => {
      setProductAttributes(data);
    });
  }, []);

  const handleSelectAttribute = (event: React.ChangeEvent<HTMLSelectElement>) => {
    event.preventDefault();
    let attName = (document.getElementById('attribute') as HTMLSelectElement).value;
    if (attName !== '0') {
      setCheckHidden(true);
    }
  };
  const onAddAttribute = (event: React.MouseEvent<HTMLElement>) => {
    event.preventDefault();
    let attName = (document.getElementById('attribute') as HTMLSelectElement).value;
    if (attName === '0') {
      toast.info('No attribute has been selected yet');
    } else {
      let attributes = getValues('productAttributeTemplates') || [];
      if (selectedAttributes.indexOf(attName) === -1) {
        setSelectedAttributes([...selectedAttributes, attName]);
        let attribute = productAttributes.find((attribute) => attribute.name === attName);
        if (attribute) {
          let newAttr: ProductAttributeOfTemplate = {
            productAttributeId: attribute.id,
            displayOrder: 0,
          };
          attributes.push(newAttr);
          setValue('productAttributeTemplates', attributes);
        }
      } else {
        toast.info(`${attName} is selected`);
      }
    }
  };
  const onDeleteSelectedAttribute = (event: React.MouseEvent<HTMLElement>, attName: string) => {
    event.preventDefault();
    let attributes = getValues('productAttributeTemplates') || [];
    const attribute = productAttributes.find((attribute) => attribute.name === attName);
    let filter = selectedAttributes.filter((item) => item !== attName);
    let attFilter = attributes.filter((_att) => _att.productAttributeId !== attribute?.id);
    setSelectedAttributes(filter);
    setValue('productAttributeTemplates', attFilter);
  };
  const handleSubmitProductTemplate = async (event: FromProductTemplate) => {
    setValue('name', event.name);
    let fromProductTemplate: FromProductTemplate = {
      name: event.name,
      productAttributeTemplates: getValues('productAttributeTemplates') || [],
    };
    let response = await createProductTemplate(fromProductTemplate);

    if (response.status === 201) {
      router.replace(PRODUCT_TEMPLATE_URL);
    }

    handleCreatingResponse(response);
  };

  return (
    <>
      <div className="row mt-5">
        <div className="col-md-8">
          <h2>Create Product Templates</h2>
          <form onSubmit={handleSubmit(handleSubmitProductTemplate)}>
            <div className="d-flex flex-column">
              <div className="mb-4">
                <label className="form-label" htmlFor="name">
                  Name
                </label>
                <input
                  className="form-control"
                  {...register('name', { required: true })}
                  type="text"
                  id="name"
                  name="name"
                />
                {errors.name && errors.name.type == 'required' && (
                  <p className="text-danger">Please enter the name</p>
                )}
              </div>
              <div className="mb-5 d-flex align-items-center">
                <label className="form-label w-50" htmlFor="attribute">
                  Set availability
                </label>
                <select
                  className="form-control me-3"
                  id="attribute"
                  defaultValue="0"
                  onChange={handleSelectAttribute}
                >
                  <option value="0" disabled hidden>
                    Select Attribute
                  </option>
                  {(productAttributes || []).map((att) => (
                    <option value={att.name} key={att.id}>
                      {att.name}
                    </option>
                  ))}
                </select>
                {checkHidden ? (
                  <button
                    className="form-control w-50 btn btn-primary"
                    style={{ height: 'auto', width: 'auto' }}
                    onClick={onAddAttribute}
                  >
                    More features
                  </button>
                ) : (
                  <button
                    className="form-control w-50 btn btn-secondary"
                    disabled
                    style={{ height: 'auto', width: 'auto' }}
                  >
                    More features
                  </button>
                )}
              </div>
              <div className="mb-3 d-flex justify-content-evenly">
                <label className="form-label w-50" htmlFor="attribute">
                  Added calculations
                </label>
                <Table>
                  <thead>
                    <tr></tr>
                  </thead>
                  <tbody>
                    {Array.isArray(selectedAttributes)
                      ? selectedAttributes.map((item) => (
                          <tr key={item}>
                            <th>{item}</th>
                            <th style={{ textAlign: 'right' }}>
                              <button
                                className="btn btn-danger"
                                onClick={(event) => onDeleteSelectedAttribute(event, item)}
                              >
                                <i className="bi bi-x"></i>
                              </button>
                            </th>
                          </tr>
                        ))
                      : null}
                  </tbody>
                </Table>
                <div className=" w-50"></div>
              </div>
            </div>
            <button className="btn btn-primary" type="submit">
              Save
            </button>
            <Link href="/catalog/product-templates">
              <button className="btn btn-danger" style={{ marginLeft: '30px' }}>
                Cancel
              </button>
            </Link>
          </form>
        </div>
      </div>
    </>
  );
};

export default ProductTemplateCreate;
