import { describe, it, expect, vi, beforeEach } from 'vitest';
import {
  handleDeletingResponse,
  handleUpdatingResponse,
  handleCreatingResponse,
  handleResponse,
} from '../../../common/services/ResponseStatusHandlingService';
import { toastError, toastSuccess } from '../../../common/services/ToastService';
import {
  ResponseStatus,
  ResponseTitle,
  CREATE_FAILED,
  CREATE_SUCCESSFULLY,
  DELETE_FAILED,
  HAVE_BEEN_DELETED,
  UPDATE_FAILED,
  UPDATE_SUCCESSFULLY,
} from '../../../constants/Common';

// Mock ToastService
vi.mock('../../../common/services/ToastService', () => ({
  toastError: vi.fn(),
  toastSuccess: vi.fn(),
}));

describe('ToastHandlerService', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  describe('handleDeletingResponse', () => {
    it('should show success toast when status is SUCCESS', () => {
      const response = { status: ResponseStatus.SUCCESS };
      const itemName = 'Product';

      handleDeletingResponse(response, itemName);

      expect(toastSuccess).toHaveBeenCalledWith(itemName + HAVE_BEEN_DELETED);
      expect(toastError).not.toHaveBeenCalled();
    });

    it('should show error toast when title is NOT_FOUND', () => {
      const response = {
        title: ResponseTitle.NOT_FOUND,
        detail: 'Product not found',
      };
      const itemName = 'Product';

      handleDeletingResponse(response, itemName);

      expect(toastError).toHaveBeenCalledWith('Product not found');
      expect(toastSuccess).not.toHaveBeenCalled();
    });

    it('should show error toast when title is BAD_REQUEST', () => {
      const response = {
        title: ResponseTitle.BAD_REQUEST,
        detail: 'Invalid request',
      };
      const itemName = 'Product';

      handleDeletingResponse(response, itemName);

      expect(toastError).toHaveBeenCalledWith('Invalid request');
      expect(toastSuccess).not.toHaveBeenCalled();
    });

    it('should show generic error toast for other responses', () => {
      const response = { title: 'SOME_OTHER_ERROR', detail: 'Something went wrong' };
      const itemName = 'Product';

      handleDeletingResponse(response, itemName);

      expect(toastError).toHaveBeenCalledWith(DELETE_FAILED);
      expect(toastSuccess).not.toHaveBeenCalled();
    });

    it('should handle itemName as number', () => {
      const response = { status: ResponseStatus.SUCCESS };
      const itemName = 123;

      handleDeletingResponse(response, itemName);

      expect(toastSuccess).toHaveBeenCalledWith(123 + HAVE_BEEN_DELETED);
    });
  });

  describe('handleUpdatingResponse', () => {
    it('should show success toast when status is SUCCESS', () => {
      const response = { status: ResponseStatus.SUCCESS };

      handleUpdatingResponse(response);

      expect(toastSuccess).toHaveBeenCalledWith(UPDATE_SUCCESSFULLY);
      expect(toastError).not.toHaveBeenCalled();
    });

    it('should show error toast when title is BAD_REQUEST', () => {
      const response = {
        title: ResponseTitle.BAD_REQUEST,
        detail: 'Invalid update data',
      };

      handleUpdatingResponse(response);

      expect(toastError).toHaveBeenCalledWith('Invalid update data');
      expect(toastSuccess).not.toHaveBeenCalled();
    });

    it('should show error toast when title is NOT_FOUND', () => {
      const response = {
        title: ResponseTitle.NOT_FOUND,
        detail: 'Item not found',
      };

      handleUpdatingResponse(response);

      expect(toastError).toHaveBeenCalledWith('Item not found');
      expect(toastSuccess).not.toHaveBeenCalled();
    });

    it('should show generic error toast for other responses', () => {
      const response = { title: 'UNKNOWN_ERROR', detail: 'Unknown' };

      handleUpdatingResponse(response);

      expect(toastError).toHaveBeenCalledWith(UPDATE_FAILED);
      expect(toastSuccess).not.toHaveBeenCalled();
    });
  });

  describe('handleCreatingResponse', () => {
    it('should show success toast when status is CREATED', async () => {
      const response = { status: ResponseStatus.CREATED };

      await handleCreatingResponse(response);

      expect(toastSuccess).toHaveBeenCalledWith(CREATE_SUCCESSFULLY);
      expect(toastError).not.toHaveBeenCalled();
    });

    it('should show error toast when status is BAD_REQUEST with response detail', async () => {
      const mockResponse = {
        status: ResponseStatus.BAD_REQUEST,
        json: vi.fn().mockResolvedValue({ detail: 'Validation error' }),
      };

      await handleCreatingResponse(mockResponse);

      expect(mockResponse.json).toHaveBeenCalled();
      expect(toastError).toHaveBeenCalledWith('Validation error');
      expect(toastSuccess).not.toHaveBeenCalled();
    });

  });

  describe('handleResponse', () => {
    it('should show success toast when response.ok is true', () => {
      const response = { ok: true };
      const successMsg = 'Operation completed successfully';
      const errorMsg = 'Operation failed';

      handleResponse(response, successMsg, errorMsg);

      expect(toastSuccess).toHaveBeenCalledWith(successMsg);
      expect(toastError).not.toHaveBeenCalled();
    });

    it('should show error toast when response.ok is false', () => {
      const response = { ok: false };
      const successMsg = 'Operation completed successfully';
      const errorMsg = 'Operation failed';

      handleResponse(response, successMsg, errorMsg);

      expect(toastError).toHaveBeenCalledWith(errorMsg);
      expect(toastSuccess).not.toHaveBeenCalled();
    });

    it('should handle different success and error messages', () => {
      const response1 = { ok: true };
      const response2 = { ok: false };

      handleResponse(response1, 'Custom success', 'Custom error');
      expect(toastSuccess).toHaveBeenCalledWith('Custom success');

      handleResponse(response2, 'Custom success', 'Custom error');
      expect(toastError).toHaveBeenCalledWith('Custom error');
    });
  });
});