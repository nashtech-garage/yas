import { Path, RegisterOptions, UseFormRegister, FieldValues } from 'react-hook-form';

type Option = {
  id: string | number;
  name: string;
};

type OptionSelectProps<T extends FieldValues> = {
  labelText: string;
  field: Path<T>;
  register: UseFormRegister<T>;
  registerOptions?: RegisterOptions;
  error?: string;
  options?: Option[];
  defaultValue?: string | number;
  placeholder?: string;
  disabled?: boolean;
};

export const OptionSelect = <T extends FieldValues>({
  labelText,
  field,
  register,
  registerOptions,
  error,
  options,
  defaultValue,
  placeholder,
  disabled,
}: OptionSelectProps<T>) => (
  <div className="mb-3">
    <label className="form-label" htmlFor={`select-option-${field}`}>
      {labelText}
    </label>
    <select
      id={`select-option-${field}`}
      className={`form-select ${error ? 'border-danger' : ''}`}
      defaultValue={defaultValue || ''}
      {...register(field, registerOptions)}
      disabled={disabled}
    >
      <option disabled hidden value="">
        {placeholder || 'Select ...'}
      </option>
      {
        Array.isArray(options) ?
        options.map((option) =>(
          <option value={option.id} key={option.id}>
            {option.name}
          </option>
        ))
        :
        null
      }
    </select>
    <p className="error-field mt-1">{error}</p>
  </div>
);
