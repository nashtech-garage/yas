import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import WarehouseGeneralInformation from '../../../../modules/inventory/components/WarehouseGeneralInformation';
import { getCountries } from '../../../../modules/location/services/CountryService';
import { getStatesOrProvinces } from '../../../../modules/location/services/StateOrProvinceService';
import { getDistricts } from '../../../../modules/location/services/DistrictService';
import { useRouter } from 'next/router';

// Mock dependencies
vi.mock('next/router', () => ({
  useRouter: vi.fn(),
}));

vi.mock('@commonItems/OptionSelect', () => ({
  OptionSelect: ({ labelText, field, placeholder, options, register, registerOptions, error, defaultValue }: any) => (
    <div>
      <label htmlFor={field}>{labelText}</label>
      <select
        id={field}
        data-testid={`select-${field}`}
        defaultValue={defaultValue}
        onChange={(e) => {
          if (registerOptions?.onChange) {
            registerOptions.onChange(e);
          }
          if (register) {
            register(field, registerOptions).onChange(e);
          }
        }}
      >
        <option value="">{placeholder}</option>
        {options?.map((opt: any) => (
          <option key={opt.id} value={opt.id}>
            {opt.name}
          </option>
        ))}
      </select>
      {error && <span data-testid={`error-${field}`}>{error}</span>}
    </div>
  ),
}));

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
      />
      {error && <span data-testid={`error-${field}`}>{error}</span>}
    </div>
  ),
}));

vi.mock('../../../../modules/location/services/CountryService', () => ({
  getCountries: vi.fn(),
}));

vi.mock('../../../../modules/location/services/StateOrProvinceService', () => ({
  getStatesOrProvinces: vi.fn(),
}));

vi.mock('../../../../modules/location/services/DistrictService', () => ({
  getDistricts: vi.fn(),
}));

