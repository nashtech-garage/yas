import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import MultipleAutoComplete from '../../../../modules/promotion/components/MultipleAutoComplete';

describe('MultipleAutoComplete', () => {
  const mockRegister = vi.fn((field) => ({
    name: field,
    onChange: vi.fn(),
    onBlur: vi.fn(),
    ref: vi.fn(),
  }));

  const mockFetchOptions = vi.fn();
  const mockOnSelect = vi.fn();
  const mockOnRemoveElement = vi.fn();

  const mockOptions = [
    { id: 1, name: 'Option 1' },
    { id: 2, name: 'Option 2' },
    { id: 3, name: 'Option 3' },
  ];

  const defaultProps = {
    labelText: 'Categories',
    field: 'categories',
    register: mockRegister,
    registerOptions: { required: true },
    defaultValue: '',
    options: mockOptions,
    fetchOptions: mockFetchOptions,
    onSelect: mockOnSelect,
    onRemoveElement: mockOnRemoveElement,
    optionSelectedIds: [],
    isSubmitting: false,
    addedOptions: [],
  };

  beforeEach(() => {
    vi.clearAllMocks();
    mockFetchOptions.mockImplementation((query) => {
      // Mock implementation for fetchOptions
      return mockOptions;
    });
  });

  describe('Rendering', () => {
    it('should render label', () => {
      render(<MultipleAutoComplete {...defaultProps} />);
      expect(screen.getByText('Categories')).toBeDefined();
    });

    it('should render input field', () => {
      render(<MultipleAutoComplete {...defaultProps} />);
      const input = screen.getByRole('textbox');
      expect(input).toBeDefined();
    });

    it('should have correct input id', () => {
      render(<MultipleAutoComplete {...defaultProps} />);
      const input = screen.getByRole('textbox');
      expect(input).toHaveAttribute('id', 'categories');
    });

    it('should have form-control class on input', () => {
      render(<MultipleAutoComplete {...defaultProps} />);
      const input = screen.getByRole('textbox');
      expect(input).toHaveClass('form-control');
    });

    it('should register the input field', () => {
      render(<MultipleAutoComplete {...defaultProps} />);
      expect(mockRegister).toHaveBeenCalledWith('categories', defaultProps.registerOptions);
    });
  });

  describe('Default Values', () => {
    it('should set defaultValue on input', () => {
      const propsWithDefault = { ...defaultProps, defaultValue: 'initial value' };
      render(<MultipleAutoComplete {...propsWithDefault} />);
      const input = screen.getByRole('textbox') as HTMLInputElement;
      expect(input.defaultValue).toBe('initial value');
    });

    it('should display addedOptions when provided', () => {
      const addedOptions = [
        { id: 1, name: 'Added Option 1' },
        { id: 2, name: 'Added Option 2' },
      ];
      const propsWithAddedOptions = { ...defaultProps, addedOptions };
      render(<MultipleAutoComplete {...propsWithAddedOptions} />);
      
      expect(screen.getByText('Added Option 1')).toBeDefined();
      expect(screen.getByText('Added Option 2')).toBeDefined();
    });
  });

  describe('AutoComplete Dropdown', () => {
    it('should show dropdown when input is focused', async () => {
      render(<MultipleAutoComplete {...defaultProps} />);
      const input = screen.getByRole('textbox');
      
      fireEvent.focus(input);
      
      await waitFor(() => {
        expect(screen.getByText('Option 1')).toBeDefined();
        expect(screen.getByText('Option 2')).toBeDefined();
        expect(screen.getByText('Option 3')).toBeDefined();
      });
    });

    it('should call fetchOptions when typing in input', async () => {
      render(<MultipleAutoComplete {...defaultProps} />);
      const input = screen.getByRole('textbox');
      
      fireEvent.change(input, { target: { value: 'test' } });
      
      expect(mockFetchOptions).toHaveBeenCalledWith('test');
    });
  });

  describe('Remove Option', () => {
    it('should remove option when clicking remove icon', async () => {
      const addedOptions = [{ id: 1, name: 'Option 1' }];
      const propsWithAddedOptions = { ...defaultProps, addedOptions };
      render(<MultipleAutoComplete {...propsWithAddedOptions} />);
      
      expect(screen.getByText('Option 1')).toBeDefined();
      
      const removeIcon = document.querySelector('.fa.fa-remove');
      expect(removeIcon).toBeDefined();
      
      if (removeIcon) {
        fireEvent.click(removeIcon);
      }
      
      expect(mockOnRemoveElement).toHaveBeenCalledWith(1);
      expect(screen.queryByText('Option 1')).toBeNull();
    });
  });

  describe('Selected Options Display', () => {
    it('should display selected options section when options are selected', async () => {
      const addedOptions = [{ id: 1, name: 'Selected Option' }];
      const propsWithAddedOptions = { ...defaultProps, addedOptions };
      render(<MultipleAutoComplete {...propsWithAddedOptions} />);
      
      expect(screen.getByText('Selected Categories')).toBeDefined();
      expect(screen.getByText('Selected Option')).toBeDefined();
    });

    it('should not display selected options section when no options selected', () => {
      render(<MultipleAutoComplete {...defaultProps} />);
      
      expect(screen.queryByText('Selected Categories')).toBeNull();
    });
  });

  describe('isSubmitting', () => {
    it('should not show dropdown when isSubmitting is true', async () => {
      const propsWithSubmitting = { ...defaultProps, isSubmitting: true };
      render(<MultipleAutoComplete {...propsWithSubmitting} />);
      const input = screen.getByRole('textbox');
      
      fireEvent.focus(input);
      
      // Wait for timeout
      await new Promise(resolve => setTimeout(resolve, 200));
      
      expect(screen.queryByText('Option 1')).toBeNull();
    });
  });

  describe('Selected Option Styling', () => {
    it('should apply selected-options class to already selected options in dropdown', async () => {
      const propsWithSelectedIds = { ...defaultProps, optionSelectedIds: [1] };
      render(<MultipleAutoComplete {...propsWithSelectedIds} />);
      const input = screen.getByRole('textbox');
      
      fireEvent.focus(input);
      
      await waitFor(() => {
        const selectedOption = document.querySelector('.dropdown-item.selected-options');
        expect(selectedOption).toBeDefined();
      });
    });
  });

  describe('Edge Cases', () => {
    it('should handle empty options array', async () => {
      const propsWithEmptyOptions = { ...defaultProps, options: [] };
      render(<MultipleAutoComplete {...propsWithEmptyOptions} />);
      const input = screen.getByRole('textbox');
      
      fireEvent.focus(input);
      
      await waitFor(() => {
        expect(screen.queryByRole('listbox')).toBeNull();
      });
    });

    it('should handle undefined options', async () => {
      const propsWithUndefinedOptions = { ...defaultProps, options: undefined };
      render(<MultipleAutoComplete {...propsWithUndefinedOptions} />);
      const input = screen.getByRole('textbox');
      
      fireEvent.focus(input);
      
      // Should not crash
      expect(input).toBeDefined();
    });

    it('should handle multiple option selections', async () => {
      render(<MultipleAutoComplete {...defaultProps} />);
      const input = screen.getByRole('textbox');
      
      fireEvent.focus(input);
      
      await waitFor(() => {
        expect(screen.getByText('Option 1')).toBeDefined();
      });
      
      fireEvent.click(screen.getByText('Option 1'));
      fireEvent.click(screen.getByText('Option 2'));
      
      expect(mockOnSelect).toHaveBeenCalledWith(1);
      expect(mockOnSelect).toHaveBeenCalledWith(2);
    });
  });
});