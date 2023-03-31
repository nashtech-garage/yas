import { HTMLInputTypeAttribute } from 'react';
import { FieldValues, Path, RegisterOptions, UseFormRegister } from 'react-hook-form';

type InputProps<T extends FieldValues> = {
  labelText: string;
  field: Path<T>;
  register: UseFormRegister<T>;
  error?: string;
  type?: HTMLInputTypeAttribute;
  registerOptions?: RegisterOptions;
  defaultValue?: number | string | string[];
  disabled?: boolean;
  placeholder?: string;
};

export const Input = <T extends FieldValues>({
  labelText,
  field,
  register,
  registerOptions = {},
  error,
  defaultValue,
  type = 'text',
  disabled = false,
  placeholder,
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
      disabled={disabled}
      placeholder={placeholder}
    />
    <p className="error-field mt-1 text-danger">{error}</p>
  </div>
);
