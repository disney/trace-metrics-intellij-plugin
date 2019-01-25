package com.disney.idea.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.annotations.VisibleForTesting;

/**
 * Prepares and performs query to remote New Relic web API server to retrieve
 * trace metrics.
 * Also provides utility methods to construct the API URL for retrieving a
 * metrics data block, and to construct a partial New Relic Insights URL for
 * opening a query view for a named metric in an external browser.
 */
public class NewRelicClient {
    private final String apiKey;
    private final String nrUrl;
    private final String appName;
    private final int numDays;

    private static final String API_URL = "https://insights-api.newrelic.com/v1/accounts/%s";
    private static final String INSIGHTS_URL = "https://insights.newrelic.com/accounts/%s/query?query=";
    private static final String queryGlue = "/query?nrql=";
    private static final String traceQueryTemplate = "SELECT count(*) from Transaction where appName = '%s' and name like 'WebTransaction/Custom/%%' since %s days ago FACET name LIMIT 1000";
    private final CloseableHttpClient httpClient;

    public NewRelicClient(String apiKey, String nrUrl, String appName, int numDays) {
        this(apiKey, nrUrl, appName, numDays, HttpClientBuilder.create().build());
    }

    @VisibleForTesting
    NewRelicClient(String apiKey,
            String nrUrl,
            String appName,
            int numDays,
            CloseableHttpClient httpClient) {
        this.apiKey = apiKey;
        this.nrUrl = nrUrl;
        this.appName = appName;
        this.numDays = numDays;
        this.httpClient = httpClient;
    }

    /**
     * Performs a HTTP query to the configured New Relic API service endpoint, retrieving
     * query results in JSON format and transforming them to a map of metric to counts.
     * @return a Map whose keys are metric names and whose values are query result counts.
     */
    public Map<String, Long> query() {
        Map<String, Long> countsByName = new LinkedHashMap<>(120);
        URI uri;
        try {
            String traceQuery = String.format(traceQueryTemplate, appName, numDays);

            uri = URI.create(nrUrl + queryGlue + URLEncoder.encode(traceQuery, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            throw new RuntimeException("UTF-8 should always be supported");
        }
        HttpGet httpGet = new HttpGet(uri);
        httpGet.addHeader("X-Query-Key", apiKey);

        int responseCode = 0;
        int tries = 0;

        while (responseCode != 200 && tries < 2) {
            ++tries;
            try (CloseableHttpResponse response = httpClient.execute(httpGet);) {
                responseCode = response.getStatusLine().getStatusCode();
                if (responseCode == 200) {
                    // extract response json
                    InputStream bodyStream = response.getEntity().getContent();
                    ObjectMapper om = new ObjectMapper();
                    JsonNode json = om.readTree(bodyStream);
                    // expect { facets: [ { name: ..., count:...}, ...] }
                    JsonNode facetsNode = json.get("facets");
                    for (JsonNode element : facetsNode) {
                        String metricName = element.get("name").asText();
                        metricName = metricName.replace("WebTransaction/Custom/", "");
                        JsonNode resultsNode = element.get("results").get(0);
                        Long count = resultsNode.get("count").asLong();
                        countsByName.put(metricName, count);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return countsByName;
    }

    /**
     * Returns the base URL for the New Relic API endpoint corresponding to
     * the given New Relic account ID. This base URL may be used, alongside
     * configured project settings, to construct a NewRelicClient instance to
     * request trace metrics.
     *
     * @param accountId the New Relic account ID
     * @return a New Relic API base URL in String format
     */
    public static String getApiUrl(String accountId) {
        return String.format(API_URL, accountId);
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
