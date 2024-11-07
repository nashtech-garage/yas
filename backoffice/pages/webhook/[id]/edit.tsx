import { NextPage } from 'next';
import Link from 'next/link';
import { useRouter } from 'next/router';
import { useEffect, useState } from 'react';
import { useForm } from 'react-hook-form';

import { handleUpdatingResponse } from '@commonServices/ResponseStatusHandlingService';
import { toastError } from '@commonServices/ToastService';
import WebhookInformation from '@webhookComponents/WebhookInformation';
import EventInformation from '@webhookComponents/EventInformation';

import { Webhook } from '@webhookModels/Webhook';
import { updateWebhook, getWebhook } from '@webhookServices/WebhookService';
import { WEBHOOKS_URL } from 'constants/Common';

const WebhookEdit: NextPage = () => {
  const router = useRouter();
  const {
    register,
    handleSubmit,
    formState: { errors },
    setValue,
    getValues,
    trigger,
  } = useForm<Webhook>();
  const [webhook, setWebhook] = useState<Webhook>();
  const [isLoading, setLoading] = useState(false);
  const { id } = router.query;
  const handleSubmitEdit = async (webhook: Webhook) => {
    if (id) {
      let payload: Webhook = {
        id: 0,
        payloadUrl: webhook.payloadUrl,
        contentType: webhook.contentType,
        secret: webhook.secret,
        isActive: webhook.isActive,
        events: webhook.events,
      };

      updateWebhook(+id, payload)
        .then((response) => {
          handleUpdatingResponse(response);
          router.replace(WEBHOOKS_URL).catch((error) => console.log(error));
        })
        .catch((error) => console.log(error));
    }
  };

  useEffect(() => {
    if (id) {
      setLoading(true);
      getWebhook(+id)
        .then((data) => {
          if (data.id) {
            setWebhook(data);
            setLoading(false);
          } else {
            toastError(data?.payloadUrl);
            setLoading(false);
            router.push(WEBHOOKS_URL).catch((error) => console.log(error));
          }
        })
        .catch((error) => console.log(error));
    }
  }, [id, router]);

  if (isLoading) return <p>Loading...</p>;
  if (!webhook) return <></>;
  return (
    <>
      <div className="row mt-5">
        <div className="col-md-8">
          <h2>Edit Webhook: {id}</h2>
          <form onSubmit={handleSubmit(handleSubmitEdit)}>
            <WebhookInformation
              register={register}
              errors={errors}
              setValue={setValue}
              trigger={trigger}
              webhook={webhook}
            />
            <label className="form-label" htmlFor="events">
              Events
            </label>
            <EventInformation events={webhook.events} setValue={setValue} getValue={getValues} />
            <button className="btn btn-primary" type="submit">
              Save
            </button>
            <Link href="\webhook">
              <button className="btn btn-primary" style={{ background: 'red', marginLeft: '30px' }}>
                Cancel
              </button>
            </Link>
          </form>
        </div>
      </div>
    </>
  );
};

export default WebhookEdit;
