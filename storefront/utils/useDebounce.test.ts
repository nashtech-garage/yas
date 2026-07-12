import { renderHook, act } from '@testing-library/react';
import { expect, test, describe, vi } from 'vitest';
import { useDebounce } from './useDebounce';

describe('Test useDebounce Hook', () => {
  vi.useFakeTimers(); // Sử dụng thời gian giả lập

  test('Phải cập nhật giá trị sau một khoảng delay', () => {
    const { result, rerender } = renderHook(({ value, delay }) => useDebounce(value, delay), {
      initialProps: { value: 'abc', delay: 500 },
    });

    expect(result.current).toBe('abc');

    // Thay đổi giá trị
    rerender({ value: 'def', delay: 500 });

    // Ngay lập tức thì chưa đổi
    expect(result.current).toBe('abc');

    // Tua nhanh thời gian
    act(() => {
      vi.advanceTimersByTime(500);
    });

    expect(result.current).toBe('def');
  });
});
