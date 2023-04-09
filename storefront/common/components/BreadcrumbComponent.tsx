import Breadcrumb from 'react-bootstrap/Breadcrumb';

import { BreadcrumbModel } from '@/modules/breadcrumb/model/BreadcrumbModel';

type Props = {
  props: BreadcrumbModel[];
};

export default function BreadcrumbComponent({ props }: Props) {
  return (
    <Breadcrumb className="pt-3">
      {props.map((page: BreadcrumbModel, index: number) => (
        <Breadcrumb.Item href={page.url} key={index} active={index === props.length - 1}>
          {page.pageName}
        </Breadcrumb.Item>
      ))}
    </Breadcrumb>
  );
}
