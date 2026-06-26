import {
  handleDeletingResponse,
  handleUpdatingResponse,
  handleCreatingResponse,
  handleResponse,
} from '@commonServices/ResponseStatusHandlingService';

// Mock ToastService
jest.mock('@commonServices/ToastService', () => ({
  toastSuccess: jest.fn(),
  toastError: jest.fn(),
}));

import { toastSuccess, toastError } from '@commonServices/ToastService';

describe('ResponseStatusHandlingService', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  describe('handleDeletingResponse', () => {
    it('should call toastSuccess when response status is 204 (SUCCESS)', () => {
      const response = { status: 204 };
      handleDeletingResponse(response, 'Product A');
      expect(toastSuccess).toHaveBeenCalledWith('Product A have been deleted');
    });

    it('should call toastError with detail when title is Not Found', () => {
      const response = { title: 'Not Found', detail: 'Item not found' };
      handleDeletingResponse(response, 'Item');
      expect(toastError).toHaveBeenCalledWith('Item not found');
    });

    it('should call toastError with detail when title is Bad Request', () => {
      const response = { title: 'Bad Request', detail: 'Invalid request' };
      handleDeletingResponse(response, 'Item');
      expect(toastError).toHaveBeenCalledWith('Invalid request');
    });

    it('should call toastError with DELETE_FAILED for unknown response', () => {
      const response = { status: 500 };
      handleDeletingResponse(response, 'Item');
      expect(toastError).toHaveBeenCalledWith('Delete failed');
    });
  });

  describe('handleUpdatingResponse', () => {
    it('should call toastSuccess when response status is 204 (SUCCESS)', () => {
      const response = { status: 204 };
      handleUpdatingResponse(response);
      expect(toastSuccess).toHaveBeenCalledWith('Update successfully');
    });

    it('should call toastError with detail when title is Bad Request', () => {
      const response = { title: 'Bad Request', detail: 'Validation failed' };
      handleUpdatingResponse(response);
      expect(toastError).toHaveBeenCalledWith('Validation failed');
    });

    it('should call toastError with detail when title is Not Found', () => {
      const response = { title: 'Not Found', detail: 'Resource not found' };
      handleUpdatingResponse(response);
      expect(toastError).toHaveBeenCalledWith('Resource not found');
    });

    it('should call toastError with UPDATE_FAILED for unknown response', () => {
      const response = { status: 500 };
      handleUpdatingResponse(response);
      expect(toastError).toHaveBeenCalledWith('Update failed');
    });
  });

  describe('handleCreatingResponse', () => {
    it('should call toastSuccess when response status is 201 (CREATED)', async () => {
      const response = { status: 201 };
      await handleCreatingResponse(response);
      expect(toastSuccess).toHaveBeenCalledWith('Create successfully');
    });

    it('should call toastError with detail when status is 400 (BAD_REQUEST)', async () => {
      const jsonData = { detail: 'Validation error occurred' };
      const response = {
        status: 400,
        json: jest.fn().mockResolvedValue(jsonData),
      };
      await handleCreatingResponse(response);
      expect(toastError).toHaveBeenCalledWith('Validation error occurred');
    });

    it('should call toastError with CREATE_FAILED for unknown response status', async () => {
      const response = { status: 500, json: jest.fn() };
      await handleCreatingResponse(response);
      expect(toastError).toHaveBeenCalledWith('Create failed');
    });
  });

  describe('handleResponse', () => {
    it('should call toastSuccess with successMsg when response.ok is true', () => {
      const response = { ok: true };
      handleResponse(response, 'Done!', 'Failed!');
      expect(toastSuccess).toHaveBeenCalledWith('Done!');
    });

    it('should call toastError with errorMsg when response.ok is false', () => {
      const response = { ok: false };
      handleResponse(response, 'Done!', 'Failed!');
      expect(toastError).toHaveBeenCalledWith('Failed!');
    });
  });
});
