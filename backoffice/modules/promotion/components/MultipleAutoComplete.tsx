import { useState } from 'react';
import { RegisterOptions, UseFormRegister } from 'react-hook-form';

type props = {
  labelText: string;
  field: string;
  register: UseFormRegister<any>;
  registerOptions?: RegisterOptions;
  // error: string,
  defaultValue?: any;
  options?: any[];
  fetchOptions: (data: any) => any;
  onSelect: (value: any) => void;
  onRemoveElement: (value: any) => void;
  optionSelectedIds: number[];
  isSubmitting: boolean;
  addedOptions?: any[];
};

const MultipleAutoComplete = (props: props) => {
  const [isFocusing, setIsFocusing] = useState(false);
  const [optionSelecteds, setOptionSelecteds] = useState<any[]>(props.addedOptions ?? []);
  const queryData = (query: string) => {
    props.fetchOptions(query);
  };

  const handleFocus = (isFocusing: boolean) => {
    setTimeout(() => {
      if (!props.isSubmitting) {
        setIsFocusing(isFocusing);
      } else {
        setIsFocusing(false);
      }
    }, 150);
  };

  const selectOption = (option: any) => {
    setOptionSelecteds([...optionSelecteds, option]);
    props.onSelect(option.id);
  };

  const removeOption = (option: any) => {
    setOptionSelecteds(optionSelecteds.filter((item) => item.id !== option.id));
    props.onRemoveElement(option.id);
  };

  return (
    <div className="autocomplete-container">
      <label className="form-label" htmlFor={props.field}>
        {props.labelText}
      </label>
      <div>
        <input
          type="text"
          id={props.field}
          {...props.register(props.field, props.registerOptions)}
          defaultValue={props.defaultValue}
          onChange={(e) => queryData(e.target.value)}
          onFocus={() => handleFocus(true)}
          onBlur={() => handleFocus(false)}
          className="form-control"
        />
        {isFocusing && props.options!.length > 0 && (
          <div className="autocomplete-list" style={{ maxHeight: '200px', overflowY: 'scroll' }}>
            {props.options!.map((option, index) => (
              <div
                key={option.id}
                aria-hidden="true"
                className={`dropdown-item ${
                  props.optionSelectedIds.includes(option.id) ? 'selected-options' : ''
                }`}
                onClick={() => selectOption(option)}
              >
                {option.name}
              </div>
            ))}
          </div>
        )}
      </div>

      {optionSelecteds.length > 0 && (
        <div className="mt-3">
          <span className="form-label">Selected {props.labelText}</span>
          {optionSelecteds.map((option, index) => (
            <div
              className="d-flex align-items-center"
              style={{
                marginBottom: '5px',
                borderRadius: '5px',
                border: '1px solid #ccc',
                padding: '5px',
              }}
              key={option.id}
            >
              <div className="mr-3" style={{ display: 'inline', marginRight: '15px' }}>
                {option.name}
              </div>
              <span
                aria-hidden="true"
                className="fa fa-remove"
                onClick={() => removeOption(option)}
              ></span>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default MultipleAutoComplete;
