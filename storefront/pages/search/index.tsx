import { Col, Container, Row } from 'react-bootstrap';

import BreadcrumbComponent from '@/common/components/BreadcrumbComponent';
import ProductCard from '@/common/components/ProductCard';
import { BreadcrumbModel } from '@/modules/breadcrumb/model/BreadcrumbModel';
import SearchFilter from '@/modules/search/components/SearchFilter';

import styles from '@/styles/modules/search/SearchPage.module.css';
import SearchSort from '@/modules/search/components/SearchSort';

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
    <div className={styles['search-page']}>
      <Container className={styles['search-container']}>
        <BreadcrumbComponent props={crumb} />

        <div className={styles['search-wrapper']}>
          <SearchFilter />

          <div className={styles['search-result']}>
            <SearchSort />

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
    </div>
  );
};

export default SearchPage;
