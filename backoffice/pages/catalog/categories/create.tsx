import type { NextPage } from 'next';
import Link from 'next/link';
import { useRouter } from 'next/router';
import React, { useEffect, useState } from 'react';
import { SubmitHandler, useForm } from 'react-hook-form';
import slugify from 'slugify';

import CategoryImage from '@catalogComponents/CategoryImage';
import { Category } from '@catalogModels/Category';
import { createCategory, getCategories } from '@catalogServices/CategoryService';
import { CheckBox, Input, TextArea } from '@commonItems/Input';
import { handleCreatingResponse } from '@commonServices/ResponseStatusHandlingService';
import { CATEGORIES_URL } from 'constants/Common';

const CategoryCreate: NextPage = () => {
  const router = useRouter();
  const { handleSubmit, setValue, register } = useForm<Category>();
  const [categories, setCategories] = useState<Category[]>([]);

  useEffect(() => {
    getCategories().then((data) => {
      setCategories(data);
    });
  }, []);

  const onHandleSubmit: SubmitHandler<Category> = async (data: Category) => {
    const category: Category = {
      id: 0,
      name: data.name,
      slug: data.slug,
      description: data.description,
      parentId: data.parentId && +data.parentId === 0 ? null : data.parentId,
      metaKeywords: data.metaKeywords,
      metaDescription: data.metaDescription,
      displayOrder: data.displayOrder,
      isPublish: data.isPublish,
      imageId: data.imageId,
    };
    const response = await createCategory(category);
    handleCreatingResponse(response);
    router.replace(CATEGORIES_URL);
  };

  const renderCategoriesHierarchy: Function = (
    id: number,
    list: Array<Category>,
    parentHierarchy: string
  ) => {
    let renderArr = list.filter((e) => e.parentId == id);
    const newArr = list.filter((e) => e.parentId != id);
    renderArr = renderArr.sort((a: Category, b: Category) => a.name.localeCompare(b.name));
    return renderArr.map((category: Category) => (
      <React.Fragment key={category.id}>
        <option value={category.id} key={category.id}>
          {parentHierarchy + category.name}
        </option>
        {renderCategoriesHierarchy(category.id, newArr, parentHierarchy + category.name + ' >> ')}
      </React.Fragment>
    ));
  };

  return (
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
                  setValue(
                    'slug',
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
            <select className="form-control" id="parentCategory" {...register('parentId')}>
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
  );
};

export default CategoryCreate;
