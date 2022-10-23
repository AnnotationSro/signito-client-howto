package sk.annotation.signito.examples;

import sk.annotation.projects.signito.client.SignitoClient;
import sk.annotation.projects.signito.client.SignitoDocumentRequestBuilder;
import sk.annotation.projects.signito.client.ids.SignerGroupId;
import sk.annotation.projects.signito.client.ids.SignerId;
import sk.annotation.projects.signito.client.ids.UploadedDocumentId;
import sk.annotation.projects.signito.data.dto.documents.group.DocumentGroupDetailDTO;
import sk.annotation.projects.signito.data.dto.documents.group.DocumentNotifyDestinationsDTO;
import sk.annotation.projects.signito.data.dto.documents.group.NotifyCustomLinkDTO;
import sk.annotation.projects.signito.data.dto.signing.SignFieldConfigDTO;
import sk.annotation.projects.signito.data.enums.FieldRequiredValueType;
import sk.annotation.projects.signito.data.enums.MethodTypesEnum;
import sk.annotation.projects.signito.data.enums.SignitoOnEvents;
import sk.annotation.signito.examples.utils.ExampleUtils;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * very basic setup
 * 1. document group with only one document item
 * 2. one signature with one text field
 * <p>
 * setup notifications to be sent when document group is signed - by webhooks
 */
public class Example_4_Notifications_webhook {
    public static void main(String[] args) {
        new Example_4_Notifications_webhook();
    }

    public Example_4_Notifications_webhook() {
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
                                "'Podpisane' yyyy.MM.dd 'o' HH:mm:ss").withFieldLabel("Ja som datum")
                );

        // ------------- setup notification web-hook------------
        DocumentNotifyDestinationsDTO notif = groupBuilder.getNotificationConfigSigned();
        notif.setCreatorByEmail(true);//notify document group creator
        notif.setCustomLink(List.of(
                //performs HTTP GET request to http://127.0.0.1:8192/
                NotifyCustomLinkDTO.createGet("http://127.0.0.1:8192/"),

                // performs HTTP POST to http://127.0.0.1:8193/something
                // send nofitications on partial updates (on every signature) and when all signatures are done as well
                // adds custom HTTP header
                NotifyCustomLinkDTO.create(
                        "http://127.0.0.1:8193/something?",
                        MethodTypesEnum.POST,
                        Set.of(SignitoOnEvents.PARTIAL_UPDATES, SignitoOnEvents.FINAL_UPDATE),
                        Map.of("CUSTOM_HEADER", List.of("CUSTOM_VALUE1"))
                )
        ));
        // ------------- END - setup notification web-hook------------

        //create document draft
        DocumentGroupDetailDTO detailDTO = groupBuilder.send();

        //finalize document
        groupBuilder.finalizeOrReturnErrors();


//    signitoClient.sendSignLinkKeyOnSms(r.getDocGroupId(), signer1.getSignerId())  <- use this to retrieve SMS url
//    signitoClient.sendSignLinkKeyOnEmail(r.getDocGroupId(), signer1.getSignerId()) <- use this to retrieve email url

        System.out.println("signer1: " + signitoClient.getSignUrlOnWindow(detailDTO.getDocGroupId(), signer1.getSignerId()));
    }

}
