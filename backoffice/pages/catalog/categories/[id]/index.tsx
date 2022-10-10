import type { NextPage } from 'next';
import React, { useState } from 'react';
import { Tab, Tabs } from 'react-bootstrap';
import Editcategory from '../[id]/edit';
import ListProduct from '../[id]/listProduct';
const CategoryGetById: NextPage = () => {
  const [tabKey, setTabKey] = useState('one');
  return (
    <>
      <div className="row mt-5">
        <div className="col-md-8">
          <h2>Edit Category</h2>
          <div className="isolate-scope" style={{ marginTop: '50px' }}>
            <Tabs activeKey={tabKey} onSelect={(e: any) => setTabKey(e)}>
              <Tab eventKey="one" title="General Information">
                <Editcategory />
              </Tab>
              <Tab eventKey="two" title="Products">
                <ListProduct />
              </Tab>
            </Tabs>
          </div>
        </div>
      </div>
    </>
  );
};

export default CategoryGetById;
