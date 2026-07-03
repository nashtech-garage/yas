// test/common/items/ProductModal.test.tsx
import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { useRouter } from 'next/router';
import ProductModal from '../../../common/items/ProductModal';
import { getProducts } from '@catalogServices/ProductService';

// Mock next/router
vi.mock('next/router', () => ({
  useRouter: vi.fn(),
}));

// Mock ProductService
vi.mock('@catalogServices/ProductService', () => ({
  getProducts: vi.fn(),
}));

// Mock react-paginate
vi.mock('react-paginate', () => ({
  default: ({ onPageChange, pageCount, forcePage }: any) => (
    <div data-testid="pagination">
      <button 
        data-testid="prev-page" 
        onClick={() => onPageChange({ selected: forcePage - 1 })}
        disabled={forcePage === 0}
      >
        Previous
      </button>
      <span>Page {forcePage + 1} of {pageCount}</span>
      <button 
        data-testid="next-page" 
        onClick={() => onPageChange({ selected: forcePage + 1 })}
        disabled={forcePage === pageCount - 1}
      >
        Next
      </button>
    </div>
  ),
}));

describe('ProductModal', () => {
  const mockProducts = [
    { id: 1, name: 'Product 1', slug: 'product-1' },
    { id: 2, name: 'Product 2', slug: 'product-2' },
    { id: 3, name: 'Product 3', slug: 'product-3' },
  ];

  const mockSelectedProducts = [
    { id: 1, name: 'Product 1', slug: 'product-1' },
  ];

  const defaultProps = {
    show: true,
    onHide: vi.fn(),
    label: 'Select Product',
    onSelected: vi.fn(),
    selectedProduct: [],
  };

  beforeEach(() => {
    vi.clearAllMocks();
    (useRouter as any).mockReturnValue({
      query: {},
    });
  });

  describe('Rendering', () => {
    it('should render modal when show is true', async () => {
      (getProducts as any).mockResolvedValue({
        productContent: mockProducts,
        totalPages: 1,
      });

      render(<ProductModal {...defaultProps} />);
      
      await waitFor(() => {
        expect(screen.getByText('Select Product')).toBeDefined();
      });
    });

    it('should render product list', async () => {
      (getProducts as any).mockResolvedValue({
        productContent: mockProducts,
        totalPages: 1,
      });

      render(<ProductModal {...defaultProps} />);
      
      await waitFor(() => {
        expect(screen.getByText('Product 1')).toBeDefined();
        expect(screen.getByText('Product 2')).toBeDefined();
        expect(screen.getByText('Product 3')).toBeDefined();
      });
    });

    it('should render table headers', async () => {
      (getProducts as any).mockResolvedValue({
        productContent: mockProducts,
        totalPages: 1,
      });

      render(<ProductModal {...defaultProps} />);
      
      await waitFor(() => {
        expect(screen.getByText('Select')).toBeDefined();
        expect(screen.getByText('Product Name')).toBeDefined();
      });
    });

    it('should render Close button', async () => {
      (getProducts as any).mockResolvedValue({
        productContent: mockProducts,
        totalPages: 1,
      });

      render(<ProductModal {...defaultProps} />);
      
      await waitFor(() => {
        expect(screen.getByText('Close')).toBeDefined();
      });
    });
  });

  describe('Pagination', () => {
    it('should show pagination when totalPage > 1', async () => {
      (getProducts as any).mockResolvedValue({
        productContent: mockProducts,
        totalPages: 3,
      });

      render(<ProductModal {...defaultProps} />);
      
      await waitFor(() => {
        expect(screen.getByTestId('pagination')).toBeDefined();
      });
    });

    it('should not show pagination when totalPage <= 1', async () => {
      (getProducts as any).mockResolvedValue({
        productContent: mockProducts,
        totalPages: 1,
      });

      render(<ProductModal {...defaultProps} />);
      
      await waitFor(() => {
        expect(screen.queryByTestId('pagination')).toBeNull();
      });
    });
  });

  describe('Product Selection', () => {
    it('should call onSelected when checkbox is clicked', async () => {
      const onSelected = vi.fn();
      (getProducts as any).mockResolvedValue({
        productContent: mockProducts,
        totalPages: 1,
      });

      render(<ProductModal {...defaultProps} onSelected={onSelected} />);
      
      await waitFor(() => {
        expect(screen.getByText('Product 1')).toBeDefined();
      });
      
      const checkbox = screen.getAllByRole('checkbox')[0];
      fireEvent.click(checkbox);
      
      expect(onSelected).toHaveBeenCalledTimes(1);
      expect(onSelected).toHaveBeenCalledWith(mockProducts[0]);
    });
  });

  describe('Modal Behavior', () => {
    it('should call onHide when Close button is clicked', async () => {
      const onHide = vi.fn();
      (getProducts as any).mockResolvedValue({
        productContent: mockProducts,
        totalPages: 1,
      });

      render(<ProductModal {...defaultProps} onHide={onHide} />);
      
      await waitFor(() => {
        expect(screen.getByText('Close')).toBeDefined();
      });
      
      const closeButton = screen.getByText('Close');
      fireEvent.click(closeButton);
      
      expect(onHide).toHaveBeenCalledTimes(1);
    });
  });

  describe('Empty State', () => {
    it('should handle empty product list', async () => {
      (getProducts as any).mockResolvedValue({
        productContent: [],
        totalPages: 0,
      });

      render(<ProductModal {...defaultProps} />);
      
      await waitFor(() => {
        const tableBody = document.querySelector('tbody');
        expect(tableBody?.children.length).toBe(0);
      });
    });
  });
});