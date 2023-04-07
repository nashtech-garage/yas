import { Col, Container, Row } from 'react-bootstrap';

import BreadcrumbComponent from '@/common/components/BreadcrumbComponent';
import ProductCard from '@/common/components/ProductCard';
import { BreadcrumbModel } from '@/modules/breadcrumb/model/BreadcrumbModel';

import styles from '@/styles/SearchPage.module.css';

const SearchPage = () => {
  const crumb: BreadcrumbModel[] = [
    {
      pageName: 'Home',
      url: '/',
    },
    {
      pageName: `Search result "${'abcdxy'}"`,
      url: '#',
    },
  ];

  return (
    <Container className={styles['search-container']}>
      <BreadcrumbComponent props={crumb} />

      <div className={styles['search-wrapper']}>
        <div className={styles['search-filter']}>
          <div className={styles['title']}>
            <i className="bi bi-funnel"></i>
            <span>Search filter</span>
          </div>
          <div className={styles['filter-group']}>
            <div className={styles['filter-group__title']}>Category</div>
            <div className={styles['filter-group__list']}>
              <div className={styles['filter-group__list-item']}>
                <input type="checkbox" id="category-1" />
                <label htmlFor="category-1">Category 1</label>
              </div>
              <div className={styles['filter-group__list-item']}>
                <input type="checkbox" id="category-2" />
                <label htmlFor="category-2">Category 2</label>
              </div>
            </div>
          </div>
          <div className={styles['filter-group']}>
            <div className={styles['filter-group__title']}>Price range</div>
            <div className={styles['filter-group__range']}>
              <input type="number" min={'1'} placeholder="₫ From" />
              <span>-</span>
              <input type="number" min={'1'} placeholder="₫ To" />
            </div>
            <button className={styles['filter-group__button']}>Apply</button>
          </div>
        </div>

        <div className={styles['search-result']}>
          <div className={styles['search-result__sort']}>
            <div className={styles['search-result__total']}>
              <span>409</span> products are found by keyword &quot;<span>sdf</span>&quot;
            </div>

            <div className={styles['search-result__action']}>
              <div>Sort by</div>
              <div className={styles['search-result__action-sort']}>
                <div className={styles['search-result__current']}>
                  <span>Default</span> <i className="bi bi-chevron-down"></i>
                </div>
                <div className={styles['search-result__dropdown']}>
                  <div className={styles['search-result__dropdown-item']}>Default</div>
                  <div className={styles['search-result__dropdown-item']}>Price: Low to High</div>
                  <div className={styles['search-result__dropdown-item']}>Price: High to Low</div>
                </div>
              </div>
            </div>
          </div>
          <Row xs={4} xl={5} className={styles['search-result__list']}>
            <Col>
              <ProductCard
                className={['products-page']}
                product={{
                  id: 1,
                  name: 'Product 1',
                  price: 100000,
                  thumbnailUrl: 'https://picsum.photos/200/300',
                  slug: 'product-1',
                }}
              />
            </Col>
            <Col>
              <ProductCard
                className={['products-page']}
                product={{
                  id: 1,
                  name: 'Product 1',
                  price: 100000,
                  thumbnailUrl: 'https://picsum.photos/200/300',
                  slug: 'product-1',
                }}
              />
            </Col>
            <Col>
              <ProductCard
                className={['products-page']}
                product={{
                  id: 1,
                  name: 'Product 1',
                  price: 100000,
                  thumbnailUrl: 'https://picsum.photos/200/300',
                  slug: 'product-1',
                }}
              />
            </Col>
            <Col>
              <ProductCard
                className={['products-page']}
                product={{
                  id: 1,
                  name: 'Product 1',
                  price: 100000,
                  thumbnailUrl: 'https://picsum.photos/200/300',
                  slug: 'product-1',
                }}
              />
            </Col>
          </Row>
        </div>
      </div>
    </Container>
  );
};

export default SearchPage;
