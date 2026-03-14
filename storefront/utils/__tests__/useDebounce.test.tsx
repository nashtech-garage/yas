import { act, renderHook } from '@testing-library/react';

import { useDebounce } from '../useDebounce';

describe('useDebounce', () => {
  beforeEach(() => {
    jest.useFakeTimers();
  });

  afterEach(() => {
    jest.runOnlyPendingTimers();
    jest.useRealTimers();
  });

  it('returns initial value and updates after delay', () => {
    const { result, rerender } = renderHook(({ value, delay }) => useDebounce(value, delay), {
      initialProps: { value: 'apple', delay: 300 },
    });

    expect(result.current).toBe('apple');

    rerender({ value: 'banana', delay: 300 });
    expect(result.current).toBe('apple');

    act(() => {
      jest.advanceTimersByTime(300);
    });
    expect(result.current).toBe('banana');
  });

  it('cleans up previous timer when value changes rapidly', () => {
    const { result, rerender } = renderHook(({ value, delay }) => useDebounce(value, delay), {
      initialProps: { value: 'one', delay: 500 },
    });

    rerender({ value: 'two', delay: 500 });
    act(() => {
      jest.advanceTimersByTime(250);
    });
    rerender({ value: 'three', delay: 500 });

    act(() => {
      jest.advanceTimersByTime(250);
    });
    expect(result.current).toBe('one');

    act(() => {
      jest.advanceTimersByTime(250);
    });
    expect(result.current).toBe('three');
  });
});
