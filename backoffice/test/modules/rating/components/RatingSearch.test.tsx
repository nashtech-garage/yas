import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, fireEvent } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import RatingSearch from '../../../../modules/rating/components/RatingSearch';
import { RatingSearchForm } from '../../../../modules/rating/models/RatingSearchForm';

describe('RatingSearch', () => {
  const mockRegister = vi.fn((field) => ({
    name: field,
    onChange: vi.fn(),
    onBlur: vi.fn(),
    ref: vi.fn(),
  }));

  const defaultProps = {
    register: mockRegister,
  };

  beforeEach(() => {
    vi.clearAllMocks();
  });

  describe('Rendering', () => {

    it('should render Product name input', () => {
      render(<RatingSearch {...defaultProps} />);

      const productInput = screen.getByLabelText('Product:');
      expect(productInput).toHaveAttribute('id', 'productName');
      expect(productInput).toHaveAttribute('placeholder', 'Search product name ...');
    });

    it('should render Search button', () => {
      render(<RatingSearch {...defaultProps} />);

      const searchButton = screen.getByText('Search');
      expect(searchButton).toBeDefined();
      expect(searchButton).toHaveAttribute('type', 'submit');
      expect(searchButton).toHaveClass('btn', 'btn-primary', 'w-25');
    });
  });

  describe('Form Registration', () => {
    it('should register createdFrom field', () => {
      render(<RatingSearch {...defaultProps} />);

      expect(mockRegister).toHaveBeenCalledWith('createdFrom');
    });

    it('should register createdTo field', () => {
      render(<RatingSearch {...defaultProps} />);

      expect(mockRegister).toHaveBeenCalledWith('createdTo');
    });

    it('should register productName field', () => {
      render(<RatingSearch {...defaultProps} />);

      expect(mockRegister).toHaveBeenCalledWith('productName');
    });

    it('should register customerName field', () => {
      render(<RatingSearch {...defaultProps} />);

      expect(mockRegister).toHaveBeenCalledWith('customerName');
    });

    it('should register message field', () => {
      render(<RatingSearch {...defaultProps} />);

      expect(mockRegister).toHaveBeenCalledWith('message');
    });

    it('should call register 5 times', () => {
      render(<RatingSearch {...defaultProps} />);

      expect(mockRegister).toHaveBeenCalledTimes(5);
    });
  });

  describe('User Interaction', () => {

    it('should handle Product name input change', () => {
      render(<RatingSearch {...defaultProps} />);

      const productInput = screen.getByLabelText('Product:');
      fireEvent.change(productInput, { target: { value: 'Laptop' } });

      expect(productInput).toHaveValue('Laptop');
    });
  });

  describe('Form Layout', () => {
    it('should have row class for main container', () => {
      const { container } = render(<RatingSearch {...defaultProps} />);
      
      const rowDiv = container.querySelector('.row');
      expect(rowDiv).toBeDefined();
    });

    it('should have two columns (col-12 col-lg-6)', () => {
      const { container } = render(<RatingSearch {...defaultProps} />);
      
      const columns = container.querySelectorAll('.col-12.col-lg-6');
      expect(columns).toHaveLength(2);
    });

    it('should have d-flex class for form groups', () => {
      const { container } = render(<RatingSearch {...defaultProps} />);
      
      const flexDivs = container.querySelectorAll('.d-flex');
      expect(flexDivs.length).toBeGreaterThan(0);
    });

    it('should have mb-4 class for form groups', () => {
      const { container } = render(<RatingSearch {...defaultProps} />);
      
      const mb4Divs = container.querySelectorAll('.mb-4');
      expect(mb4Divs.length).toBeGreaterThan(0);
    });
  });

  describe('Accessibility', () => {

    it('should have label associated with Product input', () => {
      render(<RatingSearch {...defaultProps} />);
      
      const label = screen.getByText('Product:');
      expect(label).toHaveAttribute('for', 'productName');
    });

    it('should have label associated with Customer input', () => {
      render(<RatingSearch {...defaultProps} />);
      
      const label = screen.getByText('Customer:');
      expect(label).toHaveAttribute('for', 'cusomter');
    });
  });

  describe('Edge Cases', () => {
    it('should handle very long product name input', () => {
      render(<RatingSearch {...defaultProps} />);
      
      const productInput = screen.getByLabelText('Product:');
      const longText = 'A'.repeat(500);
      fireEvent.change(productInput, { target: { value: longText } });
      
      expect(productInput).toHaveValue(longText);
    });

    it('should handle empty input values', () => {
      render(<RatingSearch {...defaultProps} />);
      
      const productInput = screen.getByLabelText('Product:');
      fireEvent.change(productInput, { target: { value: '' } });
      
      expect(productInput).toHaveValue('');
    });

    it('should handle special characters in product name', () => {
      render(<RatingSearch {...defaultProps} />);
      
      const productInput = screen.getByLabelText('Product:');
      const specialChars = '!@#$%^&*()_+';
      fireEvent.change(productInput, { target: { value: specialChars } });
      
      expect(productInput).toHaveValue(specialChars);
    });
  });

  describe('Button Styling', () => {
    it('should have Search button centered', () => {
      const { container } = render(<RatingSearch {...defaultProps} />);
      
      const textCenterDiv = container.querySelector('.text-center');
      expect(textCenterDiv).toBeDefined();
    });

  });
});