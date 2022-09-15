import {} from '../models/Brand'

export async function getBrands() {
    const response = await fetch("/api/brand//backoffice/brands");
    return await response.json();
}