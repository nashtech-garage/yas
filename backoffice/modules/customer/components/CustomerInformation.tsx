import { Input, Select } from '@commonItems/Input';
import { USER_NAME_PATTERN } from 'modules/catalog/constants/validationPattern';
import { FieldErrorsImpl, UseFormRegister, UseFormWatch } from 'react-hook-form';
import { CustomerCreateVM } from '../models/Customer';
import CustomerBaseInformation from './CustomerBaseInformation';

type Props = {
  register: UseFormRegister<CustomerCreateVM>;
  errors: FieldErrorsImpl<CustomerCreateVM>;
  watch: UseFormWatch<CustomerCreateVM>;
};

const CustomerInformation = ({ register, errors, watch }: Props) => {
  const password = watch('password');

  return (
    <>
      <Input
        labelText="Username"
        field="username"
        register={register}
        registerOptions={{
          required: { value: true, message: 'Username is required' },
          pattern: {
            value: USER_NAME_PATTERN,
            message: 'Username must not contain special characters',
          },
        }}
        error={errors.username?.message}
      />

      <Input
        labelText="Password"
        field="password"
        type="password"
        register={register}
        registerOptions={{
          required: { value: true, message: 'Password is required' },
        }}
        error={errors.password?.message}
      />
      <Input
        labelText="Confirm password"
        field="confirmPassword"
        type="password"
        register={register}
        registerOptions={{
          required: { value: true, message: 'Confirm password is required' },
          validate: (value) => value === password || 'Passwords do not match',
        }}
        error={errors.confirmPassword?.message}
      />

      <CustomerBaseInformation register={register} errors={errors} />

      <Select
        labelText="Role"
        field="role"
        placeholder="Select Role"
        register={register}
        registerOptions={{
          required: { value: true, message: 'Role is required' },
        }}
        error={errors.role?.message}
        options={[
          { value: 'ADMIN', label: 'Admin' },
          { value: 'CUSTOMER', label: 'Customer' },
        ]}
      />
    </>
  );
};

export default CustomerInformation;
