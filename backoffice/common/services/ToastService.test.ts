import { toast } from 'react-toastify';
import { toastSuccess, toastError } from './ToastService';

jest.mock('react-toastify', () => ({
  toast: {
    success: jest.fn(),
    error: jest.fn(),
  },
}));

describe('ToastService', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  test('toastSuccess should call toast.success with message and options', () => {
    const message = 'Success message';
    toastSuccess(message);
    expect(toast.success).toHaveBeenCalledWith(message, expect.objectContaining({
      position: 'top-right',
      autoClose: 3000,
      theme: 'colored',
    }));
  });

  test('toastError should call toast.error with message and options', () => {
    const message = 'Error message';
    toastError(message);
    expect(toast.error).toHaveBeenCalledWith(message, expect.objectContaining({
      position: 'top-right',
      autoClose: 3000,
      theme: 'colored',
    }));
  });

  test('should allow overriding default toast options', () => {
    const message = 'Custom toast';
    const customOptions = { autoClose: 5000 };
    toastSuccess(message, customOptions as any);
    expect(toast.success).toHaveBeenCalledWith(message, customOptions);
  });
});
