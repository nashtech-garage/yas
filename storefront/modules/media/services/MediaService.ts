import { Media } from '../models/Media';
import apiClientService from '@/common/services/ApiClientService';

export const getMediaById = async (id: number): Promise<Media> => {
  const response = await apiClientService.get(`/api/media/medias/${id}`);

  if (response.status >= 200 && response.status < 300) return await response.json();
  throw response;
};
