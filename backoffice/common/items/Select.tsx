import { Path, RegisterOptions, UseFormRegister } from 'react-hook-form';

import { Brand } from '../../modules/catalog/models/Brand';
import { ProductPost } from '../../modules/catalog/models/ProductPost';

type SelectProps = {
  labelText: string;
  field: Path<ProductPost>;
  register: UseFormRegister<ProductPost>;
  registerOptions?: RegisterOptions;
  error?: string;
  options?: Brand[];
  defaultValue?: string | number;
  placeholder?: string;
};

export default function Select({
  labelText,
  field,
  register,
  registerOptions,
  error,
  options,
  defaultValue,
  placeholder,
}: SelectProps) {
  return (
    <div className="mb-3">
      <label className="form-label" htmlFor={`brand-${field}`}>
        {labelText}
      </label>
      <select
        id={`brand-${field}`}
        className={`form-select ${error ? 'border-danger' : ''}`}
        defaultValue={defaultValue || ''}
        {...register(field, registerOptions)}
      >
        <option disabled hidden value="">
          {placeholder || 'Select ...'}
        </option>
        {(options || []).map((brand) => (
          <option value={brand.id} key={brand.id}>
            {brand.name}
          </option>
        ))}
      </select>
      <p className="error-field mt-1">{error}</p>
    </div>
  );
}
