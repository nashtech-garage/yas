import type { NextPage } from 'next'
import Link from 'next/link'
import React, { useEffect, useState } from 'react';
import { Button, Table } from 'react-bootstrap';
import type { Brand } from '../../../modules/catalog/models/Brand'
import { getBrands } from '../../../modules/catalog/services/BrandService'

const BrandList: NextPage = () => {
    const [brands, setBrands] = useState<Brand[]>([]);
    const [isLoading, setLoading] = useState(false);
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
                            <button className="btn btn-outline-danger btn-sm" type="button">Delete</button>
                        </td>
                    </tr>
                ))}
                </tbody>
            </Table>
        </>
    )
}

export default BrandList