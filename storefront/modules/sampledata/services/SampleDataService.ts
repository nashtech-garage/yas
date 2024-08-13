import { AddSampleModel } from '../models/AddSampleModel';

export async function addSampleData(addSampleModel: AddSampleModel): Promise<AddSampleModel> {
  const response = await fetch(`/api/sampledata/storefront/sampledata`, {
    method: 'POST',
    body: JSON.stringify(addSampleModel),
    headers: { 'Content-type': 'application/json; charset=UTF-8' },
  });
  if (response.status >= 200 && response.status < 300) return await response.json();
  return Promise.reject(new Error("Can't add sample data"));
}
