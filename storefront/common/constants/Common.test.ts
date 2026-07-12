import { describe, expect, test } from 'vitest';
import {
  CREATE_SUCCESSFULLY,
  DELETE_SUCCESSFULLY,
  SEARCH_URL,
  UPDATE_SUCCESSFULLY,
} from './Common';

describe('Common constants', () => {
  test('should expose expected constant values', () => {
    expect(UPDATE_SUCCESSFULLY).toBe('Update successfully');
    expect(CREATE_SUCCESSFULLY).toBe('Create successfully');
    expect(DELETE_SUCCESSFULLY).toBe('Delete successfully');
    expect(SEARCH_URL).toBe('/search');
  });
});
