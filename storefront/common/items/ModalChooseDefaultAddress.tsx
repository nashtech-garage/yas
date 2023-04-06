import { Button, Modal } from 'react-bootstrap';

type Props = {
  showModalChooseDefaultAddress: boolean;
  handleClose: () => void;
  handleChoose: () => void;
};

const ModalChooseDefaultAddress = ({
  showModalChooseDefaultAddress,
  handleClose,
  handleChoose,
}: Props) => {
  return (
    <Modal show={showModalChooseDefaultAddress} onHide={handleClose}>
      <Modal.Body>{`Are you sure you want to choose this address as default ?`}</Modal.Body>
      <Modal.Footer>
        <Button variant="outline-secondary" onClick={handleClose}>
          No
        </Button>
        <Button variant="danger" onClick={handleChoose}>
          Yes
        </Button>
      </Modal.Footer>
    </Modal>
  );
};

export default ModalChooseDefaultAddress;
