// test/common/components/Layout.test.tsx
import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, fireEvent } from '@testing-library/react';
import Layout from '../../../common/components/Layout';

// Mock next/link
vi.mock('next/link', () => ({
  default: ({ children, href }: { children: React.ReactNode; href: string }) => (
    <a href={href}>{children}</a>
  ),
}));

// Mock next/head
vi.mock('next/head', () => ({
  default: ({ children }: { children: React.ReactNode }) => <>{children}</>,
}));

// Mock AuthenticationInfo component
vi.mock('../../../common/components/AuthenticationInfo', () => ({
  default: () => <div>Mock Authentication Info</div>,
}));

// Mock react-toastify
vi.mock('react-toastify', () => ({
  ToastContainer: () => <div>Mock Toast Container</div>,
}));

// Mock sidebar data
vi.mock('../../../asset/data/sidebar', () => ({
  menu_catalog_item_data: [
    { id: 1, name: 'Brands', link: '/catalog/brands' },
    { id: 2, name: 'Categories', link: '/catalog/categories' },
    { id: 3, name: 'Products', link: '/catalog/products' },
  ],
  menu_customer_item_data: [
    { id: 1, name: 'Customers', link: '/customers' },
  ],
  menu_inventory_item_data: [
    { id: 1, name: 'Stock', link: '/inventory/stock' },
  ],
  menu_location_item_data: [
    { id: 1, name: 'Countries', link: '/location/countries' },
  ],
  menu_promotion_item_data: [
    { id: 1, name: 'Promotions', link: '/promotions' },
  ],
  menu_sale_item_data: [
    { id: 1, name: 'Orders', link: '/sales/orders' },
  ],
  menu_system_item_data: [
    { id: 1, name: 'Users', link: '/system/users' },
  ],
  menu_tax_item_data: [
    { id: 1, name: 'Tax Rates', link: '/tax/rates' },
  ],
}));

