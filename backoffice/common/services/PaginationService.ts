import { useState } from 'react';
import { DEFAULT_PAGE_NUMBER, DEFAULT_PAGE_SIZE } from '@constants/Common';

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

  const changePage = ({ selected }: { selected: number }) => setPageNo(selected);

  const handleItemsPerPageChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
    const value = parseInt(e.target.value, 10);
    setItemsPerPage(value);
    setPageNo(0); 
  };

  const handleGoToPageChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setGoToPage(e.target.value);
  };

  const goToPageHandler = () => {
    const page = parseInt(goToPage, 10) - 1;
    if (page >= 0 && page < totalPage) {
      setPageNo(page);
      setGoToPage('');
    } else {
      alert('Invalid page number');
    }
  };

  return {
    pageNo,
    totalPage,
    setTotalPage,
    itemsPerPage,
    goToPage,
    setPageNo,
    changePage,
    handleItemsPerPageChange,
    handleGoToPageChange,
    goToPageHandler,
  };
};

export default usePagination;
