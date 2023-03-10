import { faCircleCheck, faCircleExclamation } from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import clsx from 'clsx';
import { useMemo } from 'react';
import { ToastContainer } from 'react-bootstrap';
import Toast from 'react-bootstrap/Toast';
import styles from '../../styles/CustomToast.module.css';
import { TOAST_DURATION, ToastVariant, ResponseStatus } from '../../constants/Common';

const CustomToast = ({
  header = '',
  body = '',
  show = true,
  setShow = () => {},
  variant = ToastVariant.ERROR,
  ...rest
}) => {
  const renderIcon = useMemo(() => {
    switch (variant) {
      case ResponseStatus.SUCCESS:
        return <FontAwesomeIcon icon={faCircleCheck} />;
      case ResponseStatus.WARNING:
      case ResponseStatus.ERROR:
        return <FontAwesomeIcon icon={faCircleExclamation} />;
      default:
        return '';
    }
  }, [variant]);
  return (
    <ToastContainer
      containerPosition="fixed"
      position="top-end"
      className={clsx('p-3', styles.wrapper, styles[variant])}
    >
      <Toast onClose={() => setShow(false)} show={show} autohide delay={TOAST_DURATION} {...rest}>
        {header && (
          <Toast.Header className={styles.header}>
            {renderIcon}
            {header}
          </Toast.Header>
        )}
        {body && <Toast.Body className={styles.body}>{body}</Toast.Body>}
      </Toast>
    </ToastContainer>
  );
};

export default CustomToast;
