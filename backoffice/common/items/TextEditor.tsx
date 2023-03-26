import dynamic from 'next/dynamic';

const ReactQuill = dynamic(import('react-quill'), { ssr: false });

export interface TextEditorProps {
  field: string;
  labelText: string;
  defaultValue?: string;
  error?: string;
  setValue: (_value: string) => void;
}

export default function TextEditor({
  field,
  labelText,
  defaultValue,
  error,
  setValue,
}: TextEditorProps) {
  const handleChangeValue = (value: string) => {
    setValue(value);
  };

  return (
    <div className="mb-3">
      <label className="form-label" htmlFor={field}>
        {labelText}
      </label>
      <ReactQuill
        className="text-editor"
        defaultValue={defaultValue}
        onChange={handleChangeValue}
      />
      <p className="error-field mt-1">{error}</p>
    </div>
  );
}
