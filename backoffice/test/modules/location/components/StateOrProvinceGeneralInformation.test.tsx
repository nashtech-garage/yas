import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, fireEvent } from '@testing-library/react';
import StateOrProvinceGeneralInformation from '../../../../modules/location/components/StateOrProvinceGeneralInformation';
import slugify from 'slugify';

// Mock slugify
vi.mock('slugify', () => ({
  default: vi.fn((str) => str.toLowerCase().replace(/\s/g, '-')),
}));

// Mock Input component
vi.mock('common/items/Input', () => ({
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

describe('StateOrProvinceGeneralInformation', () => {
  const mockRegister = vi.fn((field, options) => ({
    name: field,
    onChange: vi.fn(),
    onBlur: vi.fn(),
    ref: vi.fn(),
  }));

  const mockSetValue = vi.fn();
  const mockTrigger = vi.fn();

  const mockStateOrProvince = {
    id: 1,
    name: 'California',
    code: 'CA',
    type: 'State',
    countryId: 1,
  };

  const defaultProps = {
    register: mockRegister,
    errors: {},
    setValue: mockSetValue,
    trigger: mockTrigger,
    stateOrProvince: undefined,
  };

  beforeEach(() => {
    vi.clearAllMocks();
  });

  describe('Rendering', () => {
    it('should render all form fields', () => {
      render(<StateOrProvinceGeneralInformation {...defaultProps} />);

      expect(screen.getByText('Name')).toBeDefined();
      expect(screen.getByText('Code')).toBeDefined();
      expect(screen.getByText('Type')).toBeDefined();
    });


  });

});