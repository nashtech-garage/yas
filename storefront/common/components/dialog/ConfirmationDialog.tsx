import Modal from 'react-bootstrap/Modal';
import Button from 'react-bootstrap/Button';

type DialogProps = {
  isOpen?: boolean;
  title?: string;
  children: JSX.Element;
  isShowOk?: boolean;
  isShowCancel?: boolean;
  okText?: string;
  cancelText?: string;
  ok: () => void;
  cancel: () => void;
};

export default function ConfirmationDialog(props: DialogProps) {
  const {
    isOpen,
    title,
    children,
    okText,
    cancelText,
    isShowOk = true,
    isShowCancel = true,
    ok,
    cancel,
  } = props;

  const handleOk = (): void => {
    ok();
  };

  const handleCancel = (): void => {
    cancel();
  };

  return (
    <>
      <Modal show={isOpen} onHide={cancel}>
        <Modal.Header closeButton>
          <Modal.Title>{title}</Modal.Title>
        </Modal.Header>
        <Modal.Body>{children}</Modal.Body>
        <Modal.Footer>
          {isShowCancel && (
            <Button variant="secondary" onClick={handleCancel}>
              {cancelText}
            </Button>
          )}
          {isShowOk && (
            <Button variant="primary" onClick={handleOk}>
              {okText}
            </Button>
          )}
        </Modal.Footer>
      </Modal>
    </>
  );
}
