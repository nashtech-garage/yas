import { TiContacts } from 'react-icons/ti';
import { FaTrash } from 'react-icons/fa';
import { FiEdit } from 'react-icons/fi';
import { HiCheckCircle } from 'react-icons/hi';
import Link from 'next/link';

type AddressCardProps = {
  cardColor: string;
};

const AddressCard = ({ cardColor }: AddressCardProps) => {
  return (
    <div
      style={{
        width: '100%',
        minHeight: '150px',
        marginBottom: '22px',
        borderRadius: '5px',
        boxShadow: '0 4px 8px 0 rgba(0, 0, 0, 0.1), 0 6px 20px 0 rgba(0, 0, 0, 0.1)',
        color: '#ffffff',
        fontWeight: '600',
      }}
    >
      <div className="d-flex" style={{ fontSize: '20px', height: '100%', borderRadius: '5px' }}>
        <div
          className="d-flex justify-content-center align-items-center"
          style={{
            width: '100px',
            background: cardColor,
            borderRadius: '5px 0 0 5px',
            filter: 'brightness(90%)',
          }}
        >
          <div style={{ fontSize: '50px' }}>
            <TiContacts style={{ color: '#ffffff' }} />
          </div>
        </div>
        <div className="p-2" style={{ background: cardColor, borderRadius: '0 5px 5px 0' }}>
          <div className="m-2" style={{ float: 'right' }}>
            <div
              style={{
                width: '15px',
                height: '15px',
                borderRadius: '50%',
                background: '#0eea5d',
              }}
            ></div>
          </div>
          <p style={{ fontSize: '14px' }}>Contact name: Trang Thuan Kiet</p>
          <p
            style={{
              fontSize: '14px',
              wordBreak: 'break-word',
            }}
          >
            Address: 555 Bình Thới, Phường 10, Quận 11
          </p>
          <p style={{ fontSize: '14px' }}>District: 11</p>
          <p style={{ fontSize: '14px' }}>Phone number: 0774091619</p>
          <div className="d-flex justify-content-end">
            <div className="m-1" data-toggle="tooltip" title="Active">
              <HiCheckCircle />
            </div>
            <div className="m-1" data-toggle="tooltip" title="Edit">
              <FiEdit />
            </div>
            <div className="m-1" data-toggle="tooltip" title="Delete">
              <FaTrash />
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default AddressCard;
