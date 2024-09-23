import Link from 'next/link';
import type { NextPage } from 'next';
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
import { PRODUCT_TEMPLATE_URL, ResponseStatus } from '../../../../constants/Common';

const ProductTemplateEdit: NextPage = () => {
  const router = useRouter();
  let { id }: any = router.query;
  const { formState, register, handleSubmit } = useForm<FromProductTemplate>();
  const { errors } = formState;
  const [isLoading, setLoading] = useState(false);
  const [checkButton, setCheckButton] = useState<boolean>(false);
  const [productTemplate, setProductTemplate] = useState<ProductTemplate>();
  const { setValue, getValues } = useForm<FromProductTemplate>();
  const [selectedAtts, setSelectedAtts] = useState<string[]>([]);
  const [productAtts, setProductAtts] = useState<ProductAttribute[]>([]);

  useEffect(() => {
    setLoading(true);
    if (id !== undefined) {
      getProductAttributes()
        .then((data) => {
          setProductAtts(data);
        })
        .catch((error) => console.log(error));

      getProductTemplate(parseInt(id)).then((data) => {
        setProductTemplate(data);
        setValue('name', data.name);
        let attributes = [];
        let att = [];
        for (const element of data.productAttributeTemplates) {
          let attribute: ProductAttributeOfTemplate = {
            productAttributeId: element.productAttribute.id,
            displayOrder: element.displayOrder,
          };
          attributes.push(attribute);
          att.push(element.productAttribute.name);
          setSelectedAtts(att);
          setValue('productAttributeTemplates', attributes);
        }
      });
      setLoading(false);
    }
  }, [id]);

  const onAddAttributeList = (event: React.MouseEvent<HTMLElement>) => {
    event.preventDefault();
    let attributeName = (document.getElementById('attribute') as HTMLSelectElement).value;
    let atts = getValues('productAttributeTemplates') || [];
    if (selectedAtts.indexOf(attributeName) === -1) {
      setSelectedAtts([...selectedAtts, attributeName]);
      let att = productAtts.find((attribute) => attribute.name === attributeName);
      if (att) {
        let newAttr: ProductAttributeOfTemplate = {
          productAttributeId: att.id,
          displayOrder: 0,
        };
        atts.push(newAttr);
        setValue('productAttributeTemplates', atts);
      }
    } else {
      toast.info(`${attributeName} is selected`);
    }
  };

  const handleSubmitEditProductTemplate = async (event: FromProductTemplate) => {
    let fromProductTemplate: FromProductTemplate = {
      name: event.name,
      productAttributeTemplates: getValues('productAttributeTemplates') || [],
    };

    const response = await updateProductTemplate(+id, fromProductTemplate);
    if (response.status === ResponseStatus.SUCCESS) {
      router.replace(PRODUCT_TEMPLATE_URL);
    }
    handleUpdatingResponse(response);
  };

  const onDelete = (event: React.MouseEvent<HTMLElement>, attName: string) => {
    event.preventDefault();
    let atts = getValues('productAttributeTemplates') || [];
    const attribute = productTemplate?.productAttributeTemplates?.find(
      (attribute) => attribute?.productAttribute?.name === attName
    );
    let filter = selectedAtts.filter((item) => item !== attName);
    let attFilter = atts.filter(
      (att) => att.productAttributeId !== attribute?.productAttribute?.id
    );
    setValue('productAttributeTemplates', attFilter);
    setSelectedAtts(filter);
  };

  const handleSelect = (event: React.ChangeEvent<HTMLSelectElement>) => {
    event.preventDefault();
    (document.getElementById('attribute') as HTMLSelectElement).value !== '0'
      ? setCheckButton(true)
      : setCheckButton(false);
  };

  if (isLoading) return <p>Loading...</p>;
  if (!productTemplate) return <p>No Product Templates Id: {id}</p>;
  return (
    <div className="row mt-5">
      <div className="col-md-8">
        <h2>Edit Product Templates</h2>
        <form onSubmit={handleSubmit(handleSubmitEditProductTemplate)}>
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
                onChange={handleSelect}
              >
                <option value="0" disabled hidden>
                  Select Attribute
                </option>
                {(productAtts || []).map((att) => (
                  <option value={att.name} key={att.id}>
                    {att.name}
                  </option>
                ))}
              </select>
              {checkButton ? (
                <button
                  className="form-control w-50 btn btn-primary"
                  style={{ height: 'auto', width: 'auto' }}
                  onClick={onAddAttributeList}
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
                <tbody>
                  {selectedAtts.map((item) => (
                    <tr key={item}>
                      <th>{item}</th>
                      <th style={{ textAlign: 'right' }}>
                        <button
                          className="btn btn-danger"
                          onClick={(event) => onDelete(event, item)}
                        >
                          <i className="bi bi-x"></i>
                        </button>
                      </th>
                    </tr>
                  ))}
                </tbody>
              </Table>
              <div className="w-50"></div>
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
  );
};

export default ProductTemplateEdit;
