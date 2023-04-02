export async function getStatesOrProvinces(id: number) {
    const response = await(fetch(`/api/location/storefront/state-or-provinces/${id}`))
    return response.json();
}