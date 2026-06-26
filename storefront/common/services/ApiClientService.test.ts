import apiClientService from './ApiClientService';

describe('ApiClientService', () => {
  const originalFetch = global.fetch;

  beforeEach(() => {
    global.fetch = jest.fn();
  });

  afterEach(() => {
    jest.resetAllMocks();
  });

  afterAll(() => {
    global.fetch = originalFetch;
  });

  it('sends GET requests without request options', async () => {
    const response = { ok: true } as Response;
    (global.fetch as jest.Mock).mockResolvedValue(response);

    const result = await apiClientService.get('/api/example');

    expect(global.fetch).toHaveBeenCalledWith('/api/example', undefined);
    expect(result).toBe(response);
  });

  it('sends POST requests with default JSON headers', async () => {
    (global.fetch as jest.Mock).mockResolvedValue({ ok: true });

    await apiClientService.post('/api/example', JSON.stringify({ foo: 'bar' }));

    expect(global.fetch).toHaveBeenCalledWith('/api/example', {
      method: 'POST',
      headers: {
        'Content-type': 'application/json; charset=UTF-8',
      },
      body: '{"foo":"bar"}',
    });
  });

  it('removes content type when payload is FormData', async () => {
    (global.fetch as jest.Mock).mockResolvedValue({ ok: true });
    const formData = new FormData();
    formData.append('file', new Blob(['content']), 'file.txt');

    await apiClientService.post('/api/upload', formData);

    expect(global.fetch).toHaveBeenCalledWith('/api/upload', {
      method: 'POST',
      headers: {},
      body: formData,
    });
  });

  it('rethrows fetch errors', async () => {
    const error = new Error('network error');
    const consoleSpy = jest.spyOn(console, 'error').mockImplementation(() => undefined);
    (global.fetch as jest.Mock).mockRejectedValue(error);

    await expect(apiClientService.delete('/api/example')).rejects.toThrow('network error');
    expect(consoleSpy).toHaveBeenCalled();

    consoleSpy.mockRestore();
  });
});
