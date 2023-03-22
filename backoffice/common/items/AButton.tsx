import clsx from 'clsx';
import { Link } from 'react-router-dom';
import styles from '../../styles/AButton.module.css';

type Props = {
  className: string;
  children: any;
  variant: string;
  props?: React.ReactNode;
};
const AButton = ({ className = '', children, variant = 'tertiary', ...props }: Props) => {
  let As = 'button';
  if (props.to) {
    As = Link;
  } else if (props.href) {
    As = 'a';
  }
  const newProps = { ...props };
  if (As === 'button') {
    newProps['type'] = props.type ? props.type : 'button';
  }
  return (
    <As className={clsx(styles.btn, styles[`btn-${variant}`], className)} {...newProps}>
      {children}
    </As>
  );
};

export default AButton;
