import { AddSampleModel } from '../models/AddSampleModel';
import apiClientService from '@/common/services/ApiClientService';

export async function addSampleData(addSampleModel: AddSampleModel) {
  const response = await apiClientService.post(
    `/api/sampledata/storefront/sampledata`,
    JSON.stringify(addSampleModel)
  );
  if (response.status >= 200 && response.status < 300) return await response.json();
  throw new Error(response.statusText);
}
