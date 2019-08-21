import React, {useState} from 'react';
import logo from './logo.svg';
import './App.css';
import { Form } from "@flowable/forms";
import "@flowable/forms/flwforms.min.css";


function FooBarPlaceHolder(props) {
  return (
    <div className="fooPlaceHolder">
      <p>Foo Value: {props.fooValue}</p>
      <p>Bar Value: {props.barValue}</p>
    </div>
  );
}

function App() {

  const onChange = (payload) => {
    console.log("VALUE CHANGED", payload);
    setPayload(payload);
  }

  const onOutcomePressed = (payload, outcome) => {
    console.log("OUTCOME PRESSED", payload, outcome);
  }

  const frmDef = {
    "rows": [
      {
        "cols": [
          {
            "designInfo": {
              "bounds": {
                "upperLeft": {
                  "x": 15,
                  "y": 25
                },
                "lowerRight": {
                  "x": 492,
                  "y": 73
                }
              },
              "stencilSuperIds": [
                "Component"
              ],
              "stencilId": "cloud-text",
              "customStencilId": "cloud-text"
            },
            "type": "text",
            "size": 6,
            "label": "Foo",
            "i18n": {
              "en_us": {
                "label": "Foo"
              },
              "de_de": {
                "label": "Label"
              },
              "fr_fr": {
                "label": "Label"
              },
              "it_it": {
                "label": "Label"
              },
              "es_es": {
                "label": "Label"
              }
            },
            "id": "text1",
            "ignore": false,
            "labelAlign": "top",
            "isRequired": false,
            "value": "{{foo}}",
            "enabled": true,
            "visible": true
          },
          {
            "designInfo": {
              "bounds": {
                "upperLeft": {
                  "x": 507,
                  "y": 25
                },
                "lowerRight": {
                  "x": 984,
                  "y": 73
                }
              },
              "stencilSuperIds": [
                "Component"
              ],
              "stencilId": "cloud-text",
              "customStencilId": "cloud-text"
            },
            "type": "text",
            "size": 6,
            "label": "Bar",
            "i18n": {
              "en_us": {
                "label": "Bar"
              },
              "de_de": {
                "label": "Label"
              },
              "fr_fr": {
                "label": "Label"
              },
              "it_it": {
                "label": "Label"
              },
              "es_es": {
                "label": "Label"
              }
            },
            "id": "text2",
            "ignore": false,
            "labelAlign": "top",
            "isRequired": false,
            "value": "{{bar}}",
            "enabled": true,
            "visible": true
          }
        ]
      }
    ], "outcomes": [
      {
        "value": "SAVE",
        "label": "Save"
      },
      {
        "value": "DELETE",
        "label": "Delete"
      },
      {
        "value": "CANCEL",
        "label": "Cancel"
      }
    ]
  };

  const [payload, setPayload] = useState({
    "foo": "fooValue",
    "bar": "barValue"
  });

  return (
    <div className="App">
      <header className="App-header">Flowable Forms Custom App</header>
      <div className="App-body">
        <Form
          config={frmDef}
          onChange={onChange}
          onOutcomePressed={onOutcomePressed}
          payload={payload}
        />
        <FooBarPlaceHolder
          fooValue={payload.foo}
          barValue={payload.bar}
        />
      </div>
    </div>
  );
}

export default App;
