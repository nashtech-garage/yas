import type { NextPage } from 'next'
import Link from 'next/link'
import { Button, Table } from 'react-bootstrap';

const ProductList: NextPage = () => {
  
    return (
      <>
      <div className='row mt-5'>
        <div className='col-md-8'>
          <h2>Product</h2>
        </div> 
        <div className='col-md-4 text-right'>
          <Link href="/catalog/products/create">
            <Button>Create Product</Button>
          </Link>
        </div>
      </div>
      <Table striped bordered hover>
        <thead>
          <tr>
            <th>#</th>
            <th>Name</th>
          </tr>
        </thead>
      </Table>
      </>
    )
  }
  
  export default ProductList