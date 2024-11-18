import React from 'react';
import ReactPaginate from 'react-paginate';
import { Form } from 'react-bootstrap';
import { DEFAULT_PAGE_SIZE } from '@constants/Common';

interface PaginationProps {
  pageNo: number;
  totalPage: number;
  itemsPerPage: number;
  goToPage?: string;
  onPageChange: (selectedItem: { selected: number }) => void;
  onItemsPerPageChange?: (e: React.ChangeEvent<HTMLSelectElement>) => void;
  onGoToPageChange?: (e: React.ChangeEvent<HTMLInputElement>) => void;
  onGoToPageSubmit?: () => void;
  showHelpers?: boolean;
}

function Pagination({
  pageNo,
  totalPage,
  itemsPerPage = DEFAULT_PAGE_SIZE,
  goToPage,
  onPageChange,
  onItemsPerPageChange,
  onGoToPageChange,
  onGoToPageSubmit,
  showHelpers = true,
}: Readonly<PaginationProps>) {
  return (
    <div>
      <ReactPaginate
        forcePage={pageNo}
        previousLabel={'Previous'}
        nextLabel={'Next'}
        pageCount={totalPage}
        onPageChange={onPageChange}
        containerClassName={'pagination-container'}
        previousClassName={'previous-btn'}
        nextClassName={'next-btn'}
        disabledClassName={'pagination-disabled'}
        activeClassName={'pagination-active'}
      />
      {showHelpers && (
        <div className="pagination-helper mt-3 mb-3">
          <div className="pagination-tool me-5">
            <p>Go to</p>
            <Form.Control
              type="number"
              value={goToPage}
              onChange={(e) => onGoToPageChange?.(e as React.ChangeEvent<HTMLInputElement>)}
              onKeyDown={(e) => {
                if (e.key === 'Enter' && onGoToPageSubmit) {
                  onGoToPageSubmit();
                }
              }}
            />
          </div>
          <div className="pagination-tool">
            <p>Show</p>
            <Form.Select
              value={itemsPerPage}
              onChange={(e) =>
                onItemsPerPageChange?.(e as unknown as React.ChangeEvent<HTMLSelectElement>)
              }
            >
              <option value="5">5</option>
              <option value="10">10</option>
              <option value="15">15</option>
              <option value="20">20</option>
            </Form.Select>
          </div>
        </div>
      )}
    </div>
  );
}

export default Pagination;
