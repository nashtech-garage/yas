import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, fireEvent } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import ProfileForm from '../../../../modules/customer/components/ProfileForm';
import { useRouter } from 'next/router';

// Mock next/link
vi.mock('next/link', () => ({
  default: ({ children, href }: any) => (
    <a href={href} data-testid="mock-link">
      {children}
    </a>
  ),
}));

// Mock Input component
vi.mock('../../../common/items/Input', () => ({
  Input: ({ labelText, field, defaultValue, register, disabled, error }: any) => (
    <div>
      <label htmlFor={field}>{labelText}</label>
      <input
        id={field}
        data-testid={`input-${field}`}
        defaultValue={defaultValue}
        disabled={disabled}
        onChange={(e) => register && register(field).onChange(e)}
        onBlur={() => register && register(field).onBlur()}
      />
      {error && <span data-testid={`error-${field}`}>{error}</span>}
    </div>
  ),
}));

describe('ProfileForm', () => {
  const mockRegister = vi.fn((field) => ({
    name: field,
    onChange: vi.fn(),
    onBlur: vi.fn(),
    ref: vi.fn(),
  }));

  const defaultProps = {
    customer: undefined,
    register: mockRegister,
  };

  beforeEach(() => {
    vi.clearAllMocks();
  });

  describe('Rendering', () => {
    it('should render all form fields', () => {
      render(<ProfileForm {...defaultProps} />);

      expect(screen.getByText('Username')).toBeDefined();
      expect(screen.getByText('First name')).toBeDefined();
      expect(screen.getByText('Last name')).toBeDefined();
      expect(screen.getByText('Email')).toBeDefined();
    });



    it('should render Update button', () => {
      render(<ProfileForm {...defaultProps} />);

      expect(screen.getByText('Update')).toBeDefined();
      expect(screen.getByRole('button', { name: 'Update' })).toBeDefined();
    });

    it('should render Cancel button', () => {
      render(<ProfileForm {...defaultProps} />);

      expect(screen.getByText('Cancel')).toBeDefined();
      expect(screen.getByRole('button', { name: 'Cancel' })).toBeDefined();
    });

    it('should have Update button with type submit', () => {
      render(<ProfileForm {...defaultProps} />);

      const updateButton = screen.getByText('Update');
      expect(updateButton).toHaveAttribute('type', 'submit');
    });

    it('should have Update button with btn-primary class', () => {
      render(<ProfileForm {...defaultProps} />);

      const updateButton = screen.getByText('Update');
      expect(updateButton).toHaveClass('btn', 'btn-primary');
    });

    it('should have Cancel button with btn-secondary and m-3 classes', () => {
      render(<ProfileForm {...defaultProps} />);

      const cancelButton = screen.getByText('Cancel');
      expect(cancelButton).toHaveClass('btn', 'btn-secondary', 'm-3');
    });
  });

  describe('Form Fields Registration', () => {


    it('should call register exactly 4 times', () => {
      render(<ProfileForm {...defaultProps} />);

      expect(mockRegister).toHaveBeenCalledTimes(4);
    });
  });

  describe('Cancel Button Link', () => {
    it('should have Cancel button wrapped in Link component', () => {
      render(<ProfileForm {...defaultProps} />);

      const link = screen.getByTestId('mock-link');
      expect(link).toHaveAttribute('href', '/customers');
    });

    it('should navigate to /customers when Cancel is clicked', () => {
      render(<ProfileForm {...defaultProps} />);

      const cancelButton = screen.getByText('Cancel');
      const link = cancelButton.closest('a');
      
      expect(link).toHaveAttribute('href', '/customers');
    });
  });

  describe('Update Button', () => {
    it('should be clickable', async () => {
      const user = userEvent.setup();
      const onSubmit = vi.fn();
      
      render(
        <form onSubmit={onSubmit}>
          <ProfileForm {...defaultProps} />
        </form>
      );

      const updateButton = screen.getByText('Update');
      await user.click(updateButton);
      
      expect(onSubmit).toHaveBeenCalled();
    });

    it('should have correct button text', () => {
      render(<ProfileForm {...defaultProps} />);

      const updateButton = screen.getByText('Update');
      expect(updateButton.textContent).toBe('Update');
    });
  });

  describe('Loading state handling', () => {
    it('should render with minimal props', () => {
      render(<ProfileForm register={mockRegister} />);
      
      expect(screen.getByText('Username')).toBeDefined();
      expect(screen.getByText('First name')).toBeDefined();
      expect(screen.getByText('Last name')).toBeDefined();
      expect(screen.getByText('Email')).toBeDefined();
    });
  });

});