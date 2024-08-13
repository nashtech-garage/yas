export async function addSampleData(): Promise<any> {
  const response = await fetch(`/api/sampledata/storefront/sampledata`, {
    method: 'POST',
    body: null,
    headers: { 'Content-type': 'application/json; charset=UTF-8' },
  });
  if (response.status >= 200 && response.status < 300) return await response;
  return Promise.reject(response);
}