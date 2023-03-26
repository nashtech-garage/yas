import type { NextPage } from 'next';
import Link from 'next/link';
import { useRouter } from 'next/router';
import React, { useEffect, useState } from 'react';
import { Category } from '../../../../modules/catalog/models/Category';
import {
  getCategories,
  getCategory,
  updateCategory,
} from '../../../../modules/catalog/services/CategoryService';
import { CATEGORIES_URL } from '../../../../constants/Common';

import { handleUpdatingResponse } from '../../../../modules/catalog/services/ResponseStatusHandlingService';
import { useForm } from 'react-hook-form';

import { uploadMedia } from '../../../../modules/catalog/services/MediaService';
import { toast } from 'react-toastify';
import { isValidFile, validTypes } from '../../../../modules/catalog/components/ChooseThumbnail';
import ChooseImageCommon from '../../../../common/components/ChooseImageCommon';
import styles from '../../../../styles/ChooseImage.module.css';

type Image = {
  id: number;
  url: string;
};

const CategoryEdit: NextPage = () => {
  const router = useRouter();
  const { setValue, handleSubmit } = useForm<Category>();
  const { id } = router.query;
  var slugify = require('slugify');
  const [categories, setCategories] = useState<Category[]>([]);
  const [category, setCategory] = useState<Category>();
  const [slug, setSlug] = useState<string>();
  const [imageId, setImageId] = useState<number>();
  const [categoryImage, setCategoryImage] = useState<Image | null>();

  const handleSubmitEdit = async (data: any, event: any) => {
    event.preventDefault();
    if (event.target.parentCategory.value == 0) event.target.parentCategory.value = null;
    let category: Category = {
      id: 0,
      name: event.target.name.value,
      slug: event.target.slug.value,
      metaKeywords: event.target.metaKeywords.value,
      metaDescription: event.target.metaDescription.value,
      parentId: event.target.parentCategory.value,
      displayOrder: event.target.displayOrder.value,
      description: event.target.description.value,
      isPublish: event.target.isPublish.checked,
      imageId,
    };

    if (id) {
      updateCategory(+id, category).then((response) => {
        handleUpdatingResponse(response);
        router.replace(CATEGORIES_URL);
      });
    }
  };
  const renderSeletedCategory: Function = (
    category: Category,
    currentCategory: Category,
    parentHierarchy: string
  ) => {
    if (category.id === currentCategory?.parentId) {
      return (
        <option selected value={category.id} key={category.id}>
          {parentHierarchy + category.name}
        </option>
      );
    }
    return (
      <option value={category.id} key={category.id}>
        {parentHierarchy + category.name}
      </option>
    );
  };
  const renderCategoriesHierarchy: Function = (
    id: number,
    list: Array<Category>,
    parentHierarchy: string,
    currentCategory: Category
  ) => {
    let renderArr = list.filter((e) => e.parentId == id);
    const newArr = list.filter((e) => e.parentId != id);
    renderArr = renderArr.sort((a: Category, b: Category) => a.name.localeCompare(b.name));
    return renderArr.map((category: Category) => {
      return (
        <React.Fragment key={category.id}>
          {renderSeletedCategory(category, currentCategory, parentHierarchy)}
          {renderCategoriesHierarchy(
            category.id,
            newArr,
            parentHierarchy + category.name + ' >> ',
            currentCategory
          )}
        </React.Fragment>
      );
    });
  };

  useEffect(() => {
    if (id)
      getCategory(+id).then((data) => {
        setCategory(data);
        setSlug(data.slug);
        if (data.categoryImage) {
          setImageId(data.categoryImage.id);
          setCategoryImage(data.categoryImage);
        }
      });
  }, [id]);

  useEffect(() => {
    getCategories().then((data) => {
      setCategories(data);
    });
  }, []);

  const onChangeProductImage = async (event: React.ChangeEvent<HTMLInputElement>) => {
    if (!event) {
      return;
    }

    const fileList = event.target.files;
    const isAllValidImage =
      fileList && Array.from(fileList).every((file) => isValidFile(file, validTypes));

    if (!isAllValidImage) {
      toast.error('Please select an image file (jpg or png)');
      return;
    }
    try {
      const file = fileList[0];
      const res = await uploadMedia(file);
      const url = URL.createObjectURL(file);
      setValue?.('imageId', res.id);
      setImageId(res.id);
      setCategoryImage({
        id: res.id,
        url,
      });
    } catch (e) {
      toast.error('Upload image failed');
    }
  };

  const onDeleteImage = () => {
    setCategoryImage(null);
  };

  return (
    <>
      <div className="row mt-5">
        <div className="col-md-8">
          <form onSubmit={handleSubmit(handleSubmitEdit)} name="form">
            <div className="mb-3">
              <label className="form-label" htmlFor="name">
                Name
              </label>
              <input
                className="form-control"
                type="text"
                id="name"
                name="name"
                defaultValue={category?.name}
                required
                onChange={(e) => {
                  let generate = slugify(e.target.value, {
                    lower: true,
                    strict: true,
                  });
                  setSlug(generate);
                }}
              />
            </div>
            <div className="mb-3">
              <label className="form-label" htmlFor="slug">
                Slug
              </label>
              <input
                className="form-control"
                type="text"
                id="slug"
                name="slug"
                defaultValue={slug}
                required
              />
            </div>
            <div className="mb-3">
              <label className="form-label" htmlFor="parentCategory">
                Parent category
              </label>
              <select className="form-control" id="parentCategory" name="parentCategory">
                <option value={0}>Top</option>
                {renderCategoriesHierarchy(-1, categories, '', category)}
              </select>
            </div>
            <div className="mb-3">
              <label className="form-label" htmlFor="description">
                Description
              </label>
              <textarea
                className="form-control"
                id="description"
                defaultValue={category?.description}
                name="description"
              />
            </div>
            <div className="mb-3">
              <label className="form-label" htmlFor="metaKeywords">
                Meta Keywords
              </label>
              <input
                className="form-control"
                type="text"
                id="metaKeywords"
                name="metaKeywords"
                defaultValue={category?.metaKeywords}
              />
            </div>
            <div className="mb-3">
              <label className="form-label" htmlFor="metaDescription">
                Meta Description
              </label>
              <textarea
                className="form-control"
                id="metaDescription"
                name="metaDescription"
                defaultValue={category?.metaDescription}
              />
            </div>
            <div className="mb-3">
              <label className="form-label" htmlFor="displayOrder">
                Display Order
              </label>
              <input
                className="form-control"
                type="number"
                defaultValue={category?.displayOrder}
                min={0}
                id="displayOrder"
                name="displayOrder"
              />
            </div>
            <div className="d-flex">
              <label
                className="form-check-label mr-3"
                htmlFor="isPublish"
                style={{ marginRight: '15px' }}
              >
                Publish
              </label>
              <div className="form-check form-switch mb-3">
                <input
                  className="form-check-input"
                  type="checkbox"
                  id="isPublish"
                  name="isPublish"
                  defaultChecked={category?.isPublish}
                />
              </div>
            </div>
            {!categoryImage && (
              <div className="mb-3">
                <label className={styles['image-label']} htmlFor="category-image">
                  Choose category image
                </label>
              </div>
            )}
            <input
              hidden
              type="file"
              multiple
              id="category-image"
              onChange={(event) => onChangeProductImage(event)}
            />
            {categoryImage && (
              <div className="mb-3">
                <ChooseImageCommon
                  id="category-image"
                  url={categoryImage.url}
                  onDeleteImage={onDeleteImage}
                />
              </div>
            )}
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
export default CategoryEdit;
