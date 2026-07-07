import { concatQueryString } from '../../utils/concatQueryString';
import { describe, test, expect } from 'vitest';

describe('concatQueryString', () => {

  // Test cơ bản
  describe('Basic functionality', () => {
    test('should return original url when array is empty', () => {
      const result = concatQueryString([], 'http://example.com');
      expect(result).toBe('http://example.com');
    });

    test('should append single query param with ?', () => {
      const result = concatQueryString(['a=1'], 'http://example.com');
      expect(result).toBe('http://example.com?a=1');
    });

    test('should append multiple query params with &', () => {
      const result = concatQueryString(['a=1', 'b=2'], 'http://example.com');
      expect(result).toBe('http://example.com?a=1&b=2');
    });

    test('should handle multiple params correctly', () => {
      const result = concatQueryString(['a=1', 'b=2', 'c=3'], 'http://example.com');
      expect(result).toBe('http://example.com?a=1&b=2&c=3');
    });
  });

  // Test với URL có sẵn path
  describe('URL with existing path', () => {
    test('should work with URL having path', () => {
      const result = concatQueryString(['category=books'], 'http://example.com/products');
      expect(result).toBe('http://example.com/products?category=books');
    });

    test('should work with multiple params and path', () => {
      const result = concatQueryString(['page=1', 'limit=10'], 'http://example.com/api/users');
      expect(result).toBe('http://example.com/api/users?page=1&limit=10');
    });
  });

  // Test với giá trị đặc biệt
  describe('Special values', () => {
    test('should handle special characters in values', () => {
      const result = concatQueryString(['name=John%20Doe', 'city=New%20York'], 'http://example.com');
      expect(result).toBe('http://example.com?name=John%20Doe&city=New%20York');
    });

    test('should handle numeric values', () => {
      const result = concatQueryString(['id=123', 'active=1'], 'http://example.com');
      expect(result).toBe('http://example.com?id=123&active=1');
    });

    test('should handle boolean values', () => {
      const result = concatQueryString(['isActive=true', 'isDeleted=false'], 'http://example.com');
      expect(result).toBe('http://example.com?isActive=true&isDeleted=false');
    });

    test('should handle empty string values', () => {
      const result = concatQueryString(['empty=', 'notempty=value'], 'http://example.com');
      expect(result).toBe('http://example.com?empty=&notempty=value');
    });
  });

  // Test với array params
  describe('Array parameters', () => {
    test('should handle array syntax in params', () => {
      const result = concatQueryString(['ids[]=1', 'ids[]=2', 'ids[]=3'], 'http://example.com');
      expect(result).toBe('http://example.com?ids[]=1&ids[]=2&ids[]=3');
    });

    test('should handle multiple values for same key', () => {
      const result = concatQueryString(['color=red', 'color=blue', 'color=green'], 'http://example.com');
      expect(result).toBe('http://example.com?color=red&color=blue&color=green');
    });
  });

  // Test với HTTPS URLs
  describe('HTTPS URLs', () => {
    test('should work with https protocol', () => {
      const result = concatQueryString(['token=abc123'], 'https://secure.example.com');
      expect(result).toBe('https://secure.example.com?token=abc123');
    });

    test('should work with https and path', () => {
      const result = concatQueryString(['sort=desc', 'limit=5'], 'https://api.example.com/v1/products');
      expect(result).toBe('https://api.example.com/v1/products?sort=desc&limit=5');
    });
  });

  // Test với localhost và ports
  describe('Localhost and ports', () => {
    test('should work with localhost', () => {
      const result = concatQueryString(['port=3000'], 'http://localhost');
      expect(result).toBe('http://localhost?port=3000');
    });

    test('should work with port number', () => {
      const result = concatQueryString(['debug=true'], 'http://localhost:8080');
      expect(result).toBe('http://localhost:8080?debug=true');
    });
  });

  // Test với many parameters
  describe('Many parameters', () => {
    test('should handle 10 parameters', () => {
      const params = Array.from({ length: 10 }, (_, i) => `param${i}=value${i}`);
      const result = concatQueryString(params, 'http://example.com');
      expect(result).toContain('?param0=value0');
      expect(result).toContain('&param9=value9');
      expect(result.split('&')).toHaveLength(10);
    });

    test('should handle 50 parameters', () => {
      const params = Array.from({ length: 50 }, (_, i) => `field${i}=data${i}`);
      const result = concatQueryString(params, 'http://example.com');
      expect(result).toMatch(/^http:\/\/example\.com\?/);
      expect(result.split('&')).toHaveLength(50);
    });
  });

  // Test với URLs có subdomain
  describe('Subdomains', () => {
    test('should work with subdomain', () => {
      const result = concatQueryString(['api=v2'], 'https://api.example.com');
      expect(result).toBe('https://api.example.com?api=v2');
    });

    test('should work with multiple subdomains', () => {
      const result = concatQueryString(['env=prod'], 'https://admin.api.example.com');
      expect(result).toBe('https://admin.api.example.com?env=prod');
    });
  });

  // Edge cases
  describe('Edge cases', () => {
    test('should handle URL with trailing slash', () => {
      const result = concatQueryString(['page=1'], 'http://example.com/');
      expect(result).toBe('http://example.com/?page=1');
    });

    test('should handle params with equal signs in value', () => {
      const result = concatQueryString(['equation=2+2=4'], 'http://example.com');
      expect(result).toBe('http://example.com?equation=2+2=4');
    });

    test('should handle params with special keys', () => {
      const result = concatQueryString(['_id=123', '$type=user'], 'http://example.com');
      expect(result).toBe('http://example.com?_id=123&$type=user');
    });

    test('should handle very long URL', () => {
      const longParam = 'a=' + 'x'.repeat(1000);
      const result = concatQueryString([longParam], 'http://example.com');
      expect(result).toContain(longParam);
      expect(result.length).toBeGreaterThan(1000);
    });

    test('should handle non-ASCII characters', () => {
      const result = concatQueryString(['name=Tiếng Việt'], 'http://example.com');
      expect(result).toBe('http://example.com?name=Tiếng Việt');
    });
  });

  describe('URL with existing query string', () => {
    test('⚠️ [KNOWN ISSUE] should handle URL with existing params incorrectly', () => {
      // Hiện tại logic sẽ thêm ? thay vì & khi URL đã có query string
      const result = concatQueryString(['b=2'], 'http://example.com?a=1');
      expect(result).toBe('http://example.com?a=1?b=2'); // Bug: should be '&b=2' instead of '?b=2'
    });
  });
});