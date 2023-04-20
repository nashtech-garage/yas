export async function getDistricts(id: number) {
  const response = await fetch(`/api/location/backoffice/district/${id}`);
  return response.json();
}
