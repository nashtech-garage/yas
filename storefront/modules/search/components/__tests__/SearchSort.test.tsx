import { fireEvent, render, screen, waitFor } from '@testing-library/react';
import { useRouter } from 'next/router';

import SearchSort from '../SearchSort';
import { ESortType } from '../../models/SortType';

jest.mock('next/router', () => ({
  useRouter: jest.fn(),
}));

describe('SearchSort', () => {
  it('updates router query and search params when selecting sort type', async () => {
    const replaceMock = jest.fn().mockResolvedValue(true);
    const query = { keyword: 'apple', page: '2' } as Record<string, string>;

    (useRouter as jest.Mock).mockReturnValue({
      pathname: '/search',
      query,
      replace: replaceMock,
    });

    const setSearchParams = jest.fn();
    const setPageNo = jest.fn();

    render(
      <SearchSort
        totalElements={10}
        keyword="apple"
        searchParams={{ keyword: 'apple' }}
        isFilter={false}
        setSearchParams={setSearchParams}
        setPageNo={setPageNo}
      />
    );

    fireEvent.click(screen.getByText('Price: Low to High'));

    await waitFor(() => {
      expect(setPageNo).toHaveBeenCalledWith(0);
      expect(replaceMock).toHaveBeenCalled();
      expect(query.sortType).toBe('priceAsc');
      expect(query.page).toBeUndefined();
      expect(setSearchParams).toHaveBeenLastCalledWith({
        keyword: 'apple',
        sortType: ESortType.priceAsc,
        page: undefined,
      });
    });
  });
});
