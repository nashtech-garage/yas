import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import BrandCreate from '../../../../pages/catalog/brands/create';
import { createBrand } from '../../../../modules/catalog/services/BrandService';
import { useRouter } from 'next/router';

// Mock dependencies
vi.mock('../../../../modules/catalog/services/BrandService', () => ({
  createBrand: vi.fn(),
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

vi.mock('../../../../common/services/ResponseStatusHandlingService', () => ({
  handleCreatingResponse: vi.fn(),
}));

vi.mock('../../../../modules/catalog/components/BrandGeneralInformation', () => ({
  default: ({ register, errors, setValue, trigger }: any) => (
    <div data-testid="brand-general-info">
      <input
        data-testid="input-name"
        {...register('name', { required: true })}
        placeholder="Name"
      />
      <input
        data-testid="input-slug"
        {...register('slug', { required: true })}
        placeholder="Slug"
      />
      <input
        type="checkbox"
        data-testid="checkbox-isPublish"
        {...register('isPublish')}
      />
      {errors.name && <span data-testid="error-name">Name is required</span>}
    </div>
  ),
}));

vi.mock('react-toastify', () => ({
  toast: {
    success: vi.fn(),
    error: vi.fn(),
  },
}));

describe('BrandCreate Page', () => {
  const mockRouterReplace = vi.fn();
  const mockRouterPush = vi.fn();

  beforeEach(() => {
    vi.clearAllMocks();
    (useRouter as any).mockReturnValue({
      push: mockRouterPush,
      replace: mockRouterReplace,
      query: {},
    });
    (createBrand as any).mockResolvedValue({ status: 201 });
  });

  describe('Rendering', () => {
    it('should render page title', () => {
      render(<BrandCreate />);
      expect(screen.getByText('Create brand')).toBeDefined();
    });

    it('should render form element', () => {
      render(<BrandCreate />);
      const form = document.querySelector('form');
      expect(form).toBeDefined();
    });

    it('should render BrandGeneralInformation component', () => {
      render(<BrandCreate />);
      expect(screen.getByTestId('brand-general-info')).toBeDefined();
    });

    it('should render Save button', () => {
      render(<BrandCreate />);
      expect(screen.getByText('Save')).toBeDefined();
      expect(screen.getByText('Save')).toHaveAttribute('type', 'submit');
    });

    it('should render Cancel button', () => {
      render(<BrandCreate />);
      expect(screen.getByText('Cancel')).toBeDefined();
    });

    it('should have Cancel button with red background', () => {
      render(<BrandCreate />);
      const cancelButton = screen.getByText('Cancel');
      expect(cancelButton).toHaveStyle({ background: 'red', marginLeft: '30px' });
    });

    it('should have Cancel button linked to brands page', () => {
      render(<BrandCreate />);
      const links = screen.getAllByTestId('mock-link');
      const cancelLink = links.find(link => link.textContent === 'Cancel');
      expect(cancelLink).toHaveAttribute('href', '/catalog/brands');
    });
  });

  describe('Form Submission', () => {
    it('should call createBrand with correct data when form is submitted', async () => {
      render(<BrandCreate />);
      
      const nameInput = screen.getByTestId('input-name');
      const slugInput = screen.getByTestId('input-slug');
      const publishCheckbox = screen.getByTestId('checkbox-isPublish');
      const saveButton = screen.getByText('Save');

      fireEvent.change(nameInput, { target: { value: 'Nike' } });
      fireEvent.change(slugInput, { target: { value: 'nike' } });
      fireEvent.click(publishCheckbox);
      fireEvent.click(saveButton);

      await waitFor(() => {
        expect(createBrand).toHaveBeenCalledWith({
          id: 0,
          name: 'Nike',
          slug: 'nike',
          isPublish: true,
        });
      });
    });

    it('should call createBrand with isPublish false when checkbox not checked', async () => {
      render(<BrandCreate />);
      
      const nameInput = screen.getByTestId('input-name');
      const slugInput = screen.getByTestId('input-slug');
      const saveButton = screen.getByText('Save');

      fireEvent.change(nameInput, { target: { value: 'Adidas' } });
      fireEvent.change(slugInput, { target: { value: 'adidas' } });
      fireEvent.click(saveButton);

      await waitFor(() => {
        expect(createBrand).toHaveBeenCalledWith({
          id: 0,
          name: 'Adidas',
          slug: 'adidas',
          isPublish: false,
        });
      });
    });

    it('should redirect to brand list on successful creation (status 201)', async () => {
      (createBrand as any).mockResolvedValue({ status: 201 });
      render(<BrandCreate />);
      
      const nameInput = screen.getByTestId('input-name');
      const slugInput = screen.getByTestId('input-slug');
      const saveButton = screen.getByText('Save');

      fireEvent.change(nameInput, { target: { value: 'Puma' } });
      fireEvent.change(slugInput, { target: { value: 'puma' } });
      fireEvent.click(saveButton);

      await waitFor(() => {
        expect(mockRouterReplace).toHaveBeenCalledWith('/catalog/brands');
      });
    });

    it('should not redirect on non-201 status', async () => {
      (createBrand as any).mockResolvedValue({ status: 400 });
      render(<BrandCreate />);
      
      const nameInput = screen.getByTestId('input-name');
      const slugInput = screen.getByTestId('input-slug');
      const saveButton = screen.getByText('Save');

      fireEvent.change(nameInput, { target: { value: 'Puma' } });
      fireEvent.change(slugInput, { target: { value: 'puma' } });
      fireEvent.click(saveButton);

      await waitFor(() => {
        expect(mockRouterReplace).not.toHaveBeenCalled();
      });
    });

    it('should call handleCreatingResponse after submission', async () => {
      const { handleCreatingResponse } = await import('../../../../common/services/ResponseStatusHandlingService');
      render(<BrandCreate />);
      
      const nameInput = screen.getByTestId('input-name');
      const slugInput = screen.getByTestId('input-slug');
      const saveButton = screen.getByText('Save');

      fireEvent.change(nameInput, { target: { value: 'Reebok' } });
      fireEvent.change(slugInput, { target: { value: 'reebok' } });
      fireEvent.click(saveButton);

      await waitFor(() => {
        expect(handleCreatingResponse).toHaveBeenCalled();
      });
    });
  });

  describe('Validation', () => {
    it('should show validation errors when submitting empty form', async () => {
      render(<BrandCreate />);
      
      const saveButton = screen.getByText('Save');
      fireEvent.click(saveButton);

      await waitFor(() => {
        expect(screen.getByTestId('error-name')).toBeDefined();
      });
    });
  });
});