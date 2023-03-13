import { useContext } from 'react';
import { ToastContext } from '../../common/components/ToastContext';

//Custom hook for deleting context
export const useDeletingContext = () => {
  const { toastVariant, toastHeader, showToast, setShowToast, handleDeletingResponse } =
    useContext(ToastContext);

  return { toastVariant, toastHeader, showToast, setShowToast, handleDeletingResponse };
};

//Custom hook for updating context
export const useUpdatingContext = () => {
  const { handleUpdatingResponse } = useContext(ToastContext);

  return { handleUpdatingResponse };
};

//Custom hook for creating context
export const useCreatingContext = () => {
  const { toastVariant, toastHeader, showToast, setShowToast, handleCreatingResponse } = useContext(ToastContext);

  return { toastVariant, toastHeader, showToast, setShowToast, handleCreatingResponse };
};
