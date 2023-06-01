import { Form, InputGroup } from 'react-bootstrap';
import { UseFormRegister } from 'react-hook-form';
import { OrderSearchForm } from '../models/OrderSearchForm';
type Props = {
  register: UseFormRegister<OrderSearchForm>;
};

const OrderSearch = ({ register }: Props) => {
  return (
    <>
      <div className="row">
        <div className="col-12 col-lg-6 py-2">
          <div className="d-flex mb-4">
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
          <div className="d-flex mb-4 py-2">
            <Form.Label htmlFor="createdTo" className="label">
              Created To:{' '}
            </Form.Label>
            <input
              id="startDate"
              className="form-control "
              type="datetime-local"
              {...register('createdTo')}
              defaultValue={new Date().toISOString().slice(0, -8)}
            />
          </div>
        </div>
        <div className="col-12 col-lg-6">
          <div className="d-flex flex-row py-2">
            <Form.Label htmlFor="productName" className="pt-2 col-4">
              Product Name:{' '}
            </Form.Label>
            <Form.Control
              id="productName"
              placeholder="Search product name ..."
              {...register('productName')}
            />
          </div>
          <div className="d-flex my-2">
            <Form.Label htmlFor="email" className="pt-2 col-lg-4">
              Customer Email:{' '}
            </Form.Label>
            <InputGroup>
              <Form.Control
                id="email"
                className="col-lg-8"
                {...register('email')}
                placeholder="Search customer email ..."
              />
            </InputGroup>
          </div>
          <div className="d-flex mb-4">
            <Form.Label htmlFor="billingPhoneNumber" className="pt-2 col-lg-4">
              Billing Phone:{' '}
            </Form.Label>
            <InputGroup>
              <Form.Control
                id="billingPhoneNumber"
                className="col-lg-8"
                {...register('billingPhoneNumber')}
                placeholder="Search phone ..."
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

export default OrderSearch;
