import { getEnabledPaymentProviders } from './PaymentProviderService';
import apiClientService from '@/common/services/ApiClientService';

jest.mock('@/common/services/ApiClientService', () => ({
  __esModule: true,
  default: {
    get: jest.fn(),
  },
}));

const mockedApiClient = apiClientService as jest.Mocked<typeof apiClientService>;

describe('PaymentProviderService', () => {
  it('gets enabled payment providers', async () => {
    const responseJson = [{ id: 1, name: 'PayPal' }];
    mockedApiClient.get.mockResolvedValue({
      json: jest.fn().mockResolvedValue(responseJson),
    } as unknown as Response);

    const result = await getEnabledPaymentProviders();

    expect(mockedApiClient.get).toHaveBeenCalledWith('/api/payment/storefront/payment-providers');
    expect(result).toEqual(responseJson);
  });
});
