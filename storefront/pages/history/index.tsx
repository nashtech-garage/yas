import Head from 'next/head';
import BreadcrumbComponent from '../../common/components/BreadcrumbComponent';
import { BreadcrumbModel } from '../../modules/breadcrumb/model/BreadcrumbModel';

const crumb: BreadcrumbModel[] = [
  {
    pageName: 'Home',
    url: '/',
  },
  {
    pageName: 'History',
    url: '/history',
  },
];

const History = () => {
  return (
    <>
      <Head>
        <title>History</title>
      </Head>
      <BreadcrumbComponent props={crumb} />
    </>
  );
};

export default History;
