package sk.annotation.signito.examples;

import sk.annotation.projects.signito.client.SignitoClient;
import sk.annotation.projects.signito.data.dto.documents.group.DocumentGroupFilterDTO;
import sk.annotation.projects.signito.data.enums.DocumentStatusEnum;
import sk.annotation.projects.signito.utils.HttpCallbacks;
import sk.annotation.projects.signito.web.HttpRequest;
import sk.annotation.projects.signito.web.HttpResponse;
import sk.annotation.signito.examples.utils.ExampleUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
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
            public void beforeRequest(HttpRequest req) {
                System.out.println("before request " + req.getRequestMethod() + ": "+ req.getUrl() + " at " + req.getTimestamp() + ", body: " + req.getPostBody());
            }

            @Override
            public void afterResponse(HttpResponse<?> httpResponse, HttpRequest resp) {
                System.out.println("after response " + httpResponse.getResponseCode() + " at " + httpResponse.getTimestamp() + " " + httpResponse.getDataRaw());
            }

            @Override
            public void afterFileUploadResponse(HttpResponse<?> httpResponse, HttpRequest httpRequest) {
                //do something
            }

            @Override
            public void onConnectionError(HttpRequest httpRequest, IOException e, Instant instant) {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                e.printStackTrace(pw);

                System.out.println("Connection error: " + sw);
            }
        };
        SignitoClient signitoClient = ExampleUtils.createSignitoClient(httpCallbacks);

        // do some arbitrary call
        DocumentGroupFilterDTO filter = new DocumentGroupFilterDTO();
        filter.setStatus(List.of(DocumentStatusEnum.DONE));
        LocalDateTime lastChecked = LocalDateTime.of(2023, 1, 1, 12, 0);

        filter.setLastModifiedFrom(lastChecked.atZone(ZoneId.systemDefault()));
        signitoClient.searchDocumentGroups(filter);
    }
}
