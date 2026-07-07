import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen } from '@testing-library/react';
import AddressTable from '../../../../modules/order/components/AddressTable';
import { OrderAddress } from '../../../../modules/order/models/OrderAddress';

describe('AddressTable', () => {
  const mockAddress: OrderAddress = {
    contactName: 'John Doe',
    phone: '123-456-7890',
    addressLine1: '123 Main Street',
    addressLine2: 'Apt 4B',
    zipCode: '10001',
    districtName: 'Manhattan',
    districtId: 1,           
    city: 'New York',
    stateOrProvinceName: 'New York',
    stateOrProvinceId: 1,   
    countryName: 'United States',
    countryId: 1,           
  };

  describe('Rendering with valid address', () => {
    it('should render the table when address is provided', () => {
      render(<AddressTable address={mockAddress} isShowOnGoogleMap={false} />);
      
      expect(screen.getByText('Billing address')).toBeDefined();
      expect(screen.getByText('Full name')).toBeDefined();
    });

    it('should display all address fields', () => {
      render(<AddressTable address={mockAddress} isShowOnGoogleMap={false} />);
      
      expect(screen.getByText('Full name')).toBeDefined();
      expect(screen.getByText('Phone')).toBeDefined();
      expect(screen.getByText('Address line 1')).toBeDefined();
      expect(screen.getByText('Address line 2')).toBeDefined();
      expect(screen.getByText('Zip code')).toBeDefined();
      expect(screen.getByText('District name')).toBeDefined();
      expect(screen.getByText('City')).toBeDefined();
      expect(screen.getByText('State or province name')).toBeDefined();
      expect(screen.getByText('Country name')).toBeDefined();
    });

    it('should render Edit button', () => {
      render(<AddressTable address={mockAddress} isShowOnGoogleMap={false} />);
      
      const editButton = screen.getByText('Edit');
      expect(editButton).toBeDefined();
      expect(editButton).toHaveClass('btn', 'btn-dark');
    });

    it('should have table with hover class', () => {
      const { container } = render(<AddressTable address={mockAddress} isShowOnGoogleMap={false} />);
      
      const table = container.querySelector('table');
      expect(table).toHaveClass('table', 'table-hover');
    });
  });

  describe('Google Maps link', () => {
    it('should show map icon when isShowOnGoogleMap is true', () => {
      const { container } = render(<AddressTable address={mockAddress} isShowOnGoogleMap={true} />);
      
      const icon = container.querySelector('.fa.fa-map-marker');
      expect(icon).toBeDefined();
      expect(icon).toHaveClass('fa', 'fa-map-marker', 'me-3', 'mt-2');
    });

    it('should not show Google Maps link when isShowOnGoogleMap is false', () => {
      render(<AddressTable address={mockAddress} isShowOnGoogleMap={false} />);
      
      expect(screen.queryByText('View on Google Maps')).toBeNull();
    });

    it('should show empty cell with height 40px when isShowOnGoogleMap is false', () => {
      const { container } = render(<AddressTable address={mockAddress} isShowOnGoogleMap={false} />);
      
      const emptyCell = container.querySelector('td[style="height: 40px;"]');
      expect(emptyCell).toBeDefined();
    });
  });

  describe('Empty address state', () => {
    it('should show "No address found" when address is null', () => {
      render(<AddressTable address={null as any} isShowOnGoogleMap={false} />);
      
      expect(screen.getByText('No address found')).toBeDefined();
    });

    it('should not render table when address is null', () => {
      const { container } = render(<AddressTable address={null as any} isShowOnGoogleMap={false} />);
      
      const table = container.querySelector('table');
      expect(table).toBeNull();
    });

    it('should show "No address found" when address is undefined', () => {
      render(<AddressTable address={undefined as any} isShowOnGoogleMap={false} />);
      
      expect(screen.getByText('No address found')).toBeDefined();
    });
  });

  describe('Table headers', () => {
    it('should have Billing address header with center justify', () => {
      const { container } = render(<AddressTable address={mockAddress} isShowOnGoogleMap={false} />);
      
      const th = container.querySelector('th.d-flex.justify-content-center');
      expect(th).toBeDefined();
      expect(th?.textContent).toContain('Billing address');
    });

    it('should have empty second header column with w-50 class', () => {
      const { container } = render(<AddressTable address={mockAddress} isShowOnGoogleMap={false} />);
      
      const ths = container.querySelectorAll('th');
      expect(ths[1]).toHaveClass('w-50');
      expect(ths[1].textContent).toBe('');
    });
  });

  describe('Edit button', () => {
    it('should render Edit button with correct classes', () => {
      render(<AddressTable address={mockAddress} isShowOnGoogleMap={false} />);
      
      const editButton = screen.getByText('Edit');
      expect(editButton).toHaveClass('mt-2', 'btn', 'btn-dark');
    });

    it('should have Edit button wrapped in proper structure', () => {
      const { container } = render(<AddressTable address={mockAddress} isShowOnGoogleMap={false} />);
      
      const editButton = screen.getByText('Edit');
      const parentCell = editButton.closest('td');
      expect(parentCell).toBeDefined();
    });
  });

  describe('Accessibility', () => {
    it('should have correct ARIA attributes for Google Maps icon', () => {
      const { container } = render(<AddressTable address={mockAddress} isShowOnGoogleMap={true} />);
      
      const icon = container.querySelector('.fa.fa-map-marker');
      expect(icon).toHaveAttribute('aria-hidden', 'true');
    });
  });

  
});