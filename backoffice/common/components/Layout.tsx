import Head from 'next/head';
import AuthenticationInfo from './AuthenticationInfo';
import { Navbar } from 'react-bootstrap';
import Sidebar from './Sidebar';
import styles from '../../styles/Layout.module.css';

type Props = {
  children: React.ReactNode;
};

export default function Layout({ children }: Props) {
  return (
    <>
      <Head>
        <title>Yas - Backoffice</title>
        <meta name="description" content="Yet another shop backoffice" />
        <link rel="icon" href="/favicon.ico" />
        <link
          rel="stylesheet"
          href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.9.1/font/bootstrap-icons.css"
        ></link>
      </Head>
      <Navbar collapseOnSelect bg="dark" variant="dark" className={styles.header}>
        {/* <div className="container-fluid"> */}
        <Navbar.Brand href="/" className="font-weight-black text-white">
          Yas - Backoffice
        </Navbar.Brand>
        <Navbar.Toggle />
        <Navbar.Collapse className="justify-content-end">
          <Navbar.Text>
            <AuthenticationInfo />
          </Navbar.Text>
        </Navbar.Collapse>
        {/* </div> */}
      </Navbar>
      <div className="container-fluid">
        <Sidebar />
        <div>
          <main className="col-md-9 py-5" id={styles.main}>
            {children}
          </main>
          <footer className={styles.footer}>
            <a
              href="https://github.com/nashtech-garage/yas"
              target="_blank"
              rel="noopener noreferrer"
            >
              Powered by {'Yas - 2022 '}
            </a>
          </footer>
        </div>
      </div>
    </>
  );
}
