import React from 'react';
import { Dropdown } from 'react-bootstrap';
import { HiFilter } from 'react-icons/hi';

type Props = {};

const Filter = (props: Props) => {
  return (
    <Dropdown>
      <Dropdown.Toggle className="btn-danger filter-button d-flex align-items-center justity-content-center margin-left-12px">
        <p className="flex-grow-1 font-weight-bold">po</p>
        <div className="fb-icon">
          <HiFilter />
        </div>
      </Dropdown.Toggle>
    </Dropdown>
  );
};

export default Filter;