describe('WarehouseGeneralInformation', () => {
  const mockRegister = vi.fn((field, options) => ({
    name: field,
    onChange: vi.fn(),
    onBlur: vi.fn(),
    ref: vi.fn(),
  }));

  const mockSetValue = vi.fn();
  const mockTrigger = vi.fn();

  const mockCountries = [
    { id: 1, name: 'USA' },
    { id: 2, name: 'Canada' },
  ];

  const mockStates = [
    { id: 1, name: 'California', countryId: 1 },
    { id: 2, name: 'Texas', countryId: 1 },
  ];

  const mockDistricts = [
    { id: 1, name: 'Los Angeles', stateOrProvinceId: 1 },
    { id: 2, name: 'San Francisco', stateOrProvinceId: 1 },
  ];

  const mockWarehouseDetail = {
    id: 1,
    name: 'Main Warehouse',
    contactName: 'John Doe',
    phone: '123-456-7890',
    addressLine1: '123 Main St',
    addressLine2: 'Suite 100',
    city: 'Los Angeles',
    countryId: 1,
    stateOrProvinceId: 1,
    districtId: 1,
    zipCode: '90210',
  };

  const defaultProps = {
    register: mockRegister,
    errors: {},
    setValue: mockSetValue,
    trigger: mockTrigger,
    warehouseDetail: undefined,
  };

  beforeEach(() => {
    vi.clearAllMocks();
    (useRouter as any).mockReturnValue({ query: {} });
    (getCountries as any).mockResolvedValue(mockCountries);
    (getStatesOrProvinces as any).mockResolvedValue(mockStates);
    (getDistricts as any).mockResolvedValue(mockDistricts);
  });

  describe('Rendering', () => {
    it('should render all form fields', async () => {
      render(<WarehouseGeneralInformation {...defaultProps} />);

      await waitFor(() => {
        expect(screen.getByText('Name')).toBeDefined();
        expect(screen.getByText('Contact Name')).toBeDefined();
        expect(screen.getByText('Phone')).toBeDefined();
        expect(screen.getByText('Address Line 1')).toBeDefined();
        expect(screen.getByText('Address Line 2')).toBeDefined();
        expect(screen.getByText('City')).toBeDefined();
        expect(screen.getByText('Country')).toBeDefined();
        expect(screen.getByText('State or province')).toBeDefined();
        expect(screen.getByText('District')).toBeDefined();
        expect(screen.getByText('Postal Code')).toBeDefined();
      });
    });

    it('should render select fields', async () => {
      render(<WarehouseGeneralInformation {...defaultProps} />);

      await waitFor(() => {
        expect(screen.getByTestId('select-countryId')).toBeDefined();
        expect(screen.getByTestId('select-stateOrProvinceId')).toBeDefined();
        expect(screen.getByTestId('select-districtId')).toBeDefined();
      });
    });
  });

  describe('Initial Data Fetching', () => {
    it('should fetch countries on mount', async () => {
      render(<WarehouseGeneralInformation {...defaultProps} />);

      await waitFor(() => {
        expect(getCountries).toHaveBeenCalledTimes(1);
      });
    });

    it('should populate countries dropdown', async () => {
      render(<WarehouseGeneralInformation {...defaultProps} />);

      await waitFor(() => {
        expect(screen.getByText('USA')).toBeDefined();
        expect(screen.getByText('Canada')).toBeDefined();
      });
    });
  });

  describe('Edit Mode - With Warehouse Data', () => {
    beforeEach(() => {
      (useRouter as any).mockReturnValue({ query: { id: '1' } });
    });

    it('should fetch states and districts when warehouseDetail is provided', async () => {
      render(<WarehouseGeneralInformation {...defaultProps} warehouseDetail={mockWarehouseDetail} />);

      await waitFor(() => {
        expect(getStatesOrProvinces).toHaveBeenCalledWith(mockWarehouseDetail.countryId);
        expect(getDistricts).toHaveBeenCalledWith(mockWarehouseDetail.stateOrProvinceId);
      });
    });

  });

  describe('Edit Mode - No Warehouse Data', () => {
    beforeEach(() => {
      (useRouter as any).mockReturnValue({ query: { id: '999' } });
    });

    it('should show "No warehouse" message when warehouseDetail is not found', () => {
      render(<WarehouseGeneralInformation {...defaultProps} warehouseDetail={undefined} />);
      
      expect(screen.getByText('No warehouse')).toBeDefined();
    });

    it('should not show form fields when warehouse not found', () => {
      render(<WarehouseGeneralInformation {...defaultProps} warehouseDetail={undefined} />);
      
      expect(screen.queryByText('Name')).toBeNull();
    });
  });

  describe('Location Selection', () => {
    beforeEach(() => {
      render(<WarehouseGeneralInformation {...defaultProps} />);
    });

    it('should fetch states when country changes', async () => {
      await waitFor(() => {
        expect(screen.getByTestId('select-countryId')).toBeDefined();
      });

      const countrySelect = screen.getByTestId('select-countryId');
      fireEvent.change(countrySelect, { target: { value: '1' } });

      await waitFor(() => {
        expect(getStatesOrProvinces).toHaveBeenCalledWith('1');
      });
    });

    it('should update districts when country changes', async () => {
      await waitFor(() => {
        expect(screen.getByTestId('select-countryId')).toBeDefined();
      });

      const countrySelect = screen.getByTestId('select-countryId');
      fireEvent.change(countrySelect, { target: { value: '1' } });

      await waitFor(() => {
        expect(getDistricts).toHaveBeenCalled();
      });
    });
  });



  describe('Location Data Loading States', () => {
    it('should show empty when countries are loading', async () => {
      (getCountries as any).mockImplementation(() => new Promise(() => {}));
      render(<WarehouseGeneralInformation {...defaultProps} />);

      await waitFor(() => {
        const countrySelect = screen.getByTestId('select-countryId');
        expect(countrySelect.children.length).toBe(1); // Only placeholder
      });
    });
  });
});