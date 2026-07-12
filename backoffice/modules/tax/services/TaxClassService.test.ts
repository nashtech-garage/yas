import {
  createTaxClass,
  deleteTaxClass,
  editTaxClass,
  getPageableTaxClasses,
  getTaxClass,
  getTaxClasses,
} from './TaxClassService';
import apiClientService from '@commonServices/ApiClientService';

vi.mock('@commonServices/ApiClientService', () => ({
  default: {
    get: vi.fn(),
    post: vi.fn(),
    put: vi.fn(),
    delete: vi.fn(),
  },
}));

describe('TaxClassService', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('gets tax classes', async () => {
    const json = vi.fn().mockResolvedValue([{ id: 1, name: 'VAT' }]);
    vi.mocked(apiClientService.get).mockResolvedValue({ json } as unknown as Response);

    const result = await getTaxClasses();

    expect(apiClientService.get).toHaveBeenCalledWith('/api/tax/backoffice/tax-classes');
    expect(result).toEqual([{ id: 1, name: 'VAT' }]);
  });

  it('gets pageable tax classes', async () => {
    const json = vi.fn().mockResolvedValue({ items: [], totalPages: 0 });
    vi.mocked(apiClientService.get).mockResolvedValue({ json } as unknown as Response);

    const result = await getPageableTaxClasses(3, 25);

    expect(apiClientService.get).toHaveBeenCalledWith(
      '/api/tax/backoffice/tax-classes/paging?pageNo=3&pageSize=25'
    );
    expect(result).toEqual({ items: [], totalPages: 0 });
  });

  it('gets one tax class by id', async () => {
    const json = vi.fn().mockResolvedValue({ id: 3, name: 'Reduced' });
    vi.mocked(apiClientService.get).mockResolvedValue({ json } as unknown as Response);

    const result = await getTaxClass(3);

    expect(apiClientService.get).toHaveBeenCalledWith('/api/tax/backoffice/tax-classes/3');
    expect(result).toEqual({ id: 3, name: 'Reduced' });
  });

  it('creates tax class', async () => {
    const response = { status: 201 } as Response;
    vi.mocked(apiClientService.post).mockResolvedValue(response);

    const payload = { name: 'Service Tax' } as any;
    const result = await createTaxClass(payload);

    expect(apiClientService.post).toHaveBeenCalledWith(
      '/api/tax/backoffice/tax-classes',
      JSON.stringify(payload)
    );
    expect(result).toBe(response);
  });

  it('deletes tax class with 204 status', async () => {
    vi.mocked(apiClientService.delete).mockResolvedValue({ status: 204 } as Response);

    const result = await deleteTaxClass(7);

    expect(result.status).toBe(204);
  });

  it('deletes tax class with non-204 status', async () => {
    const json = vi.fn().mockResolvedValue({ detail: 'tax class in use' });
    vi.mocked(apiClientService.delete).mockResolvedValue({
      status: 409,
      json,
    } as unknown as Response);

    const result = await deleteTaxClass(7);

    expect(result).toEqual({ detail: 'tax class in use' });
  });

  it('edits tax class with non-204 status', async () => {
    const json = vi.fn().mockResolvedValue({ detail: 'invalid name' });
    vi.mocked(apiClientService.put).mockResolvedValue({ status: 400, json } as unknown as Response);

    const result = await editTaxClass(7, { name: '' } as any);

    expect(apiClientService.put).toHaveBeenCalledWith(
      '/api/tax/backoffice/tax-classes/7',
      JSON.stringify({ name: '' })
    );
    expect(result).toEqual({ detail: 'invalid name' });
  });
});
