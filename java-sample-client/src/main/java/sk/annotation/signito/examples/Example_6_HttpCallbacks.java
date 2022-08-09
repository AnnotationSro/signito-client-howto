package sk.annotation.signito.examples;

import sk.annotation.projects.signito.client.SignitoClient;
import sk.annotation.projects.signito.data.dto.documents.group.DocumentGroupFilterDTO;
import sk.annotation.projects.signito.data.enums.DocumentStatusEnum;
import sk.annotation.projects.signito.utils.HttpCallbacks;
import sk.annotation.projects.signito.web.HttpRequest;
import sk.annotation.projects.signito.web.HttpResponse;
import sk.annotation.signito.examples.utils.ExampleUtils;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;

/**
 * download signed documents together with signature protocol
 */
public class Example_6_HttpCallbacks {

    public static void main(String[] args) {
        new Example_6_HttpCallbacks();
    }

    public Example_6_HttpCallbacks() {
        HttpCallbacks httpCallbacks = new HttpCallbacks() {
            @Override
            public void beforeRequest(HttpRequest httpRequest) {
                System.out.println("before request " + httpRequest.getUrl() + " at " + httpRequest.getTimestamp().toString());
            }

            @Override
            public void afterResponse(HttpResponse<?> httpResponse, HttpRequest httpRequest) {
                System.out.println("after response " + httpResponse.getResponseCode() + " at " + httpResponse.getTimestamp().toString());
            }

            @Override
            public void afterFileUploadResponse(HttpResponse<?> httpResponse, HttpRequest httpRequest) {

            }

            @Override
            public void onConnectionError(HttpRequest httpRequest, IOException e, Instant instant) {
                System.out.println("Connection error");
            }
        };
        SignitoClient signitoClient = ExampleUtils.createSignitoClient(httpCallbacks);

        // do some arbitrary call
        DocumentGroupFilterDTO filter = new DocumentGroupFilterDTO();
        filter.setStatus(List.of(DocumentStatusEnum.DONE));
        filter.setLastModifiedFrom(ZonedDateTime.from(LocalDateTime.now()));
        signitoClient.searchDocumentGroups(filter);
    }
}
