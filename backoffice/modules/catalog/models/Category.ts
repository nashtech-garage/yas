export type Category = {
  id : number;
  name: string;
  description: string;
  slug: string;
  parentId: number;
  parentName: string;
  metaKeywords: string;
  metaDescription: string;
  displayOrder: number;
};