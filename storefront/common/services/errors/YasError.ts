export class YasError extends Error {
  status?: number;
  title?: string;
  detail?: string;
  fieldErrors?: string[];

  constructor({
    status,
    statusCode,
    title = 'Unknown error',
    detail = 'unknown',
    fieldErrors = [],
  }: {
    status?: number;
    statusCode?: string;
    title?: string;
    detail?: string;
    fieldErrors?: string[];
  } = {}) {
    super(fieldErrors.length > 0 ? fieldErrors[0] : detail);
    this.status = status ?? (statusCode ? parseInt(statusCode) : 500);
    this.title = title;
    this.detail = detail;
    this.fieldErrors = fieldErrors;
  }
}
