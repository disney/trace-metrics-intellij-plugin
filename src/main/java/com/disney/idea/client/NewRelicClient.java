package com.disney.idea.client;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.annotations.VisibleForTesting;
import com.intellij.openapi.progress.ProgressIndicator;

/**
 * Prepares and performs query to remote New Relic web API server to retrieve
 * trace metrics.
 * Also provides utility methods to construct the API URL for retrieving a
 * metrics data block, and to construct a partial New Relic Insights URL for
 * opening a query view for a named metric in an external browser.
 */
public class NewRelicClient {
    private final String accountId;
    private final String apiKey;
    private final String nrUrl;
    private final String appName;
    private final String numDays;
    private final String untilDate;

    private static final String API_URL = "https://api.newrelic.com/graphql";
    private static final String INSIGHTS_URL = "https://insights.newrelic.com/accounts/%s/query?query=";
    private static final String defaultTraceQueryTemplate = "{\"query\":\"{actor{nrql(query:\\\"SELECT count(*) FROM Transaction WHERE name LIKE 'WebTransaction/Custom/%%' AND appName = '%s' SINCE %s days ago FACET name LIMIT 1000\\\",accounts:%s,timeout:200){results}}}\",\"variables\":\"\"}";
    private static final String dateRangeTraceQueryTemplate = "{\"query\":\"{actor{nrql(query:\\\"SELECT count(*) FROM Transaction WHERE name LIKE 'WebTransaction/Custom/%%' AND appName = '%s' SINCE '%s' UNTIL '%s' FACET name LIMIT 1000\\\",accounts:%s,timeout:200){results}}}\",\"variables\":\"\"}";
    private final CloseableHttpClient httpClient;

    public NewRelicClient(String accountId, String apiKey, String appName, String numDays, String untilDate) {
        this(accountId, apiKey, API_URL, appName, numDays, untilDate, HttpClientBuilder.create().build());
    }

    @VisibleForTesting
    NewRelicClient(String accountId,
            String apiKey,
            String nrUrl,
            String appName,
            String numDays,
            String untilDate,
            CloseableHttpClient httpClient) {
        this.accountId = accountId;
        this.apiKey = apiKey;
        this.nrUrl = nrUrl;
        this.appName = appName;
        this.numDays = numDays;
        this.untilDate = untilDate;
        this.httpClient = httpClient;
    }

    /**
     * Performs an HTTP query to the configured New Relic NerdGraph API service endpoint, retrieving
     * query results in JSON format and transforming them to a map of metric to counts.
     * @return a Map whose keys are metric names and whose values are query result counts.
     */
    public Map<String, Long> query(ProgressIndicator indicator) {

        List<String> traceQueries = new ArrayList<>();
        if (StringUtils.isBlank(untilDate)) {
            traceQueries.add(String.format(defaultTraceQueryTemplate, appName, numDays, accountId));
        } else {
            String endDate = untilDate;
            for (int i = 0; i < Integer.parseInt(numDays); i++) {
                String prevDate = LocalDate.parse(endDate).minusDays(1).toString();
                traceQueries.add(String.format(dateRangeTraceQueryTemplate, appName, prevDate, endDate, accountId));
                endDate = prevDate;
            }
        }

        Map<String, Long> countsByName = new LinkedHashMap<>(120);
        for (String traceQuery : traceQueries) {
            String whereClause = traceQuery.substring(traceQuery.indexOf("appName"), traceQuery.indexOf("FACET"));
            if (indicator != null) {
                indicator.setText("Running New Relic query: " + whereClause);
            }
            //System.out.println("traceQuery: " + whereClause);

            queryAndCount(countsByName, traceQuery);
        }

        return countsByName;
    }

    private void queryAndCount(Map<String, Long> countsByName, String traceQuery) {

        HttpPost httpPost = new HttpPost(URI.create(nrUrl));
        httpPost.addHeader("Content-Type", "application/json");
        httpPost.addHeader("API-Key", apiKey);
        httpPost.setEntity(new StringEntity(traceQuery.replace("'", "\\u0027"), ContentType.APPLICATION_JSON));

        int responseCode = 0;
        int tries = 0;

        while (responseCode != 200 && tries < 2) {
            ++tries;
            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                responseCode = response.getStatusLine().getStatusCode();
                if (responseCode == 200) {
                    // extract response json
                    InputStream bodyStream = response.getEntity().getContent();
                    ObjectMapper om = new ObjectMapper();
                    JsonNode json = om.readTree(bodyStream);
                    // expect: {"data":{"actor":{"nrql":{"results":[{"facet":"...","count":...,"name":"..."}, { etc... }]}}}}
                    JsonNode resultsNode = json.get("data").get("actor").get("nrql").get("results");
                    for (JsonNode element : resultsNode) {
                        String metricName = element.get("name").asText();
                        metricName = metricName.replace("WebTransaction/Custom/", "");
                        Long count = element.get("count").asLong();
                        countsByName.merge(metricName, count, Long::sum);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Returns a partial URL for the New Relic-hosted Insights
     * query page corresponding to the given New Relic account ID.
     * Appending a metric name to the partial URL produces a link to a query
     * result page for that metric.
     * See {@link com.disney.idea.utils.Utils#getNewRelicUrl}
     * @param accountId The New Relic account ID
     * @return a partial URL for the New Relic Insights site in String format containing the provided account ID
     */
    public static String getInsightsUrl(String accountId) {
        return String.format(INSIGHTS_URL, accountId);
    }

}
