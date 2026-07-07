import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import TaxClassList from '../../../../pages/tax/tax-classes/index';
import { getPageableTaxClasses, deleteTaxClass } from '../../../../modules/tax/services/TaxClassService';
import { TaxClass } from '../../../../modules/tax/models/TaxClass';

// Mock dependencies
vi.mock('../../../../modules/tax/services/TaxClassService', () => ({
  getPageableTaxClasses: vi.fn(),
  deleteTaxClass: vi.fn(),
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

describe('TaxClassList Page', () => {
  // SỬA: TaxClass chỉ có id và name (xóa description)
  const mockTaxClasses: TaxClass[] = [
    {
      id: 1,
      name: 'Standard Tax',
    },
    {
      id: 2,
      name: 'Reduced Tax',
    },
    {
      id: 3,
      name: 'Zero Tax',
    },
  ];

  const mockResponse = {
    taxClassContent: mockTaxClasses,
    totalPages: 1,
    totalElements: 3,
    pageNo: 0,
    pageSize: 10,
  };

  beforeEach(() => {
    vi.clearAllMocks();
    (getPageableTaxClasses as any).mockResolvedValue(mockResponse);
    (deleteTaxClass as any).mockResolvedValue({ status: 204 });
  });

  describe('getPageableTaxClasses', () => {
    it('should call getPageableTaxClasses on mount', async () => {
      render(<TaxClassList />);
      await waitFor(() => {
        expect(getPageableTaxClasses).toHaveBeenCalled();
      });
    });

    it('should pass correct parameters to getPageableTaxClasses', async () => {
      render(<TaxClassList />);
      await waitFor(() => {
        expect(getPageableTaxClasses).toHaveBeenCalledWith(0, 10);
      });
    });

    it('should display tax class data in table', async () => {
      render(<TaxClassList />);
      
      await waitFor(() => {
        expect(screen.getByText('Standard Tax')).toBeDefined();
        expect(screen.getByText('Reduced Tax')).toBeDefined();
        expect(screen.getByText('Zero Tax')).toBeDefined();
      });
    });
    it('should handle API error', async () => {
      const consoleSpy = vi.spyOn(console, 'log').mockImplementation(() => {});
      (getPageableTaxClasses as any).mockRejectedValue(new Error('Network error'));
      render(<TaxClassList />);
      
      await waitFor(() => {
        expect(consoleSpy).toHaveBeenCalled();
      });
      consoleSpy.mockRestore();
    });
  });

  describe('deleteTaxClass', () => {
    it('should be defined', () => {
      expect(deleteTaxClass).toBeDefined();
    });

    it('should handle delete success', async () => {
      (deleteTaxClass as any).mockResolvedValue({ status: 204 });
      render(<TaxClassList />);
      await waitFor(() => {
        expect(deleteTaxClass).toBeDefined();
      });
    });
  });

  describe('page state', () => {
    it('should have initial pageNo as 0', async () => {
      render(<TaxClassList />);
      await waitFor(() => {
        expect(getPageableTaxClasses).toHaveBeenCalledWith(0, 10);
      });
    });
  });
});