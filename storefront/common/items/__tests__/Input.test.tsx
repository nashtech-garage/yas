import { render, screen } from '@testing-library/react';
import { useForm } from 'react-hook-form';

import { Input } from '../Input';

type FormModel = {
  email: string;
};

const InputHarness = ({ error }: { error?: string }) => {
  const { register } = useForm<FormModel>();

  return (
    <Input<FormModel>
      labelText="Email"
      field="email"
      register={register}
      registerOptions={{ required: true }}
      placeholder="Enter email"
      defaultValue="demo@yas.local"
      error={error}
    />
  );
};

describe('Input', () => {
  it('renders required indicator and default value', () => {
    render(<InputHarness />);

    expect(screen.getByText('Email')).toBeInTheDocument();
    expect(screen.getByText('*')).toBeInTheDocument();
    expect(screen.getByDisplayValue('demo@yas.local')).toBeInTheDocument();
  });

  it('shows error and danger border class when error is provided', () => {
    render(<InputHarness error="Email is required" />);

    expect(screen.getByText('Email is required')).toBeInTheDocument();
    expect(screen.getByRole('textbox')).toHaveClass('border-danger');
  });
});
