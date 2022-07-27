package sk.annotation.signito.examples;

import sk.annotation.projects.signito.client.SignitoClient;
import sk.annotation.projects.signito.common.enums.DocumentItemTypeEnum;
import sk.annotation.projects.signito.common.enums.DocumentStatusEnum;
import sk.annotation.projects.signito.data.dto.documents.group.DocumentGroupDetailDTO;
import sk.annotation.projects.signito.data.dto.documents.group.DocumentGroupFilterDTO;
import sk.annotation.projects.signito.data.dto.documents.group.DocumentGroupListDTO;
import sk.annotation.projects.signito.utils.HttpCallbacks;
import sk.annotation.projects.signito.web.HttpRequest;
import sk.annotation.projects.signito.web.HttpResponse;
import sk.annotation.signito.examples.utils.ExampleUtils;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * download signed documents together with signature protocol
 */
public class Example_6_HttpCallbacks {

    public static void main(String[] args) {
        new Example_6_HttpCallbacks();
    }

    public Example_6_HttpCallbacks(){
        SignitoClient signitoClient = ExampleUtils.createSignitoClient();
        signitoClient.setHttpLogger(new HttpCallbacks() {
            @Override
            public void beforeRequest(HttpRequest httpRequest) {
                System.out.println("before request " + httpRequest.getUrl());
            }

            @Override
            public void afterResponse(HttpResponse<?> httpResponse, HttpRequest httpRequest) {
                System.out.println("after response " + httpResponse.getResponseCode());
            }

            @Override
            public void afterFileUploadResponse(HttpResponse<?> httpResponse, HttpRequest httpRequest) {

            }
        });

        // do some arbitrary call
        DocumentGroupFilterDTO filter = new DocumentGroupFilterDTO();
        filter.setStatus(List.of(DocumentStatusEnum.DONE));
        filter.setLastModifiedFrom(LocalDateTime.now());
        signitoClient.findDocumentGroups(filter);
    }
}
