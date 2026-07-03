import { describe, it, expect } from 'vitest';
import { ContentType } from '../../../../modules/webhook/models/ContentType';

describe('ContentType Enum', () => {
  describe('Values', () => {
    it('should have APPLICATION_JSON value', () => {
      expect(ContentType.APPLICATION_JSON).toBe('application/json');
    });
  });

  describe('Usage', () => {
    it('should be usable as a type', () => {
      const contentType: ContentType = ContentType.APPLICATION_JSON;
      expect(contentType).toBe('application/json');
    });

    it('should be usable in switch statements', () => {
      let result = '';
      const type = ContentType.APPLICATION_JSON;
      
      switch (type) {
        case ContentType.APPLICATION_JSON:
          result = 'JSON';
          break;
        default:
          result = 'Unknown';
      }
      
      expect(result).toBe('JSON');
    });

    it('should have correct string representation', () => {
      const jsonType = ContentType.APPLICATION_JSON;
      expect(String(jsonType)).toBe('application/json');
    });

    it('should be able to compare with string', () => {
      const contentType = ContentType.APPLICATION_JSON;
      expect(contentType === 'application/json').toBe(true);
    });
  });
});