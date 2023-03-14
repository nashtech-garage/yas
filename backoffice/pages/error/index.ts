import { toast } from "react-toastify";
import { Error } from "../../modules/catalog/models/Error";

export function showToastError(res: Error) {
    const error: Error = res;
        toast.error(error.detail + ': ' + error.fieldErrors[0], {
          position: 'top-right',
          autoClose: 2000,
          closeOnClick: true,
          pauseOnHover: false,
          theme: 'colored',
        });
}
