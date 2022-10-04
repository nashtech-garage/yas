import type { NextPage } from "next";
import Link from "next/link";
import React, { useEffect, useState } from "react";
import { Button, Modal, Table } from "react-bootstrap";
import type { Category } from "../../../modules/catalog/models/Category";
import { deleteCategory, getCategories } from "../../../modules/catalog/services/CategoryService";

const CategoryList: NextPage = () => {
  const [categories, setCategories] = useState<Category[]>([]);
  const [isLoading, setLoading] = useState(false);
  let categoryId = 0;
  const [categoryName, setCategoryName]= useState('') ;
  const [showModalDelete, setShowModalDelete] = useState(false);
  const handleClose = () => setShowModalDelete(false);
  const handleDelete = () => {
    if(categoryId){
      deleteCategory(+categoryId)
      .then((response)=>{
        if(response.status===204){
          alert("Delete successfully")
          location.replace("/catalog/categories");
        }
        else if(response.title==='Not found'){
          alert(response.detail)
          location.replace("/catalog/categories");
        }
        else if(response.title==='Bad request'){
          alert(response.detail)
        }
        else{
          alert("Delete failed")
          location.replace("/catalog/categories");
        }
      })
      }
  }
  useEffect(() => {
    setLoading(true);
    getCategories().then((data) => {
      setCategories(data);
      setLoading(false);
    });
  }, []);

  if (isLoading) return <p>Loading...</p>;
  if (!categories) return <p>No category</p>;

  const renderCategoriesHierarchy: Function = (
    id: number,
    list: Array<Category>,
    parentHierarchy: string
  ) => {
    const renderArr = list.filter((e) => e.parentId == id);
    const newArr = list.filter((e) => e.parentId != id);
    return renderArr
      .sort((a: Category, b: Category) => a.name.localeCompare(b.name))
      .map((category: Category) => {
        return (
          <React.Fragment key={category.id}>
            <tr>
              <td>{category.id}</td>
              <td>{parentHierarchy}{category.name}</td>
              <td>
              <Link href={`/catalog/categories/${category.id}/edit`}>
              <button className="btn btn-outline-primary btn-sm" type="button">Edit</button>
              </Link>
              &nbsp;
              <button className="btn btn-outline-danger btn-sm" onClick={()=> {
                categoryId = category.id;
                setCategoryName(category.name)
                setShowModalDelete(true)}}>Delete</button>
            </td>
            </tr>
            {renderCategoriesHierarchy(
              category.id,
              newArr,
              parentHierarchy + category.name + " >> "
            )}
          </React.Fragment>
        );
      });
  };

  return (
    <>
      <div className="row mt-5">
        <div className="col-md-8">
          <h2>Categories</h2>
        </div>
        <div className="col-md-4 text-right">
          <Link href="/catalog/categories/create">
            <Button>Create Category</Button>
          </Link>
        </div>
      </div>
      <Table striped bordered hover>
        <thead>
          <tr>
            <th>#</th>
            <th>Name</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>{renderCategoriesHierarchy(-1, categories, "")}</tbody>
      </Table>
      <Modal show={showModalDelete} onHide={handleClose}>
        <Modal.Body>{'Are you sure you want to delete this '+ categoryName +" ?"}</Modal.Body>
        <Modal.Footer>
          <Button variant="outline-secondary" onClick={handleClose}>
            Close
          </Button>
          <Button variant="danger" onClick={handleDelete}>
            Delete
          </Button>
        </Modal.Footer>
      </Modal>
    </>
  );
};

export default CategoryList;
