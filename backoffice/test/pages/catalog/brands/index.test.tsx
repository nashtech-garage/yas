import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, waitFor, fireEvent } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import BrandList from '../../../../pages/catalog/brands/index';
import { getPageableBrands, deleteBrand } from '../../../../modules/catalog/services/BrandService';
import { Brand } from '../../../../modules/catalog/models/Brand';

// Mock dependencies
vi.mock('../../../../modules/catalog/services/BrandService', () => ({
  getPageableBrands: vi.fn(),
  deleteBrand: vi.fn(),
}));

vi.mock('../../../../common/services/ApiClientService', () => ({
  default: {
    get: vi.fn(),
    post: vi.fn(),
    put: vi.fn(),
    delete: vi.fn(),
  },
}));

vi.mock('@commonServices/ResponseStatusHandlingService', () => ({
  handleDeletingResponse: vi.fn(),
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

vi.mock('react-bootstrap', async () => {
  const actual = await vi.importActual('react-bootstrap');
  return {
    ...actual,
    Table: ({ children, striped, bordered, hover }: any) => (
      <table data-testid="mock-table">{children}</table>
    ),
    Button: ({ children, onClick }: any) => (
      <button onClick={onClick} data-testid="mock-button">
        {children}
      </button>
    ),
  };
});

vi.mock('@commonItems/ModalDeleteCustom', () => ({
  default: ({ showModalDelete, handleClose, nameWantToDelete, handleDelete }: any) => (
    showModalDelete ? (
      <div data-testid="modal-delete">
        <h3>Delete {nameWantToDelete}</h3>
        <button onClick={handleClose} data-testid="modal-close">Close</button>
        <button onClick={handleDelete} data-testid="modal-confirm">Confirm Delete</button>
      </div>
    ) : null
  ),
}));

vi.mock('react-toastify', () => ({
  toast: {
    success: vi.fn(),
    error: vi.fn(),
  },
}));

describe('BrandList Page', () => {
  const mockBrands: Brand[] = [
    { id: 1, name: 'Nike', slug: 'nike', isPublish: true },
    { id: 2, name: 'Adidas', slug: 'adidas', isPublish: true },
    { id: 3, name: 'Puma', slug: 'puma', isPublish: false },
  ];

  const mockResponse = {
    brandContent: mockBrands,
    totalPages: 3,
    totalElements: 25,
    pageNo: 0,
    pageSize: 10,
  };

  beforeEach(() => {
    vi.clearAllMocks();
    (getPageableBrands as any).mockResolvedValue(mockResponse);
    (deleteBrand as any).mockResolvedValue({ status: 204 });
  });

  describe('Rendering', () => {
    it('should render loading state initially', () => {
      render(<BrandList />);
      expect(screen.getByText('Loading...')).toBeDefined();
    });

    it('should render brand list after loading', async () => {
      render(<BrandList />);
      
      await waitFor(() => {
        expect(screen.getByText('Nike')).toBeDefined();
        expect(screen.getByText('Adidas')).toBeDefined();
        expect(screen.getByText('Puma')).toBeDefined();
      });
    });

    it('should render table headers', async () => {
      render(<BrandList />);
      
      await waitFor(() => {
        expect(screen.getByText('#')).toBeDefined();
        expect(screen.getByText('Name')).toBeDefined();
        expect(screen.getByText('Slug')).toBeDefined();
        expect(screen.getByText('Action')).toBeDefined();
      });
    });

    it('should render Edit buttons for each brand', async () => {
      render(<BrandList />);
      
      await waitFor(() => {
        const editButtons = screen.getAllByText('Edit');
        expect(editButtons).toHaveLength(3);
      });
    });

    it('should render Delete buttons for each brand', async () => {
      render(<BrandList />);
      
      await waitFor(() => {
        const deleteButtons = screen.getAllByText('Delete');
        expect(deleteButtons).toHaveLength(3);
      });
    });
  });

  describe('Create Button', () => {
    it('should render Create Brand button', async () => {
      render(<BrandList />);
      
      await waitFor(() => {
        expect(screen.getByText('Create Brand')).toBeDefined();
      });
    });

    it('should have link to create brand page', async () => {
      render(<BrandList />);
      
      await waitFor(() => {
        const links = screen.getAllByTestId('mock-link');
        const createLink = links.find(link => link.textContent === 'Create Brand');
        expect(createLink).toHaveAttribute('href', '/catalog/brands/create');
      });
    });
  });

  describe('getPageableBrands', () => {
    it('should call getPageableBrands on mount with default params', async () => {
      render(<BrandList />);
      
      await waitFor(() => {
        expect(getPageableBrands).toHaveBeenCalledWith(0, 10);
      });
    });

    it('should handle API error', async () => {
      const consoleSpy = vi.spyOn(console, 'log').mockImplementation(() => {});
      (getPageableBrands as any).mockRejectedValue(new Error('Network error'));
      render(<BrandList />);
      
      await waitFor(() => {
        expect(consoleSpy).toHaveBeenCalled();
      });
      consoleSpy.mockRestore();
    });
  });

  describe('Delete Brand', () => {
    it('should show delete modal when clicking Delete button', async () => {
      render(<BrandList />);
      
      await waitFor(() => {
        expect(screen.getByText('Nike')).toBeDefined();
      });

      const deleteButtons = screen.getAllByText('Delete');
      fireEvent.click(deleteButtons[0]);

      expect(screen.getByTestId('modal-delete')).toBeDefined();
    });

    it('should call deleteBrand when confirming delete', async () => {
      render(<BrandList />);
      
      await waitFor(() => {
        expect(screen.getByText('Nike')).toBeDefined();
      });

      const deleteButtons = screen.getAllByText('Delete');
      fireEvent.click(deleteButtons[0]);

      const confirmButton = await screen.findByTestId('modal-confirm');
      fireEvent.click(confirmButton);

      await waitFor(() => {
        expect(deleteBrand).toHaveBeenCalledWith(1);
      });
    });

    it('should close modal without deleting when clicking Close', async () => {
      render(<BrandList />);
      
      await waitFor(() => {
        expect(screen.getByText('Nike')).toBeDefined();
      });

      const deleteButtons = screen.getAllByText('Delete');
      fireEvent.click(deleteButtons[0]);

      const closeButton = screen.getByTestId('modal-close');
      fireEvent.click(closeButton);

      expect(screen.queryByTestId('modal-delete')).toBeNull();
      expect(deleteBrand).not.toHaveBeenCalled();
    });

    it('should handle delete success', async () => {
      const { handleDeletingResponse } = await import('@commonServices/ResponseStatusHandlingService');
      render(<BrandList />);
      
      await waitFor(() => {
        expect(screen.getByText('Nike')).toBeDefined();
      });

      const deleteButtons = screen.getAllByText('Delete');
      fireEvent.click(deleteButtons[0]);

      const confirmButton = await screen.findByTestId('modal-confirm');
      fireEvent.click(confirmButton);

      await waitFor(() => {
        expect(deleteBrand).toHaveBeenCalled();
        expect(getPageableBrands).toHaveBeenCalledTimes(2);
      });
    });

    it('should handle delete error', async () => {
      const consoleSpy = vi.spyOn(console, 'log').mockImplementation(() => {});
      (deleteBrand as any).mockRejectedValue(new Error('Delete failed'));
      
      render(<BrandList />);
      
      await waitFor(() => {
        expect(screen.getByText('Nike')).toBeDefined();
      });

      const deleteButtons = screen.getAllByText('Delete');
      fireEvent.click(deleteButtons[0]);

      const confirmButton = await screen.findByTestId('modal-confirm');
      fireEvent.click(confirmButton);

      await waitFor(() => {
        expect(consoleSpy).toHaveBeenCalled();
      });
      consoleSpy.mockRestore();
    });
  });

  describe('Edit Link', () => {
    it('should have correct edit link for each brand', async () => {
      render(<BrandList />);
      
      await waitFor(() => {
        const editButtons = screen.getAllByText('Edit');
        expect(editButtons).toHaveLength(3);
        
        const links = screen.getAllByTestId('mock-link');
        const editLink = links.find(link => link.textContent === 'Edit');
        expect(editLink).toHaveAttribute('href', '/catalog/brands/1/edit');
      });
    });
  });

  describe('Pagination', () => {
    it('should show pagination when totalPage > 1', async () => {
      render(<BrandList />);
      
      await waitFor(() => {
        expect(screen.getByText('Next')).toBeDefined();
        expect(screen.getByText('Previous')).toBeDefined();
      });
    });

    it('should not show pagination when totalPage <= 1', async () => {
      (getPageableBrands as any).mockResolvedValue({
        brandContent: mockBrands,
        totalPages: 1,
        totalElements: 3,
        pageNo: 0,
        pageSize: 10,
      });
      render(<BrandList />);
      
      await waitFor(() => {
        expect(screen.queryByText('Next')).toBeNull();
      });
    });

    it('should call getPageableBrands with new page when page changes', async () => {
      render(<BrandList />);
      
      await waitFor(() => {
        expect(screen.getByText('Next')).toBeDefined();
      });

      const nextButton = screen.getByText('Next');
      fireEvent.click(nextButton);

      await waitFor(() => {
        expect(getPageableBrands).toHaveBeenCalledWith(1, 10);
      });
    });
  });
});