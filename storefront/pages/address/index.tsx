import { NextPage } from 'next';
import Head from 'next/head';
import AddressCard from '../../modules/address/components/AddressCard';

const Address: NextPage = () => {
  return (
    <>
      <Head>
        <title>Address</title>
      </Head>
      <div>
        <div className="container">
          <div className="row">
            <div className="col-lg-4 col-md-6 col-sm-12">
              <AddressCard cardColor={'#2bcede'} />
            </div>
            <div className="col-lg-4 col-md-6 col-sm-12">
              <AddressCard cardColor={'#f06715'} />
            </div>
            <div className="col-lg-4 col-md-6 col-sm-12">
              <AddressCard cardColor={'#ea1161'} />
            </div>
            <div className="col-lg-4 col-md-6 col-sm-12">
              <AddressCard cardColor={'#2bcede'} />
            </div>
          </div>
        </div>
      </div>
    </>
  );
};

export default Address;
