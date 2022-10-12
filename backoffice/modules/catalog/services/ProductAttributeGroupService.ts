import { ProductAttributeGroup } from '../models/ProductAttributeGroup';

export async function getProductAttributeGroups(): Promise<ProductAttributeGroup[]> {
  const response = await fetch('/api/product/backoffice/product-attribute-groups');
  return await response.json();
}
