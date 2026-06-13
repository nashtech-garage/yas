import type { NextPage } from 'next';
import Link from 'next/link';
import { useEffect, useState } from 'react';
import { Button, Table } from 'react-bootstrap';

import ModalDeleteCustom from '@commonItems/ModalDeleteCustom';
import { handleDeletingResponse } from '@commonServices/ResponseStatusHandlingService';
import type { Webhook } from '@webhookModels/Webhook';
import { deleteWebhook, getWebhooks } from '@webhookServices/WebhookService';
import { DEFAULT_PAGE_NUMBER, DEFAULT_PAGE_SIZE, WEBHOOKS_URL } from 'constants/Common';
import Pagination from 'common/components/Pagination';
import usePagination from '@commonHooks/usePagination';

const WebhookList: NextPage = () => {
  const [webhookClassIdWantToDelete, setWebhookIdWantToDelete] = useState<number>(-1);
  const [webhookClassNameWantToDelete, setWebhookNameWantToDelete] = useState<string>('');
  const [showModalDelete, setShowModalDelete] = useState<boolean>(false);
  const [webhooks, setWebhooks] = useState<Webhook[]>([]);
  const [isLoading, setLoading] = useState(false);

  const { pageNo, totalPage, setTotalPage, changePage } = usePagination();

  const handleClose: any = () => setShowModalDelete(false);
  const handleDelete: any = () => {
    if (webhookClassIdWantToDelete == -1) {
      return;
    }
    deleteWebhook(webhookClassIdWantToDelete)
      .then((response) => {
        setShowModalDelete(false);
        handleDeletingResponse(response, webhookClassNameWantToDelete);
        changePage({ selected: DEFAULT_PAGE_NUMBER });
        getListWebhook();
      })
      .catch((error) => console.log(error));
  };

  const getListWebhook = () => {
    getWebhooks(pageNo, DEFAULT_PAGE_SIZE)
      .then((data) => {
        setTotalPage(data.totalPages);
        setWebhooks(data.webhooks);
        setLoading(false);
      })
      .catch((error) => console.log(error));
  };

  useEffect(() => {
    setLoading(true);
    getListWebhook();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [pageNo]);

  if (isLoading) return <p>Loading...</p>;
  if (!webhooks) return <p>No Webhook</p>;
  return (
    <>
      <div className="row mt-5">
        <div className="col-md-8">
          <h2 className="text-danger font-weight-bold mb-3">Webhook</h2>
        </div>
        <div className="col-md-4 text-right">
          <Link href={`${WEBHOOKS_URL}/create`}>
            <Button>Create Webhook</Button>
          </Link>
        </div>
      </div>
      <Table striped bordered hover>
        <thead>
          <tr>
            <th>#</th>
            <th>Payload Url</th>
            <th>Content Type</th>
            <th>Status</th>
          </tr>
        </thead>
        <tbody>
          {webhooks.map((webhook) => (
            <tr key={webhook.id}>
              <td>{webhook.id}</td>
              <td>{webhook.payloadUrl}</td>
              <td>{webhook.contentType}</td>
              <td>
                <div
                  className={
                    webhook.isActive
                      ? `border border-success btn-sm text-success`
                      : `border border-warning btn-sm text-warning`
                  }
                >
                  {webhook.isActive ? `ACTIVE` : `INACTIVE`}
                </div>
              </td>
              <td>
                <Link href={`${WEBHOOKS_URL}/${webhook.id}/edit`}>
                  <button className="btn btn-outline-primary btn-sm" type="button">
                    Edit
                  </button>
                </Link>
                &nbsp;
                <button
                  className="btn btn-outline-danger btn-sm"
                  type="button"
                  onClick={() => {
                    setShowModalDelete(true);
                    setWebhookIdWantToDelete(webhook.id);
                    setWebhookNameWantToDelete(webhook.payloadUrl);
                  }}
                >
                  Delete
                </button>
              </td>
            </tr>
          ))}
        </tbody>
      </Table>
      <ModalDeleteCustom
        showModalDelete={showModalDelete}
        handleClose={handleClose}
        nameWantToDelete={webhookClassNameWantToDelete}
        handleDelete={handleDelete}
        action="delete"
      />
      {totalPage > 0 && (
        <Pagination pageNo={pageNo} totalPage={totalPage} onPageChange={changePage} />
      )}
    </>
  );
};

export default WebhookList;
