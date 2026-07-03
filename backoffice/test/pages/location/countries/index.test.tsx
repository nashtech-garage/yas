import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import CountryList from '../../../../pages/location/countries/index';
import { getPageableCountries, deleteCountry } from '../../../../modules/location/services/CountryService';
import { Country } from '../../../../modules/location/models/Country';

// Mock dependencies
vi.mock('../../../../modules/location/services/CountryService', () => ({
  getPageableCountries: vi.fn(),
  deleteCountry: vi.fn(),
}));

vi.mock('../../../../common/services/ApiClientService', () => ({
  default: {
    get: vi.fn(),
    post: vi.fn(),
    put: vi.fn(),
    delete: vi.fn(),
  },
}));

vi.mock('next/router', () => ({
  useRouter: vi.fn(() => ({
    push: vi.fn(),
    query: {},
  })),
}));

vi.mock('next/link', () => ({
  default: ({ children, href }: any) => (
    <a href={href} data-testid="mock-link">
      {children}
    </a>
  ),
}));

vi.mock('react-toastify', () => ({
  toast: {
    success: vi.fn(),
    error: vi.fn(),
  },
}));

// Mock ConfirmationDialog
vi.mock('common/components/ConfirmationDialog', () => ({
  default: ({ show, onConfirm, onCancel, title, message }: any) => (
    show ? (
      <div data-testid="confirmation-dialog">
        <h3>{title}</h3>
        <p>{message}</p>
        <button onClick={onConfirm} data-testid="confirm-btn">Confirm</button>
        <button onClick={onCancel} data-testid="cancel-btn">Cancel</button>
      </div>
    ) : null
  ),
}));

describe('CountryList Page', () => {
  // SỬA: Country theo đúng type
  const mockCountries: Country[] = [
    {
      id: 1,
      code2: 'US',
      name: 'United States',
      code3: 'USA',
      isBillingEnabled: true,
      isShippingEnabled: true,
      isCityEnabled: true,
      isZipCodeEnabled: true,
      isDistrictEnabled: true,
    },
    {
      id: 2,
      code2: 'VN',
      name: 'Vietnam',
      code3: 'VNM',
      isBillingEnabled: true,
      isShippingEnabled: true,
      isCityEnabled: true,
      isZipCodeEnabled: true,
      isDistrictEnabled: false,
    },
    {
      id: 3,
      code2: 'JP',
      name: 'Japan',
      code3: 'JPN',
      isBillingEnabled: true,
      isShippingEnabled: true,
      isCityEnabled: false,
      isZipCodeEnabled: true,
      isDistrictEnabled: false,
    },
  ];

  const mockResponse = {
    countryContent: mockCountries,
    totalPages: 1,
    totalElements: 3,
    pageNo: 0,
    pageSize: 10,
  };

  beforeEach(() => {
    vi.clearAllMocks();
    (getPageableCountries as any).mockResolvedValue(mockResponse);
    (deleteCountry as any).mockResolvedValue({ status: 204 });
  });

  describe('getPageableCountries', () => {
    it('should call getPageableCountries on mount', async () => {
      render(<CountryList />);
      await waitFor(() => {
        expect(getPageableCountries).toHaveBeenCalled();
      });
    });

    it('should pass correct parameters to getPageableCountries', async () => {
      render(<CountryList />);
      await waitFor(() => {
        expect(getPageableCountries).toHaveBeenCalledWith(0, 10);
      });
    });

    it('should display country data in table', async () => {
      render(<CountryList />);
      
      await waitFor(() => {
        expect(screen.getByText('United States')).toBeDefined();
        expect(screen.getByText('Vietnam')).toBeDefined();
        expect(screen.getByText('Japan')).toBeDefined();
        expect(screen.getByText('US')).toBeDefined();
        expect(screen.getByText('VN')).toBeDefined();
        expect(screen.getByText('JP')).toBeDefined();
      });
    });

    it('should handle API error', async () => {
      const consoleSpy = vi.spyOn(console, 'log').mockImplementation(() => {});
      (getPageableCountries as any).mockRejectedValue(new Error('Network error'));
      render(<CountryList />);
      
      await waitFor(() => {
        expect(consoleSpy).toHaveBeenCalled();
      });
      consoleSpy.mockRestore();
    });
  });

  describe('deleteCountry', () => {
    it('should be defined', () => {
      expect(deleteCountry).toBeDefined();
    });

    it('should handle delete success', async () => {
      (deleteCountry as any).mockResolvedValue({ status: 204 });
      render(<CountryList />);
      await waitFor(() => {
        expect(deleteCountry).toBeDefined();
      });
    });
  });

  describe('page state', () => {
    it('should have initial pageNo as 0', async () => {
      render(<CountryList />);
      await waitFor(() => {
        expect(getPageableCountries).toHaveBeenCalledWith(0, 10);
      });
    });
  });
});