import {
  createWebhook,
  deleteWebhook,
  getWebhook,
  getWebhooks,
  updateWebhook,
} from './WebhookService';
import apiClientService from '@commonServices/ApiClientService';

vi.mock('@commonServices/ApiClientService', () => ({
  default: {
    get: vi.fn(),
    post: vi.fn(),
    put: vi.fn(),
    delete: vi.fn(),
  },
}));

describe('WebhookService', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('gets webhook list with correct paging params', async () => {
    const json = vi.fn().mockResolvedValue({ items: [{ id: 1 }], totalPages: 1 });
    vi.mocked(apiClientService.get).mockResolvedValue({ json } as unknown as Response);

    const result = await getWebhooks(2, 20);

    expect(apiClientService.get).toHaveBeenCalledWith(
      '/api/webhook/backoffice/webhooks/paging?pageNo=2&pageSize=20'
    );
    expect(result).toEqual({ items: [{ id: 1 }], totalPages: 1 });
  });

  it('gets one webhook by id', async () => {
    const json = vi.fn().mockResolvedValue({ id: 99 });
    vi.mocked(apiClientService.get).mockResolvedValue({ json } as unknown as Response);

    const result = await getWebhook(99);

    expect(apiClientService.get).toHaveBeenCalledWith('/api/webhook/backoffice/webhooks/99');
    expect(result).toEqual({ id: 99 });
  });

  it('creates webhook', async () => {
    const response = { status: 201 } as Response;
    vi.mocked(apiClientService.post).mockResolvedValue(response);

    const payload = { name: 'Order Created' } as any;
    const result = await createWebhook(payload);

    expect(apiClientService.post).toHaveBeenCalledWith(
      '/api/webhook/backoffice/webhooks',
      JSON.stringify(payload)
    );
    expect(result).toBe(response);
  });

  it('deletes webhook with 204 status', async () => {
    vi.mocked(apiClientService.delete).mockResolvedValue({ status: 204 } as Response);

    const result = await deleteWebhook(8);

    expect(result.status).toBe(204);
  });

  it('deletes webhook with non-204 status and returns error payload', async () => {
    const json = vi.fn().mockResolvedValue({ detail: 'cannot delete' });
    vi.mocked(apiClientService.delete).mockResolvedValue({
      status: 400,
      json,
    } as unknown as Response);

    const result = await deleteWebhook(8);

    expect(result).toEqual({ detail: 'cannot delete' });
  });

  it('updates webhook with non-204 status and returns error payload', async () => {
    const json = vi.fn().mockResolvedValue({ detail: 'validation error' });
    vi.mocked(apiClientService.put).mockResolvedValue({ status: 400, json } as unknown as Response);

    const result = await updateWebhook(10, { name: 'Updated' } as any);

    expect(apiClientService.put).toHaveBeenCalledWith(
      '/api/webhook/backoffice/webhooks/10',
      JSON.stringify({ name: 'Updated' })
    );
    expect(result).toEqual({ detail: 'validation error' });
  });
});
