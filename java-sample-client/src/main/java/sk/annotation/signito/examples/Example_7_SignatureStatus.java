package sk.annotation.signito.examples;

import sk.annotation.projects.signito.client.SignitoClient;
import sk.annotation.projects.signito.utils.SigningDocumentStatusDTO;
import sk.annotation.projects.signito.utils.SigningSignerStatusDTO;
import sk.annotation.signito.examples.utils.ExampleUtils;

import java.util.Map;

/**
 * check signing status for a document
 */
public class Example_7_SignatureStatus {

    public static void main(String[] args) {
        new Example_7_SignatureStatus();
    }

    public Example_7_SignatureStatus() {
        SignitoClient signitoClient = ExampleUtils.createSignitoClient();

        String docGroupId = "1234";
        Map<String, SigningDocumentStatusDTO> documentGroupStatus = signitoClient.getSignatureStatus(docGroupId);

        for (Map.Entry<String, SigningDocumentStatusDTO> entry : documentGroupStatus.entrySet()) {
            System.out.println("documentItemId = " + entry.getKey());
            SigningDocumentStatusDTO docItemStatus = entry.getValue();
            System.out.println(" - signed = " + docItemStatus.getDocumentSigned());
            for (Map.Entry<String, SigningSignerStatusDTO> sign : docItemStatus.getSignersStatus().entrySet()) {
                SigningSignerStatusDTO status = sign.getValue();
                System.out.println("-- signerId = " + sign.getKey());
                System.out.println("---- total signature count " + status.getTotal());
                System.out.println("---- signed signatures count " + status.getDone());
                System.out.println("---- all signatures signed " + status.getSignerSigned());
                System.out.println();
            }

        }


    }
}
