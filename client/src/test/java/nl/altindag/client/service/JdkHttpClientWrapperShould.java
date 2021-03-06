package nl.altindag.client.service;

import static nl.altindag.client.ClientType.JDK_HTTP_CLIENT;
import static nl.altindag.client.TestConstants.GET_METHOD;
import static nl.altindag.client.TestConstants.HEADER_KEY_CLIENT_TYPE;
import static nl.altindag.client.TestConstants.HTTP_URL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collections;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import nl.altindag.client.model.ClientResponse;

@RunWith(MockitoJUnitRunner.class)
public class JdkHttpClientWrapperShould {

    @InjectMocks
    private JdkHttpClientWrapper victim;
    @Mock
    private HttpClient httpClient;

    @Test
    @SuppressWarnings("unchecked")
    public void executeRequest() throws Exception {
        HttpResponse httpResponse = mock(HttpResponse.class);
        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(httpResponse);
        when(httpResponse.statusCode()).thenReturn(200);
        when(httpResponse.body()).thenReturn("Hello");

        ArgumentCaptor<HttpRequest> httpRequestArgumentCaptor = ArgumentCaptor.forClass(HttpRequest.class);
        ArgumentCaptor<HttpResponse.BodyHandler<String>> bodyHandlerArgumentCaptor = ArgumentCaptor.forClass(HttpResponse.BodyHandler.class);

        ClientResponse clientResponse = victim.executeRequest(HTTP_URL);

        assertThat(clientResponse.getStatusCode()).isEqualTo(200);
        assertThat(clientResponse.getResponseBody()).isEqualTo("Hello");

        verify(httpClient, times(1)).send(httpRequestArgumentCaptor.capture(), bodyHandlerArgumentCaptor.capture());
        assertThat(httpRequestArgumentCaptor.getValue().uri().toString()).isEqualTo(HTTP_URL);
        assertThat(httpRequestArgumentCaptor.getValue().method()).isEqualTo(GET_METHOD);
        assertThat(httpRequestArgumentCaptor.getValue().headers().map()).containsExactly(Assertions.entry(HEADER_KEY_CLIENT_TYPE, Collections.singletonList(JDK_HTTP_CLIENT.getValue())));
        assertThat(bodyHandlerArgumentCaptor.getValue()).isEqualTo(HttpResponse.BodyHandlers.ofString());
    }

}
