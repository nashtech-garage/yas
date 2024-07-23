import { Media } from '../models/Media';
import apiClientService from '@commonServices/ApiClientService';
import { MEDIA_ENDPOINT } from '@constants/Endpoints';

const baseUrl = MEDIA_ENDPOINT;

export async function uploadMedia(image: File): Promise<Media> {
  const body = new FormData();
  body.append('multipartFile', image);
  const response = await apiClientService.post(baseUrl, JSON.stringify(body));
  if (response.status >= 200 && response.status < 300) return await response.json();
  return Promise.reject(response);
}
