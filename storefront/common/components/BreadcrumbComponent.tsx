import Breadcrumb from 'react-bootstrap/Breadcrumb';
import { BreadcrumbModel } from '../../modules/breadcrumb/model/BreadcrumbModel';

export default function BreadcrumbComponent({ props }: any) {
  return (
    <Breadcrumb>
      {props.map((page: any, index: number) => (
        <Breadcrumb.Item
          href={page.url}
          key={index}
          active={index === props.length - 1 ? true : false}
        >
          {page.pageName}
        </Breadcrumb.Item>
      ))}
    </Breadcrumb>
  );
}