import { Customer } from '../models/Customer';
import { Input } from '../../../common/items/Input';
import Link from 'next/link';
import { UseFormRegister } from 'react-hook-form';

type Props = {
  customer?: Customer;
  register: UseFormRegister<Customer>;
};

const ProfileForm = ({ customer, register }: Props) => {
  return (
    <>
      <Input
        labelText="Username"
        field="username"
        defaultValue={customer?.username}
        register={register}
        disabled
      />
      <Input
        labelText="First name"
        field="firstName"
        defaultValue={customer?.firstName}
        register={register}
      />
      <Input
        labelText="Last name"
        field="lastName"
        defaultValue={customer?.lastName}
        register={register}
      />
      <Input labelText="Email" field="email" defaultValue={customer?.email} register={register} />
      <div className="text-center">
        <button className="btn btn-primary" type="submit">
          Update
        </button>
        <Link href="/customers">
          <button className="btn btn-secondary m-3">Cancel</button>
        </Link>
      </div>
    </>
  );
};

export default ProfileForm;
