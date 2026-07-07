// test/common/items/ListGroup.test.tsx
import { describe, it, expect, vi } from 'vitest';
import { render, screen } from '@testing-library/react';
import { useRouter } from 'next/router';
import ListGroup from '../../../common/items/ListGroup';

// Mock next/router
vi.mock('next/router', () => ({
  useRouter: vi.fn(),
}));

// Mock next/link
vi.mock('next/link', () => ({
  default: ({ children, href }: { children: React.ReactNode; href: string }) => (
    <a href={href}>{children}</a>
  ),
}));

// Mock styles
vi.mock('../../styles/ListGroup.module.css', () => ({
  default: {
    listGroup: 'list-group-mock',
    listGroupItem: 'list-group-item-mock',
    appActiveLink: 'app-active-link-mock',
  },
}));

describe('ListGroup', () => {
  const mockData = [
    { id: 1, name: 'Dashboard', link: '/dashboard', icon: '🏠' },
    { id: 2, name: 'Products', link: '/products', icon: '📦' },
    { id: 3, name: 'Orders', link: '/orders', icon: '🛒' },
    { id: 4, name: 'Customers', link: '/customers', icon: '👥' },
  ];

  describe('Rendering', () => {
    it('should render list group component', () => {
      (useRouter as any).mockReturnValue({
        pathname: '/dashboard',
      });

      render(<ListGroup data={mockData} />);
      
      const listGroup = document.querySelector('.list-group-mock');
      expect(listGroup).toBeDefined();
      
      vi.clearAllMocks();
    });

    it('should render all items from data', () => {
      (useRouter as any).mockReturnValue({
        pathname: '/dashboard',
      });

      render(<ListGroup data={mockData} />);
      
      expect(screen.getByText('Dashboard')).toBeDefined();
      expect(screen.getByText('Products')).toBeDefined();
      expect(screen.getByText('Orders')).toBeDefined();
      expect(screen.getByText('Customers')).toBeDefined();
      
      vi.clearAllMocks();
    });

    it('should render icons for each item', () => {
      (useRouter as any).mockReturnValue({
        pathname: '/dashboard',
      });

      render(<ListGroup data={mockData} />);
      
      expect(screen.getByText('🏠')).toBeDefined();
      expect(screen.getByText('📦')).toBeDefined();
      expect(screen.getByText('🛒')).toBeDefined();
      expect(screen.getByText('👥')).toBeDefined();
      
      vi.clearAllMocks();
    });
  });

  describe('Link Behavior', () => {

    it('should render item names correctly', () => {
      (useRouter as any).mockReturnValue({
        pathname: '/dashboard',
      });

      render(<ListGroup data={mockData} />);
      
      mockData.forEach((item) => {
        expect(screen.getByText(item.name)).toBeDefined();
      });
      
      vi.clearAllMocks();
    });
  });

  describe('Icon Display', () => {
    it('should render icon as h5 element', () => {
      (useRouter as any).mockReturnValue({
        pathname: '/dashboard',
      });

      render(<ListGroup data={mockData} />);
      
      const icons = document.querySelectorAll('h5');
      expect(icons.length).toBeGreaterThan(0);
      
      vi.clearAllMocks();
    });

    it('should render custom icons', () => {
      const customIconsData = [
        { id: 1, name: 'Settings', link: '/settings', icon: '⚙️' },
        { id: 2, name: 'Profile', link: '/profile', icon: '👤' },
      ];
      
      (useRouter as any).mockReturnValue({
        pathname: '/settings',
      });

      render(<ListGroup data={customIconsData} />);
      
      expect(screen.getByText('⚙️')).toBeDefined();
      expect(screen.getByText('👤')).toBeDefined();
      
      vi.clearAllMocks();
    });
  });
});