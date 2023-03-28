export type Category = {
  id: number;
  name: string;
  description: string;
  slug: string;
  categoryImage?: {
    id: number;
    url: string;
  };
};
