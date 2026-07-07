import { describe, it, expect } from 'vitest';
import type { Webhook } from '../../../../modules/webhook/models/Webhook';
import type { WebhookEvent } from '../../../../modules/webhook/models/Event';

describe('Webhook Type', () => {
  const mockEvents: WebhookEvent[] = [
    { id: 1, name: 'Order Created' },
    { id: 2, name: 'Order Updated' },
    { id: 3, name: 'Order Cancelled' },
  ];

  describe('Type structure', () => {
    it('should have correct property types', () => {
      const webhook: Webhook = {
        id: 1,
        payloadUrl: 'https://example.com/webhook',
        secret: 'my-secret-key',
        contentType: 'application/json',
        isActive: true,
        events: mockEvents,
      };

      expect(webhook).toHaveProperty('id');
      expect(webhook).toHaveProperty('payloadUrl');
      expect(webhook).toHaveProperty('secret');
      expect(webhook).toHaveProperty('contentType');
      expect(webhook).toHaveProperty('isActive');
      expect(webhook).toHaveProperty('events');
      
      expect(typeof webhook.id).toBe('number');
      expect(typeof webhook.payloadUrl).toBe('string');
      expect(typeof webhook.secret).toBe('string');
      expect(typeof webhook.contentType).toBe('string');
      expect(typeof webhook.isActive).toBe('boolean');
      expect(Array.isArray(webhook.events)).toBe(true);
    });

    it('should accept valid webhook object', () => {
      const webhook: Webhook = {
        id: 10,
        payloadUrl: 'https://api.example.com/hook',
        secret: 'secret123',
        contentType: 'application/json',
        isActive: false,
        events: [{ id: 1, name: 'Payment Success' }],
      };

      expect(webhook.id).toBe(10);
      expect(webhook.payloadUrl).toBe('https://api.example.com/hook');
      expect(webhook.secret).toBe('secret123');
      expect(webhook.contentType).toBe('application/json');
      expect(webhook.isActive).toBe(false);
      expect(webhook.events).toHaveLength(1);
    });
  });

  describe('Events array', () => {
    it('should accept empty events array', () => {
      const webhook: Webhook = {
        id: 1,
        payloadUrl: 'https://example.com/webhook',
        secret: 'secret',
        contentType: 'application/json',
        isActive: true,
        events: [],
      };

      expect(webhook.events).toEqual([]);
      expect(webhook.events).toHaveLength(0);
    });

    it('should accept multiple events', () => {
      const webhook: Webhook = {
        id: 1,
        payloadUrl: 'https://example.com/webhook',
        secret: 'secret',
        contentType: 'application/json',
        isActive: true,
        events: mockEvents,
      };

      expect(webhook.events).toHaveLength(3);
      expect(webhook.events[0].name).toBe('Order Created');
      expect(webhook.events[1].name).toBe('Order Updated');
      expect(webhook.events[2].name).toBe('Order Cancelled');
    });

    it('should allow accessing events by index', () => {
      const webhook: Webhook = {
        id: 1,
        payloadUrl: 'https://example.com/webhook',
        secret: 'secret',
        contentType: 'application/json',
        isActive: true,
        events: mockEvents,
      };

      expect(webhook.events[0].id).toBe(1);
      expect(webhook.events[1].id).toBe(2);
      expect(webhook.events[2].id).toBe(3);
    });
  });

  describe('Field values', () => {
    it('should accept various URL formats', () => {
      const urls = [
        'https://example.com/webhook',
        'http://localhost:3000/api/webhook',
        'https://api.example.com/v1/webhooks/123',
        'https://example.com/webhook?param=value',
      ];

      urls.forEach(url => {
        const webhook: Webhook = {
          id: 1,
          payloadUrl: url,
          secret: 'secret',
          contentType: 'application/json',
          isActive: true,
          events: [],
        };
        expect(webhook.payloadUrl).toBe(url);
      });
    });

    it('should accept various secret formats', () => {
      const secrets = [
        'simple-secret',
        'complex-secret-123!@#',
        'a'.repeat(100),
        '',
      ];

      secrets.forEach(secret => {
        const webhook: Webhook = {
          id: 1,
          payloadUrl: 'https://example.com',
          secret: secret,
          contentType: 'application/json',
          isActive: true,
          events: [],
        };
        expect(webhook.secret).toBe(secret);
      });
    });

    it('should accept various content types', () => {
      const contentTypes = [
        'application/json',
        'application/xml',
        'text/plain',
        'application/x-www-form-urlencoded',
      ];

      contentTypes.forEach(contentType => {
        const webhook: Webhook = {
          id: 1,
          payloadUrl: 'https://example.com',
          secret: 'secret',
          contentType: contentType,
          isActive: true,
          events: [],
        };
        expect(webhook.contentType).toBe(contentType);
      });
    });

    it('should accept id as 0', () => {
      const webhook: Webhook = {
        id: 0,
        payloadUrl: 'https://example.com',
        secret: 'secret',
        contentType: 'application/json',
        isActive: true,
        events: [],
      };

      expect(webhook.id).toBe(0);
    });

    it('should accept negative id (if applicable)', () => {
      const webhook: Webhook = {
        id: -1,
        payloadUrl: 'https://example.com',
        secret: 'secret',
        contentType: 'application/json',
        isActive: true,
        events: [],
      };

      expect(webhook.id).toBe(-1);
    });

    it('should accept large numbers for id', () => {
      const webhook: Webhook = {
        id: 999999,
        payloadUrl: 'https://example.com',
        secret: 'secret',
        contentType: 'application/json',
        isActive: true,
        events: [],
      };

      expect(webhook.id).toBe(999999);
    });
  });

  describe('Object creation', () => {
    it('should create webhook with all required fields', () => {
      const webhookData = {
        id: 1,
        payloadUrl: 'https://example.com/webhook',
        secret: 'my-secret',
        contentType: 'application/json',
        isActive: true,
        events: mockEvents,
      };

      const webhook: Webhook = { ...webhookData };

      expect(webhook).toEqual(webhookData);
    });

    it('should allow partial webhook for updates', () => {
      const partialWebhook: Partial<Webhook> = {
        payloadUrl: 'https://new-url.com/webhook',
        isActive: false,
      };

      expect(partialWebhook.payloadUrl).toBe('https://new-url.com/webhook');
      expect(partialWebhook.isActive).toBe(false);
      expect(partialWebhook.id).toBeUndefined();
      expect(partialWebhook.secret).toBeUndefined();
    });

    it('should allow updating only events', () => {
      const eventsUpdate: Partial<Webhook> = {
        events: [{ id: 5, name: 'New Event' }],
      };

      expect(eventsUpdate.events).toHaveLength(1);
      expect(eventsUpdate.events?.[0].name).toBe('New Event');
      expect(eventsUpdate.id).toBeUndefined();
    });

    it('should allow updating only isActive status', () => {
      const statusUpdate: Partial<Webhook> = {
        isActive: false,
      };

      expect(statusUpdate.isActive).toBe(false);
      expect(statusUpdate.payloadUrl).toBeUndefined();
    });
  });

  describe('Array handling', () => {
    it('should create array of webhooks', () => {
      const webhooks: Webhook[] = [
        {
          id: 1,
          payloadUrl: 'https://example.com/webhook1',
          secret: 'secret1',
          contentType: 'application/json',
          isActive: true,
          events: [{ id: 1, name: 'Order Created' }],
        },
        {
          id: 2,
          payloadUrl: 'https://example.com/webhook2',
          secret: 'secret2',
          contentType: 'application/json',
          isActive: false,
          events: [{ id: 2, name: 'Order Updated' }],
        },
      ];

      expect(webhooks).toHaveLength(2);
      expect(webhooks[0].isActive).toBe(true);
      expect(webhooks[1].isActive).toBe(false);
    });

    it('should filter webhooks by isActive', () => {
      const webhooks: Webhook[] = [
        { id: 1, payloadUrl: 'url1', secret: 's1', contentType: 'json', isActive: true, events: [] },
        { id: 2, payloadUrl: 'url2', secret: 's2', contentType: 'json', isActive: false, events: [] },
        { id: 3, payloadUrl: 'url3', secret: 's3', contentType: 'json', isActive: true, events: [] },
      ];

      const activeWebhooks = webhooks.filter(w => w.isActive);
      
      expect(activeWebhooks).toHaveLength(2);
      expect(activeWebhooks.every(w => w.isActive)).toBe(true);
    });

    it('should find webhook by id', () => {
      const webhooks: Webhook[] = [
        { id: 1, payloadUrl: 'url1', secret: 's1', contentType: 'json', isActive: true, events: [] },
        { id: 2, payloadUrl: 'url2', secret: 's2', contentType: 'json', isActive: false, events: [] },
      ];

      const webhook = webhooks.find(w => w.id === 2);
      
      expect(webhook).toBeDefined();
      expect(webhook?.id).toBe(2);
    });
  });

  describe('Utility functions', () => {
    // Validate webhook
    const isValidWebhook = (webhook: any): webhook is Webhook => {
      return (
        typeof webhook.id === 'number' &&
        typeof webhook.payloadUrl === 'string' &&
        typeof webhook.secret === 'string' &&
        typeof webhook.contentType === 'string' &&
        typeof webhook.isActive === 'boolean' &&
        Array.isArray(webhook.events) &&
        webhook.events.every((event: any) => 
          typeof event.id === 'number' && typeof event.name === 'string'
        )
      );
    };

    it('should validate correct webhook object', () => {
      const webhook = {
        id: 1,
        payloadUrl: 'https://example.com',
        secret: 'secret',
        contentType: 'application/json',
        isActive: true,
        events: [{ id: 1, name: 'Event' }],
      };

      expect(isValidWebhook(webhook)).toBe(true);
    });

    it('should reject invalid webhook - missing field', () => {
      const invalidWebhook = {
        id: 1,
        payloadUrl: 'https://example.com',
        secret: 'secret',
        contentType: 'application/json',
        isActive: true,
        // missing events
      };

      expect(isValidWebhook(invalidWebhook)).toBe(false);
    });

    it('should reject invalid webhook - wrong events type', () => {
      const invalidWebhook = {
        id: 1,
        payloadUrl: 'https://example.com',
        secret: 'secret',
        contentType: 'application/json',
        isActive: true,
        events: 'not-an-array',
      };

      expect(isValidWebhook(invalidWebhook)).toBe(false);
    });

    // Create webhook from API
    const createWebhookFromApi = (apiData: any): Webhook => {
      return {
        id: apiData.id,
        payloadUrl: apiData.payload_url,
        secret: apiData.secret,
        contentType: apiData.content_type,
        isActive: apiData.is_active,
        events: apiData.events || [],
      };
    };

    it('should convert API response to Webhook', () => {
      const apiResponse = {
        id: 10,
        payload_url: 'https://api.example.com/webhook',
        secret: 'api-secret',
        content_type: 'application/json',
        is_active: true,
        events: [{ id: 1, name: 'Order Created' }],
      };

      const webhook = createWebhookFromApi(apiResponse);

      expect(webhook).toEqual({
        id: 10,
        payloadUrl: 'https://api.example.com/webhook',
        secret: 'api-secret',
        contentType: 'application/json',
        isActive: true,
        events: [{ id: 1, name: 'Order Created' }],
      });
    });

    it('should handle missing events in API response', () => {
      const apiResponse = {
        id: 11,
        payload_url: 'https://api.example.com/webhook',
        secret: 'api-secret',
        content_type: 'application/json',
        is_active: false,
      };

      const webhook = createWebhookFromApi(apiResponse);

      expect(webhook.events).toEqual([]);
    });
  });

  describe('Edge cases', () => {
    it('should handle webhook with empty string URL', () => {
      const webhook: Webhook = {
        id: 1,
        payloadUrl: '',
        secret: 'secret',
        contentType: 'application/json',
        isActive: true,
        events: [],
      };

      expect(webhook.payloadUrl).toBe('');
    });

    it('should handle webhook with empty secret', () => {
      const webhook: Webhook = {
        id: 1,
        payloadUrl: 'https://example.com',
        secret: '',
        contentType: 'application/json',
        isActive: true,
        events: [],
      };

      expect(webhook.secret).toBe('');
    });

    it('should handle webhook with very long URL', () => {
      const longUrl = 'https://' + 'a'.repeat(500) + '.com/webhook';
      const webhook: Webhook = {
        id: 1,
        payloadUrl: longUrl,
        secret: 'secret',
        contentType: 'application/json',
        isActive: true,
        events: [],
      };

      expect(webhook.payloadUrl).toBe(longUrl);
    });

    it('should handle webhook with very long secret', () => {
      const longSecret = 'b'.repeat(1000);
      const webhook: Webhook = {
        id: 1,
        payloadUrl: 'https://example.com',
        secret: longSecret,
        contentType: 'application/json',
        isActive: true,
        events: [],
      };

      expect(webhook.secret).toBe(longSecret);
    });

    it('should handle webhook with many events', () => {
      const manyEvents = Array.from({ length: 100 }, (_, i) => ({
        id: i,
        name: `Event ${i}`,
      }));

      const webhook: Webhook = {
        id: 1,
        payloadUrl: 'https://example.com',
        secret: 'secret',
        contentType: 'application/json',
        isActive: true,
        events: manyEvents,
      };

      expect(webhook.events).toHaveLength(100);
    });
  });
});