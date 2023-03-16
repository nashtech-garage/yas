import { toastSuccess, toastError } from './ToastService';

import {
  ResponseStatus,
  ToastVariant,
  HAVE_BEEN_DELETED,
  DELETE_FAILED,
  UPDATE_SUCCESSFULLY,
  UPDATE_FAILED,
  CREATE_SUCCESSFULLY,
  CREATE_FAILED,
  ResponseTitle,
} from '../../../constants/Common';

//Handle deleting response message from API
export const handleDeletingResponse = (response: any, itemName: string | number) => {
  if (response.status === ResponseStatus.SUCCESS) {
    toastSuccess(itemName + HAVE_BEEN_DELETED);
    //setToastProperties(itemName + HAVE_BEEN_DELETED, ToastVariant.SUCCESS, true);
  } else if (response.title === ResponseTitle.NOT_FOUND) {
    toastError(response.detail);
    //setToastProperties(response.detail, ToastVariant.ERROR, true);
  } else if (response.title === ResponseTitle.BAD_REQUEST) {
    toastError(response.detail);
    //setToastProperties(response.detail, ToastVariant.ERROR, true);
  } else {
    toastError(DELETE_FAILED);
    //setToastProperties(DELETE_FAILED, ToastVariant.ERROR, true);
  }
};

//Handle updating response message from API
export const handleUpdatingResponse = (response: any) => {
  if (response.status === ResponseStatus.SUCCESS) {
    toastSuccess(UPDATE_SUCCESSFULLY);
    //useRouter().replace(url);
  } else if (response.title === ResponseTitle.NOT_FOUND) {
    toastError(response.detail);
    // useRouter().replace(url);
  } else if (response.title === ResponseTitle.BAD_REQUEST) {
    toastError(response.detail);
  } else {
    toastError(UPDATE_FAILED);
    //  useRouter().replace(url);
  }
};

/*Handle creating response message from API
 * setup info global for toast
 *
 */
export const handleCreatingResponse = async (response: any) => {
  if (response.status === ResponseStatus.CREATED) {
    toastSuccess(CREATE_SUCCESSFULLY);
    // useRouter().replace(url);
  } else if (response.status === ResponseStatus.BAD_REQUEST) {
    response = await response.json();
    toastError(response.detail);
  } else {
    toastError(CREATE_FAILED);
    //useRouter().replace(url);
  }
};
