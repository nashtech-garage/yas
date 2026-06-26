import { toastSuccess, toastError } from '@commonServices/ToastService';

// Mock react-toastify
jest.mock('react-toastify', () => ({
  toast: {
    success: jest.fn(),
    error: jest.fn(),
  },
}));

import { toast } from 'react-toastify';

describe('ToastService', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  describe('toastSuccess', () => {
    it('should call toast.success with the message', () => {
      toastSuccess('Operation successful');
      expect(toast.success).toHaveBeenCalledWith(
        'Operation successful',
        expect.objectContaining({
          position: 'top-right',
          autoClose: 3000,
          closeOnClick: true,
          pauseOnHover: false,
          theme: 'colored',
        })
      );
    });

    it('should call toast.success with custom options when provided', () => {
      const customOptions = { position: 'bottom-left' as const, autoClose: 5000 };
      toastSuccess('Custom success', customOptions);
      expect(toast.success).toHaveBeenCalledWith('Custom success', customOptions);
    });

    it('should call toast.success once', () => {
      toastSuccess('Test message');
      expect(toast.success).toHaveBeenCalledTimes(1);
    });
  });

  describe('toastError', () => {
    it('should call toast.error with the message', () => {
      toastError('Something went wrong');
      expect(toast.error).toHaveBeenCalledWith(
        'Something went wrong',
        expect.objectContaining({
          position: 'top-right',
          autoClose: 3000,
          closeOnClick: true,
          pauseOnHover: false,
          theme: 'colored',
        })
      );
    });

    it('should call toast.error with custom options when provided', () => {
      const customOptions = { position: 'top-center' as const, autoClose: 1000 };
      toastError('Custom error', customOptions);
      expect(toast.error).toHaveBeenCalledWith('Custom error', customOptions);
    });

    it('should call toast.error once', () => {
      toastError('Error message');
      expect(toast.error).toHaveBeenCalledTimes(1);
    });
  });
});
