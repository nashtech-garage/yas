import { Media } from '../models/Media';

export const getMediaById = async (id: number): Promise<Media> => {
  const response = await fetch(`${process.env.NEXT_PUBLIC_API_BASE_PATH}/media/medias/${id}`, {
    method: 'GET',
    headers: { 'Content-type': 'application/json; charset=UTF-8' },
  });

  if (response.status >= 200 && response.status < 300) return await response.json();
  return Promise.reject(response);
};
