import { useState, useMemo, useCallback } from 'react';
import { DEFAULT_PAGE_NUMBER, DEFAULT_PAGE_SIZE } from '@constants/Common';

export const PAGE_SIZE_OPTION = [5, 10, 15, 20];

interface UsePaginationProps {
  initialPageNo?: number;
  initialItemsPerPage?: number;
}

const usePagination = ({
  initialPageNo = DEFAULT_PAGE_NUMBER,
  initialItemsPerPage = DEFAULT_PAGE_SIZE,
}: UsePaginationProps) => {
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

  const handleGoToPage = useCallback(
    (e?: React.ChangeEvent<HTMLInputElement>, submit?: boolean) => {
      if (e) {
        setGoToPage(e.target.value);
      }
      if (submit) {
        const page = parseInt(goToPage, 10) - 1; // Convert to zero-based index
        if (!isNaN(page) && page >= 0 && page < totalPage) {
          setPageNo(page);
          setGoToPage('');
        } else {
          alert(`Invalid page number: "${goToPage}" (Expected: 1 - ${totalPage})`);
        }
      }
    },
    [goToPage, totalPage]
  );

  const paginationControls = useMemo(
    () => ({
      itemsPerPage: {
        value: itemsPerPage,
        onChange: handleItemsPerPageChange,
      },
      goToPage: {
        value: goToPage,
        onChange: (e: React.ChangeEvent<HTMLInputElement>) => handleGoToPage(e),
        onSubmit: () => handleGoToPage(undefined, true),
      },
    }),
    [itemsPerPage, goToPage, handleGoToPage]
  );

  return {
    pageNo,
    totalPage,
    setTotalPage,
    changePage,
    paginationControls,
    PAGE_SIZE_OPTION, // Expose page sizes for reuse
  };
};

export default usePagination;
