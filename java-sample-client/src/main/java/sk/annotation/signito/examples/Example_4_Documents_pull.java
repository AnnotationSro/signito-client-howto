package sk.annotation.signito.examples;

import sk.annotation.projects.signito.client.SignitoClient;
import sk.annotation.projects.signito.data.dto.api.MetaPaginator;
import sk.annotation.projects.signito.data.dto.api.RequestFilterDTO;
import sk.annotation.projects.signito.data.dto.api.ResponseRowsDTO;
import sk.annotation.projects.signito.data.dto.documents.group.*;
import sk.annotation.projects.signito.data.enums.DocumentStatusEnum;
import sk.annotation.signito.examples.utils.ExampleUtils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
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
        LocalDateTime lastChecked = LocalDateTime.of(2024, 5, 1, 12, 0);
        //--------------------


        DocumentGroupFilterDTO filterDTO = new DocumentGroupFilterDTO();
        filterDTO.setStatus(List.of(DocumentStatusEnum.DONE, DocumentStatusEnum.FAILED_TIMEOUT));
        filterDTO.setLastModifiedFrom(ZonedDateTime.from(lastChecked.atZone(ZoneId.systemDefault())));

        ResponseRowsDTO<DocumentGroupFilterDTO, DocumentGroupListDTO> result = signitoClient.searchDocumentGroups(filterDTO);

        if (result.getRows().size() > 0){
            System.out.println("There are some new signed documents: " + result.getRows().stream().map(DocumentGroupListDTO::getDocGroupId).collect(Collectors.joining(", ")));
        } else {
            System.out.println("No new new signed documents");
        }
    }
}
