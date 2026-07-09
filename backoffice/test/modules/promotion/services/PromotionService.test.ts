import { describe, it, expect, vi, beforeEach } from 'vitest';
import {
  getPromotions,
  createPromotion,
  updatePromotion,
  getPromotion,
  deletePromotion,
  cancel,
} from '../../../../modules/promotion/services/PromotionService';
import { PromotionDto, PromotionListRequest, PromotionDetail, PromotionPage } from '../../../../modules/promotion/models/Promotion';
import { BrandVm } from '../../../../modules/promotion/models/Brand';
import { CategoryGetVm } from '../../../../modules/promotion/models/Category';
import { ProductVm } from '../../../../modules/promotion/models/Product';

// Mock ApiClientService
vi.mock('../../../../common/services/ApiClientService', () => ({
  default: {
    get: vi.fn(),
    post: vi.fn(),
    put: vi.fn(),
    delete: vi.fn(),
  },
}));

import apiClientService from '../../../../common/services/ApiClientService';

describe('PromotionService', () => {
  // Mock BrandVm
  const mockBrand: BrandVm = {
    id: 1,
    name: 'Nike',
    slug: 'nike',
    isPublish: true,
  };

  // Mock CategoryGetVm
  const mockCategory: CategoryGetVm = {
    id: 1,
    name: 'Electronics',
    slug: 'electronics',
    parentId: 0,
  };

  // Mock ProductVm với đầy đủ fields
  const mockProduct: ProductVm = {
    id: 1,
    name: 'Laptop',
    slug: 'laptop',
    isAllowedToOrder: true,
    isPublished: true,
    isFeatured: true,
    isVisibleIndividually: true,
    createdOn: new Date('2024-01-15T10:30:00Z'),
    taxClassId: 1,
  };

  // Mock PromotionDetail
  const mockPromotionDetail: PromotionDetail = {
    id: 1,
    name: 'Summer Sale',
    slug: 'summer-sale',
    description: 'Summer discount promotion',
    couponCode: 'SUMMER20',
    usageLimit: 100,
    usageCount: 25,
    discountType: 'PERCENTAGE',
    applyTo: 'ALL_PRODUCTS',
    usageType: 'LIMITED',
    discountPercentage: 20,
    discountAmount: 0,
    minimumOrderPurchaseAmount: 50,
    isActive: true,
    startDate: '2024-06-01T00:00:00Z',
    endDate: '2024-06-30T23:59:59Z',
    brands: [mockBrand],
    categories: [mockCategory],
    products: [mockProduct],
  };

  // Mock PromotionPage
  const mockPromotionPage: PromotionPage = {
    promotionDetailVmList: [mockPromotionDetail],
    pageNo: 1,
    pageSize: 10,
    totalPage: 5,
    totalElements: 50,
  };

  // Mock PromotionDto cho create/update
  const mockPromotionDto: PromotionDto = {
    id: 1,
    name: 'Summer Sale',
    slug: 'summer-sale',
    description: 'Summer discount promotion',
    couponCode: 'SUMMER20',
    usageLimit: 100,
    discountType: 'PERCENTAGE',
    applyTo: 'ALL_PRODUCTS',
    usageType: 'LIMITED',
    discountPercentage: 20,
    discountAmount: 0,
    minimumOrderPurchaseAmount: 50,
    isActive: true,
    startDate: '2024-06-01T00:00:00Z',
    endDate: '2024-06-30T23:59:59Z',
    brandIds: [1],
    categoryIds: [1],
    productIds: [1],
  };

  const mockPromotionListRequest: PromotionListRequest = {
    promotionName: 'sale',
    couponCode: 'SUMMER',
    pageNo: 1,
    pageSize: 10,
  };

  beforeEach(() => {
    vi.clearAllMocks();
  });

  describe('getPromotions', () => {
    it('should call API with correct URL from request object', async () => {
      const mockResponse = { json: vi.fn().mockResolvedValue(mockPromotionPage) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      await getPromotions(mockPromotionListRequest);

      expect(apiClientService.get).toHaveBeenCalledTimes(1);
      expect(apiClientService.get).toHaveBeenCalledWith(
        '/api/promotion/backoffice/promotions?promotionName=sale&couponCode=SUMMER&pageNo=1&pageSize=10'
      );
    });

    it('should return promotion page data on success', async () => {
      const mockResponse = { json: vi.fn().mockResolvedValue(mockPromotionPage) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      const result = await getPromotions(mockPromotionListRequest);

      expect(result).toEqual(mockPromotionPage);
      expect(result.promotionDetailVmList).toHaveLength(1);
      expect(result.totalPage).toBe(5);
      
      // Verify BrandVm fields
      expect(result.promotionDetailVmList[0].brands[0].name).toBe('Nike');
      expect(result.promotionDetailVmList[0].brands[0].slug).toBe('nike');
      expect(result.promotionDetailVmList[0].brands[0].isPublish).toBe(true);
      
      // Verify CategoryGetVm fields
      expect(result.promotionDetailVmList[0].categories[0].name).toBe('Electronics');
      expect(result.promotionDetailVmList[0].categories[0].slug).toBe('electronics');
      expect(result.promotionDetailVmList[0].categories[0].parentId).toBe(0);
      
      // Verify ProductVm fields
      expect(result.promotionDetailVmList[0].products[0].name).toBe('Laptop');
      expect(result.promotionDetailVmList[0].products[0].slug).toBe('laptop');
      expect(result.promotionDetailVmList[0].products[0].isAllowedToOrder).toBe(true);
      expect(result.promotionDetailVmList[0].products[0].isPublished).toBe(true);
      expect(result.promotionDetailVmList[0].products[0].isFeatured).toBe(true);
      expect(result.promotionDetailVmList[0].products[0].isVisibleIndividually).toBe(true);
      expect(result.promotionDetailVmList[0].products[0].taxClassId).toBe(1);
    });

    it('should handle empty request object', async () => {
      const emptyRequest = { promotionName: '', couponCode: '', pageNo: 0, pageSize: 0 };
      const mockResponse = { json: vi.fn().mockResolvedValue({ promotionDetailVmList: [], pageNo: 0, pageSize: 0, totalPage: 0, totalElements: 0 }) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      await getPromotions(emptyRequest);

      expect(apiClientService.get).toHaveBeenCalledWith(
        '/api/promotion/backoffice/promotions?promotionName=&couponCode=&pageNo=0&pageSize=0'
      );
    });

    it('should handle empty promotion name', async () => {
      const request = { promotionName: '', couponCode: 'SUMMER', pageNo: 1, pageSize: 10 };
      const mockResponse = { json: vi.fn().mockResolvedValue(mockPromotionPage) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      await getPromotions(request);

      expect(apiClientService.get).toHaveBeenCalledWith(
        '/api/promotion/backoffice/promotions?promotionName=&couponCode=SUMMER&pageNo=1&pageSize=10'
      );
    });

    it('should handle empty coupon code', async () => {
      const request = { promotionName: 'sale', couponCode: '', pageNo: 1, pageSize: 10 };
      const mockResponse = { json: vi.fn().mockResolvedValue(mockPromotionPage) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      await getPromotions(request);

      expect(apiClientService.get).toHaveBeenCalledWith(
        '/api/promotion/backoffice/promotions?promotionName=sale&couponCode=&pageNo=1&pageSize=10'
      );
    });

    it('should handle API error', async () => {
      const error = new Error('Network error');
      (apiClientService.get as any).mockRejectedValue(error);

      await expect(getPromotions(mockPromotionListRequest)).rejects.toThrow('Network error');
    });
  });

  describe('createPromotion', () => {
    it('should call API with correct URL and body', async () => {
      const mockResponse = { status: 201, data: { id: 1, ...mockPromotionDto } };
      (apiClientService.post as any).mockResolvedValue(mockResponse);

      await createPromotion(mockPromotionDto);

      expect(apiClientService.post).toHaveBeenCalledTimes(1);
      expect(apiClientService.post).toHaveBeenCalledWith(
        '/api/promotion/backoffice/promotions',
        JSON.stringify(mockPromotionDto)
      );
    });

    it('should return response on success', async () => {
      const mockResponse = { status: 201, data: { id: 1 } };
      (apiClientService.post as any).mockResolvedValue(mockResponse);

      const result = await createPromotion(mockPromotionDto);

      expect(result).toEqual(mockResponse);
    });

    it('should handle API error when creating promotion', async () => {
      const error = new Error('Promotion name already exists');
      (apiClientService.post as any).mockRejectedValue(error);

      await expect(createPromotion(mockPromotionDto)).rejects.toThrow('Promotion name already exists');
    });

    it('should handle validation error', async () => {
      const error = new Error('Discount must be between 0 and 100');
      (apiClientService.post as any).mockRejectedValue(error);

      await expect(createPromotion(mockPromotionDto)).rejects.toThrow('Discount must be between 0 and 100');
    });
  });

  describe('updatePromotion', () => {
    it('should call API with correct URL and body', async () => {
      const mockResponse = { status: 200, data: mockPromotionDto };
      (apiClientService.put as any).mockResolvedValue(mockResponse);

      await updatePromotion(mockPromotionDto);

      expect(apiClientService.put).toHaveBeenCalledTimes(1);
      expect(apiClientService.put).toHaveBeenCalledWith(
        '/api/promotion/backoffice/promotions',
        JSON.stringify(mockPromotionDto)
      );
    });

    it('should return response on success', async () => {
      const mockResponse = { status: 200, data: { id: 1, updated: true } };
      (apiClientService.put as any).mockResolvedValue(mockResponse);

      const result = await updatePromotion(mockPromotionDto);

      expect(result).toEqual(mockResponse);
    });

    it('should handle API error when updating promotion', async () => {
      const error = new Error('Promotion not found');
      (apiClientService.put as any).mockRejectedValue(error);

      await expect(updatePromotion(mockPromotionDto)).rejects.toThrow('Promotion not found');
    });

    it('should handle duplicate promotion name error', async () => {
      const error = new Error('Promotion name already in use');
      (apiClientService.put as any).mockRejectedValue(error);

      await expect(updatePromotion(mockPromotionDto)).rejects.toThrow('Promotion name already in use');
    });
  });

  describe('getPromotion', () => {
    const promotionId = 1;

    it('should call API with correct URL', async () => {
      const mockResponse = { json: vi.fn().mockResolvedValue(mockPromotionDetail) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      await getPromotion(promotionId);

      expect(apiClientService.get).toHaveBeenCalledTimes(1);
      expect(apiClientService.get).toHaveBeenCalledWith('/api/promotion/backoffice/promotions/1');
    });

    it('should return promotion detail on success', async () => {
      const mockResponse = { json: vi.fn().mockResolvedValue(mockPromotionDetail) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      const result = await getPromotion(promotionId);

      expect(result).toEqual(mockPromotionDetail);
      expect(result.id).toBe(1);
      expect(result.name).toBe('Summer Sale');
      expect(result.couponCode).toBe('SUMMER20');
      
      // Verify nested objects
      expect(result.brands[0].slug).toBe('nike');
      expect(result.categories[0].parentId).toBe(0);
      expect(result.products[0].slug).toBe('laptop');
      expect(result.products[0].isAllowedToOrder).toBe(true);
      expect(result.products[0].isPublished).toBe(true);
    });

    it('should handle promotionId = 0', async () => {
      const mockResponse = { json: vi.fn().mockResolvedValue(null) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      await getPromotion(0);

      expect(apiClientService.get).toHaveBeenCalledWith('/api/promotion/backoffice/promotions/0');
    });

    it('should handle API error when promotion not found', async () => {
      const error = new Error('Promotion not found');
      (apiClientService.get as any).mockRejectedValue(error);

      await expect(getPromotion(999)).rejects.toThrow('Promotion not found');
    });
  });

  describe('deletePromotion', () => {
    const promotionId = 1;

    it('should call API with correct URL', async () => {
      const mockResponse = { status: 204 };
      (apiClientService.delete as any).mockResolvedValue(mockResponse);

      await deletePromotion(promotionId);

      expect(apiClientService.delete).toHaveBeenCalledTimes(1);
      expect(apiClientService.delete).toHaveBeenCalledWith('/api/promotion/backoffice/promotions/1');
    });

    it('should return response on success', async () => {
      const mockResponse = { status: 204 };
      (apiClientService.delete as any).mockResolvedValue(mockResponse);

      const result = await deletePromotion(promotionId);

      expect(result).toEqual(mockResponse);
    });

    it('should handle API error when deleting promotion', async () => {
      const error = new Error('Cannot delete active promotion');
      (apiClientService.delete as any).mockRejectedValue(error);

      await expect(deletePromotion(promotionId)).rejects.toThrow('Cannot delete active promotion');
    });

    it('should handle API error when promotion not found', async () => {
      const error = new Error('Promotion not found');
      (apiClientService.delete as any).mockRejectedValue(error);

      await expect(deletePromotion(999)).rejects.toThrow('Promotion not found');
    });
  });

  describe('cancel', () => {
    it('should redirect to /promotion/manager-promotion', () => {
      const originalLocation = window.location;
      const mockHref = { href: '' };
      
      Object.defineProperty(window, 'location', {
        value: mockHref,
        writable: true,
      });

      cancel();

      expect(window.location.href).toBe('/promotion/manager-promotion');
      
      Object.defineProperty(window, 'location', {
        value: originalLocation,
        writable: true,
      });
    });
  });

  describe('createRequestFromObject helper', () => {
    it('should convert request object to query string correctly', async () => {
      const request = { promotionName: 'sale', couponCode: 'code', pageNo: 1, pageSize: 20 };
      const mockResponse = { json: vi.fn().mockResolvedValue(mockPromotionPage) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      await getPromotions(request);

      expect(apiClientService.get).toHaveBeenCalledWith(
        '/api/promotion/backoffice/promotions?promotionName=sale&couponCode=code&pageNo=1&pageSize=20'
      );
    });

    it('should handle single parameter request', async () => {
      const request = { promotionName: 'sale' };
      const mockResponse = { json: vi.fn().mockResolvedValue(mockPromotionPage) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      await getPromotions(request as PromotionListRequest);

      expect(apiClientService.get).toHaveBeenCalledWith(
        '/api/promotion/backoffice/promotions?promotionName=sale'
      );
    });
  });

  describe('URL construction', () => {
    it('should use correct base URL for all methods', async () => {
      (apiClientService.get as any).mockResolvedValue({ json: vi.fn().mockResolvedValue(mockPromotionPage) });
      (apiClientService.post as any).mockResolvedValue({});
      (apiClientService.put as any).mockResolvedValue({});
      (apiClientService.delete as any).mockResolvedValue({});

      await getPromotions(mockPromotionListRequest);
      await getPromotion(1);
      await createPromotion(mockPromotionDto);
      await updatePromotion(mockPromotionDto);
      await deletePromotion(1);

      expect(apiClientService.get).toHaveBeenCalledWith('/api/promotion/backoffice/promotions?promotionName=sale&couponCode=SUMMER&pageNo=1&pageSize=10');
      expect(apiClientService.get).toHaveBeenCalledWith('/api/promotion/backoffice/promotions/1');
      expect(apiClientService.post).toHaveBeenCalledWith('/api/promotion/backoffice/promotions', expect.any(String));
      expect(apiClientService.put).toHaveBeenCalledWith('/api/promotion/backoffice/promotions', expect.any(String));
      expect(apiClientService.delete).toHaveBeenCalledWith('/api/promotion/backoffice/promotions/1');
    });
  });
});