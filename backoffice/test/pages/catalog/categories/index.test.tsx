import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, waitFor, fireEvent } from '@testing-library/react';
import CategoryList from '../../../../pages/catalog/categories/index';
import { getCategories, deleteCategory } from '../../../../modules/catalog/services/CategoryService';
import { Category } from '../../../../modules/catalog/models/Category';

// Mock dependencies
vi.mock('../../../../modules/catalog/services/CategoryService', () => ({
  getCategories: vi.fn(),
  deleteCategory: vi.fn(),
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
    Modal: ({ children, show }: any) => show ? <div data-testid="modal">{children}</div> : null,
  };
});

vi.mock('../../../common/items/ModalDeleteCustom', () => ({
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

vi.mock('../../../common/services/ResponseStatusHandlingService', () => ({
  handleDeletingResponse: vi.fn(),
}));

vi.mock('react-toastify', () => ({
  toast: {
    success: vi.fn(),
    error: vi.fn(),
  },
}));

describe('CategoryList Page', () => {
  const mockCategories: Category[] = [
    {
      id: 1,
      name: 'Electronics',
      description: 'Electronic products',
      slug: 'electronics',
      parentId: null,
      metaKeywords: 'electronics',
      metaDescription: 'Electronic products',
      displayOrder: 1,
      isPublish: true,
    },
    {
      id: 2,
      name: 'Laptops',
      description: 'Laptop computers',
      slug: 'laptops',
      parentId: 1,
      metaKeywords: 'laptops',
      metaDescription: 'Laptop computers',
      displayOrder: 1,
      isPublish: true,
    },
    {
      id: 3,
      name: 'Clothing',
      description: 'Clothing items',
      slug: 'clothing',
      parentId: null,
      metaKeywords: 'clothing',
      metaDescription: 'Clothing items',
      displayOrder: 2,
      isPublish: true,
    },
  ];

  beforeEach(() => {
    vi.clearAllMocks();
    (getCategories as any).mockResolvedValue(mockCategories);
    (deleteCategory as any).mockResolvedValue({ status: 204 });
  });

  describe('Rendering', () => {
    it('should render loading state initially', () => {
      (getCategories as any).mockImplementation(() => new Promise(() => {}));
      render(<CategoryList />);
      expect(screen.getByText('Loading...')).toBeDefined();
    });

    it('should render table headers', async () => {
      render(<CategoryList />);
      
      await waitFor(() => {
        expect(screen.getByText('#')).toBeDefined();
        expect(screen.getByText('Name')).toBeDefined();
        expect(screen.getByText('Actions')).toBeDefined();
      });
    });
  });

  describe('Create Button', () => {
    it('should render Create Category button', async () => {
      render(<CategoryList />);
      
      await waitFor(() => {
        expect(screen.getByText('Create Category')).toBeDefined();
      });
    });

    it('should have link to create category page', async () => {
      render(<CategoryList />);
      
      await waitFor(() => {
        const links = screen.getAllByTestId('mock-link');
        const createLink = links.find(link => link.textContent === 'Create Category');
        expect(createLink).toHaveAttribute('href', '/catalog/categories/create');
      });
    });
  });

  describe('getCategories', () => {
    it('should call getCategories on mount', async () => {
      render(<CategoryList />);
      
      await waitFor(() => {
        expect(getCategories).toHaveBeenCalled();
      });
    });
  });


});