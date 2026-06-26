import apiClientService from '@commonServices/ApiClientService';

// Mock global fetch
const mockFetch = jest.fn();
global.fetch = mockFetch;

// Mock window.location
const mockLocationAssign = jest.fn();
Object.defineProperty(window, 'location', {
  value: { href: '' },
  writable: true,
});

describe('ApiClientService', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  describe('get', () => {
    it('should call fetch with GET method (no requestOptions for GET)', async () => {
      const mockResponse = { ok: true, type: 'basic', redirected: false };
      mockFetch.mockResolvedValue(mockResponse);

      const result = await apiClientService.get('/api/products');

      expect(mockFetch).toHaveBeenCalledWith('/api/products', undefined);
      expect(result).toBe(mockResponse);
    });

    it('should return the response from fetch', async () => {
      const mockResponse = { status: 200, ok: true, type: 'basic', redirected: false };
      mockFetch.mockResolvedValue(mockResponse);

      const result = await apiClientService.get('/api/items');

      expect(result).toEqual(mockResponse);
    });

    it('should throw error when fetch fails', async () => {
      mockFetch.mockRejectedValue(new Error('Network error'));

      await expect(apiClientService.get('/api/products')).rejects.toThrow('Network error');
    });
  });

  describe('post', () => {
    it('should call fetch with POST method and JSON body', async () => {
      const mockResponse = { status: 201, ok: true, type: 'basic', redirected: false };
      mockFetch.mockResolvedValue(mockResponse);

      const data = JSON.stringify({ name: 'Product A' });
      const result = await apiClientService.post('/api/products', data);

      expect(mockFetch).toHaveBeenCalledWith(
        '/api/products',
        expect.objectContaining({
          method: 'POST',
          headers: expect.objectContaining({
            'Content-type': 'application/json; charset=UTF-8',
          }),
          body: data,
        })
      );
      expect(result).toBe(mockResponse);
    });

    it('should call fetch with custom content type', async () => {
      const mockResponse = { status: 201, ok: true, type: 'basic', redirected: false };
      mockFetch.mockResolvedValue(mockResponse);

      await apiClientService.post('/api/upload', 'data', 'text/plain');

      expect(mockFetch).toHaveBeenCalledWith(
        '/api/upload',
        expect.objectContaining({
          headers: expect.objectContaining({
            'Content-type': 'text/plain',
          }),
        })
      );
    });

    it('should remove Content-type header when data is FormData', async () => {
      const mockResponse = { status: 201, ok: true, type: 'basic', redirected: false };
      mockFetch.mockResolvedValue(mockResponse);

      const formData = new FormData();
      formData.append('file', 'test');
      await apiClientService.post('/api/upload', formData);

      const calledWith = mockFetch.mock.calls[0][1];
      expect(calledWith.headers).not.toHaveProperty('Content-type');
    });
  });

  describe('put', () => {
    it('should call fetch with PUT method', async () => {
      const mockResponse = { status: 200, ok: true, type: 'basic', redirected: false };
      mockFetch.mockResolvedValue(mockResponse);

      const data = JSON.stringify({ name: 'Updated Product' });
      await apiClientService.put('/api/products/1', data);

      expect(mockFetch).toHaveBeenCalledWith(
        '/api/products/1',
        expect.objectContaining({
          method: 'PUT',
        })
      );
    });
  });

  describe('delete', () => {
    it('should call fetch with DELETE method', async () => {
      const mockResponse = { status: 204, ok: true, type: 'basic', redirected: false };
      mockFetch.mockResolvedValue(mockResponse);

      const result = await apiClientService.delete('/api/products/1');

      expect(mockFetch).toHaveBeenCalledWith(
        '/api/products/1',
        expect.objectContaining({
          method: 'DELETE',
        })
      );
      expect(result).toBe(mockResponse);
    });
  });

  describe('CORS redirect handling', () => {
    it('should redirect when response type is cors and redirected is true', async () => {
      const mockResponse = { type: 'cors', redirected: true, url: 'http://auth.example.com/login' };
      mockFetch.mockResolvedValue(mockResponse);

      await apiClientService.get('/api/protected');

      expect(window.location.href).toBe('http://auth.example.com/login');
    });

    it('should NOT redirect when response type is cors but redirected is false', async () => {
      window.location.href = '';
      const mockResponse = { type: 'cors', redirected: false, url: 'http://example.com' };
      mockFetch.mockResolvedValue(mockResponse);

      await apiClientService.get('/api/data');

      expect(window.location.href).toBe('');
    });
  });
});
