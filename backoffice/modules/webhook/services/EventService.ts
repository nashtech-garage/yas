import { WebhookEvent } from '../models/Event';


export async function getEvents(): Promise<WebhookEvent[]> {
  const response = await fetch('/api/webhook/backoffice/events');
  return await response.json();
}
