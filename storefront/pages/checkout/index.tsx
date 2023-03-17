import Head from 'next/head';
import BreadcrumbComponent from '../../common/components/BreadcrumbComponent';
import { BreadcrumbModel } from '../../modules/breadcrumb/model/BreadcrumbModel';

const crumb: BreadcrumbModel[] = [
  {
    pageName: 'Home',
    url: '/',
  },
  {
    pageName: 'Checkout',
    url: '/checkout',
  },
];

const Checkout = () => {
  return (
    <>
      <Head>
        <title>Checkout</title>
      </Head>
      <BreadcrumbComponent props={crumb} />
    </>
  );
};

export default Checkout;
