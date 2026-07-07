import { describe, it, expect, vi, beforeEach } from 'vitest';
import { uploadMedia } from '../../../../modules/catalog/services/MediaService';
import { Media } from '../../../../modules/catalog/models/Media';

// Mock ApiClientService
vi.mock('../../../../common/services/ApiClientService', () => ({
  default: {
    get: vi.fn(),
    post: vi.fn(),
    put: vi.fn(),
    delete: vi.fn(),
  },
}));

import apiClientService from '../../../../common/services/ApiClientService';

describe('MediaService', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  describe('uploadMedia', () => {
    const mockFile = new File(['test-image-content'], 'test-image.jpg', { type: 'image/jpeg' });
    const mockMedia: Media = {
      id: 1,
      url: '/uploads/test-image.jpg',
    };

    it('should create FormData with the uploaded file', async () => {
      const mockResponse = {
        status: 200,
        json: vi.fn().mockResolvedValue(mockMedia),
      };
      (apiClientService.post as any).mockResolvedValue(mockResponse);

      await uploadMedia(mockFile);

      // Verify FormData was created correctly
      expect(apiClientService.post).toHaveBeenCalledTimes(1);
      const callArgs = (apiClientService.post as any).mock.calls[0];
      expect(callArgs[0]).toBe('/api/media/medias');
      expect(callArgs[1]).toBeInstanceOf(FormData);
    });

    it('should call API with correct URL and FormData', async () => {
      const mockResponse = {
        status: 200,
        json: vi.fn().mockResolvedValue(mockMedia),
      };
      (apiClientService.post as any).mockResolvedValue(mockResponse);

      await uploadMedia(mockFile);

      expect(apiClientService.post).toHaveBeenCalledTimes(1);
      expect(apiClientService.post).toHaveBeenCalledWith(
        '/api/media/medias',
        expect.any(FormData)
      );
    });

    it('should return media data on success (status 200)', async () => {
      const mockResponse = {
        status: 200,
        json: vi.fn().mockResolvedValue(mockMedia),
      };
      (apiClientService.post as any).mockResolvedValue(mockResponse);

      const result = await uploadMedia(mockFile);

      expect(result).toEqual(mockMedia);
      expect(result.id).toBe(1);
      expect(result.url).toBe('/uploads/test-image.jpg');
    });

    it('should return media data on success (status 201)', async () => {
      const mockResponse = {
        status: 201,
        json: vi.fn().mockResolvedValue(mockMedia),
      };
      (apiClientService.post as any).mockResolvedValue(mockResponse);

      const result = await uploadMedia(mockFile);

      expect(result).toEqual(mockMedia);
    });

    it('should return media data on success (status 204)', async () => {
      const mockResponse = {
        status: 204,
        json: vi.fn().mockResolvedValue(mockMedia),
      };
      (apiClientService.post as any).mockResolvedValue(mockResponse);

      const result = await uploadMedia(mockFile);

      expect(result).toEqual(mockMedia);
    });

    it('should reject with response when status is 400', async () => {
      const errorResponse = {
        status: 400,
        statusText: 'Bad Request',
      };
      (apiClientService.post as any).mockResolvedValue(errorResponse);

      await expect(uploadMedia(mockFile)).rejects.toEqual(errorResponse);
    });

    it('should reject with response when status is 404', async () => {
      const errorResponse = {
        status: 404,
        statusText: 'Not Found',
      };
      (apiClientService.post as any).mockResolvedValue(errorResponse);

      await expect(uploadMedia(mockFile)).rejects.toEqual(errorResponse);
    });

    it('should reject with response when status is 500', async () => {
      const errorResponse = {
        status: 500,
        statusText: 'Internal Server Error',
      };
      (apiClientService.post as any).mockResolvedValue(errorResponse);

      await expect(uploadMedia(mockFile)).rejects.toEqual(errorResponse);
    });

    it('should reject with response when status is 413 (file too large)', async () => {
      const errorResponse = {
        status: 413,
        statusText: 'Payload Too Large',
      };
      (apiClientService.post as any).mockResolvedValue(errorResponse);

      await expect(uploadMedia(mockFile)).rejects.toEqual(errorResponse);
    });

    it('should reject with response when status is 415 (unsupported media type)', async () => {
      const errorResponse = {
        status: 415,
        statusText: 'Unsupported Media Type',
      };
      (apiClientService.post as any).mockResolvedValue(errorResponse);

      await expect(uploadMedia(mockFile)).rejects.toEqual(errorResponse);
    });
 

    it('should handle API network error', async () => {
      const networkError = new Error('Network error');
      (apiClientService.post as any).mockRejectedValue(networkError);

      await expect(uploadMedia(mockFile)).rejects.toThrow('Network error');
    });

    it('should verify FormData contains the file with correct field name', async () => {
      const mockResponse = {
        status: 200,
        json: vi.fn().mockResolvedValue(mockMedia),
      };
      (apiClientService.post as any).mockResolvedValue(mockResponse);

      await uploadMedia(mockFile);

      const formData = (apiClientService.post as any).mock.calls[0][1];
      expect(formData.has('multipartFile')).toBe(true);
      
      const appendedFile = formData.get('multipartFile');
      expect(appendedFile).toBe(mockFile);
    });
  });
});