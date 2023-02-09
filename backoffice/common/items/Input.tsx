import { HTMLInputTypeAttribute } from 'react';
import { Path, RegisterOptions, UseFormRegister } from 'react-hook-form';

import { ProductPost } from '../../modules/catalog/models/ProductPost';

type InputProps = {
  labelText: string;
  field: Path<ProductPost>;
  register: UseFormRegister<ProductPost>;
  error?: string;
  type?: HTMLInputTypeAttribute;
  registerOptions?: RegisterOptions;
  defaultValue?: number | string | string[] | undefined;
};

type CheckProps = InputProps & {
  defaultChecked?: boolean;
};

export const Input = ({
  labelText,
  field,
  register,
  registerOptions = {},
  error,
  defaultValue,
  type = 'text',
}: InputProps) => (
  <div className="mb-3">
    <label className="form-label" htmlFor={field}>
      {labelText} {registerOptions?.required && <span className="text-danger">*</span>}
    </label>
    <input
      type={type}
      id={field}
      className={`form-control ${error ? 'border-danger' : ''}`}
      {...register(field, registerOptions)}
      defaultValue={defaultValue}
    />
    <p className="error-field mt-1">{error}</p>
  </div>
);

export const Text = ({
  labelText,
  field,
  register,
  registerOptions = {},
  error,
  defaultValue,
}: InputProps) => (
  <div className="mb-3">
    <label className="form-label" htmlFor={field}>
      {labelText}
    </label>
    <textarea
      defaultValue={defaultValue}
      id={field}
      className={`form-control ${error ? 'border-danger' : ''}`}
      {...register(field, registerOptions)}
    />
    <p className="error-field mt-1">{error}</p>
  </div>
);

export const Check = ({ labelText, field, register, defaultChecked, ...restProps }: CheckProps) => (
  <div className="mb-3">
    <input
      type="checkbox"
      id={field}
      className={`form-check-input`}
      defaultChecked={defaultChecked}
      {...register(field, restProps.registerOptions)}
    />
    <label className="form-check-label ps-3" htmlFor={field}>
      {labelText}
    </label>
    <p className="error-field mt-1">{restProps.error}</p>
  </div>
);
