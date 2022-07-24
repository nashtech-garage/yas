import type { NextPage } from 'next'
import { useRouter } from 'next/router'

const ProductEdit: NextPage = () => {
    const router = useRouter()
    const { id } = router.query
  
    return (
      <>
      <div className='row mt-5'>
        <div className='col-md-8'>
          <h2>Edit product {id}</h2>
        </div>
      </div>
      </>
    )
  }
  
  export default ProductEdit