import React, { createContext, useMemo, useState, useCallback } from 'react';
import { useRouter } from 'next/router';
import { ResponseStatus,
  ToastVariant,
  HAVE_BEEN_DELETED,
  DELETE_FAILED,
  UPDATE_SUCCESSFULLY,
  UPDATE_FAILED } from '../../constants/Common';

export const ToastContext = createContext({
  showToast: false,
  toastHeader: '',
  toastVariant: '',
  setToastProperties: () => {
    // Do nothing on default
  },
  handleDeletingResponse: () => {
    // Do nothing on default
  },
  handleUpdatingResponse: () => {
    // Do nothing on default
  }
});

export function ToastProvider(props: React.PropsWithChildren) {
const router = useRouter();
  const [showToast, setShowToast] = useState(true);
  const [toastHeader, setToastHeader] = useState();
  const [toastVariant, setToastVariant] = useState('error');

  const setToastProperties = (header: any, variant: string, show: boolean) => {
    setShowToast(show);
    setToastHeader(header);
    setToastVariant(variant);
  };

  //Handle deleting response message from API
  const handleDeletingResponse = useCallback((response: any, itemName: string) => {
    if (response.status === ResponseStatus.SUCCESS) {
      setToastProperties(itemName + HAVE_BEEN_DELETED, ToastVariant.SUCCESS, true);
    } else if (response.title === ResponseStatus.NOT_FOUND) {
      setToastProperties(response.detail, ToastVariant.ERROR, true);
    } else if (response.title === ResponseStatus.BAD_REQUEST) {
      setToastProperties(response.detail, ToastVariant.ERROR, true);
    } else {
      setToastProperties(DELETE_FAILED, ToastVariant.ERROR, true);
    }
  }, []);

  //Handle updating response message from API
  const handleUpdatingResponse = useCallback((response: any, url: string) => {
    if (response.status === ResponseStatus.SUCCESS) {
      setToastProperties(UPDATE_SUCCESSFULLY, ToastVariant.SUCCESS, true);
      router.replace(url);
    }
    else if (response.title === ResponseStatus.NOT_FOUND) {
      setToastProperties(response.detail, ToastVariant.ERROR, true);
      router.replace(url);
    } else if (response.title === ResponseStatus.BAD_REQUEST) {
      setToastProperties(response.detail, ToastVariant.ERROR, true);
    } else {
      setToastProperties(UPDATE_FAILED, ToastVariant.ERROR, true);
      router.replace(url);
    }
  }, []);

  const state = useMemo(
    () => ({ toastVariant, toastHeader, showToast, setShowToast, handleDeletingResponse, handleUpdatingResponse }),
    [toastVariant, toastHeader, showToast, setShowToast, handleDeletingResponse, handleUpdatingResponse]
  );

  return <ToastContext.Provider value={state}>{props.children}</ToastContext.Provider>;
}
