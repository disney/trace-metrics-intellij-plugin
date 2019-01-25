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
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicStatusLine;
import org.junit.Before;
import org.junit.Test;

public class NewRelicClientTest {

    private final String apiKey = "fakeApiKey";
    private final String nrUrl = "http://fakehost";
    private final String appName = "fakeAppName";
    private final int numDays = 1;

    private CloseableHttpClient httpClient;
    private NewRelicClient newRelicClient;

    @Before
    public void setup() {
        httpClient = mock(CloseableHttpClient.class);
        newRelicClient = new NewRelicClient(apiKey, nrUrl, appName, numDays, httpClient);
    }

    @Test
    public void query_retryOnceAfterException() throws Exception {
        when(httpClient.execute(any(HttpGet.class))).thenThrow(new IOException("Failed"));

        Map<String, Long> result = newRelicClient.query();

        assert result.isEmpty();
        verify(httpClient, times(2)).execute(any(HttpGet.class));
    }

    @Test
    public void query_retryOnceAfterNon200Response() throws Exception {
        CloseableHttpResponse response = mock(CloseableHttpResponse.class);

        when(httpClient.execute(any(HttpGet.class))).thenReturn(response);
        when(response.getStatusLine()).thenReturn(new BasicStatusLine(HttpVersion.HTTP_1_1, 500, null));

        Map<String, Long> result = newRelicClient.query();

        assert result.isEmpty();
        verify(httpClient, times(2)).execute(any(HttpGet.class));
    }

    @Test
    public void query_noRetriesAfterSuccess() throws Exception {
        CloseableHttpResponse response = mock(CloseableHttpResponse.class);
        StringEntity entity = new StringEntity("{ \"facets\" : [ { \"name\": \"this\", \"results\": [ {\"count\": 5 } ] } ] }");

        when(httpClient.execute(any(HttpGet.class))).thenReturn(response);
        when(response.getStatusLine()).thenReturn(new BasicStatusLine(HttpVersion.HTTP_1_1, 200, null));
        when(response.getEntity()).thenReturn(entity);

        Map<String, Long> result = newRelicClient.query();

        assert result.get("this") == 5;
        verify(httpClient, times(1)).execute(any(HttpGet.class));

    }
}
