// test/common/components/ConfirmationDialog.test.tsx
import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, fireEvent } from '@testing-library/react';
import ConfirmationDialog from '../../../common/components/ConfirmationDialog';

describe('ConfirmationDialog', () => {
  const defaultProps = {
    isOpen: true,
    title: 'Confirm Action',
    children: <div>Are you sure you want to delete this item?</div>,
    ok: vi.fn(),
    cancel: vi.fn(),
    okText: 'Yes, Delete',
    cancelText: 'Cancel',
  };

  beforeEach(() => {
    vi.clearAllMocks();
  });

  describe('Rendering', () => {
    it('should render modal when isOpen is true', () => {
      render(<ConfirmationDialog {...defaultProps} />);
      
      expect(screen.getByText('Confirm Action')).toBeDefined();
      expect(screen.getByText('Are you sure you want to delete this item?')).toBeDefined();
      expect(screen.getByText('Yes, Delete')).toBeDefined();
      expect(screen.getByText('Cancel')).toBeDefined();
    });

    it('should not render modal when isOpen is false', () => {
      render(<ConfirmationDialog {...defaultProps} isOpen={false} />);
      
      expect(screen.queryByText('Confirm Action')).toBeNull();
      expect(screen.queryByText('Yes, Delete')).toBeNull();
      expect(screen.queryByText('Cancel')).toBeNull();
    });

    it('should render without title when title not provided', () => {
      render(<ConfirmationDialog {...defaultProps} title={undefined} />);
      
      expect(screen.queryByText('Confirm Action')).toBeNull();
    });

    it('should render children content correctly', () => {
      const customChildren = <div>Custom warning message here</div>;
      render(<ConfirmationDialog {...defaultProps} children={customChildren} />);
      
      expect(screen.getByText('Custom warning message here')).toBeDefined();
    });
  });

  describe('Buttons', () => {
    it('should show both OK and Cancel buttons by default', () => {
      render(<ConfirmationDialog {...defaultProps} />);
      
      expect(screen.getByText('Yes, Delete')).toBeDefined();
      expect(screen.getByText('Cancel')).toBeDefined();
    });

    it('should hide OK button when isShowOk is false', () => {
      render(<ConfirmationDialog {...defaultProps} isShowOk={false} />);
      
      expect(screen.queryByText('Yes, Delete')).toBeNull();
      expect(screen.getByText('Cancel')).toBeDefined();
    });

    it('should hide Cancel button when isShowCancel is false', () => {
      render(<ConfirmationDialog {...defaultProps} isShowCancel={false} />);
      
      expect(screen.getByText('Yes, Delete')).toBeDefined();
      expect(screen.queryByText('Cancel')).toBeNull();
    });

    it('should hide both buttons when both are false', () => {
      render(<ConfirmationDialog {...defaultProps} isShowOk={false} isShowCancel={false} />);
      
      expect(screen.queryByText('Yes, Delete')).toBeNull();
      expect(screen.queryByText('Cancel')).toBeNull();
    });
  });

  describe('Button Text', () => {
    it('should display custom OK button text', () => {
      render(<ConfirmationDialog {...defaultProps} okText="Confirm" />);
      
      expect(screen.getByText('Confirm')).toBeDefined();
    });

    it('should display custom Cancel button text', () => {
      render(<ConfirmationDialog {...defaultProps} cancelText="Close" />);
      
      expect(screen.getByText('Close')).toBeDefined();
    });

  });

  describe('Events', () => {
    it('should call ok function when OK button is clicked', () => {
      const okMock = vi.fn();
      render(<ConfirmationDialog {...defaultProps} ok={okMock} />);
      
      const okButton = screen.getByText('Yes, Delete');
      fireEvent.click(okButton);
      
      expect(okMock).toHaveBeenCalledTimes(1);
    });

    it('should call cancel function when Cancel button is clicked', () => {
      const cancelMock = vi.fn();
      render(<ConfirmationDialog {...defaultProps} cancel={cancelMock} />);
      
      const cancelButton = screen.getByText('Cancel');
      fireEvent.click(cancelButton);
      
      expect(cancelMock).toHaveBeenCalledTimes(1);
    });

    it('should call cancel function when modal close button is clicked', () => {
      const cancelMock = vi.fn();
      render(<ConfirmationDialog {...defaultProps} cancel={cancelMock} />);
      
      const closeButton = document.querySelector('.btn-close');
      if (closeButton) {
        fireEvent.click(closeButton);
        expect(cancelMock).toHaveBeenCalledTimes(1);
      }
    });

    it('should call cancel function when clicking outside modal', () => {
      const cancelMock = vi.fn();
      render(<ConfirmationDialog {...defaultProps} cancel={cancelMock} />);
      
      // Modal backdrop
      const backdrop = document.querySelector('.modal-backdrop');
      if (backdrop) {
        fireEvent.click(backdrop);
        expect(cancelMock).toHaveBeenCalledTimes(1);
      }
    });
  });

  describe('Interactions', () => {
    it('should handle multiple OK clicks', () => {
      const okMock = vi.fn();
      render(<ConfirmationDialog {...defaultProps} ok={okMock} />);
      
      const okButton = screen.getByText('Yes, Delete');
      fireEvent.click(okButton);
      fireEvent.click(okButton);
      fireEvent.click(okButton);
      
      expect(okMock).toHaveBeenCalledTimes(3);
    });

    it('should handle multiple Cancel clicks', () => {
      const cancelMock = vi.fn();
      render(<ConfirmationDialog {...defaultProps} cancel={cancelMock} />);
      
      const cancelButton = screen.getByText('Cancel');
      fireEvent.click(cancelButton);
      fireEvent.click(cancelButton);
      
      expect(cancelMock).toHaveBeenCalledTimes(2);
    });
  });

  describe('Different content types', () => {
    it('should render complex children content', () => {
      const complexChildren = (
        <div>
          <p>Warning!</p>
          <ul>
            <li>Item 1 will be deleted</li>
            <li>Item 2 will be deleted</li>
          </ul>
        </div>
      );
      
      render(<ConfirmationDialog {...defaultProps} children={complexChildren} />);
      
      expect(screen.getByText('Warning!')).toBeDefined();
      expect(screen.getByText('Item 1 will be deleted')).toBeDefined();
      expect(screen.getByText('Item 2 will be deleted')).toBeDefined();
    });

    it('should render with empty children', () => {
      render(
        <ConfirmationDialog 
          {...defaultProps} 
          children={<div></div>} 
        />
      );
      
      expect(screen.getByText('Confirm Action')).toBeDefined();
    });
  });

  describe('Button variants', () => {
    it('should have primary variant for OK button', () => {
      render(<ConfirmationDialog {...defaultProps} />);
      
      const okButton = screen.getByText('Yes, Delete');
      expect(okButton.className).toContain('btn-primary');
    });

    it('should have secondary variant for Cancel button', () => {
      render(<ConfirmationDialog {...defaultProps} />);
      
      const cancelButton = screen.getByText('Cancel');
      expect(cancelButton.className).toContain('btn-secondary');
    });
  });

  describe('Modal behavior', () => {
    it('should have close button in header', () => {
      render(<ConfirmationDialog {...defaultProps} />);
      
      const closeButton = document.querySelector('.btn-close');
      expect(closeButton).toBeDefined();
    });

    it('should have modal title when title provided', () => {
      render(<ConfirmationDialog {...defaultProps} title="Delete Confirmation" />);
      
      expect(screen.getByText('Delete Confirmation')).toBeDefined();
    });
  });
});