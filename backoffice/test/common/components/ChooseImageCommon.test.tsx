// test/common/components/ChooseImageCommon.test.tsx
import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen } from '@testing-library/react';
import ChooseImageCommon from '../../../common/components/ChooseImageCommon';

// Mock styles
vi.mock('../../styles/ChooseImage.module.css', () => ({
  default: {
    'product-image': 'product-image-mock',
    'actions': 'actions-mock',
    'icon': 'icon-mock',
    'delete': 'delete-mock',
  },
}));

describe('ChooseImageCommon', () => {
  const defaultProps = {
    id: 'test-image-id',
    url: 'https://example.com/image.jpg',
    onDeleteImage: vi.fn(),
    iconStyle: { color: 'red', fontSize: '20px' },
    wrapperStyle: { border: '1px solid #ccc' },
    actionStyle: { backgroundColor: '#f0f0f0' },
  };

  beforeEach(() => {
    vi.clearAllMocks();
  });

  describe('Rendering', () => {
    it('should render image with correct src and alt', () => {
      render(<ChooseImageCommon {...defaultProps} />);
      
      const image = screen.getByRole('img');
      expect(image).toBeDefined();
      expect(image.getAttribute('src')).toBe('https://example.com/image.jpg');
      expect(image.getAttribute('alt')).toBe('image');
    });

    it('should render refresh icon (bi-arrow-repeat)', () => {
      render(<ChooseImageCommon {...defaultProps} />);
      
      const refreshIcon = document.querySelector('.bi-arrow-repeat');
      expect(refreshIcon).toBeDefined();
    });

    it('should render delete icon (bi-x-lg)', () => {
      render(<ChooseImageCommon {...defaultProps} />);
      
      const deleteIcon = document.querySelector('.bi-x-lg');
      expect(deleteIcon).toBeDefined();
    });
  });

  describe('Props', () => {


    it('should handle different image URLs', () => {
      const urls = [
        'https://example.com/photo1.jpg',
        'https://cdn.example.com/image.png',
        '/local/image.webp',
      ];

      urls.forEach((url) => {
        const { unmount } = render(<ChooseImageCommon {...defaultProps} url={url} />);
        const image = screen.getByRole('img');
        expect(image.getAttribute('src')).toBe(url);
        unmount();
      });
    });

  });

  describe('Edge cases', () => {
    it('should handle empty URL', () => {
      render(<ChooseImageCommon {...defaultProps} url="" />);
      
      const image = screen.getByRole('img');
      expect(image.getAttribute('src')).toBe('');
    });

    it('should handle undefined styles', () => {
      render(
        <ChooseImageCommon
          id="test"
          url="test.jpg"
          onDeleteImage={vi.fn()}
        />
      );
      
      const image = screen.getByRole('img');
      expect(image).toBeDefined();
      
      const refreshIcon = document.querySelector('.bi-arrow-repeat');
      expect(refreshIcon).toBeDefined();
    });

  });

  describe('Accessibility', () => {
    it('should have alt text for image', () => {
      render(<ChooseImageCommon {...defaultProps} />);
      
      const image = screen.getByRole('img');
      expect(image.getAttribute('alt')).toBe('image');
    });

  });
});