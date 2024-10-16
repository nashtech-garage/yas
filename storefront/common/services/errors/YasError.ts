export class YasError extends Error {
  status?: number;
  title?: string;
  detail?: string;
  fieldErrors?: string[];

  constructor({
    status,
    statusCode,
    title,
    detail,
    fieldErrors,
  }: {
    status?: number;
    statusCode?: string;
    title?: string;
    detail?: string;
    fieldErrors?: string[];
  }) {
    super(fieldErrors ? fieldErrors[0] : detail ?? 'An error occurred');
    this.status = status ?? (statusCode ? parseInt(statusCode) : 500);
    this.title = title ?? 'Unknown error';
    this.detail = detail ?? 'unknown';
    this.fieldErrors = fieldErrors ?? [];
  }
}
