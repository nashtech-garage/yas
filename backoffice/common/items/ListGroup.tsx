import React from 'react';
import { ListGroup as ListGroupBootstrap } from 'react-bootstrap';
import Link from 'next/link';
import { useRouter } from 'next/router';
import styles from '../../styles/ListGroup.module.css';

type Props = {
  data: any;
};

export default function ListGroup({ data }: Props) {
  const router = useRouter();

  return (
    <ListGroupBootstrap id={styles.listGroup} as="ul">
      {data.map((e: any, index: number) => (
        <>
          {console.log(e)}
          <Link href={e.link} key={e.id.toString()}>
            <a
              className={router.pathname === e.link ? styles.appActiveLink : ' app-not-active-link'}
              key={e.id.toString()}
            >
              <ListGroupBootstrap.Item
                className={`py-3 d-flex align-items-center ${styles.listGroupItem}`}
                as="li"
                key={e.id.toString()}
              >
                <h5>{e.icon}</h5>
                <h5 className="font-weight-bold ms-3">{e.name}</h5>
              </ListGroupBootstrap.Item>
            </a>
          </Link>
        </>
      ))}
    </ListGroupBootstrap>
  );
}
