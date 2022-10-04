import type { NextPage } from 'next'
import Link from 'next/link'
import React, { useEffect, useState } from 'react';
import { Button, Modal, Table } from 'react-bootstrap';
import type { Brand } from '../../../modules/catalog/models/Brand'
import { deleteBrand, getBrands } from '../../../modules/catalog/services/BrandService'

const BrandList: NextPage = () => {
    const [brandId, setBrandId] = useState(-1);
    const [brands, setBrands] = useState<Brand[]>([]);
    const [isLoading, setLoading] = useState(false);
    const [brandName, setBrandName] = useState("");
    const [showModalDelete, setShowModalDelete] = useState(false);
    const handleClose = () => setShowModalDelete(false);
    const handleDelete = () => {
        if(brandId !== -1){
            deleteBrand(+brandId)
            .then((response) => {
                if(response.status===204){
                    alert("Delete successfully")
                    location.replace("/catalog/brands");
                  }
                  else if(response.title==='Not found'){
                    alert(response.detail)
                    location.replace("/catalog/brands");
                  }
                  else if(response.title==='Bad request'){
                    alert(response.detail)
                  }
                  else{
                    alert("Delete failed")
                    location.replace("/catalog/brands");
                  }
            })
        }
    }
    useEffect(() => {
        setLoading(true);
        getBrands()
            .then((data) => {
                setBrands(data);
                setLoading(false);
            });
    }, []);
    if (isLoading) return <p>Loading...</p>;
    if (!brands) return <p>No brand</p>;
    return (
        <>
            <div className='row mt-5'>
                <div className='col-md-8'>
                    <h2>Brands</h2>
                </div>
                <div className='col-md-4 text-right'>
                    <Link href="/catalog/brands/create">
                        <Button>Create Brand</Button>
                    </Link>
                </div>
            </div>
            <Table striped bordered hover>
                <thead>
                <tr>
                    <th>#</th>
                    <th>Name</th>
                    <th>Slug</th>
                    <th>Action</th>
                </tr>
                </thead>
                <tbody>
                {brands.map((brand) => (
                    <tr key={brand.id}>
                        <td>{brand.id}</td>
                        <td>{brand.name}</td>
                        <td>{brand.slug}</td>
                        <td>
                            <button className="btn btn-outline-primary btn-sm" type="button">Edit</button>
                            &nbsp;
                            <button className="btn btn-outline-danger btn-sm" type="button"
                                onClick={() => {
                                    setBrandId(brand.id);
                                    setBrandName(brand.name);
                                    setShowModalDelete(true);
                                }}
                            >Delete</button>
                        </td>
                    </tr>
                ))}
                </tbody>
            </Table>
            <Modal show={showModalDelete} onHide={handleClose}>
                <Modal.Body>{'Are you sure you want to delete this '+ brandName +" ?"}</Modal.Body>
                <Modal.Footer>
                <Button variant="outline-secondary" onClick={handleClose}>
                    Close
                </Button>
                <Button variant="danger" 
                onClick={handleDelete}
                >
                    Delete
                </Button>
                </Modal.Footer>
            </Modal>
        </>
    )
}

export default BrandList