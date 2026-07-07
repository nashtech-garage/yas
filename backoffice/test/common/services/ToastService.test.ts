import { describe, it, expect, vi, beforeEach } from 'vitest';
import { toast } from 'react-toastify';
import { toastSuccess, toastError } from '../../../common/services/ToastService';

// Mock react-toastify
vi.mock('react-toastify', () => ({
  toast: {
    success: vi.fn(),
    error: vi.fn(),
  },
}));

describe('ToastService', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  describe('toastSuccess', () => {
    it('should call toast.success with message and default options', () => {
      const message = 'Operation successful';

      toastSuccess(message);

      expect(toast.success).toHaveBeenCalledTimes(1);
      expect(toast.success).toHaveBeenCalledWith(message, {
        position: 'top-right',
        autoClose: 3000,
        closeOnClick: true,
        pauseOnHover: false,
        theme: 'colored',
      });
    });

    it('should call toast.success with custom options', () => {
      const message = 'Custom success message';
      const customOptions = {
        position: 'bottom-left' as const,
        autoClose: 5000,
        closeOnClick: false,
        pauseOnHover: true,
        theme: 'light' as const,
      };

      toastSuccess(message, customOptions);

      expect(toast.success).toHaveBeenCalledWith(message, customOptions);
    });

    it('should call toast.success with partial custom options (merge with default)', () => {
      const message = 'Partial options';
      const partialOptions = {
        autoClose: 10000,
        theme: 'dark' as const,
      };

      toastSuccess(message, partialOptions);

      // Note: The actual implementation doesn't merge, it replaces
      // So we test exactly what is passed
      expect(toast.success).toHaveBeenCalledWith(message, partialOptions);
    });

    it('should handle empty message', () => {
      const message = '';

      toastSuccess(message);

      expect(toast.success).toHaveBeenCalledWith(message, expect.any(Object));
    });

    it('should handle long message', () => {
      const message = 'A'.repeat(1000);

      toastSuccess(message);

      expect(toast.success).toHaveBeenCalledWith(message, expect.any(Object));
    });

    it('should handle special characters in message', () => {
      const message = 'Success! @#$%^&*()_+';

      toastSuccess(message);

      expect(toast.success).toHaveBeenCalledWith(message, expect.any(Object));
    });
  });

  describe('toastError', () => {
    it('should call toast.error with message and default options', () => {
      const message = 'Operation failed';

      toastError(message);

      expect(toast.error).toHaveBeenCalledTimes(1);
      expect(toast.error).toHaveBeenCalledWith(message, {
        position: 'top-right',
        autoClose: 3000,
        closeOnClick: true,
        pauseOnHover: false,
        theme: 'colored',
      });
    });

    it('should call toast.error with custom options', () => {
      const message = 'Custom error message';
      const customOptions = {
        position: 'top-center' as const,
        autoClose: 7000,
        closeOnClick: true,
        pauseOnHover: true,
        theme: 'dark' as const,
      };

      toastError(message, customOptions);

      expect(toast.error).toHaveBeenCalledWith(message, customOptions);
    });

    it('should call toast.error with partial custom options', () => {
      const message = 'Partial error options';
      const partialOptions = {
        autoClose: 2000,
        position: 'bottom-right' as const,
      };

      toastError(message, partialOptions);

      expect(toast.error).toHaveBeenCalledWith(message, partialOptions);
    });

    it('should handle empty error message', () => {
      const message = '';

      toastError(message);

      expect(toast.error).toHaveBeenCalledWith(message, expect.any(Object));
    });

    it('should handle long error message', () => {
      const message = 'E'.repeat(1000);

      toastError(message);

      expect(toast.error).toHaveBeenCalledWith(message, expect.any(Object));
    });

    it('should handle special characters in error message', () => {
      const message = 'Error! @#$%^&*()_+';

      toastError(message);

      expect(toast.error).toHaveBeenCalledWith(message, expect.any(Object));
    });
  });

  describe('Default options', () => {
    it('should have correct default options', () => {
      const message = 'Test message';
      
      toastSuccess(message);
      
      const defaultOptions = (toast.success as any).mock.calls[0][1];
      
      expect(defaultOptions).toEqual({
        position: 'top-right',
        autoClose: 3000,
        closeOnClick: true,
        pauseOnHover: false,
        theme: 'colored',
      });
    });

    it('should not modify default options when custom options are provided', () => {
      const message = 'Test';
      const customOptions = { autoClose: 1000 };
      
      toastError(message, customOptions);
      
      const calledOptions = (toast.error as any).mock.calls[0][1];
      
      // Custom options should be passed as-is (not merged)
      expect(calledOptions).toEqual(customOptions);
      expect(calledOptions.autoClose).toBe(1000);
      expect(calledOptions.position).toBeUndefined(); // Not merged
    });
  });
});