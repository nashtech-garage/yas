import type { NextPage } from 'next';
import { Webhook } from '@webhookModels/Webhook';
import { createWebhook } from '@webhookServices/WebhookService';
import React from 'react';
import { useForm } from 'react-hook-form';
import Link from 'next/link';
import { useRouter } from 'next/router';
import WebhookInformation from '@webhookComponents/WebhookInformation';
import { WEBHOOKS_URL } from 'constants/Common';
import { handleCreatingResponse } from '@commonServices/ResponseStatusHandlingService';
import EventInformation from '@webhookComponents/EventInformation';
import { ContentType } from '@webhookModels/ContentType';

const WebhookCreate: NextPage = () => {
  const router = useRouter();
  const {
    register,
    handleSubmit,
    getValues,
    formState: { errors },
    setValue,
    trigger,
  } = useForm<Webhook>();
  const handleSubmitWebhook = async (webhook: Webhook) => {
    let payload: Webhook = {
      id: 0,
      payloadUrl: webhook.payloadUrl,
      contentType: ContentType.APPLICATION_JSON,
      secret: webhook.secret,
      isActive: webhook.isActive,
      events: webhook.events
    };

    let response = await createWebhook(payload);
    handleCreatingResponse(response);
    router.replace(WEBHOOKS_URL);
  };

  return (
    <>
      <div className="row mt-5">
        <div className="col-md-8">
          <h2>Create Webhook</h2>
          <form onSubmit={handleSubmit(handleSubmitWebhook)}>
            <WebhookInformation
              register={register}
              errors={errors}
              setValue={setValue}
              trigger={trigger}
            />
            <label className="form-label" htmlFor="events">
              Events
            </label>
            <EventInformation setValue={setValue} getValue={getValues} />
            <button className="btn btn-primary" type="submit">
              Save
            </button>
            <Link href="/webhook">
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

export default WebhookCreate;
