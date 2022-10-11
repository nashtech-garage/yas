import { Path, UseFormRegister, FieldErrorsImpl } from 'react-hook-form';
import { ProductPost } from '../../modules/catalog/models/ProductPost';
import React from 'react';

type InputProps = {
  label: Path<ProductPost>;
  register: UseFormRegister<ProductPost>;
  error?: string;
  type?: string;
};

export const Input = ({ label, register, error, type = 'text' }: InputProps) => (
  <div className="mb-3">
    <label className="form-label" htmlFor={label}>
      {label.toUpperCase()}
    </label>
    <input
      type={type}
      id={label}
      className={`form-control ${error ? 'border-danger' : ''}`}
      {...register(label)}
    />
    <sup className="text-danger fst-italic">{error}</sup>
  </div>
);

export const Text = ({ label, register, error }: InputProps) => (
  <div className="mb-3">
    <label className="form-label" htmlFor={label}>
      {label.toUpperCase()}
    </label>
    <textarea
      id={label}
      className={`form-control ${error ? 'border-danger' : ''}`}
      {...register(label)}
    />
    <sup className="text-danger fst-italic">{error}</sup>
  </div>
);

export const Check = ({ label, register }: InputProps) => (
  <div className="mb-3">
    <input type="checkbox" id={label} className={`form-check-input`} {...register(label)} />
    <label className="form-check-label ps-3" htmlFor={label}>
      {label.toUpperCase()}
    </label>
  </div>
);
