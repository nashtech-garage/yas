import { useRouter } from 'next/router';
import { useEffect, useMemo, useState } from 'react';
import { Container } from 'react-bootstrap';

import { Category as CategoryModel } from '../../catalog/models/Category';
import { getCategories } from '../../catalog/services/CategoryService';

const Category = () => {
  const router = useRouter();

  const [categories, setCategories] = useState<CategoryModel[]>([]);
  const [currentPage, setCurrentPage] = useState<number>(1);

  const itemsPerPage = 20;
  const totalPages = useMemo(
    () => Math.ceil(categories.length / itemsPerPage),
    [categories.length]
  );
  const startIndex = useMemo(() => (currentPage - 1) * itemsPerPage, [currentPage]);
  const endIndex = useMemo(() => startIndex + itemsPerPage, [startIndex]);

  const currentItems: CategoryModel[] = categories.slice(startIndex, endIndex);
  const chunkedItems = [];
  for (let i = 0; i < currentItems.length; i += 2) {
    chunkedItems.push(currentItems.slice(i, i + 2));
  }

  useEffect(() => {
    getCategories().then((data) => {
      setCategories([...data]);
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

  const goToPage = (pageNumber: number) => {
    setCurrentPage(pageNumber);
  };

  return (
    <Container className="category-container">
      <div className="title">Categories</div>
      <div className="list-categories">
        {currentPage > 1 && (
          <div className="btn-change-page prev" onClick={() => goToPage(currentPage - 1)}>
            <i className="bi bi-chevron-left"></i>
          </div>
        )}
        <ul>
          {chunkedItems.map((chunk, index) => (
            <li key={index}>
              {chunk.map((item) => (
                <div className="category" key={item.id} onClick={() => handleClick(item.slug)}>
                  <div className="image-wrapper">
                    {item.categoryImage ? (
                      <div
                        className="image"
                        style={{
                          backgroundImage: 'url(' + item.categoryImage.url + ')',
                        }}
                      ></div>
                    ) : (
                      <div className="image">No image</div>
                    )}
                  </div>
                  <p style={{ fontSize: '14px' }}>{item.name}</p>
                </div>
              ))}
            </li>
          ))}
        </ul>
        {currentPage < totalPages && (
          <div className="btn-change-page next" onClick={() => goToPage(currentPage + 1)}>
            <i className="bi bi-chevron-right"></i>
          </div>
        )}
      </div>
    </Container>
  );
};

export default Category;
