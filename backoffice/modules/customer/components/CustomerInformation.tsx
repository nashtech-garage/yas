import { Input, Select } from '@commonItems/Input';
import { EMAIL_PATTERN, USER_NAME_PATTERN } from 'modules/catalog/constants/validationPattern';
import { FieldErrorsImpl, UseFormRegister, UseFormWatch } from 'react-hook-form';
import { CustomerCreateVM } from '../models/Customer';

type Props = {
  register: UseFormRegister<CustomerCreateVM>;
  errors: FieldErrorsImpl<CustomerCreateVM>;
  watch: UseFormWatch<CustomerCreateVM>;
  customer?: CustomerCreateVM;
};

const CustomerInformation = ({ register, errors, watch, customer }: Props) => {
  const password = watch('password');

  return (
    <>
      <Input
        labelText="Username"
        field="username"
        defaultValue={customer?.username}
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

      <Select
        labelText="Role"
        field="role"
        placeholder="Select Role"
        defaultValue={customer?.role}
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
