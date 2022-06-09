package sk.annotation.signito.examples;

import sk.annotation.projects.signito.client.SignitoClient;
import sk.annotation.projects.signito.client.SignitoDocumentRequestBuilder;
import sk.annotation.projects.signito.client.ids.SignerGroupId;
import sk.annotation.projects.signito.client.ids.SignerId;
import sk.annotation.projects.signito.client.ids.UploadedDocumentId;
import sk.annotation.projects.signito.common.enums.FieldRequiredValueType;
import sk.annotation.projects.signito.data.dto.documents.group.DocumentGroupDetailDTO;
import sk.annotation.projects.signito.data.dto.signing.SignFieldConfigDTO;
import sk.annotation.projects.signito.data.dto.signing.SigningProcessResultDTO;
import sk.annotation.projects.signito.data.dto.signing.data.SignConfigDataDTO;
import sk.annotation.signito.examples.utils.ExampleUtils;

import java.util.Date;
import java.util.Objects;

/**
 * 1. creates document group with 3 document items
 * 2. multiple signers - each signer can see his documents to sign + first signer can see also one more docItem
 * 3. one technical signer (+ fascimile)
 */
public class Example_3_TechnicalSign {
    public static void main(String[] args) {
        new Example_3_TechnicalSign();
    }

    public Example_3_TechnicalSign() {
        SignitoClient signitoClient = ExampleUtils.createSignitoClient();
        createDocument(signitoClient);
    }

    private void createDocument(SignitoClient signitoClient) {
        SignitoDocumentRequestBuilder groupBuilder = signitoClient.documentGroupBuilderNew("Documents to sign " + new Date());


        SignerId[] signerIds = ExampleUtils.addSigners(groupBuilder, 3);
        SignerId signer1 = signerIds[0];
        SignerId signer2 = signerIds[1];
        SignerId signerTechnical = signerIds[2];


        UploadedDocumentId d1 = ExampleUtils.addDocumentWith3signatures(groupBuilder, "signature_1", "signature_2", "signature_3");
        UploadedDocumentId d2 = ExampleUtils.addDocumentWith1signature1text(groupBuilder, "signature_4", "custom_text_1");
        UploadedDocumentId d3 = ExampleUtils.addDocumentWithNosignature(groupBuilder, signer1.getSignerId());

        // configure signatures
        SignerGroupId g = groupBuilder.addSignerGroup(null, null);

        //configure signature rules for document 1
        groupBuilder.addSignerRule(g, d1, signer1, SignFieldConfigDTO.createSignature("signature_1", true), new SignFieldConfigDTO[0], null);
        groupBuilder.addSignerRule(g, d1, signer2, SignFieldConfigDTO.createSignature("signature_2", true), new SignFieldConfigDTO[0], null);
        groupBuilder.addSignerRule(g, d1, signerTechnical, SignFieldConfigDTO.createSignature("signature_3", true), new SignFieldConfigDTO[0], signRuleDTO -> {
            SignConfigDataDTO signConfigDataDTO = new SignConfigDataDTO();
            signConfigDataDTO.setSignatureTemplateId(ExampleUtils.resolveFascimilePath(signitoClient));

            signRuleDTO.setTechSignatureRequest(signConfigDataDTO);
        });

        //configure signature rules for document 2
        groupBuilder.addSignerRule(g, d2, signer1, SignFieldConfigDTO.createSignature("signature_4", true),
                new SignFieldConfigDTO[]{
                        SignFieldConfigDTO.create(FieldRequiredValueType.SIGNATURE_TIME, "custom_text_1", true, "'Podpisane' yyyy.MM.dd 'o' HH:mm:ss").withFieldLabel("Ja som datum")
                }, null);


    /*DocumentNotifyDestinationsDTO notif = groupBuilder.getNotificationConfigSigned();
    notif.setCreatorByEmail(true);
    notif.setCustomEmails(Map.of("custom-email@daco.sk", "My Custom Destination"));
    notif.setCustomLink(List.of(
      NotifyCustomLinkDTO.createGet("http://127.0.0.1:8192/"),
      NotifyCustomLinkDTO.create(
        "http://127.0.0.1:8193/daco?",
        MethodTypesEnum.POST,
        Set.of(SignitoOnEvents.values()),
        Map.of("CUSTOM_HEADER", List.of("CUSTOM_VALUE1"))
      )
    ));*/

        //create document draft
        DocumentGroupDetailDTO detailDTO = groupBuilder.send();

        //finalize document
        groupBuilder.finalizeOrReturnErrors();

        //sign document as technical signer
        detailDTO.getSignRules().forEach(ruleGroup ->{
            ruleGroup.getRules().stream()
                    .filter(r -> Objects.equals(r.getSignerId(), signerTechnical.getSignerId()))
                    .forEach(rule -> {
                        SigningProcessResultDTO r = signitoClient.signManually(detailDTO.getDocGroupId(), rule, null);
                        if (Boolean.FALSE.equals(r.getSuccess())) {
                            throw new RuntimeException("technical signing error: " + r.getError().getMessage());
                        }
                    });
        });


//    signitoClient.sendSignLinkKeyOnSms(r.getDocGroupId(), signer1.getSignerId())  <- use this to retrieve SMS url
//    signitoClient.sendSignLinkKeyOnEmail(r.getDocGroupId(), signer1.getSignerId()) <- use this to retrieve email url

        System.out.println("signer1: " + signitoClient.getSignUrlOnWindow(detailDTO.getDocGroupId(), signer1.getSignerId()));
        System.out.println("signer2: " + signitoClient.getSignUrlOnWindow(detailDTO.getDocGroupId(), signer2.getSignerId()));
    }


}
