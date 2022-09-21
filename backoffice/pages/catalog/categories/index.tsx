import type { NextPage } from "next";
import Link from "next/link";
import React, { useEffect, useState } from "react";
import { Button, Table } from "react-bootstrap";
import type { Category } from "../../../modules/catalog/models/Category";
import { getCategories } from "../../../modules/catalog/services/CategoryService";

const CategoryList: NextPage = () => {
  const [categories, setCategories] = useState<Category[]>([]);
  const [isLoading, setLoading] = useState(false);
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
          </tr>
        </thead>
        <tbody>
          {renderCategoriesHierarchy(-1, categories, "")}
        </tbody>
      </Table>
    </>
  );
};

export default CategoryList;
