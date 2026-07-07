import { describe, it, expect, vi, beforeEach } from 'vitest';
import {
  getWebhooks,
  createWebhook,
  getWebhook,
  deleteWebhook,
  updateWebhook,
} from '../../../../modules/webhook/services/WebhookService';
import { Webhook } from '../../../../modules/webhook/models/Webhook';
import { WebhookEvent } from '../../../../modules/webhook/models/Event';

// Mock ApiClientService
vi.mock('../../../../common/services/ApiClientService', () => ({
  default: {
    get: vi.fn(),
    post: vi.fn(),
    put: vi.fn(),
    delete: vi.fn(),
  },
}));

import apiClientService from '../../../../common/services/ApiClientService';

describe('WebhookService', () => {
  const mockEvents: WebhookEvent[] = [
    { id: 1, name: 'Order Created' },
    { id: 2, name: 'Order Updated' },
  ];

  const mockWebhook: Webhook = {
    id: 1,
    payloadUrl: 'https://example.com/webhook',
    secret: 'my-secret-key',
    contentType: 'application/json',
    isActive: true,
    events: mockEvents,
  };

  const mockWebhookListResponse = {
    content: [mockWebhook],
    totalPages: 5,
    totalElements: 50,
    pageNo: 1,
    pageSize: 10,
  };

  beforeEach(() => {
    vi.clearAllMocks();
  });

  describe('getWebhooks', () => {
    const pageNo = 1;
    const pageSize = 10;

    it('should call API with correct URL', async () => {
      const mockResponse = { json: vi.fn().mockResolvedValue(mockWebhookListResponse) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      await getWebhooks(pageNo, pageSize);

      expect(apiClientService.get).toHaveBeenCalledTimes(1);
      expect(apiClientService.get).toHaveBeenCalledWith(
        '/api/webhook/backoffice/webhooks/paging?pageNo=1&pageSize=10'
      );
    });

    it('should return webhook list data on success', async () => {
      const mockResponse = { json: vi.fn().mockResolvedValue(mockWebhookListResponse) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      const result = await getWebhooks(pageNo, pageSize);

      expect(result).toEqual(mockWebhookListResponse);
      expect(result.content).toHaveLength(1);
      expect(result.totalPages).toBe(5);
    });

    it('should handle pageNo = 0', async () => {
      const mockResponse = { json: vi.fn().mockResolvedValue({ content: [] }) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      await getWebhooks(0, pageSize);

      expect(apiClientService.get).toHaveBeenCalledWith(
        '/api/webhook/backoffice/webhooks/paging?pageNo=0&pageSize=10'
      );
    });

    it('should handle pageSize = 0', async () => {
      const mockResponse = { json: vi.fn().mockResolvedValue({ content: [] }) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      await getWebhooks(pageNo, 0);

      expect(apiClientService.get).toHaveBeenCalledWith(
        '/api/webhook/backoffice/webhooks/paging?pageNo=1&pageSize=0'
      );
    });

    it('should handle large page numbers', async () => {
      const mockResponse = { json: vi.fn().mockResolvedValue({ content: [] }) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      await getWebhooks(100, pageSize);

      expect(apiClientService.get).toHaveBeenCalledWith(
        '/api/webhook/backoffice/webhooks/paging?pageNo=100&pageSize=10'
      );
    });

    it('should handle empty response', async () => {
      const emptyResponse = { content: [], totalPages: 0, totalElements: 0 };
      const mockResponse = { json: vi.fn().mockResolvedValue(emptyResponse) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      const result = await getWebhooks(pageNo, pageSize);

      expect(result.content).toEqual([]);
      expect(result.totalPages).toBe(0);
    });

    it('should handle API error', async () => {
      const error = new Error('Network error');
      (apiClientService.get as any).mockRejectedValue(error);

      await expect(getWebhooks(pageNo, pageSize)).rejects.toThrow('Network error');
    });
  });

  describe('createWebhook', () => {
    const newWebhook: Webhook = {
      id: 0,
      payloadUrl: 'https://new-url.com/webhook',
      secret: 'new-secret',
      contentType: 'application/json',
      isActive: true,
      events: [{ id: 1, name: 'Order Created' }],
    };

    it('should return response on success', async () => {
      const mockResponse = { status: 201, data: { id: 2 } };
      (apiClientService.post as any).mockResolvedValue(mockResponse);

      const result = await createWebhook(newWebhook);

      expect(result).toEqual(mockResponse);
    });

    it('should handle API error when creating webhook', async () => {
      const error = new Error('Webhook URL already exists');
      (apiClientService.post as any).mockRejectedValue(error);

      await expect(createWebhook(newWebhook)).rejects.toThrow('Webhook URL already exists');
    });

    it('should handle validation error', async () => {
      const error = new Error('Payload URL is required');
      (apiClientService.post as any).mockRejectedValue(error);

      await expect(createWebhook(newWebhook)).rejects.toThrow('Payload URL is required');
    });
  });

  describe('getWebhook', () => {
    const webhookId = 1;

    it('should call API with correct URL', async () => {
      const mockResponse = { json: vi.fn().mockResolvedValue(mockWebhook) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      await getWebhook(webhookId);

      expect(apiClientService.get).toHaveBeenCalledTimes(1);
      expect(apiClientService.get).toHaveBeenCalledWith('/api/webhook/backoffice/webhooks/1');
    });

    it('should return webhook data on success', async () => {
      const mockResponse = { json: vi.fn().mockResolvedValue(mockWebhook) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      const result = await getWebhook(webhookId);

      expect(result).toEqual(mockWebhook);
      expect(result.id).toBe(1);
      expect(result.payloadUrl).toBe('https://example.com/webhook');
      expect(result.secret).toBe('my-secret-key');
      expect(result.isActive).toBe(true);
      expect(result.events).toHaveLength(2);
    });

    it('should handle webhookId = 0', async () => {
      const mockResponse = { json: vi.fn().mockResolvedValue(null) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      await getWebhook(0);

      expect(apiClientService.get).toHaveBeenCalledWith('/api/webhook/backoffice/webhooks/0');
    });

    it('should handle API error when webhook not found', async () => {
      const error = new Error('Webhook not found');
      (apiClientService.get as any).mockRejectedValue(error);

      await expect(getWebhook(999)).rejects.toThrow('Webhook not found');
    });
  });

  describe('deleteWebhook', () => {
    const webhookId = 1;

    it('should call API with correct URL', async () => {
      (apiClientService.delete as any).mockResolvedValue({ status: 204 });

      await deleteWebhook(webhookId);

      expect(apiClientService.delete).toHaveBeenCalledTimes(1);
      expect(apiClientService.delete).toHaveBeenCalledWith('/api/webhook/backoffice/webhooks/1');
    });

    it('should return response directly when status is 204', async () => {
      const mockResponse = { status: 204 };
      (apiClientService.delete as any).mockResolvedValue(mockResponse);

      const result = await deleteWebhook(webhookId);

      expect(result).toEqual(mockResponse);
      expect(result.status).toBe(204);
    });

    it('should call response.json() when status is not 204', async () => {
      const mockResponse = {
        status: 200,
        json: vi.fn().mockResolvedValue({ message: 'Deleted successfully' }),
      };
      (apiClientService.delete as any).mockResolvedValue(mockResponse);

      const result = await deleteWebhook(webhookId);

      expect(mockResponse.json).toHaveBeenCalled();
      expect(result).toEqual({ message: 'Deleted successfully' });
    });

    it('should handle API error when webhook not found', async () => {
      const error = new Error('Webhook not found');
      (apiClientService.delete as any).mockRejectedValue(error);

      await expect(deleteWebhook(999)).rejects.toThrow('Webhook not found');
    });

    it('should handle API error', async () => {
      const error = new Error('Network error');
      (apiClientService.delete as any).mockRejectedValue(error);

      await expect(deleteWebhook(webhookId)).rejects.toThrow('Network error');
    });
  });

  describe('updateWebhook', () => {
    const webhookId = 1;
    const updatedWebhook: Webhook = {
      id: 1,
      payloadUrl: 'https://updated-url.com/webhook',
      secret: 'updated-secret',
      contentType: 'application/json',
      isActive: false,
      events: [{ id: 1, name: 'Order Created' }],
    };

    it('should call API with correct URL and body', async () => {
      (apiClientService.put as any).mockResolvedValue({ status: 204 });

      await updateWebhook(webhookId, updatedWebhook);

      expect(apiClientService.put).toHaveBeenCalledTimes(1);
      expect(apiClientService.put).toHaveBeenCalledWith(
        '/api/webhook/backoffice/webhooks/1',
        JSON.stringify(updatedWebhook)
      );
    });

    it('should return response directly when status is 204', async () => {
      const mockResponse = { status: 204 };
      (apiClientService.put as any).mockResolvedValue(mockResponse);

      const result = await updateWebhook(webhookId, updatedWebhook);

      expect(result).toEqual(mockResponse);
    });

    it('should call response.json() when status is not 204', async () => {
      const mockResponse = {
        status: 200,
        json: vi.fn().mockResolvedValue({ message: 'Updated successfully', id: 1 }),
      };
      (apiClientService.put as any).mockResolvedValue(mockResponse);

      const result = await updateWebhook(webhookId, updatedWebhook);

      expect(mockResponse.json).toHaveBeenCalled();
      expect(result).toEqual({ message: 'Updated successfully', id: 1 });
    });

    it('should handle API error when webhook not found', async () => {
      const error = new Error('Webhook not found');
      (apiClientService.put as any).mockRejectedValue(error);

      await expect(updateWebhook(999, updatedWebhook)).rejects.toThrow('Webhook not found');
    });

    it('should handle duplicate URL error', async () => {
      const error = new Error('Webhook URL already exists');
      (apiClientService.put as any).mockRejectedValue(error);

      await expect(updateWebhook(webhookId, updatedWebhook)).rejects.toThrow('Webhook URL already exists');
    });
  });

});