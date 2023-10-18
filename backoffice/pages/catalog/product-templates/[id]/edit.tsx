import Link from 'next/link';
import { useRouter } from 'next/router';
import { useEffect, useState } from 'react';
import { useForm } from 'react-hook-form';
import { Table } from 'react-bootstrap';
import { ProductAttribute } from '@catalogModels/ProductAttribute';
import { getProductAttributes } from '@catalogServices/ProductAttributeService';
import { toast } from 'react-toastify';
import {
  FromProductTemplate,
  ProductAttributeOfTemplate,
  ProductTemplate,
} from '@catalogModels/FormProductTemplate';
import { getProductTemplate, updateProductTemplate } from '@catalogServices/ProductTemplateService';
import { handleUpdatingResponse } from '../../../../common/services/ResponseStatusHandlingService';
import { PRODUCT_TEMPLATE_URL } from '../../../../constants/Common';

const ProductTemplateEdit = () => {
  const router = useRouter();
  let { id }: any = router.query;
  const { setValue, getValues } = useForm<FromProductTemplate>();
  const [isLoading, setLoading] = useState(false);
  const [productAttributes, setProductAttributes] = useState<ProductAttribute[]>([]);
  const [checkHidden, setCheckHidden] = useState<boolean>(false);
  const [productTemplate, setProductTemplate] = useState<ProductTemplate>();
  const [selectedAttributes, setSelectedAttributes] = useState<string[]>([]);
  const { formState, register, handleSubmit } = useForm<FromProductTemplate>();
  const { errors } = formState;

  useEffect(() => {
    setLoading(true);
    if (id !== undefined) {
      getProductTemplate(parseInt(id)).then((data) => {
        setProductTemplate(data);
        setValue('name', data.name);
        console.log(data);
        let attributes = [];
        let att = [];
        for (let i = 0; i < data.productAttributeTemplates.length; i++) {
          let newAttr: ProductAttributeOfTemplate = {
            ProductAttributeId: data.productAttributeTemplates[i].productAttribute.id,
            displayOrder: data.productAttributeTemplates[i].displayOrder,
          };
          attributes.push(newAttr);
          setValue('ProductAttributeTemplates', attributes);
          att.push(data.productAttributeTemplates[i].productAttribute.name);
          setSelectedAttributes(att);
        }
        setLoading(false);
      });
    }
  }, [id]);

  useEffect(() => {
    getProductAttributes().then((data) => {
      setProductAttributes(data);
    });
  }, []);

  const onAddAttribute = (event: React.MouseEvent<HTMLElement>) => {
    event.preventDefault();
    let attName = (document.getElementById('attribute') as HTMLSelectElement).value;
    if (attName === '0') {
      toast.info('No attribute has been selected yet');
    } else {
      let attributes = getValues('ProductAttributeTemplates') || [];
      if (selectedAttributes.indexOf(attName) === -1) {
        setSelectedAttributes([...selectedAttributes, attName]);
        let attribute = productAttributes.find((attribute) => attribute.name === attName);
        if (attribute) {
          let newAttr: ProductAttributeOfTemplate = {
            ProductAttributeId: attribute.id,
            displayOrder: 0,
          };
          attributes.push(newAttr);
          setValue('ProductAttributeTemplates', attributes);
        }
      } else {
        toast.info(`${attName} is selected`);
      }
    }
  };

  const handleSelectAttribute = (event: React.ChangeEvent<HTMLSelectElement>) => {
    event.preventDefault();
    let attName = (document.getElementById('attribute') as HTMLSelectElement).value;
    if (attName !== '0') {
      setCheckHidden(true);
    }
  };

  const onDeleteSelectedAttribute = (event: React.MouseEvent<HTMLElement>, attName: string) => {
    event.preventDefault();
    let attributes = getValues('ProductAttributeTemplates') || [];
    const attribute = productTemplate?.productAttributeTemplates?.find(
      (attribute) => attribute?.productAttribute?.name === attName
    );
    let filter = selectedAttributes.filter((item) => item !== attName);
    let attFilter = attributes.filter(
      (_att) => _att.ProductAttributeId !== attribute?.productAttribute?.id
    );
    setSelectedAttributes(filter);
    setValue('ProductAttributeTemplates', attFilter);
  };

  const handleSubmitProductTemplate = async (event: FromProductTemplate) => {
    setValue('name', event.name);
    let fromProductTemplate: FromProductTemplate = {
      name: event.name,
      ProductAttributeTemplates: getValues('ProductAttributeTemplates'),
    };
    console.log(fromProductTemplate);
    let response = await updateProductTemplate(id, fromProductTemplate);
    handleUpdatingResponse(response);
    console.log(response);
    router.replace(PRODUCT_TEMPLATE_URL);
  };

  if (isLoading) return <p>Loading...</p>;
  if (!productTemplate) return <p>No Product Templates</p>;
  return (
    <>
      <div className="row mt-5">
        <div className="col-md-8">
          <h2>Edit Product Templates</h2>
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
                  defaultValue={productTemplate ? productTemplate.name : ''}
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
                  {Array.isArray(selectedAttributes)
                    ? selectedAttributes.map((item) => (
                        <tbody>
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
                        </tbody>
                      ))
                    : null}
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

export default ProductTemplateEdit;
