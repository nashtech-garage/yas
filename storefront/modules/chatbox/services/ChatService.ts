import apiClientService from '@/common/services/ApiClientService';

const RECOMMENDATION_CHAT_API = `/api/recommendation/cs/chat`;

export async function chat(message: string): Promise<string> {
  try {
    const response = await apiClientService.get(`${RECOMMENDATION_CHAT_API}?message=${message}`);

    if (response.status >= 400 && response.status < 600) {
      throw new Error(`Error ${response.status}: ${response.statusText}`);
    }

    return await response.text();
  } catch (error) {
    console.error('Error in chat API:', error);
    throw new Error('Failed to fetch chat recommendation. Please try again later.');
  }
}
