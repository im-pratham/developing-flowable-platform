# Flowable Work Indexing Tour

In the following blog post, we are going to talk about the Flowable Work integration with Elasticsearch (ES from now on).

We will explain, step by step, how to build a Flowable Work application with indexing customizations, custom aliases and dynamic queries. Also, we will describe how to create a Flowable Page that uses the dynamic query.

For more advanced topics, you can have a look to [The Flowable Work Indexing documentation](https://documentation.flowable.com/dev-guide/3.2.0/825-work-indexing.html#indexingArchitecture).

This document was written with the following software versions:

* Java 8
* Elasticsearch 6.2.4
* Postgresql 10
* Flowable Work 3.2.2
* Flowable Design 3.2.0
* Flowable Control 3.2.1

Also, to visualize the ES indices we are going to use two tools: [Elasticsearch Head](https://github.com/tobias74/ES-head) and [Dejavu](https://github.com/appbaseio/dejavu).

## Starting a Flowable Work project with the Flowable Initializr

The best way to start a new flowable project is to use the [Flowable Initializr](https://initializr.flowable.io/).

![initializr](img/initializr.png)

As other initializers, the Flowable Initializr will generate a maven project with a structure and minimum content necessary to run a Hello World.
Just fill in the fields and download the project clicking on *"Get Project"* button.

The generated project generated has everything you need to run a Hello World. The first thing that you need is to run the docker compose file included in the project. The docker compose file includes a PostgreSQL instance, an ES OSS node, Flowable Design and Flowable Control.

![diagram](img/ArchitectureDiagram.png)

To run the project, first, you need to run the docker-compose file. Then, you'll need to run the generated Flowable Work Spring Boot Application. Once everything is up'n'running, you will find the servers in the following URLS:

* <http://localhost:8090/> - Flowable Work (admin:test)
* <http://localhost:8091/> - Flowable Design (admin:test)
* <http://localhost:8092/> - Flowable Control (admin:test)
* <http://localhost:1358/> - Dejavu - ES data browser
* <http://localhost:9100/> - Elasticsearch Head - ES Admin console

## Default Flowable features

So far, we haven't developed anything yet but it's time to play around with the "out of the shelf" Flowable Work indexing features ;)

Flowable Work creates the following aliases and indices:

| Alias                                 | Index                                                         |
| ------------------------------------- | ------------------------------------------------------------- |
| flowableworkindexingtasks             | flowableworkindexingtasks-YYYYMMDD-HHMM-SS-ssssss             |
| flowableworkindexingplan-items        | flowableworkindexingplan-items-YYYYMMDD-HHMM-SS-ssssss        |
| flowableworkindexingactivities        | flowableworkindexingactivities-YYYYMMDD-HHMM-SS-ssssss        |
| flowableworkindexingprocess-instances | flowableworkindexingprocess-instances-YYYYMMDD-HHMM-SS-ssssss |
| flowableworkindexingusers             | flowableworkindexingusers-YYYYMMDD-HHMM-SS-ssssss             |
| flowableworkindexingcase-instances    | flowableworkindexingcase-instances-YYYYMMDD-HHMM-SS-ssssss    |
| flowableworkindexingwork              | flowableworkindexingwork-YYYYMMDD-HHMM-SS-ssssss              |

The indices are created with a timestamp suffix and an alias is associated to each of them. The alias is used to reference the data. The index timestamp suffix allows the creation of different indices with the same dataset, for instance, during a reindexing process. At the end of the reindexing process, the alias is switched to the new process allowing a seamless transition.

Flowable Work exposes some endpoints to consume the information stored in ES.

* **Work:** [/platform-api/search/query-work-instances](https://documentation.flowable.com/appdev-swagger/3.2.0/_attachments/platform.html#/Work%20instances/searchWorkInstancesWithQuery)
* **Cases:** [/platform-api/search/query-case-instances](https://documentation.flowable.com/appdev-swagger/3.2.0/_attachments/platform.html#/Case%20instances/customSearchCaseInstancesWithQuery)
* **Process:** [/platform-api/search/query-process-instances](https://documentation.flowable.com/appdev-swagger/3.2.0/_attachments/platform.html#/Process%20instances/searchProcessInstancesWithQuery)
* **Tasks:** [/platform-api/search/query-tasks](https://documentation.flowable.com/appdev-swagger/3.2.0/_attachments/platform.html#/Tasks/customSearchTasksWithQuery)

Right now, there is a bug in the documentation of the endpoints. The parameters are documented as "body" values for the request but they are GET requests. The fields that are shown in the body can be used as query parameters. For instance:

<http://localhost:8090/platform-api/search/query-tasks?completed=false&caseDefinitionName=Simple%20Case/>

The endpoints return a standard json response with a predefined format by Flowable Work. An example of how this response can be overwritten is shown below.

```json
{
  "data" : [ {
    "id" : "TSK-e01ad48d-b944-11e9-9bb3-723dcf61d880",
    "assignee" : "admin",
    "name" : "A Task",
    "createTime" : "2019-08-07T20:55:08.023+02:00",
    "priority" : 50,
    "suspended" : false,
    "scopeDefinitionId" : "PRC-aProcess:1:4bc3bf3f-b92c-11e9-bf74-723dcf61d880",
    "scopeId" : "PRC-bd80a534-b944-11e9-9bb3-723dcf61d880",
    "scopeType" : "bpmn",
    "tenantId" : "",
    "formKey" : "aForm",
    "endTime" : "2019-08-07T20:56:54.788+02:00",
    "executionId" : "PRC-e0188a97-b944-11e9-9bb3-723dcf61d880",
    "processInstanceId" : "PRC-bd80a534-b944-11e9-9bb3-723dcf61d880",
    "processDefinitionId" : "PRC-aProcess:1:4bc3bf3f-b92c-11e9-bf74-723dcf61d880",
    "processDefinitionName" : "A Process",
    "taskDefinitionId" : "formtask1",
    "rootScopeId" : "PRC-bd80a534-b944-11e9-9bb3-723dcf61d880",
    "rootScopeType" : "bpmn",
    "rootScopeName" : null,
    "rootScopeBusinessKey" : null,
    "rootScopeDefinitionId" : "PRC-aProcess:1:4bc3bf3f-b92c-11e9-bf74-723dcf61d880",
    "rootScopeDefinitionKey" : "aProcess",
    "rootScopeDefinitionName" : "A Process",
    "parentScopeId" : "PRC-bd80a534-b944-11e9-9bb3-723dcf61d880",
    "parentScopeType" : "bpmn",
    "parentScopeName" : null,
    "parentScopeBusinessKey" : null,
    "parentScopeDefinitionId" : "PRC-aProcess:1:4bc3bf3f-b92c-11e9-bf74-723dcf61d880",
    "parentScopeDefinitionKey" : "aProcess",
    "parentScopeDefinitionName" : "A Process",
    "root" : {
      "foo" : "fooValue",
      "bar" : "barValue",
      "form_aForm_outcome" : "COMPLETE",
      "initiator" : "admin"
    },
    "parent" : {
      "foo" : "fooValue",
      "bar" : "barValue",
      "form_aForm_outcome" : "COMPLETE",
      "initiator" : "admin"
    }
  } ],
  "total" : 1,
  "start" : 0,
  "sort" : "createTime",
  "order" : "desc",
  "size" : 1
}
```

Also, for every index there is [a POST endpoint](https://documentation.flowable.com/appdev-swagger/3.2.0/_attachments/platform.html#/User%20Admin) available to trigger the reindexation process.

You can obtain more information about this topic in the [official documentation](https://documentation.flowable.com/dev-guide/3.2.0/825-work-indexing.html).

## Building Indexing Flowable Work Application

We will use a very simple Flowable app. You will find it in folder `design-app` from the sources.

We will start with just a case with a human task associated to a form with two fields "foo" and "bar". When the case is created, Flowable Work starts a process to save the information in ES. The saved in  data looks like this:

```json
{
    "_index": "flowableworkindexingtasks-20190806-1003-08-36188034",
    "_type": "_doc",
    "_id": "TSK-cff3a065-b838-11e9-9521-723dcf61d880",
    "_version": 7,
    "_score": null,
    "_source": {
        "__flowableVersion": 2,
        "rootScopeId": "CAS-cff267de-b838-11e9-9521-723dcf61d880",
        "caseDefinitionKey": "simpleCase",
        "dueDate": null,
        "caseDefinitionName": "Simple Case",
        "scopeDefinitionName": "Simple Case",
        "id": "TSK-cff3a065-b838-11e9-9521-723dcf61d880",
        //[...more fields ...]
        "variables": [
            {
                "scopeId": "CAS-cff267de-b838-11e9-9521-723dcf61d880",
                "scopeDefinitionId": "CAS-a72db2f0-b838-11e9-9521-723dcf61d880",
                "scopeDefinitionKey": "simpleCase",
                "textValue": "admin",
                "scopeType": "cmmn",
                "rawValue": "admin",
                "name": "initiator",
                "id": "VAR-cff28ef0-b838-11e9-9521-723dcf61d880",
                "type": "string",
                "textValueKeyword": "admin"
            },
            //[...more variables ...]
            {
                "scopeId": "TSK-cff3a065-b838-11e9-9521-723dcf61d880",
                "scopeDefinitionId": "CAS-a72db2f0-b838-11e9-9521-723dcf61d880",
                "scopeDefinitionKey": "simpleCase",
                "textValue": "barValue",
                "scopeType": "task",
                "rawValue": "barValue",
                "name": "bar",
                "scopeHierarchyType": "root",
                "id": "VAR-cd3af31b-b8d8-11e9-bf74-723dcf61d880",
                "type": "string",
                "textValueKeyword": "barValue"
            },
            {
                "scopeId": "TSK-cff3a065-b838-11e9-9521-723dcf61d880",
                "scopeDefinitionId": "CAS-a72db2f0-b838-11e9-9521-723dcf61d880",
                "scopeDefinitionKey": "simpleCase",
                "textValue": "fooValue",
                "scopeType": "task",
                "rawValue": "fooValue",
                "name": "foo",
                "scopeHierarchyType": "root",
                "id": "VAR-cd3ca0cc-b8d8-11e9-bf74-723dcf61d880",
                "type": "string",
                "textValueKeyword": "fooValue"
            }
        ],
        //[...more fields ...]
    }
}
```

Flowable Work extracts the form variables information and stores this data inside the variables object.

## Transforming JSON Variables into regular variables

By default, when you use sub forms, the variables of the sub forms are indexed inside a single JSON variable. This is the most efficient way to store a variable but it's not very helpful when you are searching for data. We will create a new Process (`aProcess`) with a task referencing a form with a sub form. The following JSON shows how the variables are stored in ES:

```json
{
    "_index": "flowableworkindexingtasks-20190806-1003-08-36188034",
    "_type": "_doc",
    "_id": "TSK-7736f27c-b92c-11e9-bf74-723dcf61d880",
    "_version": 7,
    "_score": null,
    "_source": {
        "__flowableVersion": 2,
        "rootScopeId": "CAS-5e0e5d50-b92c-11e9-bf74-723dcf61d880",
        //[...more fields ...]
        "rootScopeDefinitionName": "Case with a Process with a Task with a Sub Form",
        "variables": [
            //[...more variables ...]
            {
                "scopeId": "TSK-7736f27c-b92c-11e9-bf74-723dcf61d880",
                "scopeDefinitionId": "PRC-aProcess:1:4bc3bf3f-b92c-11e9-bf74-723dcf61d880",
                "scopeDefinitionKey": "aProcess",
                "scopeType": "task",
                "rawValue": "{\"aTextInASubForm\":\"This is a text in a subform\"}",
                "jsonValue": {
                    "aTextInASubForm": "This is a text in a subform"
                },
                "name": "aSubForm",
                "scopeHierarchyType": "parent",
                "id": "VAR-8460168f-b92c-11e9-bf74-723dcf61d880",
                "type": "json"
            }
        ],
        //[...more fields ...]
    }
}
```

As you can see, "aSubForm" is JSON variable that contains another variable called "aTextInASubForm" with no type information. For this reason, we will not be able to create queries using the "aTextInASubForm" values.

To solve this problem, we can use the extraction of variables feature. This feature is a part of [mapping extension configuration](https://documentation.flowable.com/dev-guide/3.2.0/825-work-indexing.html#indexingMappingExtensions).

To use this feature, you must create a json file in the path **/com/flowable/indexing/mapping-extension/custom** with a custom ES mapping and a "variableExtractors" section:

```json
{
    "key": "aTextInASubForm-mapping-extension",
    "extends": "tasks",
    "version": 1,
    "properties": {
        "extractedTextFromASubForm": {
            "type": "text"
        }
    },
    "variableExtractors": [{
        "filter": {
            "name": "aSubForm",
            "scopeDefinitionKey": "aProcess"
        },
        "path": "/aTextInASubForm",
        "to": "extractedTextFromASubForm",
        "type": "string"
    }]
}
```

Restart the project and create a new process. Now, the result when ES document is saved is as shown below:

```json
{
    "_index": "flowableworkindexingtasks-20190807-1720-24-62424242",
    "_type": "_doc",
    "_id": "TSK-6e43e0c2-b938-11e9-b217-723dcf61d880",
    "_version": 7,
    "_score": null,
    "_source": {
        "__flowableVersion": 2,
        "rootScopeId": "PRC-6e42cf49-b938-11e9-b217-723dcf61d880",
        "dueDate": null,
        "processDefinitionName": "A Process",
        "scopeDefinitionName": "A Process",
        "id": "TSK-6e43e0c2-b938-11e9-b217-723dcf61d880",
        //[...more fields ...]
        "taskModelName": "A Task",
        "rootScopeDefinitionName": "A Process",
        "variables": [
            //[...more variables ...]
            {
                "scopeId": "TSK-6e43e0c2-b938-11e9-b217-723dcf61d880",
                "scopeDefinitionId": "PRC-aProcess:1:4bc3bf3f-b92c-11e9-bf74-723dcf61d880",
                "scopeDefinitionKey": "aProcess",
                "scopeType": "task",
                "rawValue": "{\"aTextInASubForm\":\"This is a text is a sub form\"}",
                "jsonValue": {
                    "aTextInASubForm": "This is a text is a sub form"
                },
                "name": "aSubForm",
                "scopeHierarchyType": "root",
                "id": "VAR-7910ec5f-b938-11e9-b217-723dcf61d880",
                "type": "json"
            },
            {
                "scopeId": "TSK-6e43e0c2-b938-11e9-b217-723dcf61d880",
                "scopeDefinitionId": "PRC-aProcess:1:4bc3bf3f-b92c-11e9-bf74-723dcf61d880",
                "scopeDefinitionKey": "aProcess",
                "textValue": "This is a text is a sub form",
                "scopeType": "task",
                "rawValue": "This is a text is a sub form",
                "name": "extractedTextFromASubForm",
                "scopeHierarchyType": "root",
                "id": "VAR-7910ec5f-b938-11e9-b217-723dcf61d880-extractedTextFromASubForm",
                "type": "string",
                "textValueKeyword": "This is a text is a sub form"
            }
        ],
        //[...more fields ...]
    }
}
```

As we can see in the json above, in addition to the json variable, now we can find a new variable called "extractedTextFromASubForm" and we can use it like any other *"regular"* variable.

## Advanced customization of the Flowable Work indices

At the moment, we have worked with default data model, that is, we have not modified the ES indices. Sometimes, we will need some *"special"* field in ES. For instance, we might require a field that is calculated from an existing variable. This new field can be the key to simplify the queries.

The first step is add new properties to the index with a mapping extension file. We just have to add the properties section to a new json mapping-extension file. You can put this file next to the previous one.

```json
{
    "key": "extractedTextFromASubFormInUppercaseAsAField-mapping-extension",
    "extends": "tasks",
    "version": 1,
    "properties": {
        "extractedTextFromASubFormInUppercaseAsAField": {
            "type": "text"
        }
    }
}
```

After defining the properties, we have to [provide the data](https://documentation.flowable.com/dev-guide/3.2.0/825-work-indexing.html#providing-data-for-mapping-extensions) by implementing the `PlatformIndexedDataEnhancer` interface or extending the `IndexedDataEnhancerAdapter`.

For our example we are going to extend the `IndexedDataEnhancerAdapter`.

```java
@Component
public class CustomIndexedDataEnhancerAdapter extends IndexedDataEnhancerAdapter {

    public static final String TEXT_VALUE = "textValue";
    public static final String EXTRACTED_TEXT_FROM_A_SUB_FORM = "extractedTextFromASubForm";
    public static final String EXTRACTED_TEXT_FROM_A_SUB_FORM_IN_UPPERCASE_AS_A_FIELD = "extractedTextFromASubFormInUppercaseAsAField";

    @Override
    public void enhanceVariableCreateData(VariableInstanceEntity variable, String scopeId, String scopeType, String scopeHierarchyType, ObjectNode data, IndexingManagerHelper indexingManagerHelper) {
        if (data.has(CREATED_VARIABLES)) {
            JsonNode createdVariables = data.get(CREATED_VARIABLES);
            if (!createdVariables.isNull() && createdVariables.size() > 0) {
                for (JsonNode variableNode : createdVariables) {
                    if (EXTRACTED_TEXT_FROM_A_SUB_FORM.equals(variableNode.get(FIELD_VARIABLE_NAME).asText())) {
                        data.put(EXTRACTED_TEXT_FROM_A_SUB_FORM_IN_UPPERCASE_AS_A_FIELD, variableNode.get(TEXT_VALUE).asText().toUpperCase());
                    }
                }
            }
        }
        super.enhanceVariableCreateData(variable, scopeId, scopeType, scopeHierarchyType, data, indexingManagerHelper);
    }

}
```

Now, there is a new property with an absurdly long name at the end of the index: "extractedTextFromASubFormInUppercaseAsAField".

```json
{
    "_index": "flowableworkindexingtasks-20190807-1853-41-68021882",
    "_type": "_doc",
    "_id": "TSK-e01ad48d-b944-11e9-9bb3-723dcf61d880",
    "_version": 8,
    "_score": null,
    "_source": {
        "__flowableVersion": 2,
        "rootScopeId": "PRC-bd80a534-b944-11e9-9bb3-723dcf61d880",
        "dueDate": null,
        "processDefinitionName": "A Process",
        "scopeDefinitionName": "A Process",
        "id": "TSK-e01ad48d-b944-11e9-9bb3-723dcf61d880",
 //[...more fields ...]
        "taskModelName": "A Task",
        "rootScopeDefinitionName": "A Process",
        "variables": [
//[...more variables ...]
            {
                "scopeId": "TSK-e01ad48d-b944-11e9-9bb3-723dcf61d880",
                "scopeDefinitionId": "PRC-aProcess:1:4bc3bf3f-b92c-11e9-bf74-723dcf61d880",
                "scopeDefinitionKey": "aProcess",
                "scopeType": "task",
                "rawValue": "{\"aTextInASubForm\":\"asdfghjk\"}",
                "jsonValue": {
                    "aTextInASubForm": "asdfghjk"
                },
                "name": "aSubForm",
                "scopeHierarchyType": "root",
                "id": "VAR-0e27502a-b945-11e9-9bb3-723dcf61d880",
                "type": "json"
            },
            {
                "scopeId": "TSK-e01ad48d-b944-11e9-9bb3-723dcf61d880",
                "scopeDefinitionId": "PRC-aProcess:1:4bc3bf3f-b92c-11e9-bf74-723dcf61d880",
                "scopeDefinitionKey": "aProcess",
                "textValue": "asdfghjk",
                "scopeType": "task",
                "rawValue": "asdfghjk",
                "name": "extractedTextFromASubForm",
                "scopeHierarchyType": "root",
                "id": "VAR-0e27502a-b945-11e9-9bb3-723dcf61d880-extractedTextFromASubForm",
                "type": "string",
                "textValueKeyword": "asdfghjk"
            }
        ],
 //[...more fields ...]
        "endTime": "2019-08-07T18:56:54.788Z",
        "extractedTextFromASubFormInUppercaseAsAField": "ASDFGHJK"
    }
}
```

## Querying ES with aliases and dynamic queries

So far, we have used the query-* REST endpoints of the Flowable REST API to query the index. Now, we are going to introduce two ways of doing queries: aliases and dynamic queries. Aliases do not allow the parameterization of queries. This means that the query of an alias will always be about a static value. However, dynamic queries are designed to fill this gap and it allows the parameterization of queries on ES. The version attribute in aliases and queries is very important. At startup, Flowable will read all alias/query definitions (json file). If the key does not exist in ES it will be created but if the key already exists in ES, the version number will be checked. Aliases will only be updated in ES when the version number is higher.

First, we are going to create an alias. We must create a JSON file in the same path as the mapping extensions.

```json
{
    "key": "process-search-alias",
    "sourceIndex": "process-instances",
    "type": "alias",
    "version": 1,
    "customFilter": {
        "bool": {
            "must": [{
                    "term": {
                        "processDefinitionKey": "aProcess"
                    }
                },
                {
                    "nested": {
                        "path": "variables",
                        "query": {
                            "bool": {
                                "must": [{
                                    "term": {
                                        "variables.name": "aText"
                                    }
                                }]
                            }
                        }
                    }
                }
            ]
        }
    }
}
```

Next, we are going to create a dynamic query based in the structure of the alias bellow.

```json
{
    "key": "process-search-query",
    "sourceIndex": "process-instances",
    "type": "query",
    "version": 1,
    "parameters": {
        "aTextValue": "string"
    },
    "customFilter": {
        "bool": {
            "must": [{
                    "term": {
                        "processDefinitionKey": "aProcess"
                    }
                },
                {
                    "nested": {
                        "path": "variables",
                        "query": {
                            "bool": {
                                "must": [{
                                    "term": {
                                        "variables.name": "aText"
                                    }
                                }, {
                                    "prefix": {
                                        "variables.textValue": "{aTextValue}"
                                    }
                                }]
                            }
                        }
                    }
                }
            ]
        }
    }
}
```

The structure is very similar but this time we are adding the parameters that this dynamic query is going to accept.

After setting this files and restarting the project, we will be able to invoke the alias and the dynamic query with the following Flowable REST API URLs:

* <http://localhost:8090/platform-api/search/query-process-instances/alias/process-search-alias/>
* <http://localhost:8090/platform-api/search/query-process-instances/query/process-search-query?aTextValue=qwedrftgyuiop/>

Behind the scenes, Flowable Work is using aliases to the corresponding index. In this example, we now have two more aliases for the process-instances index.

![newAliases](img/newAliases.png)

The `process-search-alias` can be used "as-is" without Flowable Work. The `process-search-query` alias corresponding to the dynamic query cannot be used without Flowable Work because it contains the parameter reference in the filter criteria. This parameter reference cannot be handled by ES alone (it will return zero results).

For more information about aliases and dynamic queries, you can take a look at the [official documentation](https://documentation.flowable.com/dev-guide/3.2.0/825-work-indexing.html#indexingCustomAliases).

## Using a Flowable Page to display the results of a query

We are going to use a Flowable Page to display the results of a query. The Page will contain a Text input and a Data table.

![aNewPageDesign](img/aNewPageDesign.png)

We need to set the data source `Query URL` property to "/platform-api/search/query-process-instances/query/process-search-query?aTextValue={{searchText}}" and the `Path` property to "data". The `Query URL` property is bound to the `{{searchText}}` variable so every time the variable changes, the Data table component will call the URL for new data.

![datasource](img/datasource.png)

Finally, we need to set the columns of the Data table. We are going to show only a couple of fields and the value of the "aText" variable.

![columns](img/columns.png)

When we include the new page in the application and we publish it, we will be able to see in the page all the processes that have an "aText" variable. When the page is displayed initially, the value of `{{searchText}}` is empty. The dynamic query is configured with a "prefix" criteria for the variable value so in absence of any prefix, all variable values match.

![aNewPageWork](img/aNewPageWork.png)

As soon as we start typing, we will see how the Data table gets effectively filtered.

![aNewPageWorkFilterd](img/aNewPageWorkFiltered.png)

## Conclusions

In this blog post, we have seen how to create simple queries and how to use them in Flowable pages. Other more advanced concepts, such as *Dynamic Queries with Templates* have been left out of the scope of this post.

Nevertheless, we hope that you find this tour useful to get a taste of how the ES integration works before getting into more complex scenarios.
