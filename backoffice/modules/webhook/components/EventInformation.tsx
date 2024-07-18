import { useEffect, useState } from 'react';
import { UseFormGetValues, UseFormSetValue } from 'react-hook-form';

import { WebhookEvent } from '@webhookModels/Event';
import { Webhook } from '@webhookModels/Webhook';
import { getEvents } from '@webhookServices/EventService';

type Props = {
  events?: WebhookEvent[];
  setValue: UseFormSetValue<Webhook>;
  getValue: UseFormGetValues<Webhook>;
};

const EventInformation = ({ events, setValue, getValue: _getValue }: Props) => {
  const [allEvents, setAllEvents] = useState<WebhookEvent[]>([]);
  let [latestCheckedEvent, setLatestCheckedEvent] = useState<WebhookEvent[]>([]);
  let listCheckEvent: WebhookEvent[] = [];

  useEffect(() => {
    getEvents().then((data) => {
      setAllEvents(data);
      if (events !== undefined && latestCheckedEvent.length === 0) {
        events.map((item: any) => {
          latestCheckedEvent.push(item);
        });
        setLatestCheckedEvent(latestCheckedEvent);
      }
    });
  }, []);

  function checkedTrue(id: number) {
    const found = latestCheckedEvent.find((element) => element.id === id);
    if (found === undefined) return false;
    return true;
  }

  const onUpdateCheckedEvent = (e: any) => {
    const checkedEventId = Number(e.target.value);
    if (e.target.checked) {
      const webhookEvent = allEvents.find((element) => element.id === checkedEventId);
      if (webhookEvent !== undefined) {
        setLatestCheckedEvent([webhookEvent, ...latestCheckedEvent]);
        latestCheckedEvent = [webhookEvent, ...latestCheckedEvent];
      }
    } else {
      latestCheckedEvent = latestCheckedEvent.filter((item) => item.id !== checkedEventId);
      setLatestCheckedEvent(latestCheckedEvent);
    }
    setValue('events', latestCheckedEvent);
  };

  return (
    <div className="choice-event">
      <ul style={{ listStyleType: 'none' }}>
        {allEvents.map((event, index) => (
          <li key={event.id}>
            <input
              value={event.id || ''}
              type="checkbox"
              name="event"
              checked={checkedTrue(event.id) === true ? true : false}
              id={`checkbox-${event.id}`}
              onChange={onUpdateCheckedEvent}
            />
            <label
              htmlFor={`checkbox-${event.id}`}
              style={{
                paddingLeft: '15px',
                fontSize: '1rem',
                paddingTop: '10px',
                paddingBottom: '5px',
              }}
            >
              {' '}
              {event.name}
            </label>
          </li>
        ))}
      </ul>
    </div>
  );
};

export default EventInformation;
