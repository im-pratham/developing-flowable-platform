# Walk through Flowable Work Indexing

In the following blog post, we are going to talk about how to use the new Flowable Work features to work with Elasticsearch. 
Throughout it, we will explain, step by step, how to build a Flowable Work application that uses these features.
In addition, we will describe how to build a Flow-App to generate custom dashboards.

## Starting with Flowable Work: Initializr

The best way to start a flowable project is to use the [initializr](https://initializr.flowable.io/).

![initializr]

As other initializers, Flowable Initializr will generate a maven project with a structure and minimum content necessary to run a Hello World.
Just fill in the fields and download the project clicking on *"Get Project"* button.

### Basic configuration

The project generated has everything you need to run a Hello World.

![projectStructure]

**Docker folder:** It contains a docker compose file with PostgreSQL, Elasticsearch OSS, Flowable Design and Flowable Control. We have added a Kibana OSS to work with Elasticsearch. The use of Kibana is completely optional and is not required for the startup.

![diagram]

## Default Flowable features

Flowable Work creates the indices *work*, *case-instances*, *tasks*, *users*, *plan-items* and *activities*.

![defaultIndices]

Flowable Work exposes some endpoints to consume the information stored in elasticsearch.

- **Work:** /platform-api/search/query-work-instances
- **Cases:** /platform-api/search/query-case-instances
- **Process:** /platform-api/search/query-process-instances
- **Tasks:** /platform-api/search/query-tasks

The endpoints send a standard json response with a predefined format by Flowable Work. An example of how this response can be overwritten is shown below.

You can obtain more information about this topic in the [official documentation](https://documentation.flowable.com/dev-guide/3.2.0/825-work-indexing.html).

## Building Indexing Flowable Work Application

At the moment, we haven't developed anything yet. It's time to play around with Flowable Work features ;)

To explain these features we need to create an App and use it as deployment unit. This means that the app is a container where the processes, cases and tasks will be deployed.
We will explain more about this later.

In this case, we are going to use two apps: the first one is *Flowable Platform Example Apps*. They are a group of applications that can be used as samples.
For using them, we will add their maven dependencies:

```xml
    <!-- Default models and example apps -->
        <dependency>
            <groupId>com.flowable.platform</groupId>
            <artifactId>flowable-platform-default-models</artifactId>
        </dependency>
        <dependency>
            <groupId>com.flowable.platform</groupId>
            <artifactId>flowable-platform-default-idm-models</artifactId>
        </dependency>
        <dependency>
            <groupId>com.flowable.platform</groupId>
            <artifactId>flowable-platform-example-apps</artifactId>
        </dependency>
```

With this, we can now use these sample applications. For this demo, we will use Travel Request App.

![travelExampleApp]

It's a good example because Travel Request App has a form with all information of the travel. When the case is created, Flowable Work starts a process to save the information in elasticsearch.

The saved data looks like this:

    {
      "_index": "case-instances-20190703-1357-50-50270117",
      "_type": "_doc",
      "_id": "CAS-389baf35-9e50-11e9-8e17-a8206644d596",
      "_version": 3,
      "_score": 1,
      "_source": {
       .
       .
       .
        "variables": [
          .
          .
          .
          {
            "scopeId": "CAS-389baf35-9e50-11e9-8e17-a8206644d596",
            "scopeDefinitionId": "CAS-c3183a56-9d9a-11e9-af44-a8206644d596",
            "scopeDefinitionKey": "travelRequest",
            "textValue": "Spain",
            "scopeType": "cmmn",
            "rawValue": "Spain",
            "name": "origin",
            "id": "VAR-38a01c0b-9e50-11e9-8e17-a8206644d596",
            "type": "string",
            "textValueKeyword": "Spain"
          },
          {
            "scopeId": "CAS-389baf35-9e50-11e9-8e17-a8206644d596",
            "scopeDefinitionId": "CAS-c3183a56-9d9a-11e9-af44-a8206644d596",
            "scopeDefinitionKey": "travelRequest",
            "textValue": "France",
            "scopeType": "cmmn",
            "rawValue": "France",
            "name": "destination",
            "id": "VAR-38a01c0c-9e50-11e9-8e17-a8206644d596",
            "type": "string",
            "textValueKeyword": "France"
          }
        ]
      }
    } 
 
Flowable Work extracts the form variables information and creates a child inside the variables object. This process is very important because thanks to it, we will be able to do different queries on any variable. 

However, it is very common that we want to extract some components to be reused in another part of the application. For this reason, often sub-forms appear inside a form.  

To show this case of use with an example, we have created a new app with Flowable Design. It's a very simple app modeled with a case and a human task. 

![employeeInformation_model]

Now, the human task has a form with the employee information and the form contains a sub-form with origin and destination travel information. When a case is created, the information is stored in elasticsearch like this:

    {
      "_index": "case-instances-20190703-1357-50-50270117",
      "_type": "_doc",
      "_id": "CAS-2b0f931c-9e6f-11e9-b8dc-a8206644d596",
      "_version": 5,
      "_score": 1,
      "_source": {
        "variables": [
         .
         .
         .
          {
            "scopeId": "CAS-2b0f931c-9e6f-11e9-b8dc-a8206644d596",
            "scopeDefinitionId": "CAS-8c36d94f-9d9a-11e9-b538-a8206644d596",
            "scopeDefinitionKey": "employeeTravelRequest",
            "scopeType": "cmmn",
            "rawValue": "{\"origin\":\"ES\",\"destination\":\"CH\",\"outwardTripDate\":\"2019-07-14T00:00:00.000Z\",\"returnTripDate\":\"2019-07-13T00:00:00.000Z\"}",
            "jsonValue": {
              "returnTripDate": "2019-07-13T00:00:00.000Z",
              "outwardTripDate": "2019-07-14T00:00:00.000Z",
              "origin": "ES",
              "destination": "CH"
            },
            "name": "travelRequest",
            "id": "VAR-3db410fc-9e6f-11e9-b8dc-a8206644d596",
            "type": "json"
          }
        ]
      }
    } 

As you can see, origin and destination are not "normal" variables. Now, the information is saved as a json object. For this
reason, we will not be able to create queries using the origin and destination attributes on this type of elasticsearch documents. 

### Indexing customization

To solve this type of setbacks, we can use the extraction of variables. Flowable Work allows to extract variables from two different sources:

- Extracting Variable Values Into Custom Variable Fields
- Extracting Values from JSON Variables

You can read more about this topic on the [official documentation](https://documentation.flowable.com/dev-guide/3.2.0/825-work-indexing.html#indexingExtractingVariableValues).

Only the second one applies to this example.

#### Extracting Values from JSON Variables

It's a part of mapping [extension configuration](https://documentation.flowable.com/dev-guide/3.2.0/825-work-indexing.html#indexingMappingExtensions).

To use this feature, you just have to generate a json file in the path **/com/flowable/indexing/mapping-extension/custom**.

By using this functionality we can convert values from json object to a *"normal"* variable.


    "variableExtractors": [
        {
          "filter": {
            "name": "travelRequest",
            "scopeDefinitionKey": "employeeTravelRequest"
          },
          "path": "/origin",
          "to": "extractedOrigin",
          "type": "string"
        },
        {
          "filter": {
            "name": "travelRequest",
            "scopeDefinitionKey": "employeeTravelRequest"
          },
          "path": "/destination",
          "to": "extractedDestination",
          "type": "string"
        }
    ]
    
The result when elasticsearch document is saved is as shown below:

    {
      "_index": "case-instances-20190703-1357-50-50270117",
      "_type": "_doc",
      "_id": "CAS-ba2f5921-9ef2-11e9-b5ed-82a0b5abd6bc",
      "_version": 5,
      "_score": 1,
      "_source": {
        .
        .
        .
        "variables": [
          {
            "scopeId": "CAS-ba2f5921-9ef2-11e9-b5ed-82a0b5abd6bc",
            "scopeDefinitionId": "CAS-8c36d94f-9d9a-11e9-b538-a8206644d596",
            "scopeDefinitionKey": "employeeTravelRequest",
            "scopeType": "cmmn",
            "rawValue": "{\"origin\":\"CH\",\"destination\":\"FR\",\"outwardTripDate\":\"2019-07-28T00:00:00.000Z\",\"returnTripDate\":\"2019-08-03T00:00:00.000Z\"}",
            "jsonValue": {
              "returnTripDate": "2019-08-03T00:00:00.000Z",
              "outwardTripDate": "2019-07-28T00:00:00.000Z",
              "origin": "CH",
              "destination": "FR"
            },
            "name": "travelRequest",
            "id": "VAR-cb8d2631-9ef2-11e9-b5ed-82a0b5abd6bc",
            "type": "json"
          },
          {
            "scopeId": "CAS-ba2f5921-9ef2-11e9-b5ed-82a0b5abd6bc",
            "scopeDefinitionId": "CAS-8c36d94f-9d9a-11e9-b538-a8206644d596",
            "scopeDefinitionKey": "employeeTravelRequest",
            "textValue": "CH",
            "scopeType": "cmmn",
            "rawValue": "CH",
            "name": "extractedOrigin",
            "id": "VAR-cb8d2631-9ef2-11e9-b5ed-82a0b5abd6bc-extractedOrigin",
            "type": "string",
            "textValueKeyword": "CH"
          },
          {
            "scopeId": "CAS-ba2f5921-9ef2-11e9-b5ed-82a0b5abd6bc",
            "scopeDefinitionId": "CAS-8c36d94f-9d9a-11e9-b538-a8206644d596",
            "scopeDefinitionKey": "employeeTravelRequest",
            "textValue": "FR",
            "scopeType": "cmmn",
            "rawValue": "FR",
            "name": "extractedDestination",
            "id": "VAR-cb8d2631-9ef2-11e9-b5ed-82a0b5abd6bc-extractedDestination",
            "type": "string",
            "textValueKeyword": "FR"
          }
        ]
      }
    }
    
As we can see in the json above, in addition to the json varibale, there are now two new variables with name extractedOrigin and extractedDestination.
From now on, we can use them like any other *"normal"* variable.

#### Creating queries over Elasticsearch

In this example, we have used aliases and dynamic queries. I you want read more about it, you can take 
a look at the [official documentation](https://documentation.flowable.com/dev-guide/3.2.0/825-work-indexing.html#indexingCustomAliases).

Two of the most significant differences between aliases and dynamic queries are:
 
- Aliases do not allow the parameterization of queries. This means that the query of an alias will always be about a static value.
However, dynamic queries are designed to fill this gap and it allows the parameterization of queries on elasticsearch.

- On the other hand, aliases are stored in the metadata field of Elastisearch, while dynamic queries are generated on the fly. For this reason, version attribute in aliases is very important 
because at startup, Flowable will read all alias definitions (json file). If the key does not exist in Elasticsearch it will be created 
but if the key already exists in Elasticsearch, the version number will be checked. 
Aliases will only be updated in Elasticsearch when the version number is higher.

#### Alias and Dynamic queries examples
In the example below we are creating a static query on *case-instances* index and the response will be all travel request cases with the origin attribute.

    {
      "key": "travel-request-alias",
      "sourceIndex": "case-instances",
      "type": "alias",
      "version": 1,
      "customFilter": {
        "bool": {
          "must": [
            {
              "term": {
                "caseDefinitionKey": "travelRequest"
              }
            },
            {
              "nested": {
                "path": "variables",
                "query": {
                  "bool": {
                    "must": [
                      {
                        "term": {
                          "variables.name": "origin"
                        }
                      }
                    ]
                  }
                }
              }
            }
          ]
        }
      }
    }

Conversely, we are creating a dynamic query in the example below. We can parameterize the query with origin or destination property.

    {
      "key": "travel-request-origin-case",
      "name": "travel-request-origin-case",
      "type": "query",
      "sourceIndex": "case-instances",
      "version": 1,
      "parameters": {
        "origin": "string"
      },
      "customFilter": {
        "bool": {
          "must": [
            {
              "term": {
                "caseDefinitionKey": "travelRequest"
              }
            },
            {
              "nested": {
                "path": "variables",
                "query": {
                  "bool": {
                    "must": [
                      {
                        "term": {
                          "variables.name": "origin"
                        }
                      },
                      {
                        "match": {
                          "variables.textValue": "{origin}"
                        }
                      }
                    ]
                  }
                }
              }
            }
          ]
        }
      }
    }
    
    {
      "key": "travel-request-destination-prefix-case",
      "name": "travel-request-destination-prefix-case",
      "type": "query",
      "sourceIndex": "case-instances",
      "version": 1,
      "parameters": {
        "destination": "string"
      },
      "customFilter": {
        "bool": {
          "must": [
            {
              "term": {
                "caseDefinitionKey": "travelRequest"
              }
            },
            {
              "nested": {
                "path": "variables",
                "query": {
                  "bool": {
                    "must": [
                      {
                        "term": {
                          "variables.name": "destination"
                        }
                      },
                      {
                        "prefix": {
                          "variables.textValue": "{destination}"
                        }
                      }
                    ]
                  }
                }
              }
            }
          ]
        }
      }
    }

After defining the queries, we can invoke them using Flowable API Rest:

    /platform-api/search/query-case-instances/alias/travel-request-alias
    
    /query-case-instances/query/travel-request-origin-case?origin=Spain

Later on, we will explain how, where and why we are going to use this type of queries.

### Advanced Indexing customization

At the moment, we have worked with default data model, that is, we have not modified the Elasticsearch indices.
Sometimes, we will need some *"special"* field in Elasticsearch. A typical case could be when we want to save a data type like geo-point.
For instance, it may be interesting to persist the origin and destination field as a geo-point too.

In this case, the first step is add new properties to the index. Like the extraction of variables, the functionality of adding properties is also part of mapping-extensions.

We just have to add the properties in our json mapping-extension file.

    "properties": {
        "originGeoPoint": {
          "type": "geo_point"
        },
        "destinationGeoPoint": {
          "type": "geo_point"
        }
    }

After defining the properties, we have to [provide the data](https://documentation.flowable.com/dev-guide/3.2.0/825-work-indexing.html#providing-data-for-mapping-extensions).
Providing the data is done by implementing the **PlatformIndexedDataEnhancer** interface or extending the **IndexedDataEnhancerAdapter**.

For our example we could do something like this:

    @Override
    public void enhanceCaseInstanceEndData(CaseInstanceEntity caseInstanceEntity, ObjectNode data, IndexingManagerHelper indexingManagerHelper) {
        if (data.has(CREATED_VARIABLES)) {
            JsonNode createdVariables = data.get(CREATED_VARIABLES);
            if (!createdVariables.isNull() && createdVariables.size() > 0) {
                for (JsonNode variableNode : createdVariables) {
                    if (EXTRACTED_ORIGIN.equals(variableNode.get(FIELD_VARIABLE_NAME).asText())) {
                        JsonNode geopointNode = getJsonNode(variableNode);

                        data.replace(ORIGIN_GEO_POINT, geopointNode);
                    } else if (EXTRACTED_DESTINATION.equals(variableNode.get(FIELD_VARIABLE_NAME).asText())) {
                        JsonNode geopointNode = getJsonNode(variableNode);

                        data.replace(DESTINATION_GEO_POINT, geopointNode);
                    }
                }
            }
        }

    }

Now, two new properties appear in the index, destinationGeoPoint and originGeoPoint.

    {
      "_index": "case-instances-20190703-1357-50-50270117",
      "_type": "_doc",
      "_id": "CAS-ba2f5921-9ef2-11e9-b5ed-82a0b5abd6bc",
      "_version": 5,
      "_score": 1,
      "_source": {
        "__flowableVersion": 2,
        "caseDefinitionKey": "employeeTravelRequest",
        "caseDefinitionName": "Employee Travel Request",
        "startUserId": "admin",
        "startTime": "2019-07-05T07:01:35.445Z",
        "id": "CAS-ba2f5921-9ef2-11e9-b5ed-82a0b5abd6bc",
        "state": "completed",
        "caseDefinitionId": "CAS-8c36d94f-9d9a-11e9-b538-a8206644d596",
        "tenantId": "",
        .
        .
        .
        "destinationGeoPoint": {
          "lon": 2.213749,
          "lat": 46.227638
        },
        "originGeoPoint": {
          "lon": 8.227512,
          "lat": 46.818188
        }
      }
    }

One of the possibilities that gives having this type of data in Elasticsearch is using a tool to generate dashboards like Kibana.
Now, it's possible to create some dashboards like this:

![kibanaOrigin]

![kibanaDestination]

These visualizations allow us to show the origin/destination properties of the Employee Travel Cases in real time.

Another of the features of the enhancers is that they allow us to overwrite the default response of the Rest Endpoints.
In this way, for example, we could return a simple object to the front layer. To get it, we have to create a new Spring Component and 
implement **TaskResultMapper.Enhancer** interface.

An example could be something like this:

    @Component
    public class TravelRequestTaskEnhancer implements TaskResultMapper.Enhancer {
        private static final Logger LOGGER = LoggerFactory.getLogger(TravelRequestTaskEnhancer.class);
    
        @Override
        public void enhance(TaskSearchRepresentation response, JsonNode indexedData) {
    
            JsonNode scopeDefinitionKey = indexedData.get("scopeDefinitionKey");
            if (scopeDefinitionKey != null && scopeDefinitionKey.asText().equals("travelRequest")) {
    
                TaskSearchRepresentation myCustomResponse = new TaskSearchRepresentation();
    
                Map<String, Object> myVariables = response.getVariables();
    
                BeanUtils.copyProperties(myCustomResponse, response);
    
                LOGGER.info("Cleaning default response");
    
                response.setVariables(myVariables);
    
                LOGGER.info("Setting travel request variables");
            }
        }
    }

### Building sample application step by step

#### My first App

Well, once we have our queries ready, painting time begins ;) 

As we explained before, in Flowable, an application is as a container, that is, we can deploy process, cases, tasks, etc. in an App.
We can use Flowable Design to create a new App Definition.
If you want to read more about this topic, you can read the [official documentation](https://documentation.flowable.com/modeler-bpmn/3.2.0/01-bpmn-sample.html).

![createApp]

The next step is to add our models (CMMN and Forms models) by clicking on *"edit included models"* button.

![includedCases]

The result is something like this.

![casesAndModels]

In this way, we have linked our cases and forms with our application. To deploy our application inside Flowable Work just click on the publish App button. 

Now, we have a new application in our Flowable Work menu!

![indexingWorkApp]

Of course, we can create cases with our custom forms.

![employeeForm]

##### Flow-App concept

In addition to model cases, process, decision tables and forms with Flowable Design we can create Flow-Apps with custom dashboards.
A [Flow-App](https://documentation.flowable.com/dev-guide/3.2.0/200-flow-app-with-design.html#creating-a-flow-app)
is a specific type of application that provides Pages, which appear in the left menu of Flowable Work.
A Page provides a dashboard view that can contain, for example, a list of open tasks, open case instances or some custom dashboard.

In this example, we are going to create a Flow-App with two pages to show the cases of the Travel Request type.
Also, we will use the elasticsearch queries (alias and dynamic query) to filter the dashboards.

##### Creating our Flow-App

The first page will have a dashboard with a travel request cases list, and it will be possible to filter by origin. All cases of Travel Request will be loaded at first. 
In addition, the combo-box component will be filled with all possible values from Elasticsearch.

To implement this behavior, we have put two equal data table component. The first one will be visible when the origin filter is not active or we want to see all the origins. 
The second data table will only be visible when we use the origin combo-box filter.

![page1]

To load the combo-box with the data from Elasticsearch, we are going to use the alias that we have defined previously.

![page1_alias]

We are creating a query in Elasticsearch and it will return all matching cases. 
One problem with this approach is that if we have a high number of cases stored in Elasticsearch, 
the object returned will be very heavy. 

To improve the application performance, we will change the approach. For this reason, we are going to create a new page 
(similar to the previous one) and now, we will filter by the destination attribute.

![page2_query]

In this case, the Query URL attribute of select component has been configured with the previous dynamic query (destination dynamic query). In addition, we have enabled autocomplete query using **{{$searchText}}** variable on Query URL.
A benefit of this approach is that, the component is creating queries in Elasticsearch each time it's modified. The return objects are more lightweight and we have a greater performance.

#### Using Flow-App in Flowable Work

Finally, we publish the app and we can see the Flow-App on the left menu of Flowable Work.

![flowApp]

## Conclusions
Elasticsearch is a requirement of Flowable Work. In order to take advantage of this part of the architecture, Flowable exposes different features to work with Elasticsearch.
In this blog post, we have seen how to create simple queries and how to use them in examples. Other more advanced concepts, such as *Dynamic Queries with Templates* or *Reindenxig*, have been left out. 
Probably, these concepts are a good point to continue.

## Links
### Indexing Application Example
#### Apps
[Flowable Work Indexing App](http://localhost:8090/flowable-work)

[Flowable Design](http://localhost:8091)

[Kibana](http://localhost:5601)

#### Default Flowable Endpoints
[Tasks Query](http://localhost:8090/flowable-work/platform-api/search/query-tasks)

[Cases Query](http://localhost:8090/flowable-work/platform-api/search/query-case-instances)

#### Alias Example
[Alias Query](http://localhost:8090/flowable-work/platform-api/search/query-case-instances/alias/travel-request-alias)

#### Dynamic Queries Example 
[Custom Task Dynamic Query](http://localhost:8090/flowable-work/platform-api/search/query-tasks/query/travel-request-origin-query?origin=Spain)

[Custom Case Dynamic Query](http://localhost:8090/flowable-work/platform-api/search/query-case-instances/query/travel-request-origin-case?origin=Spain)

---

<!-- Images -->

<!-- Initializr -->
[initializr]: img/initializr/initializr.png
[diagram]: img/initializr/ArchitectureDiagram.png

<!-- Main Configuration -->
[projectStructure]: img/main-conf/projectStructure.png
[kibanaOrigin]: img/main-conf/kibanaOrigin.png
[kibanaDestination]: img/main-conf/kibanaDestination.png
[defaultIndices]: img/main-conf/defaultIndices.png

<!-- Work Application -->
[employeeInformation_model]: img/work-app/sampleEmployeeApp.png
[travelExampleApp]: img/work-app/travelRequestExampleApp.png

<!-- Building App -->
[createApp]: img/building-app/createApp.png
[casesAndModels]: img/building-app/casesAndModels.png
[includedCases]: img/building-app/includedCases.png
[indexingWorkApp]: img/building-app/indeingTrainingWorkApp.png
[employeeForm]: img/building-app/createEmployeeForm.png

<!-- Building Flow-App -->
[page1]: img/building-flow-app/page1.png
[page1_alias]: img/building-flow-app/page1_alias.png
[page2_query]: img/building-flow-app/page2_query.png
[flowApp]: img/building-flow-app/flowApp.png
