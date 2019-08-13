package com.flowable.training.indexing;

import static com.flowable.indexing.api.IndexingJsonConstants.FIELD_VARIABLES;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.awaitility.Duration;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.ResponseException;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.mapper.MapperService;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.flowable.common.engine.api.FlowableException;
import org.flowable.engine.ManagementService;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.form.api.FormInfo;
import org.flowable.job.api.HistoryJob;
import org.flowable.task.api.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.flowable.autoconfigure.indexing.FlowableIndexingProperties;
import com.flowable.indexing.IndexManager;
import com.flowable.platform.service.index.PlatformReindexService;

@SpringBootTest
@ExtendWith(SpringExtension.class)
class FlowableWorkIndexingApplicationTest {

    @Autowired
    public ProcessEngine processEngine;

    @Autowired
    protected ManagementService managementService;

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Autowired
    private FlowableIndexingProperties flowableIndexingProperties;

    @Autowired
    private IndexManager indexManager;

    @Autowired
    private PlatformReindexService platformReindexService;

    @Test
    void canCreateApplication() throws IOException {
        ProcessInstance comeOn = processEngine.getRuntimeService().startProcessInstanceByKey("aProcess");

        Task task = processEngine.getTaskService().createTaskQuery().processInstanceId(comeOn.getId()).singleResult();

        FormInfo form = processEngine.getTaskService().getTaskFormModel(task.getId());

        Map<String, Object> variables = new HashMap<>();
        variables.put("aSubForm", Collections.singletonMap("aTextInASubForm", "Make me upper case"));
        processEngine.getTaskService().completeTaskWithForm(task.getId(), form.getId(), "complete", variables);

        executeHistoryJobsAndReloadIndex();

        SearchResponse result = findIndexedTask(task);
        assertTaskIndexEnhancements(result);

        deleteTaskFromIndex(task);

        platformReindexService.reindexTasks();

        result = await("Wait for reindex to finish")
            .atMost(new Duration(20, TimeUnit.SECONDS))
            .pollDelay(Duration.ONE_SECOND)
            .ignoreExceptions()
            .until(() -> {
                executeHistoryJobsAndReloadIndex();
                return findIndexedTask(task);
            }, r -> r.getHits().getTotalHits() == 1L);

        assertTaskIndexEnhancements(result);
    }

    @SuppressWarnings("unchecked")
    private void assertTaskIndexEnhancements(SearchResponse result) {
        assertThat(result.getHits().getTotalHits()).isEqualTo(1L);
        assertThat(result.getHits().getAt(0).getSourceAsMap().get("extractedTextFromASubFormInUppercaseAsAField"))
            .isEqualTo("MAKE ME UPPER CASE");
        assertThat(((Collection<Map<String, Object>>) result.getHits().getAt(0).getSourceAsMap().get(FIELD_VARIABLES)).stream()
            .filter(v -> v.get("name").equals("extractedTextFromASubForm")).map(v -> v.get("textValue")).findFirst().get()).isEqualTo("Make me upper case");
    }

    private void deleteTaskFromIndex(Task task) throws IOException {
        DeleteRequest deleteRequest = new DeleteRequest();
        deleteRequest.index(taskIndex());
        deleteRequest.id(task.getId());
        deleteRequest.type(MapperService.SINGLE_MAPPING_NAME);
        restHighLevelClient.delete(deleteRequest, RequestOptions.DEFAULT);

        indexManager.reloadIndices();

        assertThat(findIndexedTask(task).getHits().getTotalHits()).isEqualTo(0L);
    }

    private SearchResponse findIndexedTask(Task task) throws IOException {
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices(taskIndex());
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.idsQuery().addIds(task.getId()));
        searchRequest.source(searchSourceBuilder);

        return restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
    }

    private String taskIndex() {
        return flowableIndexingProperties.getIndexNamePrefix() + "tasks";
    }

    @BeforeEach
    public void setUp() {
        // clean up
        cleanUpHistoryJobs();
        resetIndices();
    }

    protected void executeHistoryJobsAndReloadIndex() {
        // Manually execute all history jobs
        executeDefaultHistoryJobs();
        indexManager.reloadIndices();
    }

    private void executeDefaultHistoryJobs() {
        while (managementService.createHistoryJobQuery().count() > 0) {
            managementService.createHistoryJobQuery().list().forEach(this::executeHistoryJob);
        }
    }

    private void executeHistoryJob(HistoryJob historyJob) {
        try {
            managementService.executeHistoryJob(historyJob.getId());
        } catch (FlowableException ex) {
            if (ex.getCause() instanceof ResponseException) {
                // If the cause of the exception was a response exception due to not found, just ignore it
                // see ElasticsearchClientImpl
                ResponseException cause = (ResponseException) ex.getCause();
                if (cause.getResponse().getStatusLine().getStatusCode() == HttpStatus.NOT_FOUND.value()) {
                    managementService.deleteHistoryJob(historyJob.getId());
                }
            }
        }
    }

    private void cleanUpHistoryJobs() {
        // clean up history jobs
        managementService.createHistoryJobQuery()
            .list()
            .forEach(j -> managementService.deleteHistoryJob(j.getId()));
    }

    private void resetIndices() {
        indexManager.deleteAllKnownIndices();
        indexManager.createIndexesFromMappings();
        indexManager.reloadIndices();
    }
}