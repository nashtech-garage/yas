import React from 'react';
import { Button, Modal } from 'react-bootstrap';

type Props = {
  showModalDelete: boolean;
  handleClose: () => void;
  handleDelete: () => void;
  action: string;
};

const ModalDeleteCustom = ({
  showModalDelete,
  action,
  handleClose,
  handleDelete,
}: Props) => {
  return (
    <Modal show={showModalDelete} onHide={handleClose}>
      <Modal.Body>{`Are you sure you want to ${action} ?`}</Modal.Body>
      <Modal.Footer>
        <Button variant="outline-secondary" onClick={handleClose}>
          Close
        </Button>
        <Button variant="danger" onClick={handleDelete}>
          Delete
        </Button>
      </Modal.Footer>
    </Modal>
  );
};

export default ModalDeleteCustom;
