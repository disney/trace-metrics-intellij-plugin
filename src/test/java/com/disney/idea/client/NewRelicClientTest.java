package com.disney.idea.client;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Map;

import org.apache.http.HttpVersion;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicStatusLine;
import org.junit.Before;
import org.junit.Test;

public class NewRelicClientTest {

    private final String accountId = "fakeAccountId";
    private final String apiKey = "fakeApiKey";
    private final String nrUrl = "http://fakehost";
    private final String appName = "fakeAppName";
    private final String numDays = "1";
    private final String untilDate = "";

    private CloseableHttpClient httpClient;
    private NewRelicClient newRelicClient;

    @Before
    public void setup() {
        httpClient = mock(CloseableHttpClient.class);
        newRelicClient = new NewRelicClient(accountId, apiKey, nrUrl, appName, numDays, untilDate, httpClient);
    }

    @Test
    public void query_retryOnceAfterException() throws Exception {
        when(httpClient.execute(any(HttpPost.class))).thenThrow(new IOException("Failed"));

        Map<String, Long> result = newRelicClient.query(null);

        assert result.isEmpty();
        verify(httpClient, times(2)).execute(any(HttpPost.class));
    }

    @Test
    public void query_retryOnceAfterNon200Response() throws Exception {
        CloseableHttpResponse response = mock(CloseableHttpResponse.class);

        when(httpClient.execute(any(HttpPost.class))).thenReturn(response);
        when(response.getStatusLine()).thenReturn(new BasicStatusLine(HttpVersion.HTTP_1_1, 500, null));

        Map<String, Long> result = newRelicClient.query(null);

        assert result.isEmpty();
        verify(httpClient, times(2)).execute(any(HttpPost.class));
    }

    @Test
    public void query_noRetriesAfterSuccess() throws Exception {
        CloseableHttpResponse response = mock(CloseableHttpResponse.class);
        // {"data":{"actor":{"nrql":{"results":[{"facet":"...","count":...,"name":"..."}, { etc... }]}}}}
        StringEntity entity = new StringEntity("{\"data\":{\"actor\":{\"nrql\":{\"results\":[{\"facet\":\"this\",\"count\":5,\"name\":\"this\"}]}}}}");

        when(httpClient.execute(any(HttpPost.class))).thenReturn(response);
        when(response.getStatusLine()).thenReturn(new BasicStatusLine(HttpVersion.HTTP_1_1, 200, null));
        when(response.getEntity()).thenReturn(entity);

        Map<String, Long> result = newRelicClient.query(null);

        assert result.get("this") == 5;
        verify(httpClient, times(1)).execute(any(HttpPost.class));

    }
}
