// test/common/items/Input.test.tsx
import { describe, it, expect, vi } from 'vitest';
import { render, screen } from '@testing-library/react';
import { useForm } from 'react-hook-form';
import {
  Input,
  NumberFormatInput,
  TextArea,
  CheckBox,
  Switch,
  Select,
  DatePicker,
} from '../../../common/items/Input';

// Mock react-number-format
vi.mock('react-number-format', () => ({
  NumericFormat: ({ onValueChange, ...props }: any) => (
    <input
      data-testid="numeric-input"
      onChange={(e) => onValueChange?.({ value: e.target.value, floatValue: parseFloat(e.target.value) })}
      {...props}
    />
  ),
}));

// Wrapper component for testing
const TestWrapper = ({ children }: { children: (form: any) => React.ReactNode }) => {
  const form = useForm();
  return <form>{children(form)}</form>;
};

describe('Form Components', () => {
  describe('Input', () => {
    it('should render input with label', () => {
      render(
        <TestWrapper>
          {({ register }) => (
            <Input
              labelText="Username"
              field="username"
              register={register}
            />
          )}
        </TestWrapper>
      );
      
      expect(screen.getByLabelText('Username')).toBeDefined();
      expect(screen.getByRole('textbox')).toBeDefined();
    });

    it('should show required asterisk when required', () => {
      render(
        <TestWrapper>
          {({ register }) => (
            <Input
              labelText="Email"
              field="email"
              register={register}
              registerOptions={{ required: 'Email is required' }}
            />
          )}
        </TestWrapper>
      );
      
      expect(screen.getByText('*')).toBeDefined();
    });

    it('should display error message when error provided', () => {
      render(
        <TestWrapper>
          {({ register }) => (
            <Input
              labelText="Password"
              field="password"
              register={register}
              error="Password is required"
            />
          )}
        </TestWrapper>
      );
      
      expect(screen.getByText('Password is required')).toBeDefined();
    });

    it('should apply border-danger class when error', () => {
      render(
        <TestWrapper>
          {({ register }) => (
            <Input
              labelText="Name"
              field="name"
              register={register}
              error="Name is required"
            />
          )}
        </TestWrapper>
      );
      
      const input = screen.getByRole('textbox');
      expect(input.className).toContain('border-danger');
    });

    it('should have disabled attribute when disabled prop is true', () => {
      render(
        <TestWrapper>
          {({ register }) => (
            <Input
              labelText="Disabled Field"
              field="disabled"
              register={register}
              disabled={true}
            />
          )}
        </TestWrapper>
      );
      
      const input = screen.getByRole('textbox');
      expect(input.getAttribute('disabled')).not.toBeNull();
    });

    it('should not have disabled attribute when disabled prop is false', () => {
      render(
        <TestWrapper>
          {({ register }) => (
            <Input
              labelText="Enabled Field"
              field="enabled"
              register={register}
              disabled={false}
            />
          )}
        </TestWrapper>
      );
      
      const input = screen.getByRole('textbox');
      expect(input.getAttribute('disabled')).toBeNull();
    });

    it('should render different input types', () => {
      render(
        <TestWrapper>
          {({ register }) => (
            <>
              <Input labelText="Text" field="text" register={register} type="text" />
              <Input labelText="Password" field="password" register={register} type="password" />
              <Input labelText="Email" field="email" register={register} type="email" />
            </>
          )}
        </TestWrapper>
      );
      
      expect(screen.getByLabelText('Text')).toBeDefined();
      expect(screen.getByLabelText('Password')).toBeDefined();
      expect(screen.getByLabelText('Email')).toBeDefined();
    });
  });

  describe('NumberFormatInput', () => {
    it('should render numeric input', () => {
      render(
        <TestWrapper>
          {({ register, setValue }) => (
            <NumberFormatInput
              labelText="Price"
              field="price"
              register={register}
              setValue={setValue}
            />
          )}
        </TestWrapper>
      );
      
      expect(screen.getByLabelText('Price')).toBeDefined();
    });

    it('should show required asterisk when required', () => {
      render(
        <TestWrapper>
          {({ register, setValue }) => (
            <NumberFormatInput
              labelText="Quantity"
              field="quantity"
              register={register}
              setValue={setValue}
              registerOptions={{ required: 'Quantity is required' }}
            />
          )}
        </TestWrapper>
      );
      
      expect(screen.getByText('*')).toBeDefined();
    });

    it('should display error message', () => {
      render(
        <TestWrapper>
          {({ register, setValue }) => (
            <NumberFormatInput
              labelText="Amount"
              field="amount"
              register={register}
              setValue={setValue}
              error="Amount must be positive"
            />
          )}
        </TestWrapper>
      );
      
      expect(screen.getByText('Amount must be positive')).toBeDefined();
    });

    it('should have disabled attribute when disabled is true', () => {
      render(
        <TestWrapper>
          {({ register, setValue }) => (
            <NumberFormatInput
              labelText="Disabled Number"
              field="disabledNum"
              register={register}
              setValue={setValue}
              disabled={true}
            />
          )}
        </TestWrapper>
      );
      
      const input = screen.getByTestId('numeric-input');
      expect(input.getAttribute('disabled')).not.toBeNull();
    });
  });

  describe('TextArea', () => {
    it('should render textarea', () => {
      render(
        <TestWrapper>
          {({ register }) => (
            <TextArea
              labelText="Description"
              field="description"
              register={register}
            />
          )}
        </TestWrapper>
      );
      
      expect(screen.getByLabelText('Description')).toBeDefined();
      expect(screen.getByRole('textbox')).toBeDefined();
    });

    it('should display error message', () => {
      render(
        <TestWrapper>
          {({ register }) => (
            <TextArea
              labelText="Bio"
              field="bio"
              register={register}
              error="Bio is required"
            />
          )}
        </TestWrapper>
      );
      
      expect(screen.getByText('Bio is required')).toBeDefined();
    });
  });

  describe('CheckBox', () => {
    it('should render checkbox', () => {
      render(
        <TestWrapper>
          {({ register }) => (
            <CheckBox
              labelText="Agree to terms"
              field="agree"
              register={register}
            />
          )}
        </TestWrapper>
      );
      
      const checkbox = screen.getByRole('checkbox');
      expect(checkbox).toBeDefined();
      expect(screen.getByText('Agree to terms')).toBeDefined();
    });

    it('should be checked by default when defaultChecked is true', () => {
      render(
        <TestWrapper>
          {({ register }) => (
            <CheckBox
              labelText="Subscribe"
              field="subscribe"
              register={register}
              defaultChecked={true}
            />
          )}
        </TestWrapper>
      );
      
      const checkbox = screen.getByRole('checkbox') as HTMLInputElement;
      expect(checkbox.checked).toBe(true);
    });

    it('should display error message', () => {
      render(
        <TestWrapper>
          {({ register }) => (
            <CheckBox
              labelText="Accept"
              field="accept"
              register={register}
              error="Must accept terms"
            />
          )}
        </TestWrapper>
      );
      
      expect(screen.getByText('Must accept terms')).toBeDefined();
    });
  });

  describe('Switch', () => {
    it('should render switch', () => {
      render(
        <TestWrapper>
          {({ register }) => (
            <Switch
              labelText="Enable notifications"
              field="notifications"
              register={register}
            />
          )}
        </TestWrapper>
      );
      
      const switchInput = screen.getByRole('checkbox');
      expect(switchInput).toBeDefined();
      expect(switchInput.className).toContain('form-check-input');
    });

    it('should be checked by default when defaultChecked is true', () => {
      render(
        <TestWrapper>
          {({ register }) => (
            <Switch
              labelText="Dark mode"
              field="darkMode"
              register={register}
              defaultChecked={true}
            />
          )}
        </TestWrapper>
      );
      
      const switchInput = screen.getByRole('checkbox') as HTMLInputElement;
      expect(switchInput.checked).toBe(true);
    });
  });

  describe('Select', () => {
    const options = [
      { value: '1', label: 'Option 1' },
      { value: '2', label: 'Option 2' },
      { value: '3', label: 'Option 3' },
    ];

    it('should render select with options', () => {
      render(
        <TestWrapper>
          {({ register }) => (
            <Select
              labelText="Choose option"
              field="option"
              register={register}
              options={options}
            />
          )}
        </TestWrapper>
      );
      
      expect(screen.getByLabelText('Choose option')).toBeDefined();
      expect(screen.getByText('Option 1')).toBeDefined();
      expect(screen.getByText('Option 2')).toBeDefined();
      expect(screen.getByText('Option 3')).toBeDefined();
    });

    it('should show required asterisk when required', () => {
      render(
        <TestWrapper>
          {({ register }) => (
            <Select
              labelText="Category"
              field="category"
              register={register}
              options={options}
              registerOptions={{ required: 'Category is required' }}
            />
          )}
        </TestWrapper>
      );
      
      expect(screen.getByText('*')).toBeDefined();
    });

    it('should display placeholder', () => {
      render(
        <TestWrapper>
          {({ register }) => (
            <Select
              labelText="Select"
              field="select"
              register={register}
              options={options}
              placeholder="-- Please select --"
            />
          )}
        </TestWrapper>
      );
      
      expect(screen.getByText('-- Please select --')).toBeDefined();
    });

    it('should have disabled attribute when disabled prop is true', () => {
      render(
        <TestWrapper>
          {({ register }) => (
            <Select
              labelText="Disabled Select"
              field="disabled"
              register={register}
              options={options}
              disabled={true}
            />
          )}
        </TestWrapper>
      );
      
      const select = screen.getByRole('combobox');
      expect(select.getAttribute('disabled')).not.toBeNull();
    });

    it('should not have disabled attribute when disabled prop is false', () => {
      render(
        <TestWrapper>
          {({ register }) => (
            <Select
              labelText="Enabled Select"
              field="enabled"
              register={register}
              options={options}
              disabled={false}
            />
          )}
        </TestWrapper>
      );
      
      const select = screen.getByRole('combobox');
      expect(select.getAttribute('disabled')).toBeNull();
    });
  });

  describe('DatePicker', () => {
    it('should render date input', () => {
      render(
        <TestWrapper>
          {({ register }) => (
            <DatePicker
              labelText="Birth date"
              field="birthDate"
              register={register}
            />
          )}
        </TestWrapper>
      );
      
      const dateInput = screen.getByLabelText('Birth date');
      expect(dateInput).toBeDefined();
      expect(dateInput.getAttribute('type')).toBe('date');
    });

    it('should show required asterisk when required', () => {
      render(
        <TestWrapper>
          {({ register }) => (
            <DatePicker
              labelText="Start date"
              field="startDate"
              register={register}
              registerOptions={{ required: 'Start date is required' }}
            />
          )}
        </TestWrapper>
      );
      
      expect(screen.getByText('*')).toBeDefined();
    });

    it('should display error message', () => {
      render(
        <TestWrapper>
          {({ register }) => (
            <DatePicker
              labelText="End date"
              field="endDate"
              register={register}
              error="End date is invalid"
            />
          )}
        </TestWrapper>
      );
      
      expect(screen.getByText('End date is invalid')).toBeDefined();
    });

    it('should have date type', () => {
      render(
        <TestWrapper>
          {({ register }) => (
            <DatePicker
              labelText="Date"
              field="date"
              register={register}
            />
          )}
        </TestWrapper>
      );
      
      const dateInput = screen.getByLabelText('Date');
      expect(dateInput.getAttribute('type')).toBe('date');
    });
  });
});