import { Form, InputGroup } from 'react-bootstrap';
import { RatingSearchForm } from '../models/RatingSearchForm';
import { UseFormRegister } from 'react-hook-form';
import { Brand } from 'modules/catalog/models/Brand';
type Props = {
  brandList: Brand[];
  register: UseFormRegister<RatingSearchForm>;
};

const RatingSearch = ({ brandList, register }: Props) => {
  return (
    <>
      <div className="row">
        <div className="col-12 col-lg-6">
          <div className="d-flex mb-4 w-75">
            <Form.Label htmlFor="Created-From" className="label">
              Created From:{' '}
            </Form.Label>
            <input
              id="startDate"
              className="form-control "
              type="datetime-local"
              {...register('createdFrom')}
              defaultValue="2001-01-01T19:30"
            />
          </div>
          <div className="d-flex mb-4 w-75">
            <Form.Label htmlFor="createdTo" className="label">
              Created To:{' '}
            </Form.Label>
            <input
              id="startDate"
              className="form-control "
              type="datetime-local"
              {...register('createdTo')}
              defaultValue="2024-01-01T19:30"
              // defaultValue={new Date().toISOString().slice(0, -8)}
            />
          </div>
        </div>
        <div className="col-12 col-lg-6">
          <div className="d-flex mb-4">
            <Form.Label htmlFor="productName" className="mx-2 pt-2">
              Product:{' '}
            </Form.Label>
            <Form.Control
              id="productName"
              placeholder="Search product name ..."
              {...register('productName')}
            />
          </div>
          <div className="d-flex mb-4">
            <Form.Label htmlFor="cusomter" className="mx-2 pt-2">
              Customer:{' '}
            </Form.Label>
            <InputGroup>
              <Form.Control
                id="customer-name"
                {...register('customerName')}
                placeholder="Search customer name ..."
              />
            </InputGroup>
          </div>
          <div className="d-flex mb-4">
            <Form.Label htmlFor="message" className="mx-2 pt-2">
              Message:{' '}
            </Form.Label>
            <InputGroup>
              <Form.Control
                id="search-message"
                {...register('message')}
                placeholder="Search message ..."
              />
            </InputGroup>
          </div>
        </div>
      </div>
      <div className="text-center">
        <button className="btn btn-primary w-25" type="submit">
          Search
        </button>
      </div>
    </>
  );
};

export default RatingSearch;
