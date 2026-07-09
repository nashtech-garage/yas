import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import WarehouseList from '../../../../pages/inventory/warehouses/index';
import { getPageableWarehouses, deleteWarehouse } from '../../../../modules/inventory/services/WarehouseService';
import { WarehouseDetail } from '../../../../modules/inventory/models/WarehouseDetail';

// Mock dependencies
vi.mock('../../../../modules/inventory/services/WarehouseService', () => ({
  getPageableWarehouses: vi.fn(),
  deleteWarehouse: vi.fn(),
}));

vi.mock('../../../../commonServices/ApiClientService', () => ({
  default: {
    get: vi.fn(),
    post: vi.fn(),
    put: vi.fn(),
    delete: vi.fn(),
  },
}));

vi.mock('react', () => ({
  ...vi.importActual('react'),
  useEffect: vi.fn(),
}));

describe('WarehouseList Page', () => {

  describe('deleteWarehouse', () => {
    it('should be defined', () => {
      expect(deleteWarehouse).toBeDefined();
    });
  });

});