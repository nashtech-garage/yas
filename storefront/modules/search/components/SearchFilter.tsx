import styles from '@/styles/modules/search/SearchFilter.module.css';

export interface SearchFilterProps {}

export default function SearchFilter(_props: SearchFilterProps) {
  return (
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
  );
}
