import React, { useState, useEffect } from 'react';
import './App.css';
import { Form } from "@flowable/forms";
import "@flowable/forms/flwforms.min.css";
import axios from 'axios';

function FormContainer(props) {
  const [payload, setPayload] = useState({});
  const [formData, setFormData] = useState({
    formDefinition: { "rows": [{ "cols": [] }], "outcomes": [] },
    variables: {}
  });
  useEffect(() => {
    const fetchData = async () => {
      const taskFormDefinition = await props.httpClient.get(`/platform-api/tasks/${props.taskId}/form`);
      const task = await props.httpClient.get(`/platform-api/tasks/${props.taskId}`);
      const formVariables = await props.httpClient.get(`/platform-api/process-instances/${task.data.processInstanceId}/work-form/variables`);
      setFormData({
        formDefinition: taskFormDefinition.data,
        variables: formVariables.data
      });
    };
    fetchData();
  }, [props]);

  const onChange = (newPayload) => {
    console.log("VALUE CHANGED", newPayload);
    setPayload(newPayload);
  }

  const onOutcomePressed = (newPayload, outcomePressed) => {
    console.log("OUTCOME PRESSED", newPayload, outcomePressed);
    setPayload(newPayload);
  }

  return (
    <div className="Form-container">
      <Form
        config={formData.formDefinition}
        onChange={onChange}
        onOutcomePressed={onOutcomePressed}
        payload={formData.variables}
      />
    </div>
  );
}

function App() {
  const [selectedTaskId, setSelectedTaskId] = useState("TSK-c98c1eb0-c4c7-11e9-9796-3a304270b7a7");
  const httpClient = axios.create({
    baseURL: 'http://localhost:8090',
    timeout: 1000,
    auth: {
      username: "admin",
      password: "test"
    }
  });
  const [taskList, setTaskList] = useState([]);
  useEffect(() => {
    const fetchData = async () => {
      const taskListItems = await httpClient.get("/platform-api/search/query-tasks");
      console.log(taskListItems);
      setTaskList(taskListItems.data.data);
    };
    fetchData();
  }, []);
  
  function handleButtonClick(taskId) {
    setSelectedTaskId(taskId);
    console.log(taskId);
  }; 
  const listItems = taskList.map((item) =>
    <li key={item.id}>
      <button id="item.id" onClick={() => handleButtonClick(item.id)}>{item.name}</button>
    </li>
  );

  return (
    <div className="App">
      <header className="App-header">Flowable Forms Custom App</header>
      <div className="App-body">
        <div className="Task-list">
          <table>
            <tr>
              <td><ul>{listItems}</ul></td>
              <td valign="top">
                <FormContainer
                  taskId={selectedTaskId}
                  httpClient={httpClient}
                />
              </td>
            </tr>
          </table>
        </div>
      </div>
    </div>
  );
}

export default App;
