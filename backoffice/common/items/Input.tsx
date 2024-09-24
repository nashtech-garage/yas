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
  onChange?: (event: React.ChangeEvent<HTMLInputElement>) => void;
};

type CheckProps<T extends FieldValues> = InputProps<T> & {
  defaultChecked?: any;
};

type SelectProps<T extends FieldValues> = InputProps<T> & {
  options: any[];
  placeholder?: string;
  defaultValue?: number | string | string[];
  disabled?: boolean;
  isMultiple?: boolean;
  onChange?: (event: React.ChangeEvent<HTMLSelectElement>) => void;
};

type DateProps<T extends FieldValues> = InputProps<T> & {
  minDate?: Date;
  maxDate?: Date;
  defaultValue?: string;
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

export const Switch = <T extends FieldValues>({
  labelText,
  field,
  register,
  defaultChecked,
  registerOptions,
  error,
}: CheckProps<T>) => (
  <div className="d-flex">
    <label className="form-check-label mr-3" htmlFor={field} style={{ marginRight: '15px' }}>
      {labelText}
    </label>
    <div className="form-check form-switch mb-3">
      <input
        className={`form-check-input`}
        type="checkbox"
        id={field}
        defaultChecked={defaultChecked}
        {...register(field, registerOptions)}
      />
      <p className="error-field mt-1">{error}</p>
    </div>
  </div>
);

export const Select = <T extends FieldValues>({
  labelText,
  field,
  register,
  registerOptions,
  error,
  options,
  defaultValue,
  placeholder,
  disabled,
  isMultiple,
  onChange,
}: SelectProps<T> & {
  options: any[];
}) => (
  <div className="mb-3">
    <label className="form-label" htmlFor={field}>
      {labelText} {registerOptions?.required && <span className="text-danger">*</span>}
    </label>
    <select
      id={field}
      className={`form-select ${error ? 'border-danger' : ''}`}
      {...register(field, registerOptions)}
      disabled={disabled}
      multiple={isMultiple}
      onChange={onChange}
    >
      <option value="" selected={!defaultValue} disabled>
        {placeholder}
      </option>
      {options.map((item) => (
        <option key={item.value} value={item.value} selected={item.value === defaultValue}>
          {item.label}
        </option>
      ))}
    </select>
    <p className="error-field mt-1">{error}</p>
  </div>
);

export const DatePicker = <T extends FieldValues>({
  labelText,
  field,
  register,
  registerOptions = {},
  error,
  defaultValue,
}: DateProps<T>) => (
  <div className="mb-3">
    <label className="form-label" htmlFor={field}>
      {labelText} {registerOptions?.required && <span className="text-danger">*</span>}
    </label>
    <input
      id={field}
      className={`form-control ${error ? 'border-danger' : ''}`}
      {...register(field, registerOptions)}
      defaultValue={defaultValue}
      type="date"
      placeholder="dd/mm/yyyy"
    />
    <p className="error-field mt-1">{error}</p>
  </div>
);
