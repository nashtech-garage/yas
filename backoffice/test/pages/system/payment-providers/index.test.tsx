import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import PaymentProviders from '../../../../pages/system/payment-providers/index';

// Mock dependencies
vi.mock('../../../../commonServices/ApiClientService', () => ({
  default: {
    get: vi.fn(),
    post: vi.fn(),
    put: vi.fn(),
    delete: vi.fn(),
  },
}));

vi.mock('react', () => ({
  ...vi.importActual('react'),
  useState: vi.fn((initial) => [initial, vi.fn()]),
}));

describe('PaymentProviders Page', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('should render payment providers page', () => {
    render(<PaymentProviders />);
    expect(screen.getByText('Payment Providers')).toBeDefined();
  });

  it('should display payment methods', () => {
    render(<PaymentProviders />);
    expect(screen.getByText('Paypal')).toBeDefined();
    expect(screen.getByText('Cash on Delivery')).toBeDefined();
    expect(screen.getByText('Stripe')).toBeDefined();
  });

  it('should display enabled status for payment methods', () => {
    render(<PaymentProviders />);
    // Check for enabled icons
    const checkIcons = document.querySelectorAll('.fa-check');
    expect(checkIcons.length).toBeGreaterThan(0);
  });

  it('should display disabled status for payment methods', () => {
    render(<PaymentProviders />);
    // Check for disabled icons
    const timesIcons = document.querySelectorAll('.fa-times');
    expect(timesIcons.length).toBeGreaterThan(0);
  });

  it('should display table headers', () => {
    render(<PaymentProviders />);
    expect(screen.getByText('Payment method')).toBeDefined();
    expect(screen.getByText('Is enabled')).toBeDefined();
    expect(screen.getByText('Configure')).toBeDefined();
  });

  it('should display configure buttons', () => {
    render(<PaymentProviders />);
    const configureButtons = document.querySelectorAll('.fa-cog');
    expect(configureButtons.length).toBe(3);
  });

  it('should display play buttons', () => {
    render(<PaymentProviders />);
    const playButtons = document.querySelectorAll('.fa-play');
    expect(playButtons.length).toBe(3);
  });

  it('should handle loading state', () => {
    render(<PaymentProviders />);
    // Should not show loading since isLoading is false by default
    expect(screen.queryByText('Loading...')).toBeNull();
  });
});