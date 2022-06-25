import Head from 'next/head'

type Props = {
    children: React.ReactNode
}

export default function Layout({ children } : Props) {
  return (
      <div className="container">
          <Head>
              <title>Yas - Backoffice</title>
              <meta name="description" content="Yet another shop backoffice" />
              <link rel="icon" href="/favicon.ico" />
          </Head>
          <main>{children}</main>
          <footer className="footer">
              <a href="https://github.com/nashtech-garage/yas" target="_blank" rel="noopener noreferrer">
              Powered by {'Yas - 2022 '}
              </a>
          </footer>
      </div>
  );
}