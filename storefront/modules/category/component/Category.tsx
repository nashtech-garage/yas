import { useRouter } from 'next/router';
import { useEffect, useState } from 'react';
import { Category } from '../../catalog/models/Category';
import { getCategories } from '../../catalog/services/CategoryService';

const Category = () => {
  const router = useRouter();
  const [categories, setCategories] = useState<Category[]>([]);
  useEffect(() => {
    getCategories().then((data) => {
      setCategories(data);
    });
  }, []);

  const handleClick = (slug: string) => {
    router.push({
      pathname: '/products',
      query: {
        categorySlug: slug,
      },
    });
  };

  return (
    <>
      <div className="container-fluid" style={{ minHeight: '50px' }}>
        <h4>Categories</h4>
        <div className="row">
          {categories.map((category) => (
            <div
              key={category.id}
              className="col-2 p-0"
              style={{ border: '1px solid #eaeaea', height: '200px' }}
              onClick={() => {
                handleClick(category.slug);
              }}
            >
              <div className="justify-content-center d-flex">
                {category.categoryImage ? (
                  <div
                    className="mt-4 mb-3"
                    style={{
                      border: '1px solid #eaeaea',
                      height: '120px',
                      width: '120px',
                      backgroundImage: 'url(' + category.categoryImage.url + ')',
                      backgroundSize: 'cover',
                      backgroundPosition: 'center',
                    }}
                  ></div>
                ) : (
                  <div
                    className="mt-4 mb-3"
                    style={{
                      border: '1px solid #eaeaea',
                      height: '120px',
                      width: '120px',
                    }}
                  ></div>
                )}
              </div>
              <p className="text-center" style={{ fontSize: '18px' }}>
                {category.name}
              </p>
            </div>
          ))}
        </div>
      </div>
    </>
  );
};
export default Category;
