import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen } from '@testing-library/react';
import BillingNShippingInfo from '../../../../modules/order/components/BillingNShippingInfo';
import { Order } from '../../../../modules/order/models/Order';
import { OrderItem } from '../../../../modules/order/models/OrderItem';
import { OrderAddress } from '../../../../modules/order/models/OrderAddress';

// Mock AddressTable component
vi.mock('../../../../modules/order/components/AddressTable', () => ({
  default: ({ address, isShowOnGoogleMap }: any) => (
    <div data-testid="address-table" data-show-map={isShowOnGoogleMap}>
      <span>Address: {address?.contactName || 'No address'}</span>
      <span>Show map: {isShowOnGoogleMap ? 'Yes' : 'No'}</span>
    </div>
  ),
}));

// Mock next/link
vi.mock('next/link', () => ({
  default: ({ children, href }: any) => (
    <a href={href} data-testid="mock-link">
      {children}
    </a>
  ),
}));

describe('BillingNShippingInfo', () => {
  // Tạo mock address
  const mockAddress: OrderAddress = {
    contactName: 'John Doe',
    phone: '123-456-7890',
    addressLine1: '123 Main St',
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

  const mockShippingAddress: OrderAddress = {
    contactName: 'Jane Doe',
    phone: '987-654-3210',
    addressLine1: '456 Oak Ave',
    addressLine2: 'Suite 200',
    zipCode: '90210',
    districtName: 'Beverly Hills',
    districtId: 2,
    city: 'Los Angeles',
    stateOrProvinceName: 'California',
    stateOrProvinceId: 2,
    countryName: 'United States',
    countryId: 1,
  };

  // Tạo mock order items
  const mockOrderItems: OrderItem[] = [
  {
    id: 1,
    productId: 100,
    productName: 'Laptop',
    quantity: 1,
    productPrice: 999.99,      
    note: 'Include warranty',  
    discountAmount: 10.00,     
    taxAmount: 50.00,          
    taxPercent: 5,             
  }];


  // SỬA: Mock Order với đầy đủ các trường
  const mockOrder: Order = {
    id: 1,
    email: 'john@example.com',
    note: 'Please leave at front door',
    tax: 50.00,
    discount: 10.00,
    numberItem: 5,
    totalPrice: 299.99,
    deliveryFee: 15.00,
    couponCode: 'SAVE10',
    deliveryMethod: 'Standard Shipping',
    deliveryStatus: 'Shipped',
    paymentMethod: 'Credit Card',
    paymentStatus: 'Paid',
    createdOn: new Date('2024-01-15T10:30:00Z'),
    orderStatus: 'Processing',
    orderItemVms: mockOrderItems,
    shippingAddressVm: mockShippingAddress,
    billingAddressVm: mockAddress,
    checkoutId: 'checkout_123',
  };

  // Order không có address
  const mockOrderWithoutAddress: Order = {
    ...mockOrder,
    billingAddressVm: {} as OrderAddress,
    shippingAddressVm: {} as OrderAddress,
  };

  beforeEach(() => {
    vi.clearAllMocks();
  });

  describe('Rendering with valid order', () => {
    it('should render accordion section', () => {
      render(<BillingNShippingInfo order={mockOrder} />);
      
      expect(screen.getByText('Billing & shipping')).toBeDefined();
    });

    it('should show accordion button with icon', () => {
      const { container } = render(<BillingNShippingInfo order={mockOrder} />);
      
      const icon = container.querySelector('.fa.fa-truck');
      expect(icon).toBeDefined();
      expect(icon).toHaveClass('fa', 'fa-truck', 'me-2');
    });

    it('should render accordion body', () => {
      render(<BillingNShippingInfo order={mockOrder} />);
      
      const accordionBody = document.querySelector('.accordion-body');
      expect(accordionBody).toBeDefined();
    });

    it('should render two AddressTable components', () => {
      render(<BillingNShippingInfo order={mockOrder} />);
      
      const addressTables = screen.getAllByTestId('address-table');
      expect(addressTables).toHaveLength(2);
    });
  });

  describe('AddressTable props', () => {
    it('should pass billing address to first AddressTable with showMap false', () => {
      render(<BillingNShippingInfo order={mockOrder} />);
      
      const addressTables = screen.getAllByTestId('address-table');
      const billingTable = addressTables[0];
      
      expect(billingTable.textContent).toContain('Address: John Doe');
      expect(billingTable.getAttribute('data-show-map')).toBe('false');
    });

    it('should pass shipping address to second AddressTable with showMap true', () => {
      render(<BillingNShippingInfo order={mockOrder} />);
      
      const addressTables = screen.getAllByTestId('address-table');
      const shippingTable = addressTables[1];
      
      expect(shippingTable.textContent).toContain('Address: Jane Doe');
      expect(shippingTable.getAttribute('data-show-map')).toBe('true');
    });
  });

  describe('Shipment section', () => {
    it('should render Shipment header', () => {
      render(<BillingNShippingInfo order={mockOrder} />);
      
      expect(screen.getByText('Shipment')).toBeDefined();
    });

    it('should render shipment table', () => {
      render(<BillingNShippingInfo order={mockOrder} />);
      
      expect(screen.getByText('Shipment #')).toBeDefined();
      expect(screen.getByText('Order #')).toBeDefined();
      expect(screen.getByText('Tracking number')).toBeDefined();
      expect(screen.getByText('Total weight')).toBeDefined();
      expect(screen.getByText('Date shipped')).toBeDefined();
      expect(screen.getByText('Date delivered')).toBeDefined();
      expect(screen.getByText('Actions')).toBeDefined();
    });

    it('should display shipment details', () => {
      render(<BillingNShippingInfo order={mockOrder} />);
      
      expect(screen.getByText('100')).toBeDefined();
      expect(screen.getByText(mockOrder.id!.toString())).toBeDefined();
      expect(screen.getByText('2.00 [lb(s)]')).toBeDefined();
      expect(screen.getByText('3/13/2017 4:20:10 AM')).toBeDefined();
      expect(screen.getByText('8-24-2001')).toBeDefined();
    });

    it('should render View button with eye icon', () => {
      const { container } = render(<BillingNShippingInfo order={mockOrder} />);
      
      const viewButton = screen.getByText('View');
      const icon = container.querySelector('.fa.fa-eye');
      
      expect(viewButton).toBeDefined();
      expect(icon).toBeDefined();
      expect(icon).toHaveClass('fa', 'fa-eye', 'me-2');
    });

    it('should have View button with correct classes', () => {
      render(<BillingNShippingInfo order={mockOrder} />);
      
      const viewButton = screen.getByText('View');
      expect(viewButton).toHaveClass('btn', 'btn-outline-primary', 'btn-sm');
    });

    it('should have correct link href for View button', () => {
      render(<BillingNShippingInfo order={mockOrder} />);
      
      const link = screen.getByTestId('mock-link');
      expect(link).toHaveAttribute('href', `/sales/orders/${mockOrder.id}/edit`);
    });
  });

  describe('Accordion functionality', () => {
    it('should have accordion button with correct attributes', () => {
      render(<BillingNShippingInfo order={mockOrder} />);
      
      const button = screen.getByRole('button', { name: /Billing & shipping/i });
      expect(button).toHaveAttribute('type', 'button');
      expect(button).toHaveAttribute('data-bs-toggle', 'collapse');
      expect(button).toHaveAttribute('data-bs-target', '#collapseAddress');
      expect(button).toHaveAttribute('aria-expanded', 'true');
      expect(button).toHaveAttribute('aria-controls', 'collapseAddress');
    });

    it('should have accordion body with show class', () => {
      render(<BillingNShippingInfo order={mockOrder} />);
      
      const collapseDiv = document.getElementById('collapseAddress');
      expect(collapseDiv).toHaveClass('accordion-collapse', 'collapse', 'show');
    });

    it('should have accordion header with correct id', () => {
      render(<BillingNShippingInfo order={mockOrder} />);
      
      const header = document.querySelector('.accordion-header');
      expect(header).toHaveAttribute('id', 'accordionAddress');
    });
  });

  describe('Shipment table structure', () => {
    it('should have table with striped, bordered, hover classes', () => {
      const { container } = render(<BillingNShippingInfo order={mockOrder} />);
      
      const table = container.querySelector('table');
      expect(table).toHaveClass('table', 'table-striped', 'table-bordered', 'table-hover');
    });

    it('should have 7 table headers', () => {
      render(<BillingNShippingInfo order={mockOrder} />);
      
      const headers = screen.getAllByRole('columnheader');
      expect(headers).toHaveLength(7);
    });

    it('should have Actions column with width 12%', () => {
      const { container } = render(<BillingNShippingInfo order={mockOrder} />);
      
      const actionsCell = container.querySelector('td[style="width: 12%;"]');
      expect(actionsCell).toBeDefined();
    });
  });

  describe('Empty order state', () => {
    it('should show "No order found" when order is null', () => {
      render(<BillingNShippingInfo order={null as any} />);
      
      expect(screen.getByText('No order found')).toBeDefined();
    });

    it('should not render accordion when order is null', () => {
      render(<BillingNShippingInfo order={null as any} />);
      
      expect(screen.queryByText('Billing & shipping')).toBeNull();
      expect(screen.queryByText('Shipment')).toBeNull();
    });
  });

  describe('No address data', () => {
    it('should handle order with empty billing address', () => {
      render(<BillingNShippingInfo order={mockOrderWithoutAddress} />);
      
      const addressTables = screen.getAllByTestId('address-table');
      expect(addressTables[0].textContent).toContain('Address: No address');
    });

    it('should handle order with empty shipping address', () => {
      render(<BillingNShippingInfo order={mockOrderWithoutAddress} />);
      
      const addressTables = screen.getAllByTestId('address-table');
      expect(addressTables[1].textContent).toContain('Address: No address');
    });
  });

  describe('Styling', () => {
    it('should have border container for address section', () => {
      const { container } = render(<BillingNShippingInfo order={mockOrder} />);
      
      const addressContainer = container.querySelector('.border.border-1.shadow-sm.p-3.mb-3.bg-body.rounded');
      expect(addressContainer).toBeDefined();
    });

    it('should have flex row for address columns', () => {
      const { container } = render(<BillingNShippingInfo order={mockOrder} />);
      
      const flexRow = container.querySelector('.d-flex.flex-row.gap-2.rounded');
      expect(flexRow).toBeDefined();
    });

    it('should have col-6 class for each address column', () => {
      const { container } = render(<BillingNShippingInfo order={mockOrder} />);
      
      const columns = container.querySelectorAll('.col-6');
      expect(columns).toHaveLength(2);
    });

    it('should have border container for shipment section', () => {
      const { container } = render(<BillingNShippingInfo order={mockOrder} />);
      
      const shipmentContainer = container.querySelector('.border.border-1.shadow-sm.p-3.mb-3.bg-body.rounded:last-child');
      expect(shipmentContainer).toBeDefined();
    });

    it('should have h4 header for Shipment section', () => {
      render(<BillingNShippingInfo order={mockOrder} />);
      
      const shipmentHeader = screen.getByText('Shipment');
      expect(shipmentHeader).toHaveClass('mb-3');
    });
  });

  describe('Edge cases', () => {
    it('should handle very long order ID', () => {
      const orderWithLongId: Order = {
        ...mockOrder,
        id: 999999,
      };
      
      render(<BillingNShippingInfo order={orderWithLongId} />);
      
      expect(screen.getByText('999999')).toBeDefined();
    });

    it('should handle order without optional fields', () => {
      const orderWithoutOptional: Order = {
        ...mockOrder,
        note: undefined,
        tax: undefined,
        discount: undefined,
        deliveryFee: undefined,
        couponCode: undefined,
        deliveryStatus: undefined,
        checkoutId: undefined,
      };
      
      render(<BillingNShippingInfo order={orderWithoutOptional} />);
      
      expect(screen.getByText('Billing & shipping')).toBeDefined();
    });
  });
});