import { useEffect, useState } from 'react';
import { FieldErrorsImpl, UseFormRegister, UseFormSetValue } from 'react-hook-form';
import { OptionSelect } from '@/common/items/OptionSelect';

// Mock data for delivery providers and their methods
const MOCK_DELIVERY_PROVIDERS = [
  {
    id: 'fedex',
    name: 'FedEx',
    methods: [
      { id: 'standard', name: 'Standard' },
      { id: 'express', name: 'Express' },
      { id: 'priority', name: 'Priority' },
    ],
  },
  {
    id: 'ups',
    name: 'UPS',
    methods: [
      { id: 'express', name: 'Express' },
      { id: 'economy', name: 'Economy' },
    ],
  },
];

type DeliveryFormProps = {
  register: UseFormRegister<any>;
  setValue: UseFormSetValue<any>;
  errors: FieldErrorsImpl<any>;
  isDisplay?: boolean | true;
};

const DeliveryForm = ({ register, setValue, errors, isDisplay }: DeliveryFormProps) => {
  const [deliveryProviders] = useState(MOCK_DELIVERY_PROVIDERS);
  const [shippingMethods, setShippingMethods] = useState<{ id: string; name: string }[]>([]);

  const onProviderChange = (event: React.ChangeEvent<HTMLSelectElement>) => {
    const providerId = event.target.value;

    // Clear shipping method field
    setShippingMethods([]); // Reset shipping methods
    setValue('methodId', ''); // Reset selected shipping method
    setValue('methodName', ''); // Reset method name in form state

    // Set provider details
    setValue('providerName', event.target.selectedOptions[0]?.text || ''); // Set provider name
    setValue('providerId', providerId || ''); // Set provider ID

    // Find and set shipping methods for the selected provider
    const selectedProvider = deliveryProviders.find((provider) => provider.id === providerId);
    setShippingMethods(selectedProvider?.methods || []);
  };

  const onMethodChange = (event: React.ChangeEvent<HTMLSelectElement>) => {
    setValue('methodName', event.target.selectedOptions[0]?.text || ''); // Set method name
    setValue('methodId', event.target.value || ''); // Set method ID
  };

  // Safely extract error messages
  const getErrorMessage = (error: any): string | undefined => {
    return error?.message;
  };

  return (
    <>
      <div className={`delivery_form_new ${isDisplay ? '' : 'd-none'}`}>
        <div className="row mb-5">
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
                error={getErrorMessage(errors.providerId)} // Extract error message safely
              />
            </div>
          </div>
          <div className="col-lg-8">
            <div className="checkout__input">
              <OptionSelect
                labelText="Shipping Method"
                field="methodId"
                placeholder="Select shipping method"
                options={shippingMethods}
                register={register}
                registerOptions={{
                  required: { value: true, message: 'Please select a shipping method' },
                  onChange: onMethodChange,
                }}
                error={getErrorMessage(errors.methodId)} // Extract error message safely
              />
            </div>
          </div>
        </div>
      </div>
    </>
  );
};

export default DeliveryForm;
