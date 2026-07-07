import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, fireEvent } from '@testing-library/react';
import CustomerInformation from '../../../../modules/customer/components/CustomerInformation';
import { USER_NAME_PATTERN } from '../../../../modules/catalog/constants/validationPattern';

// Mock child component CustomerBaseInformation
vi.mock('../../../../modules/customer/components/CustomerBaseInformation', () => ({
  default: ({ register, errors }: any) => (
    <div data-testid="customer-base-info">
      <span>Customer Base Information Mock</span>
      <input data-testid="mock-email" {...register('email')} />
      {errors.email && <span data-testid="mock-email-error">{errors.email.message}</span>}
    </div>
  ),
}));

// SỬA: Mock Input và Select components với đường dẫn tương đối thay vì alias
vi.mock('../../../common/items/Input', () => ({
  Input: ({ labelText, field, type, defaultValue, register, registerOptions, error }: any) => (
    <div>
      <label htmlFor={field}>{labelText}</label>
      <input
        id={field}
        data-testid={`input-${field}`}
        type={type || 'text'}
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
  Select: ({ labelText, field, placeholder, register, registerOptions, error, options }: any) => (
    <div>
      <label htmlFor={field}>{labelText}</label>
      <select
        id={field}
        data-testid={`select-${field}`}
        onChange={(e) => register && register(field, registerOptions).onChange(e)}
        onBlur={() => register && register(field, registerOptions).onBlur()}
      >
        <option value="">{placeholder}</option>
        {options?.map((opt: any) => (
          <option key={opt.value} value={opt.value}>
            {opt.label}
          </option>
        ))}
      </select>
      {error && <span data-testid={`error-${field}`}>{error}</span>}
    </div>
  ),
}));

describe('CustomerInformation', () => {
  const mockRegister = vi.fn((field, options) => ({
    name: field,
    onChange: vi.fn(),
    onBlur: vi.fn(),
    ref: vi.fn(),
  }));

  // SỬA: Thêm type cho mockWatch
  const mockWatch = vi.fn() as any;

  const defaultProps = {
    register: mockRegister,
    errors: {},
    watch: mockWatch,
  };

  beforeEach(() => {
    vi.clearAllMocks();
    mockWatch.mockImplementation((field: string) => {
      if (field === 'password') return 'password123';
      return undefined;
    });
  });

  describe('Rendering', () => {
    it('should render all form fields', () => {
      render(<CustomerInformation {...defaultProps} />);

      expect(screen.getByText('Username')).toBeDefined();
      expect(screen.getByText('Password')).toBeDefined();
      expect(screen.getByText('Confirm password')).toBeDefined();
      expect(screen.getByText('Role')).toBeDefined();
      expect(screen.getByTestId('customer-base-info')).toBeDefined();
    });


    it('should render CustomerBaseInformation component', () => {
      render(<CustomerInformation {...defaultProps} />);
      expect(screen.getByTestId('customer-base-info')).toBeDefined();
      expect(screen.getByText('Customer Base Information Mock')).toBeDefined();
    });
  });

  describe('Register Options - Username', () => {
    it('should register username with required validation', () => {
      render(<CustomerInformation {...defaultProps} />);

      expect(mockRegister).toHaveBeenCalledWith('username', {
        required: { value: true, message: 'Username is required' },
        pattern: {
          value: USER_NAME_PATTERN,
          message: 'Username must not contain special characters',
        },
      });
    });
  });

  describe('Register Options - Password', () => {
    it('should register password with required validation', () => {
      render(<CustomerInformation {...defaultProps} />);

      expect(mockRegister).toHaveBeenCalledWith('password', {
        required: { value: true, message: 'Password is required' },
      });
    });
  });

  describe('Register Options - Confirm Password', () => {
    it('should register confirmPassword with required validation and custom validate', () => {
      render(<CustomerInformation {...defaultProps} />);

      expect(mockRegister).toHaveBeenCalledWith('confirmPassword', {
        required: { value: true, message: 'Confirm password is required' },
        validate: expect.any(Function),
      });
    });

  });

  describe('Register Options - Role', () => {
    it('should register role with required validation', () => {
      render(<CustomerInformation {...defaultProps} />);

      expect(mockRegister).toHaveBeenCalledWith('role', {
        required: { value: true, message: 'Role is required' },
      });
    });
  });
   
  describe('Watch functionality', () => {
    it('should call watch with password field', () => {
      render(<CustomerInformation {...defaultProps} />);
      expect(mockWatch).toHaveBeenCalledWith('password');
    });
  });

});