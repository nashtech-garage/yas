import { Path, UseFormRegister } from 'react-hook-form';
import { ProductPost } from '../../modules/catalog/models/ProductPost';

type InputProps = {
  labelText: string;
  field: Path<ProductPost>;
  register: UseFormRegister<ProductPost>;
  error?: string;
  type?: string;
};

export const Input = ({ labelText, field, register, error, type = 'text' }: InputProps) => (
  <div className="mb-3">
    <label className="form-label" htmlFor={field}>
      {labelText}
    </label>
    <input
      type={type}
      id={field}
      className={`form-control ${error ? 'border-danger' : ''}`}
      {...register(field)}
    />
    <sup className="text-danger fst-italic">{error}</sup>
  </div>
);

export const Text = ({ labelText, field, register, error }: InputProps) => (
  <div className="mb-3">
    <label className="form-label" htmlFor={field}>
      {labelText}
    </label>
    <textarea
      id={field}
      className={`form-control ${error ? 'border-danger' : ''}`}
      {...register(field)}
    />
    <sup className="text-danger fst-italic">{error}</sup>
  </div>
);

export const Check = ({ labelText, field, register }: InputProps) => (
  <div className="mb-3">
    <input type="checkbox" id={field} className={`form-check-input`} {...register(field)} />
    <label className="form-check-label ps-3" htmlFor={field}>
      {labelText}
    </label>
  </div>
);
