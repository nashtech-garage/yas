import { WebhookEvent } from '../models/Event';
import apiClientService from '@commonServices/ApiClientService';

const baseUrl = '/api/webhook/backoffice/events';

export async function getEvents(): Promise<WebhookEvent[]> {
  const response = await fetch('/api/webhook/backoffice/events');
  return (await apiClientService.get(baseUrl)).json();
}
