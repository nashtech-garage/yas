import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import PromotionList from '../../../../pages/promotion/manager-promotion/index';
import { getPromotions, deletePromotion } from '../../../../modules/promotion/services/PromotionService';
import { PromotionPage, PromotionListRequest } from '../../../../modules/promotion/models/Promotion';

// Mock dependencies
vi.mock('../../../../modules/promotion/services/PromotionService', () => ({
  getPromotions: vi.fn(),
  deletePromotion: vi.fn(),
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

describe('PromotionList Page', () => {
  const mockPromotions = [
    {
      id: 1,
      name: 'Summer Sale',
      couponCode: 'SUMMER2024',
      discountAmount: 10,
      startDate: '2024-01-01',
      endDate: '2024-12-31',
      isActive: true,
    },
    {
      id: 2,
      name: 'Winter Sale',
      couponCode: 'WINTER2024',
      discountAmount: 20,
      startDate: '2024-01-01',
      endDate: '2024-12-31',
      isActive: false,
    },
  ];

  describe('deletePromotion', () => {
    it('should be defined', () => {
      expect(deletePromotion).toBeDefined();
    });

  });

});