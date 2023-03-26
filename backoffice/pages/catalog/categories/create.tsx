import type { NextPage } from 'next';
import Link from 'next/link';
import React, { useEffect, useState } from 'react';
import { Category } from '../../../modules/catalog/models/Category';
import { createCategory, getCategories } from '../../../modules/catalog/services/CategoryService';
import { CATEGORIES_URL } from '../../../constants/Common';
import { handleCreatingResponse } from '../../../modules/catalog/services/ResponseStatusHandlingService';
import { useRouter } from 'next/router';
import CategoryImage from '../../../modules/catalog/components/CategoryImage';
import { SubmitHandler, useForm } from 'react-hook-form';
import { Input, TextArea, CheckBox } from '../../../common/items/Input';
import { OptionSelect } from '../../../common/items/OptionSelect';

const CategoryCreate: NextPage = () => {
  const router = useRouter();
  const { handleSubmit, setValue, register } = useForm<Category>();
  var slugify = require('slugify');
  const [categories, setCategories] = useState<Category[]>([]);
  const onHandleSubmit: SubmitHandler<Category> = async (data: any, event: any) => {
    if (event.target.parentCategory.value == 0) event.target.parentCategory.value = null;
    let category: Category = {
      id: 0,
      name: data.name,
      slug: data.slug,
      description: data.description,
      parentId: event.target.parentCategory.value,
      metaKeywords: data.metaKeywords,
      metaDescription: data.metaDescription,
      displayOrder: data.displayOrder,
      isPublish: data.isPublish,
      imageId: data.imageId,
    };
    let response = await createCategory(category);
    handleCreatingResponse(response);
    router.replace(CATEGORIES_URL);
  };
  useEffect(() => {
    getCategories().then((data) => {
      setCategories(data);
    });
  }, []);
  const renderCategoriesHierarchy: Function = (
    id: number,
    list: Array<Category>,
    parentHierarchy: string
  ) => {
    let renderArr = list.filter((e) => e.parentId == id);
    const newArr = list.filter((e) => e.parentId != id);
    renderArr = renderArr.sort((a: Category, b: Category) => a.name.localeCompare(b.name));
    return renderArr.map((category: Category) => {
      return (
        <React.Fragment key={category.id}>
          <option value={category.id} key={category.id}>
            {parentHierarchy + category.name}
          </option>
          {renderCategoriesHierarchy(category.id, newArr, parentHierarchy + category.name + ' >> ')}
        </React.Fragment>
      );
    });
  };
  return (
    <>
      <div className="row mt-5">
        <div className="col-md-8">
          <h2>Create category</h2>
          <form onSubmit={handleSubmit(onHandleSubmit)} name="form">
            <div className="mb-3">
              <Input
                labelText="Name"
                field="name"
                register={register}
                registerOptions={{
                  required: { value: true, message: 'Category name is required' },
                  onChange: (e) => {
                    let slug = document.getElementById('slug');
                    slug?.setAttribute(
                      'value',
                      slugify(e.target.value, {
                        lower: true,
                        strict: true,
                      })
                    );
                  },
                }}
              />
            </div>
            <div className="mb-3">
              <Input
                labelText="Slug"
                field="slug"
                register={register}
                registerOptions={{
                  required: { value: true, message: 'Slug is required' },
                }}
              />
            </div>
            <div className="mb-3">
              <label className="form-label" htmlFor="parentCategory">
                Parent category
              </label>
              <select className="form-control" id="parentCategory" name="parentCategory">
                <option value={0}>Top</option>
                {renderCategoriesHierarchy(-1, categories, '')}
              </select>
            </div>
            <div className="mb-3">
              <TextArea labelText="Description" field="description" register={register} />
            </div>
            <div className="mb-3">
              <Input labelText="Meta Keywords" field="metaKeywords" register={register} />
            </div>
            <div className="mb-3">
              <TextArea labelText="Meta Description" field="metaDescription" register={register} />
            </div>
            <div className="mb-3">
              <Input
                labelText="Display Order"
                defaultValue={0}
                field="displayOrder"
                register={register}
              />
            </div>
            <div className="d-flex">
              <CheckBox
                labelText="Publish"
                field="isPublish"
                register={register}
                defaultChecked={false}
              />
            </div>
            <CategoryImage setValue={setValue} id="category-image" image={null} />
            <button className="btn btn-primary" type="submit">
              Save
            </button>
            &emsp;
            <Link href="/catalog/categories">
              <button className="btn btn-outline-secondary" type="button">
                Cancel
              </button>
            </Link>
          </form>
        </div>
      </div>
    </>
  );
};

export default CategoryCreate;
