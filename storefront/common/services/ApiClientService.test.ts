import { afterEach, beforeEach, describe, expect, test, vi } from 'vitest';
import apiClientService from './ApiClientService';

describe('ApiClientService', () => {
  beforeEach(() => {
    vi.restoreAllMocks();
  });

  afterEach(() => {
    vi.restoreAllMocks();
  });

  test('get should call fetch without request options', async () => {
    const response = { type: 'basic', redirected: false } as Response;
    const fetchSpy = vi.spyOn(globalThis, 'fetch').mockResolvedValue(response);

    const result = await apiClientService.get('/api/test');

    expect(fetchSpy).toHaveBeenCalledWith('/api/test', undefined);
    expect(result).toBe(response);
  });

  test('post should send JSON content-type by default', async () => {
    const response = { type: 'basic', redirected: false } as Response;
    const fetchSpy = vi.spyOn(globalThis, 'fetch').mockResolvedValue(response);

    await apiClientService.post('/api/post', JSON.stringify({ name: 'yas' }));

    expect(fetchSpy).toHaveBeenCalledWith('/api/post', {
      method: 'POST',
      headers: {
        'Content-type': 'application/json; charset=UTF-8',
      },
      body: JSON.stringify({ name: 'yas' }),
    });
  });

  test('post should remove content-type for FormData body', async () => {
    const response = { type: 'basic', redirected: false } as Response;
    const fetchSpy = vi.spyOn(globalThis, 'fetch').mockResolvedValue(response);
    const body = new FormData();
    body.append('file', new Blob(['abc']), 'a.txt');

    await apiClientService.post('/api/upload', body);

    expect(fetchSpy).toHaveBeenCalledWith('/api/upload', {
      method: 'POST',
      headers: {},
      body,
    });
  });

  test('put should support custom content-type', async () => {
    const response = { type: 'basic', redirected: false } as Response;
    const fetchSpy = vi.spyOn(globalThis, 'fetch').mockResolvedValue(response);

    await apiClientService.put('/api/update', '{}', 'application/xml');

    expect(fetchSpy).toHaveBeenCalledWith('/api/update', {
      method: 'PUT',
      headers: {
        'Content-type': 'application/xml',
      },
      body: '{}',
    });
  });

  test('should return response object for CORS response', async () => {
    const response = {
      type: 'cors',
      redirected: false,
      url: 'https://example.com/new-location',
    } as Response;

    const fetchSpy = vi.spyOn(globalThis, 'fetch').mockResolvedValue(response);

    const result = await apiClientService.get('/api/redirect');

    expect(fetchSpy).toHaveBeenCalledTimes(1);
    expect(result).toBe(response);
  });

  test('should rethrow fetch error and log it', async () => {
    const error = new Error('network down');
    vi.spyOn(globalThis, 'fetch').mockRejectedValue(error);
    const errorSpy = vi.spyOn(console, 'error').mockImplementation(() => undefined);

    await expect(apiClientService.delete('/api/delete')).rejects.toThrow('network down');
    expect(errorSpy).toHaveBeenCalledWith('API call error:', error);
  });
});
