import { useState, useMemo, useCallback } from 'react';
import {
  DEFAULT_PAGE_NUMBER,
  DEFAULT_PAGE_SIZE,
  DEFAULT_PAGE_SIZE_OPTION,
} from '@constants/Common';

interface UsePaginationProps {
  initialPageNo?: number;
  initialItemsPerPage?: number;
}

const usePagination = (props: UsePaginationProps = {}) => {
  const { initialPageNo = DEFAULT_PAGE_NUMBER, initialItemsPerPage = DEFAULT_PAGE_SIZE } = props;
  const [pageNo, setPageNo] = useState<number>(initialPageNo);
  const [totalPage, setTotalPage] = useState<number>(1);
  const [itemsPerPage, setItemsPerPage] = useState<number>(initialItemsPerPage);
  const [goToPage, setGoToPage] = useState<string>('');

  const changePage = useCallback(({ selected }: { selected: number }) => {
    setPageNo(selected);
  }, []);

  const handleItemsPerPageChange = useCallback((e: React.ChangeEvent<HTMLSelectElement>) => {
    const value = parseInt(e.target.value, 10);
    if (!isNaN(value) && value > 0) {
      setItemsPerPage(value);
      setPageNo(0);
    }
  }, []);

  const handleGoToPageChange = useCallback((e: React.ChangeEvent<HTMLInputElement>) => {
    setGoToPage(e.target.value);
  }, []);

  const handleGoToPageSubmit = useCallback(() => {
    const page = parseInt(goToPage, 10) - 1; // Convert to zero-based index
    if (!isNaN(page) && page >= 0 && page < totalPage) {
      setPageNo(page);
      setGoToPage('');
    } else {
      alert(`Invalid page number: "${goToPage}" (Expected: 1 - ${totalPage})`);
    }
  }, [goToPage, totalPage]);

  const paginationControls = useMemo(
    () => ({
      itemsPerPage: {
        value: itemsPerPage,
        onChange: handleItemsPerPageChange,
      },
      goToPage: {
        value: goToPage,
        onChange: handleGoToPageChange,
        onSubmit: handleGoToPageSubmit,
      },
    }),
    [itemsPerPage, goToPage, handleGoToPageChange, handleGoToPageSubmit]
  );

  return {
    pageNo,
    totalPage,
    setTotalPage,
    changePage,
    paginationControls,
    DEFAULT_PAGE_SIZE_OPTION, // Expose page sizes for reuse
  };
};

export default usePagination;
