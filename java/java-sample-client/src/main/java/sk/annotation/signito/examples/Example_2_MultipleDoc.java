package sk.annotation.signito.examples;

import sk.annotation.projects.signito.client.SignitoClient;
import sk.annotation.projects.signito.client.SignitoDocumentRequestBuilder;
import sk.annotation.projects.signito.client.ids.SignerGroupId;
import sk.annotation.projects.signito.client.ids.SignerId;
import sk.annotation.projects.signito.client.ids.UploadedDocumentId;
import sk.annotation.projects.signito.data.dto.documents.group.DocumentGroupDetailDTO;
import sk.annotation.projects.signito.data.dto.signing.SignFieldConfigDTO;
import sk.annotation.signito.examples.utils.ExampleUtils;

import java.util.Date;

/**
 * 1. document group with 3 document items
 * 2. 2 signers - one for each document and one document has no signers
 * 3. every signer can see only his document to sign + first signer can see document without any signature as well
 */
public class Example_2_MultipleDoc {
    public static void main(String[] args) {
        new Example_2_MultipleDoc();
    }

    public Example_2_MultipleDoc() {
        SignitoClient signitoClient = ExampleUtils.createSignitoClient();
        createDocument(signitoClient);
    }

    private void createDocument(SignitoClient signitoClient) {
        SignitoDocumentRequestBuilder groupBuilder = signitoClient.documentGroupBuilderNew("Documents to sign " + new Date());

        //create signers
        SignerId[] signerIds = ExampleUtils.addSigners(groupBuilder, 2);
        SignerId signer1 = signerIds[0];
        SignerId signer2 = signerIds[1];

        //upload and configure documentItem
        UploadedDocumentId doc1 = ExampleUtils.addDocumentWith1signature1text(groupBuilder, "signature_1", "custom_text_1");
        UploadedDocumentId doc2 = ExampleUtils.addDocumentWith1signature(groupBuilder, "signature_2");
        UploadedDocumentId doc3 = ExampleUtils.addDocumentWithNosignature(groupBuilder, signer1.getSignerId());

        //configure signatures
        SignerGroupId signersGroup = groupBuilder.addSignerGroup(null, null);
        groupBuilder.addSignerRule(signersGroup, doc1, signer1, SignFieldConfigDTO.createSignature("signature_1", true));
        groupBuilder.addSignerRule(signersGroup, doc2, signer2, SignFieldConfigDTO.createSignature("signature_2", true));

        //create document draft
        DocumentGroupDetailDTO detailDTO = groupBuilder.send();

        //finalize document
        groupBuilder.finalizeOrReturnErrors();


//    signitoClient.sendSignLinkKeyOnSms(r.getDocGroupId(), signer1.getSignerId())  <- use this to retrieve SMS url
//    signitoClient.sendSignLinkKeyOnEmail(r.getDocGroupId(), signer1.getSignerId()) <- use this to retrieve email url

        System.out.println("signer1: " + signitoClient.getSignUrlOnWindow(detailDTO.getDocGroupId(), signer1.getSignerId()));
        System.out.println("signer2: " + signitoClient.getSignUrlOnWindow(detailDTO.getDocGroupId(), signer2.getSignerId()));
    }
}
