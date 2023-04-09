import styles from '@/styles/modules/search/SearchSort.module.css';

export interface SearchSortProps {}

export default function SearchSort(props: SearchSortProps) {
  return (
    <div className={styles['search-sort']}>
      <div className={styles['search-total']}>
        <span>409</span> products are found by keyword &quot;<span>sdf</span>&quot;
      </div>

      <div className={styles['search-action']}>
        <div>Sort by</div>
        <div className={styles['search-action-sort']}>
          <div className={styles['search-current']}>
            <span>Default</span> <i className="bi bi-chevron-down"></i>
          </div>
          <div className={styles['search-dropdown']}>
            <div className={styles['search-dropdown-item']}>Default</div>
            <div className={styles['search-dropdown-item']}>Price: Low to High</div>
            <div className={styles['search-dropdown-item']}>Price: High to Low</div>
          </div>
        </div>
      </div>
    </div>
  );
}
