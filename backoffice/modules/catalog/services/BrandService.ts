import {} from '../models/Brand'

export async function getBrands() {
    const response = await fetch("/api/product/backoffice/brands");
    return await response.json();
}