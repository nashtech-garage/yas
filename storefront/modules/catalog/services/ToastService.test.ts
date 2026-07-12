import { beforeEach, describe, expect, test, vi } from 'vitest';

vi.mock('react-toastify', () => ({
  toast: {
    success: vi.fn(),
    error: vi.fn(),
  },
}));

import { toast } from 'react-toastify';
import { toastError, toastSuccess } from './ToastService';

describe('ToastService', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  test('toastSuccess should use default options when not provided', () => {
    toastSuccess('saved');

    expect(toast.success).toHaveBeenCalledWith('saved', {
      position: 'top-right',
      autoClose: 1000,
      closeOnClick: true,
      pauseOnHover: false,
      theme: 'colored',
    });
  });

  test('toastError should pass custom options when provided', () => {
    const customOption = { autoClose: 2000 };
    toastError('failed', customOption);

    expect(toast.error).toHaveBeenCalledWith('failed', customOption);
  });
});
