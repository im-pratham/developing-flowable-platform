# t78-creating-fe-app-with-forms steps

- Create an app with the [initializr](https://initializr.flowable.io/)
- Add cors configuration
- Testing the app with spring-boot:run
- Create a Simple App with a Simple Process and a Simple Form.
- Export bar file and put it in the deployment folder.
- Export zip file to the design app
- Endpoints to use in the frontend app
  - Get the Process definition for a process definition key

        ```
        GET /process-api/repository/process-definitions?key={{processDefinitionKey}}&amp; latest=true HTTP/1.1
        Host: localhost:8090
        Authorization: Basic YWRtaW46dGVzdA==
        User-Agent: PostmanRuntime/7.15.2
        Accept: */*
        Cache-Control: no-cache
        Postman-Token: b944a71b-3a58-426e-bc28-7545d91184f7,ef6fb984-a5d9-48e0-917d-7518e6be9b23
        Host: localhost:8090
        Cookie: JSESSIONID=1614C8D1F2EE5021F826CC80B4E658D8
        Accept-Encoding: gzip, deflate
        Connection: keep-alive
        cache-control: no-cache

        ```


  - Start the process

        ```
        POST /platform-api/process-instances HTTP/1.1
        Host: localhost:8090
        Content-Type: application/json
        Authorization: Basic YWRtaW46dGVzdA==
        User-Agent: PostmanRuntime/7.15.2
        Accept: */*
        Cache-Control: no-cache
        Postman-Token: 67fcaaed-c6eb-4b8f-96b0-a01db08597f4,b79010b7-5221-46c7-991f-2f0b1bac8621
        Host: localhost:8090
        Cookie: JSESSIONID=1614C8D1F2EE5021F826CC80B4E658D8
        Accept-Encoding: gzip, deflate
        Content-Length: 85
        Connection: keep-alive
        cache-control: no-cache

        {
            "processDefinitionId": "PRC-aProcess:12:bcc5558e-c41f-11e9-9161-3a304270b7a7"
        }
        ```

  - Get Tasks GET

        ```
        /platform-api/search/query-tasks
        ```

  - Get Form definition and process variables

        ```
        GET /platform-api/tasks/{{taskId}}/form HTTP/1.1
        ```

  - Get the task (and process) variables

    ```
    ```

  - Save a Task

        ```
        POST /platform-api/tasks/<string>/save-form HTTP/1.1
        Host: localhost:8090
        Content-Type: application/json
        cache-control: no-cache
        Postman-Token: 1e2f74ca-eb1e-4c2a-939c-9b38c76c3e95

        {
            "values": "<object>"
        }
        ```
  - Complete Task
        ```
        POST /platform-api/tasks/{{taskId}}/complete HTTP/1.1
        Host: localhost:8090
        Content-Type: application/json
        Authorization: Basic YWRtaW46dGVzdA==
        User-Agent: PostmanRuntime/7.15.2
        Accept: */*
        Cache-Control: no-cache
        Postman-Token: 6e69d4de-0554-49ea-bf4e-a2cab38451d9,a4c95aea-4cec-4433-8249-be30d09f5d3b
        Host: localhost:8090
        Cookie: JSESSIONID=1614C8D1F2EE5021F826CC80B4E658D8
        Accept-Encoding: gzip, deflate
        Content-Length: 95
        Connection: keep-alive
        cache-control: no-cache

        {
            "values": {
                "foo": "foo",
                "bar": "bar"
            },
            "outcome": "COMPLETE"
        }
        ```

- Create a React App
  - npx create-react-app frontend
  - Clean default App.js
  - Include Flowable Forms component
  - Initialize the form with static values
  - Get the form definition and the variables from a hardcoded task
  - Put the form in a form container
  - Get the Payload of the form from the REST API
  - 

