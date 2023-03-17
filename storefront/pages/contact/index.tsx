import Head from 'next/head';
import BreadcrumbComponent from '../../common/components/BreadcrumbComponent';
import { BreadcrumbModel } from '../../modules/breadcrumb/model/BreadcrumbModel';

const crumb: BreadcrumbModel[] = [
  {
    pageName: 'Home',
    url: '/',
  },
  {
    pageName: 'Contact',
    url: '/contact',
  },
];

const Contact = () => {
  return (
    <>
      <Head>
        <title>Contact</title>
      </Head>
      <BreadcrumbComponent props={crumb} />
    </>
  );
};

export default Contact;
