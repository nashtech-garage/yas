import { HTMLInputTypeAttribute } from 'react';
import { Path, RegisterOptions, UseFormRegister, FieldValues } from 'react-hook-form';

type InputProps<T extends FieldValues> = {
  labelText: string;
  field: Path<T>;
  register: UseFormRegister<T>;
  error?: string;
  type?: HTMLInputTypeAttribute;
  registerOptions?: RegisterOptions;
  defaultValue?: number | string | string[];
};

type CheckProps<T extends FieldValues> = InputProps<T> & {
  defaultChecked?: any;
};

export const Input = <T extends FieldValues>({
  labelText,
  field,
  register,
  registerOptions = {},
  error,
  defaultValue,
  type = 'text',
}: InputProps<T>) => (
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

export const TextArea = <T extends FieldValues>({
  labelText,
  field,
  register,
  registerOptions = {},
  error,
  defaultValue,
}: InputProps<T>) => (
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

export const CheckBox = <T extends FieldValues>({
  labelText,
  field,
  register,
  defaultChecked,
  registerOptions,
  error,
}: CheckProps<T>) => (
  <div className="mb-3">
    <input
      type="checkbox"
      id={field}
      className={`form-check-input`}
      defaultChecked={defaultChecked}
      {...register(field, registerOptions)}
    />
    <label className="form-check-label ps-3" htmlFor={field}>
      {labelText}
    </label>
    <p className="error-field mt-1">{error}</p>
  </div>
);
