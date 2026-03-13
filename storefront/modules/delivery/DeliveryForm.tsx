import { useState } from 'react';
import { FieldErrorsImpl, UseFormRegister, UseFormSetValue } from 'react-hook-form';
import { OptionSelect } from '@/common/items/OptionSelect';

type ShippingMethod = {
  id: string;
  name: string;
  price: number;
  deliveryDate: string;
};

type DeliveryProvider = {
  id: string;
  name: string;
  methods: ShippingMethod[];
};

// Mock data for delivery providers and their methods
const MOCK_DELIVERY_PROVIDERS: DeliveryProvider[] = [
  {
    id: 'fedex',
    name: 'FedEx',
    methods: [
      { id: 'standard', name: 'Standard', price: 7.56, deliveryDate: 'Sep 21' },
      { id: 'express', name: 'Express', price: 12.99, deliveryDate: 'Sep 19' },
      { id: 'priority', name: 'Priority', price: 19.99, deliveryDate: 'Sep 18' },
    ],
  },
  {
    id: 'ups',
    name: 'UPS',
    methods: [
      { id: 'economy', name: 'Economy', price: 4.99, deliveryDate: 'Sep 23' },
      { id: 'express', name: 'Express', price: 10.99, deliveryDate: 'Sep 20' },
    ],
  },
];

type DeliveryFormProps = {
  register: UseFormRegister<any>;
  setValue: UseFormSetValue<any>;
  errors: FieldErrorsImpl<any>;
  isDisplay?: boolean;
};

const DeliveryForm = ({ register, setValue, errors, isDisplay }: DeliveryFormProps) => {
  const [deliveryProviders] = useState(MOCK_DELIVERY_PROVIDERS);
  const [shippingMethods, setShippingMethods] = useState<ShippingMethod[]>([]);
  const [selectedMethodId, setSelectedMethodId] = useState<string | null>(null); // Track selected method

  const onProviderChange = (event: React.ChangeEvent<HTMLSelectElement>) => {
    const providerId = event.target.value;

    setShippingMethods([]);
    setValue('methodId', '');
    setValue('methodName', '');
    setSelectedMethodId(null);
    setValue('providerName', event.target.selectedOptions[0]?.text || '');
    setValue('providerId', providerId || '');

    const selectedProvider = deliveryProviders.find((provider) => provider.id === providerId);
    setShippingMethods(selectedProvider?.methods || []);
  };

  const onMethodChange = (method: ShippingMethod) => {
    setSelectedMethodId(method.id);
    setValue('methodName', method.name);
    setValue('methodId', method.id);
  };

  const getErrorMessage = (error: any): string | undefined => {
    return error?.message;
  };

  return (
    <div className={`delivery_form_new ${isDisplay ? '' : 'd-none'}`}>
      <div className="row mb-5">
        {deliveryProviders.length === 0 && (
          <div className="col-12">
            <h3 className="text-danger-bg no-wrap">No delivery service available</h3>
          </div>
        )}

        {deliveryProviders.length > 0 && (
          <>
            <div className="col-lg-4">
              <div className="checkout__input">
                <OptionSelect
                  labelText="Delivery Provider"
                  field="providerId"
                  placeholder="Select delivery provider"
                  options={deliveryProviders.map((provider) => ({
                    id: provider.id,
                    name: provider.name,
                  }))}
                  register={register}
                  registerOptions={{
                    required: { value: true, message: 'Please select a delivery provider' },
                    onChange: onProviderChange,
                  }}
                  error={getErrorMessage(errors.providerId)}
                />
              </div>
            </div>
            <div className="col-lg-8">
              <div className="checkout__input">
                <div className="mb-2">
                  Shipping Method<span className="text-danger ms-1">*</span>
                </div>
                <div className="custom-dropdown">
                  {shippingMethods.length === 0 ? (
                    <p className="mb-0">Select a delivery provider to see methods</p>
                  ) : (
                    <ul className="shipping-method-list">
                      {shippingMethods.map((method) => (
                        <li key={method.id}>
                          <button
                            type="button"
                            className={`shipping-method-item ${
                              selectedMethodId === method.id ? 'selected' : ''
                            }`}
                            onClick={() => onMethodChange(method)}
                            onKeyDown={() => {}}
                          >
                            <div className="method-info">
                              <span className="method-name">{method.name}</span>
                              <span className="method-price">${method.price.toFixed(2)}</span>
                              <span className="method-date">Delivery by {method.deliveryDate}</span>
                            </div>
                          </button>
                        </li>
                      ))}
                    </ul>
                  )}
                </div>
                <span className="text-danger">{getErrorMessage(errors.methodId)}</span>
              </div>
            </div>
          </>
        )}
      </div>
    </div>
  );
};

export default DeliveryForm;
