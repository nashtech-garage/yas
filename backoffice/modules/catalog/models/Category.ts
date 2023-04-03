export type Category = {
  id: number;
  name: string;
  description: string;
  slug: string;
  parentId: number;
  metaKeywords: string;
  metaDescription: string;
  displayOrder: number;
  isPublish: boolean;
  imageId?: number;
  categoryImage?: {
    id: number;
    url: string;
  };
};
