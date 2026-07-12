import {
  handleCreatingResponse,
  handleDeletingResponse,
  handleResponse,
  handleUpdatingResponse,
} from './ResponseStatusHandlingService';
import {
  CREATE_FAILED,
  CREATE_SUCCESSFULLY,
  DELETE_FAILED,
  HAVE_BEEN_DELETED,
  ResponseStatus,
  ResponseTitle,
  UPDATE_FAILED,
  UPDATE_SUCCESSFULLY,
} from '../../constants/Common';
import { toastError, toastSuccess } from './ToastService';

vi.mock('./ToastService', () => ({
  toastSuccess: vi.fn(),
  toastError: vi.fn(),
}));

describe('ResponseStatusHandlingService', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('handles delete success', () => {
    handleDeletingResponse({ status: ResponseStatus.SUCCESS }, 'Brand A');
    expect(toastSuccess).toHaveBeenCalledWith('Brand A' + HAVE_BEEN_DELETED);
  });

  it('handles delete bad request and fallback error', () => {
    handleDeletingResponse({ title: ResponseTitle.BAD_REQUEST, detail: 'bad' }, 'X');
    expect(toastError).toHaveBeenCalledWith('bad');

    handleDeletingResponse({ title: 'UNKNOWN' }, 'X');
    expect(toastError).toHaveBeenCalledWith(DELETE_FAILED);
  });

  it('handles update success and not found error', () => {
    handleUpdatingResponse({ status: ResponseStatus.SUCCESS });
    expect(toastSuccess).toHaveBeenCalledWith(UPDATE_SUCCESSFULLY);

    handleUpdatingResponse({ title: ResponseTitle.NOT_FOUND, detail: 'not found' });
    expect(toastError).toHaveBeenCalledWith('not found');
  });

  it('handles update fallback error', () => {
    handleUpdatingResponse({ title: 'UNEXPECTED' });
    expect(toastError).toHaveBeenCalledWith(UPDATE_FAILED);
  });

  it('handles creating success, bad request and fallback error', async () => {
    await handleCreatingResponse({ status: ResponseStatus.CREATED });
    expect(toastSuccess).toHaveBeenCalledWith(CREATE_SUCCESSFULLY);

    const badRequestResponse = {
      status: ResponseStatus.BAD_REQUEST,
      json: vi.fn().mockResolvedValue({ detail: 'invalid payload' }),
    };
    await handleCreatingResponse(badRequestResponse);
    expect(toastError).toHaveBeenCalledWith('invalid payload');

    await handleCreatingResponse({ status: 500 });
    expect(toastError).toHaveBeenCalledWith(CREATE_FAILED);
  });

  it('handles generic response success and error', () => {
    handleResponse({ ok: true }, 'ok message', 'error message');
    expect(toastSuccess).toHaveBeenCalledWith('ok message');

    handleResponse({ ok: false }, 'ok message', 'error message');
    expect(toastError).toHaveBeenCalledWith('error message');
  });
});
