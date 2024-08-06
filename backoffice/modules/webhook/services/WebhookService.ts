import { Webhook } from '../models/Webhook';
import apiClientService from '@commonServices/ApiClientService';

const baseUrl = '/api/webhook/backoffice/webhooks';

export async function getWebhooks(pageNo: number, pageSize: number) {
  const url = `${baseUrl}/paging?pageNo=${pageNo}&pageSize=${pageSize}`;
  return (await apiClientService.get(url)).json();
}

export async function createWebhook(webhook: Webhook) {
  return await apiClientService.post(baseUrl, JSON.stringify(webhook));
}

export async function getWebhook(id: number) {
  const url = `${baseUrl}/${id}`;
  return (await apiClientService.get(url)).json();
}

export async function deleteWebhook(id: number) {
  const url = `${baseUrl}/${id}`;
  const response = await apiClientService.delete(url);
  if (response.status === 204) return response;
  else return await response.json();
}

export async function updateWebhook(id: number, webhook: Webhook) {
  const url = `${baseUrl}/${id}`;
  const response = await apiClientService.put(url, JSON.stringify(webhook));
  if (response.status === 204) return response;
  else return await response.json();
}
