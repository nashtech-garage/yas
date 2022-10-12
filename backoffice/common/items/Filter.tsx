import React from 'react';
import { Dropdown, Form } from 'react-bootstrap';
import { Filter } from '../../modules/catalog/models/Filter';
import styles from '../../styles/Filter.module.css';

type Props = { filter: Filter };

const Filter = ({ filter }: Props) => {
  return (
    <Dropdown>
      <Dropdown.Toggle
        id={styles.filterButton}
        className={`btn-danger d-flex align-items-center justity-content-center mr-2 ${styles.filterButton}`}
      >
        <p className="flex-grow-1 font-weight-bold">{filter.name}</p>
        <div className={styles.fbIcon}>{filter.icon}</div>
      </Dropdown.Toggle>
      <Dropdown.Menu className={`${styles.categoryTypeFilterMenu} ${styles.dropdownMenu}`}>
        <Form>
          <Dropdown.Item
            id={styles.dropdownItem}
            // onClick={() => filter.setFilter('')}
            className={`${styles.categoryTypeFilterMenuItem} ${styles.dropdownItem}`}
          >
            <Form.Check
              type="checkbox"
              id="checkbox-all"
              className="mx-4 my-2 font-weight-bold"
              label="All"
              // checked={props.currentFilter === 'All'}
              // onChange={() => handleFilter('All')}
            />
          </Dropdown.Item>
          {/* {props.data !== undefined &&
            props.data.map((item, index) => {
              return (
                <Dropdown.Item
                  onClick={() => handleFilter(item.id)}
                  key={index}
                  className="categoryTypeFilterMenuItem"
                >
                  <Form.Check
                    type="checkbox"
                    id={`checkbox-${item.id}`}
                    className="mx-4 my-2 font-weight-bold"
                    label={item.name}
                    checked={props.currentFilter === item.id}
                    onChange={() => handleFilter(item.id)}
                  />
                </Dropdown.Item>
              );
            })} */}
        </Form>
      </Dropdown.Menu>
    </Dropdown>
  );
};

export default Filter;
