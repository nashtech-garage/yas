import { beforeEach, describe, expect, test, vi } from 'vitest';

vi.mock('@/common/services/ApiClientService', () => ({
  default: {
    post: vi.fn(),
  },
}));

import apiClientService from '@/common/services/ApiClientService';
import { addSampleData } from './SampleDataService';

describe('SampleDataService', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  test('addSampleData should return json when status is 2xx', async () => {
    vi.mocked(apiClientService.post).mockResolvedValue({
      status: 201,
      json: vi.fn().mockResolvedValue({ message: 'ok' }),
    } as unknown as Response);

    const payload = { key: 'value' };
    const result = await addSampleData(payload as never);

    expect(apiClientService.post).toHaveBeenCalledWith(
      '/api/sampledata/storefront/sampledata',
      JSON.stringify(payload)
    );
    expect(result).toEqual({ message: 'ok' });
  });

  test('addSampleData should throw Error when status is non-2xx', async () => {
    vi.mocked(apiClientService.post).mockResolvedValue({
      status: 400,
      statusText: 'Bad request',
    } as unknown as Response);

    await expect(addSampleData({} as never)).rejects.toThrow('Bad request');
  });
});
