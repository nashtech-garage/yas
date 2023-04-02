export async function getDistricts(id: number) {
    const response = await(fetch(`/api/location/storefront/district/${id}`))
    return response.json();
}