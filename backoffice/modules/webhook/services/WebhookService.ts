import { Webhook } from '../models/Webhook';

export async function getWebhooks(pageNo: number, pageSize: number) {
  const url = `/api/webhook/backoffice/webhooks/paging?pageNo=${pageNo}&pageSize=${pageSize}`;
  const response = await fetch(url);
  return await response.json();
}

export async function getWebhook(id: number): Promise<Webhook> {
  const response = await fetch('/api/webhook/backoffice/webhooks/' + id);
  return await response.json();
}

export async function createWebhook(webhook: Webhook) {
  const response = await fetch('/api/webhook/backoffice/webhooks', {
    method: 'POST',
    body: JSON.stringify(webhook),
    headers: { 'Content-type': 'application/json; charset=UTF-8' },
  });
  return response;
}
export async function updateWebhook(id: number, webhook: Webhook) {
  const response = await fetch('/api/webhook/backoffice/webhooks/' + id, {
    method: 'PUT',
    body: JSON.stringify(webhook),
    headers: { 'Content-type': 'application/json; charset=UTF-8' },
  });
  if (response.status === 204) return response;
  else return await response.json();
}
export async function deleteWebhook(id: number) {
  const response = await fetch('/api/webhook/backoffice/webhooks/' + id, {
    method: 'DELETE',
    headers: { 'Content-type': 'application/json; charset=UTF-8' },
  });
  if (response.status === 204) return response;
  else return await response.json();
}
