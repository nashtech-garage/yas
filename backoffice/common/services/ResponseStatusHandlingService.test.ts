jest.mock('./ToastService', () => ({
  toastSuccess: jest.fn(),
  toastError: jest.fn(),
}));

import {
  handleCreatingResponse,
  handleDeletingResponse,
  handleResponse,
  handleUpdatingResponse,
} from './ResponseStatusHandlingService';
import {
  CREATE_FAILED,
  DELETE_FAILED,
  HAVE_BEEN_DELETED,
  ResponseStatus,
  ResponseTitle,
  UPDATE_FAILED,
  UPDATE_SUCCESSFULLY,
} from '../../constants/Common';
import { toastError, toastSuccess } from './ToastService';

describe('ResponseStatusHandlingService', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('shows success toast for successful delete', () => {
    handleDeletingResponse({ status: ResponseStatus.SUCCESS }, 'Country');

    expect(toastSuccess).toHaveBeenCalledWith(`Country${HAVE_BEEN_DELETED}`);
  });

  it('shows detail toast when delete returns not found', () => {
    handleDeletingResponse({ title: ResponseTitle.NOT_FOUND, detail: 'Missing' }, 'Country');

    expect(toastError).toHaveBeenCalledWith('Missing');
  });

  it('uses default delete failed message for unknown delete errors', () => {
    handleDeletingResponse({ status: 500 }, 'Country');

    expect(toastError).toHaveBeenCalledWith(DELETE_FAILED);
  });

  it('shows update success toast for successful update', () => {
    handleUpdatingResponse({ status: ResponseStatus.SUCCESS });

    expect(toastSuccess).toHaveBeenCalledWith(UPDATE_SUCCESSFULLY);
  });

  it('uses default update failed message for unknown update errors', () => {
    handleUpdatingResponse({ status: 500 });

    expect(toastError).toHaveBeenCalledWith(UPDATE_FAILED);
  });

  it('handles bad request create response by reading json detail', async () => {
    const response = {
      status: ResponseStatus.BAD_REQUEST,
      json: jest.fn().mockResolvedValue({ detail: 'Validation failed' }),
    };

    await handleCreatingResponse(response);

    expect(response.json).toHaveBeenCalledTimes(1);
    expect(toastError).toHaveBeenCalledWith('Validation failed');
  });

  it('uses default create failed message for non-created and non-bad-request', async () => {
    await handleCreatingResponse({ status: 500 });

    expect(toastError).toHaveBeenCalledWith(CREATE_FAILED);
  });

  it('routes generic response based on ok flag', () => {
    handleResponse({ ok: true }, 'Saved', 'Failed');
    expect(toastSuccess).toHaveBeenCalledWith('Saved');

    handleResponse({ ok: false }, 'Saved', 'Failed');
    expect(toastError).toHaveBeenCalledWith('Failed');
  });
});
