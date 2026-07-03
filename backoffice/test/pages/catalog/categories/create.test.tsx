import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, waitFor, fireEvent } from '@testing-library/react';
import CategoryCreate from '../../../../pages/catalog/categories/create';
import { createCategory, getCategories } from '../../../../modules/catalog/services/CategoryService';
import { useRouter } from 'next/router';
import slugify from 'slugify';

// Mock dependencies
vi.mock('../../../../modules/catalog/services/CategoryService', () => ({
  createCategory: vi.fn(),
  getCategories: vi.fn(),
}));

vi.mock('next/router', () => ({
  useRouter: vi.fn(() => ({
    push: vi.fn(),
    replace: vi.fn(),
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

vi.mock('slugify', () => ({
  default: vi.fn((str) => str.toLowerCase().replace(/\s/g, '-')),
}));

vi.mock('@catalogComponents/CategoryImage', () => ({
  default: ({ setValue, id, image }: any) => (
    <div data-testid="category-image">
      <input
        data-testid="input-imageId"
        onChange={(e) => setValue('imageId', parseInt(e.target.value))}
        placeholder="Image ID"
      />
    </div>
  ),
}));

vi.mock('@commonItems/Input', () => ({
  Input: ({ labelText, field, register, registerOptions, defaultValue }: any) => {
    const { onChange, ...rest } = registerOptions || {};
    return (
      <div>
        <label htmlFor={field}>{labelText}</label>
        <input
          id={field}
          data-testid={`input-${field}`}
          defaultValue={defaultValue}
          onChange={(e) => {
            if (onChange) onChange(e);
            if (register) register(field, registerOptions).onChange(e);
          }}
          {...rest}
        />
      </div>
    );
  },
  TextArea: ({ labelText, field, register }: any) => (
    <div>
      <label htmlFor={field}>{labelText}</label>
      <textarea id={field} data-testid={`textarea-${field}`} {...register(field)} />
    </div>
  ),
  CheckBox: ({ labelText, field, register, defaultChecked }: any) => (
    <div>
      <label htmlFor={field}>
        <input
          type="checkbox"
          id={field}
          data-testid={`checkbox-${field}`}
          defaultChecked={defaultChecked}
          {...register(field)}
        />
        {labelText}
      </label>
    </div>
  ),
}));

vi.mock('@commonServices/ResponseStatusHandlingService', () => ({
  handleCreatingResponse: vi.fn(),
}));

vi.mock('react-toastify', () => ({
  toast: {
    success: vi.fn(),
    error: vi.fn(),
  },
}));

describe('CategoryCreate Page', () => {
  const mockRouterReplace = vi.fn();
  const mockCategories = [
    { id: 1, name: 'Electronics', slug: 'electronics', parentId: null },
    { id: 2, name: 'Clothing', slug: 'clothing', parentId: null },
  ];

  beforeEach(() => {
    vi.clearAllMocks();
    (useRouter as any).mockReturnValue({
      push: vi.fn(),
      replace: mockRouterReplace,
      query: {},
    });
    (getCategories as any).mockResolvedValue(mockCategories);
    (createCategory as any).mockResolvedValue({ status: 201 });
  });

  describe('Rendering', () => {
    it('should render page title', async () => {
      render(<CategoryCreate />);
      expect(screen.getByText('Create category')).toBeDefined();
    });

    it('should render all form fields', async () => {
      render(<CategoryCreate />);
      
      await waitFor(() => {
        expect(screen.getByText('Name')).toBeDefined();
        expect(screen.getByText('Slug')).toBeDefined();
        expect(screen.getByText('Parent category')).toBeDefined();
        expect(screen.getByText('Description')).toBeDefined();
        expect(screen.getByText('Meta Keywords')).toBeDefined();
        expect(screen.getByText('Meta Description')).toBeDefined();
        expect(screen.getByText('Display Order')).toBeDefined();
        expect(screen.getByText('Publish')).toBeDefined();
      });
    });

    it('should render Name input', async () => {
      render(<CategoryCreate />);
      expect(screen.getByTestId('input-name')).toBeDefined();
    });

    it('should render Slug input', async () => {
      render(<CategoryCreate />);
      expect(screen.getByTestId('input-slug')).toBeDefined();
    });

    it('should render Parent category select', async () => {
      render(<CategoryCreate />);
      await waitFor(() => {
        expect(screen.getByLabelText('Parent category')).toBeDefined();
      });
    });

    it('should render Description textarea', async () => {
      render(<CategoryCreate />);
      expect(screen.getByTestId('textarea-description')).toBeDefined();
    });

    it('should render Meta Keywords input', async () => {
      render(<CategoryCreate />);
      expect(screen.getByTestId('input-metaKeywords')).toBeDefined();
    });

    it('should render Meta Description textarea', async () => {
      render(<CategoryCreate />);
      expect(screen.getByTestId('textarea-metaDescription')).toBeDefined();
    });

    it('should render Display Order input', async () => {
      render(<CategoryCreate />);
      const displayOrderInput = screen.getByTestId('input-displayOrder') as HTMLInputElement;
      expect(displayOrderInput.defaultValue).toBe('0');
    });

    it('should render Publish checkbox', async () => {
      render(<CategoryCreate />);
      expect(screen.getByTestId('checkbox-isPublish')).toBeDefined();
    });

    it('should render CategoryImage component', async () => {
      render(<CategoryCreate />);
      expect(screen.getByTestId('category-image')).toBeDefined();
    });

    it('should render Save button', async () => {
      render(<CategoryCreate />);
      const saveButton = screen.getByText('Save');
      expect(saveButton).toBeDefined();
      expect(saveButton).toHaveAttribute('type', 'submit');
    });

    it('should render Cancel button', async () => {
      render(<CategoryCreate />);
      const cancelButton = screen.getByText('Cancel');
      expect(cancelButton).toBeDefined();
    });

    it('should have Cancel button linked to categories page', async () => {
      render(<CategoryCreate />);
      const links = screen.getAllByTestId('mock-link');
      const cancelLink = links.find(link => link.textContent === 'Cancel');
      expect(cancelLink).toHaveAttribute('href', '/catalog/categories');
    });
  });

  describe('Parent Category Select', () => {
    it('should load categories for parent selection', async () => {
      render(<CategoryCreate />);
      
      await waitFor(() => {
        expect(getCategories).toHaveBeenCalled();
      });
    });

    it('should display Top option', async () => {
      render(<CategoryCreate />);
      
      await waitFor(() => {
        expect(screen.getByText('Top')).toBeDefined();
      });
    });
  });

  describe('Auto-generate Slug', () => {
    it('should auto-generate slug when name changes', async () => {
      render(<CategoryCreate />);
      
      const nameInput = screen.getByTestId('input-name');
      fireEvent.change(nameInput, { target: { value: 'Test Category' } });

      await waitFor(() => {
        expect(slugify).toHaveBeenCalledWith('Test Category', { lower: true, strict: true });
      });
    });
  });

  describe('Form Submission', () => {
    const fillForm = async () => {
      const nameInput = screen.getByTestId('input-name');
      const slugInput = screen.getByTestId('input-slug');
      
      fireEvent.change(nameInput, { target: { value: 'New Category' } });
      fireEvent.change(slugInput, { target: { value: 'new-category' } });
    };

    it('should not redirect on non-201 status', async () => {
      (createCategory as any).mockResolvedValue({ status: 400 });
      render(<CategoryCreate />);
      
      await fillForm();
      
      const saveButton = screen.getByText('Save');
      fireEvent.click(saveButton);

      await waitFor(() => {
        expect(mockRouterReplace).not.toHaveBeenCalled();
      });
    });
  });
    
});