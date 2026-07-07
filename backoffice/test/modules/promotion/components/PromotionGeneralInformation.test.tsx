import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import PromotionGeneralInformation from '../../../../modules/promotion/components/PromotionGeneralInformation';
import { searchBrands, searchCategories, searchProducts } from '../../../../modules/promotion/services/ProductService';

// Mock dependencies
vi.mock('../../../../modules/promotion/services/ProductService', () => ({
  searchBrands: vi.fn(),
  searchCategories: vi.fn(),
  searchProducts: vi.fn(),
}));

vi.mock('../../../common/items/Input', () => ({
  Input: ({ labelText, field, defaultValue, register, registerOptions, error, type }: any) => (
    <div>
      <label htmlFor={field}>{labelText}</label>
      <input
        id={field}
        data-testid={`input-${field}`}
        defaultValue={defaultValue}
        type={type || 'text'}
        {...register(field, registerOptions)}
      />
      {error && <span data-testid={`error-${field}`}>{error}</span>}
    </div>
  ),
  Select: ({ labelText, field, placeholder, options, register, registerOptions, error, defaultValue, onChange }: any) => (
    <div>
      <label htmlFor={field}>{labelText}</label>
      <select
        id={field}
        data-testid={`select-${field}`}
        defaultValue={defaultValue}
        {...register(field, registerOptions)}
        onChange={(e) => {
          if (register) register(field, registerOptions).onChange(e);
          if (onChange) onChange(e);
        }}
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
  TextArea: ({ labelText, field, defaultValue, register, error }: any) => (
    <div>
      <label htmlFor={field}>{labelText}</label>
      <textarea id={field} data-testid={`textarea-${field}`} defaultValue={defaultValue} {...register(field)} />
      {error && <span data-testid={`error-${field}`}>{error}</span>}
    </div>
  ),
  Switch: ({ labelText, field, defaultChecked, register }: any) => (
    <div>
      <label htmlFor={field}>
        <input
          type="checkbox"
          id={field}
          data-testid={`switch-${field}`}
          defaultChecked={defaultChecked}
          {...register(field)}
        />
        {labelText}
      </label>
    </div>
  ),
  DatePicker: ({ labelText, field, defaultValue, register, registerOptions, error }: any) => (
    <div>
      <label htmlFor={field}>{labelText}</label>
      <input
        type="date"
        id={field}
        data-testid={`date-${field}`}
        defaultValue={defaultValue}
        {...register(field, registerOptions)}
      />
      {error && <span data-testid={`error-${field}`}>{error}</span>}
    </div>
  ),
}));

vi.mock('../MultipleAutoComplete', () => ({
  default: ({ labelText, options, fetchOptions, onSelect, optionSelectedIds, onRemoveElement, addedOptions }: any) => (
    <div data-testid={`autocomplete-${labelText.toLowerCase()}`}>
      <span>{labelText}</span>
      <button onClick={() => onSelect(options?.[0]?.id)} data-testid="select-option">Select</button>
      {optionSelectedIds.map((id: number) => (
        <div key={id} data-testid={`selected-${id}`}>
          Selected ID: {id}
          <button onClick={() => onRemoveElement(id)}>Remove</button>
        </div>
      ))}
    </div>
  ),
}));

describe('PromotionGeneralInformation', () => {
  const mockRegister = vi.fn((field, options) => ({
    name: field,
    onChange: vi.fn(),
    onBlur: vi.fn(),
    ref: vi.fn(),
  }));

  const mockSetValue = vi.fn();
  const mockTrigger = vi.fn();

  const mockPromotion = {
    id: 1,
    name: 'Summer Sale',
    slug: 'summer-sale',
    couponCode: 'SUMMER20',
    description: 'Summer discount promotion',
    discountType: 'PERCENTAGE',
    usageType: 'LIMITED',
    applyTo: 'PRODUCT',
    discountPercentage: 20,
    discountAmount: 0,
    usageLimit: 100,
    minimumOrderPurchaseAmount: 50,
    isActive: true,
    startDate: '2024-06-01',
    endDate: '2024-06-30',
    brands: [{ id: 1, name: 'Nike' }],
    categories: [{ id: 1, name: 'Electronics' }],
    products: [{ id: 1, name: 'Laptop' }],
  };

  const defaultProps = {
    register: mockRegister,
    errors: {},
    setValue: mockSetValue,
    trigger: mockTrigger,
    promotion: undefined,
    isSubmitting: false,
  };

  beforeEach(() => {
    vi.clearAllMocks();
    (searchProducts as any).mockResolvedValue({ productContent: [{ id: 1, name: 'Laptop' }] });
    (searchCategories as any).mockResolvedValue([{ id: 1, name: 'Electronics' }]);
    (searchBrands as any).mockResolvedValue([{ id: 1, name: 'Nike' }]);
  });

  describe('Rendering', () => {
    it('should render all basic fields', () => {
      render(<PromotionGeneralInformation {...defaultProps} />);
      
      expect(screen.getByText('Name')).toBeDefined();
      expect(screen.getByText('Slug')).toBeDefined();
      expect(screen.getByText('Coupon code')).toBeDefined();
      expect(screen.getByText('Description')).toBeDefined();
      expect(screen.getByText('Discount type')).toBeDefined();
      expect(screen.getByText('Usage type')).toBeDefined();
      expect(screen.getByText('Minimum order purchase amount')).toBeDefined();
      expect(screen.getByText('Active')).toBeDefined();
      expect(screen.getByText('Start date')).toBeDefined();
      expect(screen.getByText('End date')).toBeDefined();
      expect(screen.getByText('Apply to')).toBeDefined();
    });

  });

  describe('Register Options', () => {
    it('should register name with required validation', () => {
      render(<PromotionGeneralInformation {...defaultProps} />);
      expect(mockRegister).toHaveBeenCalledWith('name', expect.objectContaining({
        required: { value: true, message: 'Name is required' },
      }));
    });

    it('should register slug with required validation', () => {
      render(<PromotionGeneralInformation {...defaultProps} />);
      expect(mockRegister).toHaveBeenCalledWith('slug', expect.objectContaining({
        required: { value: true, message: 'Slug is required' },
      }));
    });
  });
});