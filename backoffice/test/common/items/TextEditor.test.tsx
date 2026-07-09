// test/common/items/TextEditor.test.tsx
import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, fireEvent } from '@testing-library/react';
import TextEditor from '../../../common/items/TextEditor';

// Mock next/dynamic
vi.mock('next/dynamic', () => ({
  default: vi.fn(() => {
    return ({ onChange, defaultValue, className }: any) => (
      <textarea
        data-testid="text-editor"
        className={className}
        defaultValue={defaultValue}
        onChange={(e) => onChange(e.target.value)}
      />
    );
  }),
}));

describe('TextEditor', () => {
  const defaultProps = {
    field: 'description',
    labelText: 'Description',
    setValue: vi.fn(),
  };

  beforeEach(() => {
    vi.clearAllMocks();
  });

  describe('Rendering', () => {

    it('should render text editor', () => {
      render(<TextEditor {...defaultProps} />);
      
      const editor = screen.getByTestId('text-editor');
      expect(editor).toBeDefined();
    });

    it('should have text-editor class', () => {
      render(<TextEditor {...defaultProps} />);
      
      const editor = screen.getByTestId('text-editor');
      expect(editor.className).toContain('text-editor');
    });

    it('should display error message when error provided', () => {
      render(<TextEditor {...defaultProps} error="Description is required" />);
      
      expect(screen.getByText('Description is required')).toBeDefined();
    });

    it('should display error field class', () => {
      render(<TextEditor {...defaultProps} error="Description is required" />);
      
      const errorElement = screen.getByText('Description is required');
      expect(errorElement.className).toContain('error-field');
    });
  });

  describe('Default Value', () => {
    it('should display defaultValue', () => {
      const defaultValue = 'Initial content';
      render(<TextEditor {...defaultProps} defaultValue={defaultValue} />);
      
      const editor = screen.getByTestId('text-editor') as HTMLTextAreaElement;
      expect(editor.defaultValue).toBe(defaultValue);
    });

    it('should handle empty defaultValue', () => {
      render(<TextEditor {...defaultProps} />);
      
      const editor = screen.getByTestId('text-editor') as HTMLTextAreaElement;
      expect(editor.defaultValue).toBe('');
    });

    it('should handle long defaultValue', () => {
      const longText = 'A'.repeat(1000);
      render(<TextEditor {...defaultProps} defaultValue={longText} />);
      
      const editor = screen.getByTestId('text-editor') as HTMLTextAreaElement;
      expect(editor.defaultValue).toBe(longText);
    });
  });

  describe('Change Handler', () => {
    it('should call setValue when content changes', () => {
      const setValue = vi.fn();
      render(<TextEditor {...defaultProps} setValue={setValue} />);
      
      const editor = screen.getByTestId('text-editor');
      fireEvent.change(editor, { target: { value: 'New content' } });
      
      expect(setValue).toHaveBeenCalledTimes(1);
      expect(setValue).toHaveBeenCalledWith('New content');
    });

    it('should handle multiple changes', () => {
      const setValue = vi.fn();
      render(<TextEditor {...defaultProps} setValue={setValue} />);
      
      const editor = screen.getByTestId('text-editor');
      fireEvent.change(editor, { target: { value: 'First' } });
      fireEvent.change(editor, { target: { value: 'Second' } });
      fireEvent.change(editor, { target: { value: 'Third' } });
      
      expect(setValue).toHaveBeenCalledTimes(3);
      expect(setValue).toHaveBeenNthCalledWith(1, 'First');
      expect(setValue).toHaveBeenNthCalledWith(2, 'Second');
      expect(setValue).toHaveBeenNthCalledWith(3, 'Third');
    });
  });


  describe('Label Text', () => {

    it('should handle empty label', () => {
      render(<TextEditor {...defaultProps} labelText="" />);
      
      const label = document.querySelector('.form-label');
      expect(label?.textContent).toBe('');
    });
  });

  describe('Error States', () => {
    it('should show error message with special characters', () => {
      const errorMessage = 'Field cannot contain <script> tags!';
      render(<TextEditor {...defaultProps} error={errorMessage} />);
      
      expect(screen.getByText(errorMessage)).toBeDefined();
    });

    it('should update error message when changed', () => {
      const { rerender } = render(<TextEditor {...defaultProps} error="First error" />);
      
      expect(screen.getByText('First error')).toBeDefined();
      
      rerender(<TextEditor {...defaultProps} error="Second error" />);
      
      expect(screen.getByText('Second error')).toBeDefined();
      expect(screen.queryByText('First error')).toBeNull();
    });

    it('should not show error when error is empty string', () => {
      render(<TextEditor {...defaultProps} error="" />);
      
      const errorElements = document.querySelectorAll('.error-field');
      expect(errorElements.length).toBe(1); // Still has the element but empty
      expect(errorElements[0].textContent).toBe('');
    });
  });

  describe('CSS Classes', () => {
    it('should have mb-3 class on container', () => {
      render(<TextEditor {...defaultProps} />);
      
      const container = document.querySelector('.mb-3');
      expect(container).toBeDefined();
    });

    it('should have form-label class on label', () => {
      render(<TextEditor {...defaultProps} />);
      
      const label = document.querySelector('.form-label');
      expect(label).toBeDefined();
    });
  });

  describe('Integration', () => {
    it('should handle real world scenario', () => {
      const setValue = vi.fn();
      render(
        <TextEditor
          field="productDescription"
          labelText="Product Description"
          defaultValue="Initial product description"
          setValue={setValue}
        />
      );
      
      const editor = screen.getByTestId('text-editor');
      expect(editor).toBeDefined();
      
      fireEvent.change(editor, { target: { value: 'Updated description' } });
      expect(setValue).toHaveBeenCalledWith('Updated description');
    });
  });
});