import apiClientService from './ApiClientService';

describe('storefront ApiClientService', () => {
  const mockFetch = jest.fn();

  beforeEach(() => {
    mockFetch.mockReset();
    (global as any).fetch = mockFetch;
  });

  it('calls fetch without options for GET', async () => {
    const mockResponse = { ok: true, type: 'basic', redirected: false } as Response;
    mockFetch.mockResolvedValueOnce(mockResponse);

    const response = await apiClientService.get('/api/products');

    expect(response).toBe(mockResponse);
    expect(mockFetch).toHaveBeenCalledWith('/api/products', undefined);
  });

  it('sends JSON body with default content type for POST', async () => {
    const payload = JSON.stringify({ name: 'Phone' });
    const mockResponse = { ok: true, type: 'basic', redirected: false } as Response;
    mockFetch.mockResolvedValueOnce(mockResponse);

    await apiClientService.post('/api/products', payload);

    expect(mockFetch).toHaveBeenCalledWith('/api/products', {
      method: 'POST',
      headers: {
        'Content-type': 'application/json; charset=UTF-8',
      },
      body: payload,
    });
  });

  it('removes content-type header when posting FormData', async () => {
    const formData = new FormData();
    formData.append('file', 'dummy');
    const mockResponse = { ok: true, type: 'basic', redirected: false } as Response;
    mockFetch.mockResolvedValueOnce(mockResponse);

    await apiClientService.post('/api/upload', formData);

    expect(mockFetch).toHaveBeenCalledWith('/api/upload', {
      method: 'POST',
      headers: {},
      body: formData,
    });
  });

  it('logs and rethrows errors from fetch', async () => {
    const networkError = new Error('Network down');
    const consoleSpy = jest.spyOn(console, 'error').mockImplementation(() => undefined);
    mockFetch.mockRejectedValueOnce(networkError);

    await expect(apiClientService.get('/api/fail')).rejects.toThrow('Network down');
    expect(consoleSpy).toHaveBeenCalledWith('API call error:', networkError);

    consoleSpy.mockRestore();
  });
});
