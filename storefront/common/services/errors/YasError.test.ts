import { describe, expect, test } from 'vitest';
import { YasError } from './YasError';

describe('YasError', () => {
  test('should use first field error as message', () => {
    const error = new YasError({
      status: 400,
      title: 'Validation error',
      detail: 'invalid payload',
      fieldErrors: ['name is required', 'email is invalid'],
    });

    expect(error.message).toBe('name is required');
    expect(error.status).toBe(400);
    expect(error.title).toBe('Validation error');
    expect(error.details).toBe('invalid payload');
    expect(error.fieldErrors).toEqual(['name is required', 'email is invalid']);
  });

  test('should fallback to parsed statusCode and detail', () => {
    const error = new YasError({
      statusCode: '409',
      detail: 'already exists',
    });

    expect(error.message).toBe('already exists');
    expect(error.status).toBe(409);
    expect(error.title).toBe('Unknown error');
    expect(error.details).toBe('already exists');
    expect(error.fieldErrors).toEqual([]);
  });
});
