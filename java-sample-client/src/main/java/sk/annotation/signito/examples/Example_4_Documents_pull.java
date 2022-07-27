package sk.annotation.signito.examples;

import sk.annotation.projects.signito.client.SignitoClient;
import sk.annotation.projects.signito.common.enums.DocumentStatusEnum;
import sk.annotation.projects.signito.data.dto.documents.group.*;
import sk.annotation.signito.examples.utils.ExampleUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * programmatically checking signature state
 */
public class Example_4_Documents_pull {
    public static void main(String[] args) {
        new Example_4_Documents_pull();
    }

    public Example_4_Documents_pull() {
        SignitoClient signitoClient = ExampleUtils.createSignitoClient();

        checkDocumentStatus(signitoClient);
    }

    private void checkDocumentStatus(SignitoClient signitoClient) {

        //--------------------
        LocalDateTime lastChecked = LocalDateTime.of(2022, 1, 1, 12, 0);
        //--------------------


        DocumentGroupFilterDTO filter = new DocumentGroupFilterDTO();
        filter.setStatus(List.of(DocumentStatusEnum.DONE));
        filter.setLastModifiedFrom(lastChecked);

        List<DocumentGroupListDTO> result = signitoClient.findDocumentGroups(filter);

        if (result.size() > 0){
            System.out.println("There are some new signed documents: " + result.stream().map(DocumentGroupListDTO::getDocGroupId).collect(Collectors.joining()));
        } else {
            System.out.println("No new new signed documents");
        }
    }
}
