import type { NextPage } from 'next'
import { useRouter } from 'next/router'

const ProductDetails: NextPage = () => {
    const router = useRouter()
    const { id } = router.query
  
    return (
      <>
      <div className='row mt-5'>
        <div className='col-md-8'>
          <h2>Product details {id}</h2>
        </div>
      </div>
      </>
    )
  }
  
  export default ProductDetails