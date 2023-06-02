import * as React from 'react';

export class ComponentToPrint extends React.PureComponent {
  constructor(props) {
    super(props);

    this.state = { checked: false };
  }

  canvasEl;

  componentDidMount() {
    const ctx = this.canvasEl.getContext('2d');
    if (ctx) {
      ctx.beginPath();
      ctx.arc(95, 50, 40, 0, 2 * Math.PI);
      ctx.stroke();
      ctx.fillStyle = 'rgb(200, 0, 0)';
      ctx.fillRect(85, 40, 20, 20);
      ctx.save();
    }
  }

  handleCheckboxOnChange = () => this.setState({ checked: !this.state.checked });

  setRef = (ref) => (this.canvasEl = ref);

  render() {
    const { text } = this.props;

    return (
      <div className="relativeCSS">
        <style type="text/css" media="print">
          {'\
   @page { size: landscape; }\
'}
        </style>
        <div className="flash" />
        <table className="testClass">
          <thead>
            <tr>
              <th className="column1">Test Name</th>
              <th>Test</th>
            </tr>
          </thead>
          <tbody>
            <tr>
              <td>Canvass</td>
              <td>
                <canvas height="100" ref={this.setRef} width="200">
                  Your browser does not support the HTML5 canvas tag.
                </canvas>
              </td>
            </tr>
            <tr>
              <td>Dynamic Content From Prop</td>
              <td>{text ?? 'Custom Text Here'}</td>
            </tr>
            <tr>
              <td>Fonts</td>
              <td>
                <div className="customFontText">Some Cool Font Text</div>
              </td>
            </tr>
            <tr>
              <td>Image: Local Import</td>
              <td></td>
            </tr>
            <tr>
              <td>Image: URL</td>
              <td>
                <img
                  alt="Google logo"
                  src="https://www.google.com/images/branding/googlelogo/2x/googlelogo_color_272x92dp.png"
                  width="200"
                />
              </td>
            </tr>
            <tr>
              <td>Input</td>
              <td>
                <input />
              </td>
            </tr>
            <tr>
              <td>Input: Checkbox</td>
              <td>
                <input
                  checked={this.state.checked}
                  onChange={this.handleCheckboxOnChange}
                  type="checkbox"
                />
              </td>
            </tr>
            <tr>
              <td>Input: Date</td>
              <td>
                <input type="date" />
              </td>
            </tr>
            <tr>
              <td>Input: Radio</td>
              <td>
                Blue <input type="radio" id="blue" name="color" value="blue" />
                Red <input type="radio" id="red" name="color" value="red" />
              </td>
            </tr>
            <tr>
              <td>Select</td>
              <td>
                <select name="cars" id="cars">
                  <option value="volvo">Volvo</option>
                  <option value="saab">Saab</option>
                  <option value="mercedes">Mercedes</option>
                  <option value="audi">Audi</option>
                </select>
              </td>
            </tr>
            <tr>
              <td>TextArea</td>
              <td>
                <textarea />
              </td>
            </tr>
            <tr>
              <td>SVG</td>
              <td>
                <svg height="100" width="100">
                  <circle cx="50" cy="50" fill="yellow" r="40" stroke="green" strokeWidth="4" />
                </svg>
              </td>
            </tr>
            <tr>
              <td>Video</td>
              <td>
                <video src="https://www.w3schools.com/html/mov_bbb.mp4" width="200" />
              </td>
            </tr>
            <tr>
              <td>Video: With Poster</td>
              <td>
                <video
                  poster="https://images.freeimages.com/images/large-previews/9a9/tuscany-landscape-4-1500765.jpg"
                  src="https://www.w3schools.com/html/mov_bbb.mp4"
                  width="200"
                />
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    );
  }
}

export const FunctionalComponentToPrint = React.forwardRef((props, ref) => {
  // eslint-disable-line max-len
  return <ComponentToPrint ref={ref} text={props.text} />;
});