describe('Layout', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  describe('Rendering', () => {
    it('should render children content', () => {
      render(
        <Layout>
          <div>Test Child Content</div>
        </Layout>
      );
      
      expect(screen.getByText('Test Child Content')).toBeDefined();
    });

    it('should render header with title', () => {
      render(
        <Layout>
          <div>Content</div>
        </Layout>
      );
      
      expect(document.title).toBe('Yas - Backoffice');
    });

    it('should render footer with link', () => {
      render(
        <Layout>
          <div>Content</div>
        </Layout>
      );
      
      const footerLink = screen.getByText(/Powered by/);
      expect(footerLink).toBeDefined();
      expect(footerLink.getAttribute('href')).toBe('https://github.com/nashtech-garage/yas');
    });

    it('should render AuthenticationInfo component', () => {
      render(
        <Layout>
          <div>Content</div>
        </Layout>
      );
      
      expect(screen.getByText('Mock Authentication Info')).toBeDefined();
    });

    it('should render ToastContainer', () => {
      render(
        <Layout>
          <div>Content</div>
        </Layout>
      );
      
      expect(screen.getByText('Mock Toast Container')).toBeDefined();
    });
  });

  describe('Sidebar', () => {
    it('should render logo link', () => {
      render(
        <Layout>
          <div>Content</div>
        </Layout>
      );
      
      const logo = screen.getByText('Yas');
      expect(logo).toBeDefined();
      expect(logo.getAttribute('href')).toBe('/');
    });

    it('should render Catalog menu', () => {
      render(
        <Layout>
          <div>Content</div>
        </Layout>
      );
      
      expect(screen.getByText('Catalog')).toBeDefined();
    });



    it('should render Sales menu', () => {
      render(
        <Layout>
          <div>Content</div>
        </Layout>
      );
      
      expect(screen.getByText('Sales')).toBeDefined();
    });

    it('should render Location menu', () => {
      render(
        <Layout>
          <div>Content</div>
        </Layout>
      );
      
      expect(screen.getByText('Location')).toBeDefined();
    });

    it('should render Inventory menu', () => {
      render(
        <Layout>
          <div>Content</div>
        </Layout>
      );
      
      expect(screen.getByText('Inventory')).toBeDefined();
    });

    it('should render Promotion menu', () => {
      render(
        <Layout>
          <div>Content</div>
        </Layout>
      );
      
      expect(screen.getByText('Promotion')).toBeDefined();
    });

    it('should render Tax menu', () => {
      render(
        <Layout>
          <div>Content</div>
        </Layout>
      );
      
      expect(screen.getByText('Tax')).toBeDefined();
    });

    it('should render System menu', () => {
      render(
        <Layout>
          <div>Content</div>
        </Layout>
      );
      
      expect(screen.getByText('System')).toBeDefined();
    });
  });

  describe('Menu Interaction', () => {
    it('should change active menu when clicking on Catalog', () => {
      render(
        <Layout>
          <div>Content</div>
        </Layout>
      );
      
      const catalogMenu = screen.getByText('Catalog');
      const parentLi = catalogMenu.closest('li');
      
      fireEvent.click(catalogMenu);
      
      expect(parentLi?.className).toContain('active');
    });


    it('should change active menu when clicking on Sales', () => {
      render(
        <Layout>
          <div>Content</div>
        </Layout>
      );
      
      const salesMenu = screen.getByText('Sales');
      const parentLi = salesMenu.closest('li');
      
      fireEvent.click(salesMenu);
      
      expect(parentLi?.className).toContain('active');
    });
  });

  describe('Sidebar Toggle', () => {
    it('should toggle sidebar when clicking toggle button', () => {
      render(
        <Layout>
          <div>Content</div>
        </Layout>
      );
      
      const toggleButton = screen.getByRole('button', { name: /Toggle Menu/i });
      const sidebar = document.querySelector('#sidebar');
      
      expect(sidebar?.className).not.toContain('active');
      
      fireEvent.click(toggleButton);
      expect(sidebar?.className).toContain('active');
      
      fireEvent.click(toggleButton);
      expect(sidebar?.className).not.toContain('active');
    });
  });

  describe('Child Menu Items', () => {
    it('should render Brands submenu item', () => {
      render(
        <Layout>
          <div>Content</div>
        </Layout>
      );
      
      expect(screen.getByText('Brands')).toBeDefined();
    });

    it('should render Categories submenu item', () => {
      render(
        <Layout>
          <div>Content</div>
        </Layout>
      );
      
      expect(screen.getByText('Categories')).toBeDefined();
    });

    it('should render Products submenu item', () => {
      render(
        <Layout>
          <div>Content</div>
        </Layout>
      );
      
      expect(screen.getByText('Products')).toBeDefined();
    });

    it('should navigate to correct link when clicking submenu item', () => {
      render(
        <Layout>
          <div>Content</div>
        </Layout>
      );
      
      const brandsLink = screen.getByText('Brands');
      expect(brandsLink.getAttribute('href')).toBe('/catalog/brands');
    });
  });

  describe('Responsive Behavior', () => {
    it('should have sidebar initially inactive', () => {
      render(
        <Layout>
          <div>Content</div>
        </Layout>
      );
      
      const sidebar = document.querySelector('#sidebar');
      expect(sidebar?.className).not.toContain('active');
    });

    it('should have toggle button with correct text', () => {
      render(
        <Layout>
          <div>Content</div>
        </Layout>
      );
      
      const toggleButton = screen.getByRole('button', { name: /Toggle Menu/i });
      expect(toggleButton).toBeDefined();
    });
  });

  describe('Meta Tags', () => {
    it('should have correct meta description', () => {
      render(
        <Layout>
          <div>Content</div>
        </Layout>
      );
      
      const metaDescription = document.querySelector('meta[name="description"]');
      expect(metaDescription).toBeDefined();
      expect(metaDescription?.getAttribute('content')).toBe('Yet another shop backoffice');
    });

    it('should have correct favicon', () => {
      render(
        <Layout>
          <div>Content</div>
        </Layout>
      );
      
      const favicon = document.querySelector('link[rel="icon"]');
      expect(favicon).toBeDefined();
      expect(favicon?.getAttribute('href')).toBe('/favicon.ico');
    });
  });
});