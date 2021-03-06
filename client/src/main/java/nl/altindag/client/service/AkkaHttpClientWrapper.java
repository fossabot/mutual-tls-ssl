package nl.altindag.client.service;

import static nl.altindag.client.ClientType.AKKA_HTTP_CLIENT;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import akka.actor.ActorSystem;
import akka.http.javadsl.Http;
import akka.http.javadsl.model.HttpHeader;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import akka.stream.javadsl.Sink;
import akka.util.ByteString;
import nl.altindag.client.ClientType;
import nl.altindag.client.Constants;
import nl.altindag.client.model.ClientResponse;

@Service
public class AkkaHttpClientWrapper extends RequestService {

    private final Http akkaHttpClient;

    @Autowired
    public AkkaHttpClientWrapper(Http akkaHttpClient) {
        this.akkaHttpClient = akkaHttpClient;
    }

    @Override
    public ClientResponse executeRequest(String url) {
        return akkaHttpClient.singleRequest(HttpRequest.create(url).addHeader(HttpHeader.parse(Constants.HEADER_KEY_CLIENT_TYPE, getClientType().getValue())))
                             .thenApply(httpResponse -> new ClientResponse(extractBody(httpResponse), httpResponse.status().intValue()))
                             .toCompletableFuture()
                             .join();
    }

    private String extractBody(HttpResponse httpResponse) {
        return httpResponse.entity()
                           .getDataBytes()
                           .fold(ByteString.empty(), ByteString::concat)
                           .map(ByteString::utf8String)
                           .runWith(Sink.head(), ActorSystem.create())
                           .toCompletableFuture()
                           .join();
    }

    @Override
    public ClientType getClientType() {
        return AKKA_HTTP_CLIENT;
    }

}
