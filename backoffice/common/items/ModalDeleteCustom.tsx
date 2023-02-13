import React from 'react';
import { Button, Modal } from 'react-bootstrap';

type Props = {
  showModalDelete: boolean;
  handleClose: () => void;
  nameWantToDelete: string;
  handleDelete: () => void;
  action: string;
};

const ModalDeleteCustom = ({
  showModalDelete,
  nameWantToDelete,
  action,
  handleClose,
  handleDelete,
}: Props) => {
  return (
    <Modal show={showModalDelete} onHide={handleClose}>
      <Modal.Body>{`Are you sure you want to ${action} this ${nameWantToDelete} ?`}</Modal.Body>
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
