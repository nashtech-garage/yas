import apiClientService from '../../../common/services/ApiClientService';
import { describe, it, expect, vi, beforeEach } from 'vitest';

// Mock fetch global bằng vi.fn()
global.fetch = vi.fn();

describe('apiClientService', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  describe('GET', () => {
    it('should make GET request without body', async () => {
      const mockResponse = { ok: true, json: async () => ({ id: 1 }) };
      (fetch as any).mockResolvedValue(mockResponse);

      await apiClientService.get('/users/1');

      expect(fetch).toHaveBeenCalledWith('/users/1', undefined);
      expect(fetch).toHaveBeenCalledTimes(1);
    });
  });

  describe('POST', () => {

    it('should make POST request with custom content type', async () => {
      const mockResponse = { ok: true };
      (fetch as any).mockResolvedValue(mockResponse);

      const data = 'plain text';
      await apiClientService.post('/logs', data, 'text/plain');

      expect(fetch).toHaveBeenCalledWith('/logs', {
        method: 'POST',
        headers: { 'Content-type': 'text/plain' },
        body: data,
      });
    });

    it('should handle FormData (remove Content-type header)', async () => {
      const mockResponse = { ok: true };
      (fetch as any).mockResolvedValue(mockResponse);

      const formData = new FormData();
      formData.append('file', new Blob(['test']), 'test.txt');

      await apiClientService.post('/upload', formData);

      const callArgs = (fetch as any).mock.calls[0][1];
      expect(callArgs.headers['Content-type']).toBeUndefined();
      expect(callArgs.body).toBe(formData);
    });

    it('should make POST request without body when data is null', async () => {
      const mockResponse = { ok: true };
      (fetch as any).mockResolvedValue(mockResponse);

      await apiClientService.post('/users', null);

      expect(fetch).toHaveBeenCalledWith('/users', {
        method: 'POST',
        headers: { 'Content-type': 'application/json; charset=UTF-8' },
      });
      expect((fetch as any).mock.calls[0][1].body).toBeUndefined();
    });
  });

  describe('PUT', () => {

    it('should make PUT request with custom content type', async () => {
      const mockResponse = { ok: true };
      (fetch as any).mockResolvedValue(mockResponse);

      await apiClientService.put('/users/1', 'raw data', 'text/plain');

      expect(fetch).toHaveBeenCalledWith('/users/1', {
        method: 'PUT',
        headers: { 'Content-type': 'text/plain' },
        body: 'raw data',
      });
    });
  });

  describe('DELETE', () => {
    it('should make DELETE request without body', async () => {
      const mockResponse = { ok: true };
      (fetch as any).mockResolvedValue(mockResponse);

      await apiClientService.delete('/users/1');

      expect(fetch).toHaveBeenCalledWith('/users/1', {
        method: 'DELETE',
        headers: { 'Content-type': 'application/json; charset=UTF-8' },
      });
    });
  });

  describe('Error handling', () => {
    it('should throw error when fetch fails', async () => {
      const networkError = new Error('Network error');
      (fetch as any).mockRejectedValue(networkError);

      await expect(apiClientService.get('/users')).rejects.toThrow('Network error');
    });

    it('should log error to console', async () => {
      const consoleSpy = vi.spyOn(console, 'error').mockImplementation(() => {});
      const error = new Error('API Error');
      (fetch as any).mockRejectedValue(error);

      await apiClientService.get('/users').catch(() => {});

      expect(consoleSpy).toHaveBeenCalledWith('API call error:', error);
      consoleSpy.mockRestore();
    });
  });

  describe('CORS redirect handling', () => {
    it('should redirect when response is CORS and redirected', async () => {
      const mockResponse = {
        type: 'cors',
        redirected: true,
        url: 'https://example.com/login',
      };
      (fetch as any).mockResolvedValue(mockResponse);

      const windowSpy = vi.spyOn(window, 'location', 'get');
      Object.defineProperty(window, 'location', {
        value: { href: '' },
        writable: true,
      });

      await apiClientService.get('/protected');

      expect(window.location.href).toBe('https://example.com/login');
      windowSpy.mockRestore();
    });

    it('should NOT redirect when response is not CORS', async () => {
      const mockResponse = {
        type: 'basic',
        redirected: false,
        ok: true,
      };
      (fetch as any).mockResolvedValue(mockResponse);

      const windowSpy = vi.spyOn(window, 'location', 'get');
      await apiClientService.get('/users');

      expect(window.location.href).not.toBe('https://example.com/login');
      windowSpy.mockRestore();
    });
  });

  describe('Response return', () => {
    it('should return the fetch response', async () => {
      const mockResponse = { ok: true, status: 200 };
      (fetch as any).mockResolvedValue(mockResponse);

      const response = await apiClientService.get('/users');

      expect(response).toBe(mockResponse);
    });
  });
});