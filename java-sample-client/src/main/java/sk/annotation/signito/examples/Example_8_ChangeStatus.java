package sk.annotation.signito.examples;

import sk.annotation.projects.signito.client.SignitoClient;
import sk.annotation.projects.signito.data.dto.documents.group.DocumentGroupDetailDTO;
import sk.annotation.projects.signito.data.enums.DocumentStatusEnum;
import sk.annotation.projects.signito.utils.SigningDocumentStatusDTO;
import sk.annotation.signito.examples.utils.ExampleUtils;

import java.util.Map;

/**
 * changes document state
 */
public class Example_8_ChangeStatus {

    public static void main(String[] args) {
        new Example_8_ChangeStatus();
    }

    public Example_8_ChangeStatus() {
        SignitoClient signitoClient = ExampleUtils.createSignitoClient();
        String docGroupId = "1234";
        // as an example - make document expired (set document state to FAILED_TIMEOUT)
        signitoClient.changeStatus(docGroupId, DocumentStatusEnum.FAILED_TIMEOUT);

    }
}
