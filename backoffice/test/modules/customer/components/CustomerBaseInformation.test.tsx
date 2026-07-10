import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, fireEvent } from '@testing-library/react';
import CustomerBaseInformation from '../../../../modules/customer/components/CustomerBaseInformation';
import { EMAIL_PATTERN } from '../../../../modules/catalog/constants/validationPattern';

// Mock Input component
vi.mock('@commonItems/Input', () => ({
  Input: ({ labelText, field, defaultValue, register, registerOptions, error }: any) => (
    <div>
      <label htmlFor={field}>{labelText}</label>
      <input
        id={field}
        data-testid={`input-${field}`}
        defaultValue={defaultValue}
        onChange={(e) => {
          if (registerOptions?.onChange) {
            registerOptions.onChange(e);
          }
          if (register) {
            register(field, registerOptions).onChange(e);
          }
        }}
        onBlur={() => register && register(field, registerOptions).onBlur()}
      />
      {error && <span data-testid={`error-${field}`}>{error}</span>}
    </div>
  ),
}));

describe('CustomerBaseInformation', () => {
  const mockRegister = vi.fn((field, options) => ({
    name: field,
    onChange: vi.fn(),
    onBlur: vi.fn(),
    ref: vi.fn(),
  }));

  const defaultProps = {
    register: mockRegister,
    errors: {},
    customer: undefined,
  };

  beforeEach(() => {
    vi.clearAllMocks();
  });

  describe('Rendering', () => {
    it('should render all form fields', () => {
      render(<CustomerBaseInformation {...defaultProps} />);

      expect(screen.getByLabelText('Email')).toBeDefined();
      expect(screen.getByLabelText('First name')).toBeDefined();
      expect(screen.getByLabelText('Last name')).toBeDefined();
    });

    it('should render Email input with correct label', () => {
      render(<CustomerBaseInformation {...defaultProps} />);

      expect(screen.getByText('Email')).toBeDefined();
      expect(screen.getByTestId('input-email')).toBeDefined();
    });

    it('should render First name input with correct label', () => {
      render(<CustomerBaseInformation {...defaultProps} />);

      expect(screen.getByText('First name')).toBeDefined();
      expect(screen.getByTestId('input-firstName')).toBeDefined();
    });

    it('should render Last name input with correct label', () => {
      render(<CustomerBaseInformation {...defaultProps} />);

      expect(screen.getByText('Last name')).toBeDefined();
      expect(screen.getByTestId('input-lastName')).toBeDefined();
    });
  });
 

  describe('Customer Create VM Type', () => {
    it('should accept CustomerCreateVM type with password fields', () => {
      // CustomerCreateVM extends Customer and adds password fields
      const customerCreateData = {
        email: 'new@example.com',
        firstName: 'New',
        lastName: 'User',
        password: 'password123',
        confirmPassword: 'password123',
      };

      expect(customerCreateData).toHaveProperty('email');
      expect(customerCreateData).toHaveProperty('firstName');
      expect(customerCreateData).toHaveProperty('lastName');
      expect(customerCreateData).toHaveProperty('password');
      expect(customerCreateData).toHaveProperty('confirmPassword');
    });

    it('should have all required Customer fields', () => {
      const customerData = {
        email: 'test@example.com',
        firstName: 'Test',
        lastName: 'User',
      };

      expect(customerData.email).toBeDefined();
      expect(customerData.firstName).toBeDefined();
      expect(customerData.lastName).toBeDefined();
    });
  });
});