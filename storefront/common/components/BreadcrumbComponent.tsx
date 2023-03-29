import Breadcrumb from 'react-bootstrap/Breadcrumb';

export default function BreadcrumbComponent({ props }: any) {
  return (
    <Breadcrumb className="mt-4">
      {props.map((page: any, index: number) => (
        <Breadcrumb.Item href={page.url} key={index} active={index === props.length - 1}>
          {page.pageName}
        </Breadcrumb.Item>
      ))}
    </Breadcrumb>
  );
}
