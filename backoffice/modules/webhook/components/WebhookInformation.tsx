import { FieldErrorsImpl, UseFormRegister, UseFormSetValue, UseFormTrigger } from 'react-hook-form';
import { CheckBox } from 'common/items/Input';
import { Input } from 'common/items/Input';
import { Webhook } from '../models/Webhook';
import { ContentType } from '@webhookModels/ContentType';

type Props = {
  register: UseFormRegister<Webhook>;
  errors: FieldErrorsImpl<Webhook>;
  setValue: UseFormSetValue<Webhook>;
  trigger: UseFormTrigger<Webhook>;
  webhook?: Webhook;
};

const WebhookInformation = ({ register, errors, setValue, trigger, webhook }: Props) => {

  return (
    <>
      <Input
        labelText="Payload URL"
        field="payloadUrl"
        defaultValue={webhook?.payloadUrl}
        register={register}
        registerOptions={{
          required: { value: true, message: 'Payload URL is required' },
        }}
        error={errors.payloadUrl?.message}
      />
      <Input
        labelText="Content Type"
        field="contentType"
        defaultValue={ContentType.APPLICATION_JSON}
        register={register}
        disabled={true}
      />
      <Input labelText="Secret" field="secret" defaultValue={webhook?.secret} register={register} />
      <CheckBox
        labelText="Active"
        field="isActive"
        register={register}
        defaultChecked={webhook?.isActive}
      />
    </>
  );
};

export default WebhookInformation;
