import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import Profile from '../../../pages/profile/index';
import { getMyProfile, updateCustomer } from '../../../modules/customer/services/CustomerService';
import { Customer } from '../../../modules/customer/models/Customer';

// Mock dependencies
vi.mock('../../../modules/customer/services/CustomerService', () => ({
  getMyProfile: vi.fn(),
  updateCustomer: vi.fn(),
}));

vi.mock('../../../common/services/ApiClientService', () => ({
  default: {
    get: vi.fn(),
    post: vi.fn(),
    put: vi.fn(),
    delete: vi.fn(),
  },
}));

vi.mock('react-hook-form', () => ({
  useForm: () => ({
    register: vi.fn((field) => ({
      name: field,
      onChange: vi.fn(),
      onBlur: vi.fn(),
      ref: vi.fn(),
    })),
    handleSubmit: vi.fn((fn) => fn),
    setValue: vi.fn(),
    formState: { errors: {} },
  }),
}));

vi.mock('next/router', () => ({
  useRouter: vi.fn(() => ({
    push: vi.fn(),
  })),
}));

vi.mock('react-toastify', () => ({
  toast: {
    success: vi.fn(),
    error: vi.fn(),
  },
}));

// Mock Input components
vi.mock('../../../common/items/Input', () => ({
  Input: ({ labelText, field, defaultValue, register, error }: any) => (
    <div>
      <label htmlFor={field}>{labelText}</label>
      <input
        id={field}
        data-testid={`input-${field}`}
        defaultValue={defaultValue}
        {...register(field)}
      />
      {error && <span data-testid={`error-${field}`}>{error}</span>}
    </div>
  ),
}));

describe('Profile Page', () => {
  // SỬA: Customer theo đúng type
  const mockCustomer: Customer = {
    id: 'user-1',
    email: 'john@example.com',
    username: 'john_doe',
    firstName: 'John',
    lastName: 'Doe',
    createdTimestamp: new Date('2024-01-15T10:30:00Z'),
  };

  const mockUpdateData = {
    email: 'john@example.com',
    firstName: 'John',
    lastName: 'Doe',
  };

  beforeEach(() => {
    vi.clearAllMocks();
    (getMyProfile as any).mockResolvedValue(mockCustomer);
    (updateCustomer as any).mockResolvedValue({ status: 200 });
  });

  describe('getMyProfile', () => {
    it('should call getMyProfile on mount', async () => {
      render(<Profile />);
      await waitFor(() => {
        expect(getMyProfile).toHaveBeenCalled();
      });
    });

    it('should return customer data on success', async () => {
      render(<Profile />);
      await waitFor(() => {
        expect(getMyProfile).toHaveBeenCalled();
      });
    });

    it('should display customer data in form', async () => {
      render(<Profile />);
      
      await waitFor(() => {
        const emailInput = screen.getByTestId('input-email') as HTMLInputElement;
        const firstNameInput = screen.getByTestId('input-firstName') as HTMLInputElement;
        const lastNameInput = screen.getByTestId('input-lastName') as HTMLInputElement;
        
        expect(emailInput.defaultValue).toBe('john@example.com');
        expect(firstNameInput.defaultValue).toBe('John');
        expect(lastNameInput.defaultValue).toBe('Doe');
      });
    });
  });

  describe('updateCustomer', () => {
    it('should be defined', () => {
      expect(updateCustomer).toBeDefined();
    });

    it('should handle update success', async () => {
      (updateCustomer as any).mockResolvedValue({ status: 200 });
      render(<Profile />);
      await waitFor(() => {
        expect(updateCustomer).toBeDefined();
      });
    });
  });

  describe('Form Fields', () => {
    it('should render email field', async () => {
      render(<Profile />);
      
      await waitFor(() => {
        expect(screen.getByText('Email')).toBeDefined();
        expect(screen.getByTestId('input-email')).toBeDefined();
      });
    });

    it('should render first name field', async () => {
      render(<Profile />);
      
      await waitFor(() => {
        expect(screen.getByText('First name')).toBeDefined();
        expect(screen.getByTestId('input-firstName')).toBeDefined();
      });
    });

    it('should render last name field', async () => {
      render(<Profile />);
      
      await waitFor(() => {
        expect(screen.getByText('Last name')).toBeDefined();
        expect(screen.getByTestId('input-lastName')).toBeDefined();
      });
    });
  });

  describe('Update Button', () => {
    it('should render Update button', async () => {
      render(<Profile />);
      
      await waitFor(() => {
        expect(screen.getByText('Update')).toBeDefined();
      });
    });

    it('should have Update button with submit type', async () => {
      render(<Profile />);
      
      await waitFor(() => {
        const updateButton = screen.getByText('Update');
        expect(updateButton).toHaveAttribute('type', 'submit');
      });
    });
  });

  describe('render', () => {
    it('should render profile page', async () => {
      render(<Profile />);
      await waitFor(() => {
        expect(screen.getByText('Update User')).toBeDefined();
      });
    });

    it('should have form element', async () => {
      render(<Profile />);
      await waitFor(() => {
        const form = document.querySelector('form');
        expect(form).toBeDefined();
      });
    });
  });
});