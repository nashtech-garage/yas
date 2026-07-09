// test/common/items/OptionSelect.test.tsx
import { describe, it, expect, vi } from 'vitest';
import { render, screen } from '@testing-library/react';
import { useForm } from 'react-hook-form';
import { OptionSelect } from '../../../common/items/OptionSelect';

// Wrapper component for testing
const TestWrapper = ({ children }: { children: (form: any) => React.ReactNode }) => {
  const form = useForm();
  return <form>{children(form)}</form>;
};

describe('OptionSelect', () => {
  const mockOptions = [
    { id: 1, name: 'Option 1' },
    { id: 2, name: 'Option 2' },
    { id: 3, name: 'Option 3' },
    { id: 4, name: 'Option 4' },
  ];

  describe('Rendering', () => {
    it('should render select with label', () => {
      render(
        <TestWrapper>
          {({ register }) => (
            <OptionSelect
              labelText="Category"
              field="category"
              register={register}
              options={mockOptions}
            />
          )}
        </TestWrapper>
      );
      
      expect(screen.getByLabelText('Category')).toBeDefined();
    });

    it('should render all options', () => {
      render(
        <TestWrapper>
          {({ register }) => (
            <OptionSelect
              labelText="Category"
              field="category"
              register={register}
              options={mockOptions}
            />
          )}
        </TestWrapper>
      );
      
      expect(screen.getByText('Option 1')).toBeDefined();
      expect(screen.getByText('Option 2')).toBeDefined();
      expect(screen.getByText('Option 3')).toBeDefined();
      expect(screen.getByText('Option 4')).toBeDefined();
    });

    it('should render placeholder when provided', () => {
      render(
        <TestWrapper>
          {({ register }) => (
            <OptionSelect
              labelText="Category"
              field="category"
              register={register}
              options={mockOptions}
              placeholder="-- Select category --"
            />
          )}
        </TestWrapper>
      );
      
      expect(screen.getByText('-- Select category --')).toBeDefined();
    });

    it('should render default placeholder when not provided', () => {
      render(
        <TestWrapper>
          {({ register }) => (
            <OptionSelect
              labelText="Category"
              field="category"
              register={register}
              options={mockOptions}
            />
          )}
        </TestWrapper>
      );
      
      expect(screen.getByText('Select ...')).toBeDefined();
    });

    it('should render no options when options array is empty', () => {
      render(
        <TestWrapper>
          {({ register }) => (
            <OptionSelect
              labelText="Category"
              field="category"
              register={register}
              options={[]}
            />
          )}
        </TestWrapper>
      );
      
      expect(screen.getByText('Select ...')).toBeDefined();
      expect(screen.queryByText('Option 1')).toBeNull();
    });

    it('should render without options when options is undefined', () => {
      render(
        <TestWrapper>
          {({ register }) => (
            <OptionSelect
              labelText="Category"
              field="category"
              register={register}
            />
          )}
        </TestWrapper>
      );
      
      expect(screen.getByText('Select ...')).toBeDefined();
    });
  });

  describe('Error Handling', () => {
    it('should display error message when error provided', () => {
      render(
        <TestWrapper>
          {({ register }) => (
            <OptionSelect
              labelText="Category"
              field="category"
              register={register}
              options={mockOptions}
              error="Category is required"
            />
          )}
        </TestWrapper>
      );
      
      expect(screen.getByText('Category is required')).toBeDefined();
    });

    it('should apply border-danger class when error', () => {
      render(
        <TestWrapper>
          {({ register }) => (
            <OptionSelect
              labelText="Category"
              field="category"
              register={register}
              options={mockOptions}
              error="Category is required"
            />
          )}
        </TestWrapper>
      );
      
      const select = screen.getByRole('combobox');
      expect(select.className).toContain('border-danger');
    });

    it('should not have border-danger class when no error', () => {
      render(
        <TestWrapper>
          {({ register }) => (
            <OptionSelect
              labelText="Category"
              field="category"
              register={register}
              options={mockOptions}
            />
          )}
        </TestWrapper>
      );
      
      const select = screen.getByRole('combobox');
      expect(select.className).not.toContain('border-danger');
    });
  });

  describe('Default Value', () => {
    it('should set defaultValue when provided', () => {
      render(
        <TestWrapper>
          {({ register }) => (
            <OptionSelect
              labelText="Category"
              field="category"
              register={register}
              options={mockOptions}
              defaultValue={2}
            />
          )}
        </TestWrapper>
      );
      
      const select = screen.getByRole('combobox') as HTMLSelectElement;
      expect(select.value).toBe('2');
    });

    it('should have empty value when defaultValue not provided', () => {
      render(
        <TestWrapper>
          {({ register }) => (
            <OptionSelect
              labelText="Category"
              field="category"
              register={register}
              options={mockOptions}
            />
          )}
        </TestWrapper>
      );
      
      const select = screen.getByRole('combobox') as HTMLSelectElement;
      expect(select.value).toBe('');
    });
  });

  describe('Disabled State', () => {
    it('should have disabled attribute when disabled is true', () => {
      render(
        <TestWrapper>
          {({ register }) => (
            <OptionSelect
              labelText="Category"
              field="category"
              register={register}
              options={mockOptions}
              disabled={true}
            />
          )}
        </TestWrapper>
      );
      
      const select = screen.getByRole('combobox');
      expect(select.getAttribute('disabled')).not.toBeNull();
    });

    it('should not have disabled attribute when disabled is false', () => {
      render(
        <TestWrapper>
          {({ register }) => (
            <OptionSelect
              labelText="Category"
              field="category"
              register={register}
              options={mockOptions}
              disabled={false}
            />
          )}
        </TestWrapper>
      );
      
      const select = screen.getByRole('combobox');
      expect(select.getAttribute('disabled')).toBeNull();
    });
  });

  describe('Option Types', () => {
    it('should handle string ids', () => {
      const stringIdOptions = [
        { id: 'opt1', name: 'Option 1' },
        { id: 'opt2', name: 'Option 2' },
      ];
      
      render(
        <TestWrapper>
          {({ register }) => (
            <OptionSelect
              labelText="Category"
              field="category"
              register={register}
              options={stringIdOptions}
            />
          )}
        </TestWrapper>
      );
      
      const option1 = screen.getByText('Option 1');
      const option2 = screen.getByText('Option 2');
      expect(option1).toBeDefined();
      expect(option2).toBeDefined();
    });

    it('should handle number ids', () => {
      render(
        <TestWrapper>
          {({ register }) => (
            <OptionSelect
              labelText="Category"
              field="category"
              register={register}
              options={mockOptions}
            />
          )}
        </TestWrapper>
      );
      
      const select = screen.getByRole('combobox') as HTMLSelectElement;
      const options = select.querySelectorAll('option');
      expect(options[1].value).toBe('1');
      expect(options[2].value).toBe('2');
    });
  });

  describe('Select ID', () => {
    it('should have unique id based on field name', () => {
      render(
        <TestWrapper>
          {({ register }) => (
            <>
              <OptionSelect
                labelText="Category"
                field="category"
                register={register}
                options={mockOptions}
              />
              <OptionSelect
                labelText="Status"
                field="status"
                register={register}
                options={mockOptions}
              />
            </>
          )}
        </TestWrapper>
      );
      
      const categorySelect = document.querySelector('#select-option-category');
      const statusSelect = document.querySelector('#select-option-status');
      
      expect(categorySelect).toBeDefined();
      expect(statusSelect).toBeDefined();
    });
  });

  describe('Different Data Types', () => {
    it('should handle large number of options', () => {
      const largeOptions = Array.from({ length: 100 }, (_, i) => ({
        id: i,
        name: `Option ${i}`,
      }));
      
      render(
        <TestWrapper>
          {({ register }) => (
            <OptionSelect
              labelText="Large Select"
              field="largeSelect"
              register={register}
              options={largeOptions}
            />
          )}
        </TestWrapper>
      );
      
      const select = screen.getByRole('combobox');
      const options = select.querySelectorAll('option');
      expect(options.length).toBe(101); // 100 options + 1 placeholder
    });

    it('should handle options with special characters', () => {
      const specialOptions = [
        { id: 1, name: 'Option & Special' },
        { id: 2, name: 'Option <tag>' },
        { id: 3, name: 'Option "quote"' },
      ];
      
      render(
        <TestWrapper>
          {({ register }) => (
            <OptionSelect
              labelText="Special"
              field="special"
              register={register}
              options={specialOptions}
            />
          )}
        </TestWrapper>
      );
      
      expect(screen.getByText('Option & Special')).toBeDefined();
      expect(screen.getByText('Option <tag>')).toBeDefined();
      expect(screen.getByText('Option "quote"')).toBeDefined();
    });
  });
});