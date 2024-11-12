import { ProductOption } from '@catalogModels/ProductOption';
import React from 'react';
import { Modal, Button, Form, Row, Col } from 'react-bootstrap';
import { SingleValue } from 'react-select';

type DisplayTypeModalProps = {
  show: boolean;
  handleCloseModel: () => void;
  handleColorChange: (event: React.ChangeEvent<HTMLInputElement>, optionValueKey: string) => void;
  currentModelOption: SingleValue<ProductOption>;
  productOptionValuePost: {
    productOptionId?: number;
    value: { [key: string]: string };
    displayType?: string;
    displayOrder?: number;
  }[];
  setDisplayType: (type: string) => void;
};

const DisplayTypeModal: React.FC<DisplayTypeModalProps> = ({
  show,
  handleCloseModel,
  handleColorChange,
  currentModelOption,
  productOptionValuePost,
  setDisplayType,
}) => {
  const getOption = () => {
    const option = productOptionValuePost.find((t) => t.productOptionId === currentModelOption?.id);
    return option || null;
  };

  return (
    <Modal show={show} onHide={handleCloseModel}>
      <Modal.Header closeButton>
        <Modal.Title>Select display type</Modal.Title>
      </Modal.Header>
      <Modal.Body>
        <Form>
          <fieldset>
            <Form.Group as={Row} className="mb-3 align-items-center">
              <Form.Label as="legend" column sm={2} className="align-items-center">
                Type:
              </Form.Label>
              <Col sm={5} className="d-flex align-items-center">
                <Row>
                  <Col className="d-flex align-items-center">
                    <Form.Check
                      type="radio"
                      label="Text"
                      checked={getOption()?.displayType === 'text'}
                      onChange={() => setDisplayType('text')}
                      name="displayType"
                      id="text"
                      className="d-flex align-items-center gap-2"
                    />
                  </Col>
                  <Col className="d-flex align-items-center">
                    <Form.Check
                      type="radio"
                      label="Color"
                      checked={getOption()?.displayType === 'color'}
                      onChange={() => setDisplayType('color')}
                      name="displayType"
                      id="color"
                      className="d-flex align-items-center gap-2"
                    />
                  </Col>
                </Row>
              </Col>
            </Form.Group>
          </fieldset>
          <Form.Group as={Row} className="mb-3 align-items-center">
            <Form.Label as="legend" column sm={2} className="align-items-center">
              Options
            </Form.Label>
            <Col sm={9} className="d-flex align-items-center">
              <Row>
                {Object.entries(getOption()?.value || '').map(([key, value]) => (
                  <React.Fragment key={key}>
                    <Row>
                      <Col className="d-flex align-items-center w-100 mb-2">
                        <Form.Label column sm={4} className="align-items-center">
                          {key}
                        </Form.Label>
                        <Col sm={8}>
                          <Row className="d-inline-flex justify-content-start">
                            <Col className="d-flex align-items-center m-0 p-0 col-3">
                              <Form.Control
                                type="color"
                                className="m-0 p-1 w-100"
                                value={value}
                                onChange={(event) =>
                                  handleColorChange(
                                    event as React.ChangeEvent<HTMLInputElement>,
                                    key
                                  )
                                }
                              />
                            </Col>
                            <Col className="d-flex align-items-center">
                              <Form.Control
                                type="text"
                                value={value}
                                onChange={(event) =>
                                  handleColorChange(
                                    event as React.ChangeEvent<HTMLInputElement>,
                                    key
                                  )
                                }
                                placeholder="#000000"
                              />
                            </Col>
                          </Row>
                        </Col>
                      </Col>
                    </Row>
                  </React.Fragment>
                ))}
              </Row>
            </Col>
          </Form.Group>
        </Form>
      </Modal.Body>
      <Modal.Footer>
        <Button variant="primary" onClick={handleCloseModel}>
          Close
        </Button>
      </Modal.Footer>
    </Modal>
  );
};

export default DisplayTypeModal;
