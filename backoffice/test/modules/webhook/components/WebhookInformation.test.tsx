import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, fireEvent } from '@testing-library/react';
import WebhookInformation from '../../../../modules/webhook/components/WebhookInformation';
import { ContentType } from '../../../../modules/webhook/models/ContentType';

// Mock Input component
vi.mock('common/items/Input', () => ({
  Input: ({ labelText, field, defaultValue, register, registerOptions, error, disabled }: any) => (
    <div>
      <label htmlFor={field}>{labelText}</label>
      <input
        id={field}
        data-testid={`input-${field}`}
        defaultValue={defaultValue}
        disabled={disabled}
        onChange={(e) => {
          if (registerOptions?.onChange) {
            registerOptions.onChange(e);
          }
          if (register) {
            register(field, registerOptions).onChange(e);
          }
        }}
        onBlur={() => register && register(field, registerOptions).onBlur()}
      />
      {error && <span data-testid={`error-${field}`}>{error}</span>}
    </div>
  ),
  CheckBox: ({ labelText, field, register, defaultChecked }: any) => (
    <div>
      <label htmlFor={field}>
        <input
          id={field}
          type="checkbox"
          data-testid={`checkbox-${field}`}
          defaultChecked={defaultChecked}
          onChange={(e) => register && register(field).onChange(e)}
        />
        {labelText}
      </label>
    </div>
  ),
}));

describe('WebhookInformation', () => {
  const mockRegister = vi.fn((field, options) => ({
    name: field,
    onChange: vi.fn(),
    onBlur: vi.fn(),
    ref: vi.fn(),
  }));

  const mockSetValue = vi.fn();
  const mockTrigger = vi.fn();

  const mockWebhook = {
    id: 1,
    payloadUrl: 'https://example.com/webhook',
    secret: 'my-secret-key',
    isActive: true,
  };

  const defaultProps = {
    register: mockRegister,
    errors: {},
    setValue: mockSetValue,
    trigger: mockTrigger,
    webhook: undefined,
  };

  beforeEach(() => {
    vi.clearAllMocks();
  });

  describe('Rendering', () => {
    it('should render all form fields', () => {
      render(<WebhookInformation {...defaultProps} />);

      expect(screen.getByText('Payload URL')).toBeDefined();
      expect(screen.getByText('Content Type')).toBeDefined();
      expect(screen.getByText('Secret')).toBeDefined();
      expect(screen.getByText('Active')).toBeDefined();
    });

  });


  describe('Error Display', () => {

    it('should not display error when no errors', () => {
      render(<WebhookInformation {...defaultProps} />);

      expect(screen.queryByTestId('error-payloadUrl')).toBeNull();
      expect(screen.queryByTestId('error-secret')).toBeNull();
    });
  });

});