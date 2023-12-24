type Props = {
  text: string;
};

const HeaderPdf = ({ text }: Props) => {
  return (
    <>
      <div className={`my-3 ms-3`} dangerouslySetInnerHTML={{ __html: text }}></div>
    </>
  );
};

export default HeaderPdf;
