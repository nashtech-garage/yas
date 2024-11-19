import type { NextPage } from 'next';
import Link from 'next/link';
import { useState, useEffect } from 'react';
import { Button, Stack, Table } from 'react-bootstrap';
import { getCustomers } from '../../modules/customer/services/CustomerService';
import moment from 'moment';
import { toast } from 'react-toastify';
import { Customer } from '../../modules/customer/models/Customer';
import { DEFAULT_PAGE_SIZE, DEFAULT_PAGE_NUMBER } from 'constants/Common';
import Pagination from 'common/components/Pagination';
import usePagination from '@commonServices/PaginationService';

const Customers: NextPage = () => {
  const [isLoading, setIsLoading] = useState(false);
  const [customers, setCustomers] = useState<Customer[]>([]);
  const [totalUser, setTotalUser] = useState<number>(0);

  const { pageNo, totalPage, setTotalPage, itemsPerPage, setPageNo, changePage } = usePagination({
    initialPageNo: DEFAULT_PAGE_NUMBER,
    initialItemsPerPage: DEFAULT_PAGE_SIZE,
  });

  useEffect(() => {
    setIsLoading(true);
    getCustomers(pageNo)
      .then((data) => {
        setCustomers(data.customers);
        setTotalPage(data.totalPage);
        setTotalUser(data.totalUser);
        setIsLoading(false);
      })
      .catch((err) => {
        toast.error('Something was wrong! Try later!');
      });
  }, [pageNo]);

  if (isLoading) return <p>Loading...</p>;
  if (!customers) return <p>No Customer</p>;
  return (
    <>
      <div className="row mt-5">
        <div className="col-md-8">
          <h2>Customer. Total {totalUser}</h2>
        </div>

        <div className="col-md-4 text-right">
          <Link href="/customers/create">
            <Button>Create Customer</Button>
          </Link>
        </div>
      </div>
      <Table striped bordered hover>
        <thead>
          <tr>
            <th>ID</th>
            <th>Username</th>
            <th>Email</th>
            <th>FirstName</th>
            <th>LastName</th>
            <th>Created Timestamp</th>
            <th>Action</th>
          </tr>
        </thead>
        <tbody>
          {(customers || []).map((customer) => (
            <tr key={customer.id}>
              <td>
                <Link href={`/customer/${customer.id}/view`}>{customer.id}</Link>
              </td>
              <td>{customer.username}</td>
              <td>{customer.email}</td>
              <td>{customer.firstName}</td>
              <td>{customer.lastName}</td>
              <td>{moment(customer.createdTimestamp).format('MMMM Do YYYY, h:mm:ss a')}</td>
              <td>
                <Stack direction="horizontal" gap={3}>
                  <Link href={`/customer/${customer.id}/edit`}>
                    <button className="btn btn-outline-primary btn-sm" type="button">
                      Edit
                    </button>
                  </Link>
                  <Link href={`/customer/${customer.id}/delete`}>
                    <button className="btn btn-outline-primary btn-sm" type="button">
                      Del
                    </button>
                  </Link>
                </Stack>
              </td>
            </tr>
          ))}
        </tbody>
      </Table>
      {totalPage > 1 && (
        <Pagination
          pageNo={pageNo}
          totalPage={totalPage}
          itemsPerPage={itemsPerPage}
          onPageChange={changePage}
          showHelpers={false}
        />
      )}
    </>
  );
};

export default Customers;
