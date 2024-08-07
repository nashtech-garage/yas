import { WebhookEvent } from './Event';

export type Webhook = {
  id: number;
  payloadUrl: string;
  secret: string;
  contentType: string;
  isActive: boolean;
  events: WebhookEvent[];
};
