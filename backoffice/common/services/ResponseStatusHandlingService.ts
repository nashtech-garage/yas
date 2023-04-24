import { toastError, toastSuccess } from './ToastService';

import {
  CREATE_FAILED,
  CREATE_SUCCESSFULLY,
  DELETE_FAILED,
  HAVE_BEEN_DELETED,
  ResponseStatus,
  ResponseTitle,
  UPDATE_FAILED,
  UPDATE_SUCCESSFULLY,
} from '@constants/Common';

//Handle deleting response message from API
export const handleDeletingResponse = (response: any, itemName: string | number) => {
  if (response.status === ResponseStatus.SUCCESS) {
    toastSuccess(itemName + HAVE_BEEN_DELETED);
  } else if (response.title === ResponseTitle.NOT_FOUND) {
    toastError(response.detail);
  } else if (response.title === ResponseTitle.BAD_REQUEST) {
    toastError(response.detail);
  } else {
    toastError(DELETE_FAILED);
  }
};

//Handle updating response message from API
export const handleUpdatingResponse = (response: any) => {
  if (response.status === ResponseStatus.SUCCESS) {
    toastSuccess(UPDATE_SUCCESSFULLY);
  } else if (response.title === ResponseTitle.NOT_FOUND) {
    toastError(response.detail);
  } else if (response.title === ResponseTitle.BAD_REQUEST) {
    toastError(response.detail);
  } else {
    toastError(UPDATE_FAILED);
  }
};

/*Handle creating response message from API
 * setup info global for toast
 *
 */
export const handleCreatingResponse = async (response: any) => {
  if (response.status === ResponseStatus.CREATED) {
    toastSuccess(CREATE_SUCCESSFULLY);
  } else if (response.status === ResponseStatus.BAD_REQUEST) {
    response = await response.json();
    toastError(response.detail);
  } else {
    toastError(CREATE_FAILED);
  }
};
