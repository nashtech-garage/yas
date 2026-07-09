import apiClientService from './ApiClientService';

describe('backoffice ApiClientService', () => {
  const mockFetch = jest.fn();

  beforeEach(() => {
    mockFetch.mockReset();
    (global as any).fetch = mockFetch;
  });

  it('uses uppercase HTTP method and custom content type in PUT', async () => {
    const payload = JSON.stringify({ active: true });
    const mockResponse = { ok: true, type: 'basic', redirected: false } as Response;
    mockFetch.mockResolvedValueOnce(mockResponse);

    await apiClientService.put('/api/items/1', payload, 'application/json');

    expect(mockFetch).toHaveBeenCalledWith('/api/items/1', {
      method: 'PUT',
      headers: {
        'Content-type': 'application/json',
      },
      body: payload,
    });
  });

  it('passes default headers for DELETE', async () => {
    const mockResponse = { ok: true, type: 'basic', redirected: false } as Response;
    mockFetch.mockResolvedValueOnce(mockResponse);

    await apiClientService.delete('/api/items/2');

    expect(mockFetch).toHaveBeenCalledWith('/api/items/2', {
      method: 'DELETE',
      headers: {
        'Content-type': 'application/json; charset=UTF-8',
      },
    });
  });
});
