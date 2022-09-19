
import { Brand } from "../models/Brand"
import {Product} from "../models/Product";

export async function getBrands(): Promise<Brand[]> {
    const response = await fetch('/api/product/backoffice/brands');
    return await response.json();
}

export async function createBrand(brand: Brand): Promise<Brand> {
    const response = await fetch('/api/product/backoffice/brands', {
        method: 'POST',
        body: JSON.stringify(brand),
        headers: {"Content-type": "application/json; charset=UTF-8"}
    })
    return await response.json();
}
export async function getBrand(id: number): Promise<Product> {
    const response = await fetch('/api/product/backoffice/brands/' + id);
    return await response.json();
}