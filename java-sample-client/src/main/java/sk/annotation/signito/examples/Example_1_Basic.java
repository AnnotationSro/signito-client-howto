package sk.annotation.signito.examples;

import sk.annotation.projects.signito.client.SignitoClient;
import sk.annotation.projects.signito.client.SignitoDocumentRequestBuilder;
import sk.annotation.projects.signito.client.ids.SignerGroupId;
import sk.annotation.projects.signito.client.ids.SignerId;
import sk.annotation.projects.signito.client.ids.UploadedDocumentId;
import sk.annotation.projects.signito.data.dto.documents.group.DocumentGroupDetailDTO;
import sk.annotation.projects.signito.data.dto.signing.SignFieldConfigDTO;
import sk.annotation.projects.signito.data.enums.FieldRequiredValueType;
import sk.annotation.signito.examples.utils.ExampleUtils;

import java.util.Date;

/**
 * 1. document group with only one document item
 * 2. one signature with one text field
 */
public class Example_1_Basic {
    public static void main(String[] args) {
        new Example_1_Basic();
    }

    public Example_1_Basic() {
        SignitoClient signitoClient = ExampleUtils.createSignitoClient();
        createDocument(signitoClient);
    }

    private void createDocument(SignitoClient signitoClient) {
        SignitoDocumentRequestBuilder groupBuilder = signitoClient.documentGroupBuilderNew("Documents to sign " + new Date());

        //create signers
        SignerId[] signerIds = ExampleUtils.addSigners(groupBuilder, 1);
        SignerId signer1 = signerIds[0]; //for purposes of this example we only need one signer

        //upload and configure documentItem
        UploadedDocumentId doc = ExampleUtils.addDocumentWith1signature1text(groupBuilder, "signature_1", "custom_text_1");

        //configure signatures
        SignerGroupId signersGroup = groupBuilder.addSignerGroup(null, null);
        groupBuilder.addSignerRule(signersGroup, doc, signer1, SignFieldConfigDTO.createSignature("signature_1", true),
                SignFieldConfigDTO.create(FieldRequiredValueType.SIGNATURE_TIME, "custom_text_1", true,
                        "'Podpisane' yyyy.MM.dd 'o' HH:mm:ss").withFieldLabel("Ja som datum"));


        // this document will become inaccessible to signers, only to user who created this document
        // it will become "private" in 10 days
        groupBuilder.setPrivateAfterDays(10);

        //create document draft
        DocumentGroupDetailDTO detailDTO = groupBuilder.send();

        //finalize document
        groupBuilder.finalizeOrReturnErrors();


//    signitoClient.sendSignLinkKeyOnSms(r.getDocGroupId(), signer1.getSignerId())  <- use this to retrieve SMS url
//    signitoClient.sendSignLinkKeyOnEmail(r.getDocGroupId(), signer1.getSignerId()) <- use this to retrieve email url

        System.out.println("signer1: " + signitoClient.getSignUrlOnWindow(detailDTO.getDocGroupId(), signer1.getSignerId()));
    }
}
