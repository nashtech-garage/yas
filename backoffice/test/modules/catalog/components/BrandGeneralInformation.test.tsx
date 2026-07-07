import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import BrandGeneralInformation from '../../../../modules/catalog/components/BrandGeneralInformation';
import slugify from 'slugify';

// Mock slugify
vi.mock('slugify', () => ({
  default: vi.fn((str) => str.toLowerCase().replace(/\s/g, '-')),
}));

// Mock Input and Switch components
vi.mock('../../../common/items/Input', () => ({
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
  Switch: ({ labelText, field, defaultChecked, register }: any) => (
    <div>
      <label htmlFor={field}>{labelText}</label>
      <input
        type="checkbox"
        id={field}
        data-testid={`switch-${field}`}
        defaultChecked={defaultChecked}
        onChange={(e) => register && register(field).onChange(e)}
      />
    </div>
  ),
}));

describe('BrandGeneralInformation', () => {
  const mockRegister = vi.fn((field, options) => ({
    name: field,
    onChange: vi.fn(),
    onBlur: vi.fn(),
    ref: vi.fn(),
  }));

  const mockSetValue = vi.fn();
  const mockTrigger = vi.fn();

  const defaultProps = {
    register: mockRegister,
    errors: {},
    setValue: mockSetValue,
    trigger: mockTrigger,
    brand: undefined,
  };

  beforeEach(() => {
    vi.clearAllMocks();
    (slugify as any).mockClear();
  });

  describe('Register Options', () => {
    it('should register name field with required validation', () => {
      render(<BrandGeneralInformation {...defaultProps} />);

      expect(mockRegister).toHaveBeenCalledWith('name', expect.objectContaining({
        required: { value: true, message: 'Brand name is required' },
      }));
    });

    it('should register slug field with required and pattern validation', () => {
      render(<BrandGeneralInformation {...defaultProps} />);

      expect(mockRegister).toHaveBeenCalledWith('slug', expect.objectContaining({
        required: { value: true, message: 'Slug brand is required' },
        pattern: expect.objectContaining({
          value: expect.any(RegExp),
          message: expect.stringContaining('Slug must not contain special characters'),
        }),
      }));
    });

    it('should register isPublish field', () => {
      render(<BrandGeneralInformation {...defaultProps} />);

      expect(mockRegister).toHaveBeenCalledWith('isPublish', undefined);
    });
  });

  describe('Form Integration', () => {
    it('should call register with correct field names', () => {
      render(<BrandGeneralInformation {...defaultProps} />);

      expect(mockRegister).toHaveBeenCalledWith('name', expect.any(Object));
      expect(mockRegister).toHaveBeenCalledWith('slug', expect.any(Object));
      expect(mockRegister).toHaveBeenCalledWith('isPublish', undefined);
    });
  });
});