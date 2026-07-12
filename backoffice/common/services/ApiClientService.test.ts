import apiClientService from './ApiClientService';

describe('ApiClientService', () => {
  beforeEach(() => {
    vi.restoreAllMocks();
  });

  it('sends GET request without options and returns response', async () => {
    const response = { type: 'basic', redirected: false, url: 'http://localhost' } as Response;
    const fetchMock = vi.fn().mockResolvedValue(response);
    vi.stubGlobal('fetch', fetchMock);

    const result = await apiClientService.get('/api/test');

    expect(result).toBe(response);
    expect(fetchMock).toHaveBeenCalledWith('/api/test', undefined);
  });

  it('sends POST request with default content type', async () => {
    const response = { type: 'basic', redirected: false, url: 'http://localhost' } as Response;
    const fetchMock = vi.fn().mockResolvedValue(response);
    vi.stubGlobal('fetch', fetchMock);

    await apiClientService.post('/api/test', JSON.stringify({ name: 'abc' }));

    expect(fetchMock).toHaveBeenCalledWith('/api/test', {
      method: 'POST',
      headers: { 'Content-type': 'application/json; charset=UTF-8' },
      body: JSON.stringify({ name: 'abc' }),
    });
  });

  it('removes content type header when sending FormData', async () => {
    const response = { type: 'basic', redirected: false, url: 'http://localhost' } as Response;
    const fetchMock = vi.fn().mockResolvedValue(response);
    vi.stubGlobal('fetch', fetchMock);

    const formData = new FormData();
    formData.append('file', new Blob(['x']), 'x.txt');

    await apiClientService.post('/api/upload', formData);

    const options = fetchMock.mock.calls[0][1] as RequestInit;
    expect(options.method).toBe('POST');
    expect(options.headers).toEqual({});
    expect(options.body).toBe(formData);
  });

  it('redirects browser when CORS redirected response is returned', async () => {
    const response = {
      type: 'cors',
      redirected: true,
      url: 'https://redirect.example.com/login',
    } as Response;
    const fetchMock = vi.fn().mockResolvedValue(response);
    vi.stubGlobal('fetch', fetchMock);

    const hrefSetter = vi.fn();
    Object.defineProperty(window, 'location', {
      configurable: true,
      value: {
        set href(value: string) {
          hrefSetter(value);
        },
        get href() {
          return '';
        },
      },
    });

    await apiClientService.get('/api/secure');

    expect(hrefSetter).toHaveBeenCalledWith('https://redirect.example.com/login');
  });

  it('rethrows fetch errors', async () => {
    const fetchMock = vi.fn().mockRejectedValue(new Error('network error'));
    vi.stubGlobal('fetch', fetchMock);

    await expect(apiClientService.get('/api/fail')).rejects.toThrow('network error');
  });

  it('sends PUT request with overridden content type', async () => {
    const response = { type: 'basic', redirected: false, url: 'http://localhost' } as Response;
    const fetchMock = vi.fn().mockResolvedValue(response);
    vi.stubGlobal('fetch', fetchMock);

    await apiClientService.put('/api/put', JSON.stringify({ a: 1 }), 'text/plain');

    expect(fetchMock).toHaveBeenCalledWith('/api/put', {
      method: 'PUT',
      headers: { 'Content-type': 'text/plain' },
      body: JSON.stringify({ a: 1 }),
    });
  });

  it('sends DELETE request with default headers and no body', async () => {
    const response = { type: 'basic', redirected: false, url: 'http://localhost' } as Response;
    const fetchMock = vi.fn().mockResolvedValue(response);
    vi.stubGlobal('fetch', fetchMock);

    await apiClientService.delete('/api/item/1');

    expect(fetchMock).toHaveBeenCalledWith('/api/item/1', {
      method: 'DELETE',
      headers: { 'Content-type': 'application/json; charset=UTF-8' },
    });
  });
});
