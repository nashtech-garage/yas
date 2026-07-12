import { beforeEach, describe, expect, test, vi } from 'vitest';

vi.mock('@/common/services/ApiClientService', () => ({
  default: {
    get: vi.fn(),
  },
}));

import apiClientService from '@/common/services/ApiClientService';
import { getMediaById } from './MediaService';

describe('MediaService', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  test('getMediaById should return media json on success', async () => {
    vi.mocked(apiClientService.get).mockResolvedValue({
      status: 200,
      json: vi.fn().mockResolvedValue({ id: 1, url: '/img.png' }),
    } as unknown as Response);

    const result = await getMediaById(1);

    expect(apiClientService.get).toHaveBeenCalledWith('/api/media/medias/1');
    expect(result).toEqual({ id: 1, url: '/img.png' });
  });

  test('getMediaById should throw response on failure', async () => {
    const response = { status: 500 } as Response;
    vi.mocked(apiClientService.get).mockResolvedValue(response);

    await expect(getMediaById(1)).rejects.toBe(response);
  });
});
