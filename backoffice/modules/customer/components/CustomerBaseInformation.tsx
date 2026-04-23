import { Input } from '@commonItems/Input';
import { EMAIL_PATTERN } from 'modules/catalog/constants/validationPattern';
import { FieldErrorsImpl, UseFormRegister } from 'react-hook-form';
import { Customer, CustomerCreateVM } from '../models/Customer';

type Props = {
  register: UseFormRegister<CustomerCreateVM>;
  errors: FieldErrorsImpl<CustomerCreateVM>;
  customer?: Customer;
};

const CustomerBaseInformation = ({ register, errors, customer }: Props) => {
  return (
    <>
      <Input
        labelText="Email"
        field="email"
        defaultValue={customer?.email}
        register={register}
        registerOptions={{
          required: { value: true, message: 'Email is required' },
          pattern: {
            value: EMAIL_PATTERN,
            message: 'Please provide correct email',
          },
        }}
        error={errors.email?.message}
      />
      <Input
        labelText="First name"
        field="firstName"
        defaultValue={customer?.firstName}
        register={register}
        registerOptions={{
          required: { value: true, message: 'First name is required' },
        }}
        error={errors.firstName?.message}
      />
      <Input
        labelText="Last name"
        field="lastName"
        defaultValue={customer?.lastName}
        register={register}
        registerOptions={{
          required: { value: true, message: 'Last name is required' },
        }}
        error={errors.lastName?.message}
      />
    </>
  );
};

export default CustomerBaseInformation;
