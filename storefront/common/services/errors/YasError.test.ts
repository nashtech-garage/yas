import { YasError } from './YasError';

describe('YasError', () => {
  it('uses the first field error as the error message', () => {
    const error = new YasError({
      status: 400,
      title: 'Validation error',
      detail: 'Invalid payload',
      fieldErrors: ['productId is required', 'quantity is required'],
    });

    expect(error.message).toBe('productId is required');
    expect(error.status).toBe(400);
    expect(error.title).toBe('Validation error');
    expect(error.details).toBe('Invalid payload');
  });

  it('falls back to statusCode and default values', () => {
    const error = new YasError({
      statusCode: '401',
    });

    expect(error.message).toBe('unknown');
    expect(error.status).toBe(401);
    expect(error.title).toBe('Unknown error');
    expect(error.fieldErrors).toEqual([]);
  });
});
