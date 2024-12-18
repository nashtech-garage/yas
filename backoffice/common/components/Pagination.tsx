import React from 'react';
import ReactPaginate from 'react-paginate';
import { Form } from 'react-bootstrap';
import { PAGE_SIZE_OPTION } from '@commonHooks/usePagination';
import styles from 'styles/Pagination.module.css';

interface PaginationControls {
  itemsPerPage?: {
    value: number;
    onChange: (e: React.ChangeEvent<HTMLSelectElement>) => void;
  };
  goToPage?: {
    value: string;
    onChange: (e: React.ChangeEvent<HTMLInputElement>) => void;
    onSubmit: () => void;
  };
}

interface PaginationProps {
  pageNo: number;
  totalPage: number;
  paginationControls?: PaginationControls;
  onPageChange: (selectedItem: { selected: number }) => void;
  pageSizeOption?: number[];
}

function Pagination({
  pageNo,
  totalPage,
  paginationControls,
  onPageChange,
  pageSizeOption = PAGE_SIZE_OPTION,
}: Readonly<PaginationProps>) {
  return (
    <div>
      <ReactPaginate
        forcePage={pageNo}
        previousLabel="Previous"
        nextLabel="Next"
        pageCount={totalPage}
        onPageChange={onPageChange}
        containerClassName="pagination-container"
        previousClassName="previous-btn"
        nextClassName="next-btn"
        disabledClassName="pagination-disabled"
        activeClassName="pagination-active"
      />
      {paginationControls && (
        <div className={`${styles['pagination-helper']} mt-3 mb-3`}>
          {paginationControls.goToPage && (
            <div
              className={`${styles['pagination-tool']} ${
                paginationControls.itemsPerPage ? 'me-5' : ''
              }`}
            >
              <p>Go to</p>
              <Form.Control
                type="number"
                value={paginationControls.goToPage.value || ''}
                placeholder=""
                aria-label="Go to page number"
                onChange={paginationControls.goToPage.onChange}
                onKeyDown={(e) => e.key === 'Enter' && paginationControls.goToPage?.onSubmit()}
              />
            </div>
          )}
          {paginationControls.itemsPerPage && (
            <div className={styles['pagination-tool']}>
              <p>Show</p>
              <Form.Select
                value={paginationControls.itemsPerPage.value}
                onChange={paginationControls.itemsPerPage.onChange}
                aria-label="Select items per page"
              >
                {pageSizeOption.map((size) => (
                  <option key={size} value={size}>
                    {size}
                  </option>
                ))}
              </Form.Select>
            </div>
          )}
        </div>
      )}
    </div>
  );
}

export default Pagination;
