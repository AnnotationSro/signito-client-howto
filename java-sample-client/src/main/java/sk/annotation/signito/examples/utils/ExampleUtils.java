package sk.annotation.signito.examples.utils;

import sk.annotation.projects.signito.client.SignitoClient;
import sk.annotation.projects.signito.client.SignitoDocumentRequestBuilder;
import sk.annotation.projects.signito.client.SignitoSignatureTemplateBuilder;
import sk.annotation.projects.signito.client.ids.SignerId;
import sk.annotation.projects.signito.client.ids.UploadedDocumentId;
import sk.annotation.projects.signito.data.dto.documents.DocumentFieldConfigDTO;
import sk.annotation.projects.signito.data.dto.signing.SignerDTOBuilder;
import sk.annotation.projects.signito.data.enums.DocumentAttachmentConfigEnum;
import sk.annotation.projects.signito.data.enums.DocumentFieldTypeEnum;
import sk.annotation.projects.signito.utils.HttpCallbacks;

import java.io.*;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Set;

public class ExampleUtils {

    public static SignitoClient createSignitoClient(HttpCallbacks httpCallbacks) {
        String url = "http://localhost:4208";
        String user = "test@signito.sk";
        String pass = "test";
        if (httpCallbacks == null){
            return new SignitoClient(url, user, pass);
        }
        return new SignitoClient(url, user, pass, httpCallbacks);
    }
    public static SignitoClient createSignitoClient() {
        return createSignitoClient(null);
    }

    public static SignerId[] addSigners(SignitoDocumentRequestBuilder groupBuilder, int signersCount) {
        SignerId signer1 = null;
        SignerId signer2 = null;
        SignerId signerTechnical = null;

        signer1 = groupBuilder.registerSigner("sign1",
                new SignerDTOBuilder("Jurko Mlady", "jurko.mlady@email.sk", "+421901234567")
                        .withNotifyByEmail(false)
                        .withNotifyBySms(false)
                        .withSendFinalEmail(false)
                        .build()
        );

        if (signersCount>1) {
            signer2 = groupBuilder.registerSigner("sign2",
                    new SignerDTOBuilder("Janko Stary", "janko.stary@email.sk", "+421901234568")
                            .withNotifyByEmail(false)
                            .withNotifyBySms(false)
                            .withSendFinalEmail(false)
                            .build()
            );
        }
        if (signersCount>2) {
            signerTechnical = groupBuilder.registerSigner("signTechnical",
                    new SignerDTOBuilder("Technical signer", "technical@email.sk", null)
                            .withNotifyByEmail(false)
                            .withNotifyBySms(false)
                            .withSendFinalEmail(false)
                            .build()
            );
        }

        return new SignerId[]{signer1, signer2, signerTechnical};
    }

    public static UploadedDocumentId addDocumentWith3signatures(SignitoDocumentRequestBuilder groupBuilder, String signatureCode1, String signatureCode2, String signatureCode3) {
        UploadedDocumentId d1 = groupBuilder.uploadDocumentAsInputStream("filename1.pdf", createDummyInputStream(1), "This is document 1",
                Map.of(signatureCode1,
                        DocumentFieldConfigDTO.create(DocumentFieldTypeEnum.SIGNATURE, 59.192, 573.07501, 156, 40, 1, true),
                        signatureCode2,
                        DocumentFieldConfigDTO.create(DocumentFieldTypeEnum.SIGNATURE, 219.822, 573.07501, 156, 40, 1, true),
                        signatureCode3,
                        DocumentFieldConfigDTO.create(DocumentFieldTypeEnum.SIGNATURE, 380.492, 573.07501, 156, 40, 1, true))
                ,(config) -> {
                    config.setStrictVisibility(true); //visible only for signers
                    config.setFinalEmailConfig(DocumentAttachmentConfigEnum.IGNORED); //no notifications sent to signers upon docGroup is completely signed
                });
        return d1;
    }

    private static InputStream createDummyInputStream(int index) {
        return ExampleUtils.class.getResourceAsStream("/pdfs/doc" + index + ".pdf");
    }

    public static UploadedDocumentId addDocumentWith1signature1text(SignitoDocumentRequestBuilder groupBuilder, String signatureCode, String textFieldCode) {


        UploadedDocumentId d2 = groupBuilder.uploadDocumentAsInputStream("filename2.pdf", createDummyInputStream(2), "This is document 2",
                Map.of(
                        signatureCode,
                        DocumentFieldConfigDTO.create(DocumentFieldTypeEnum.SIGNATURE, 317.63702, 642.282, 140.0, 35.0, 1, true),
//                        DocumentFieldConfigDTO.createRelativeToText(DocumentFieldTypeEnum.SIGNATURE, "text_to_replace", 0,0,100,100,1, true),
                        textFieldCode,
                        DocumentFieldConfigDTO.create(DocumentFieldTypeEnum.TEXT, 100.0, 642.282, 200.0, 35.0, 1, true)
                                .withDefaultText("This is default text"))
                , (config) -> {
            config.setStrictVisibility(true); //visible only for signers
            config.setFinalEmailConfig(DocumentAttachmentConfigEnum.IGNORED); //no notifications sent to signers upon docGroup is completely signed
        });
        return d2;
    }

    //DocumentFieldConfigDTO.createRelativeToText(DocumentFieldTypeEnum.SIGNATURE, "ahoj", 0,0,100,100,1, true),

    public static UploadedDocumentId addDocumentWith1signature(SignitoDocumentRequestBuilder groupBuilder, String signatureCode) {
        UploadedDocumentId d2 = groupBuilder.uploadDocumentAsInputStream("filename4.pdf", createDummyInputStream(2), "This is document 4",
                Map.of(signatureCode,
                        DocumentFieldConfigDTO.create(DocumentFieldTypeEnum.SIGNATURE, 317.63702, 642.282, 140.0, 35.0, 1, true))
                , (config) -> {
                    config.setStrictVisibility(true); //visible only for signers
                    config.setFinalEmailConfig(DocumentAttachmentConfigEnum.IGNORED); //no notifications sent to signers upon docGroup is completely signed
                });
        return d2;
    }

    /**
     * document without signture or text field
     * visible to only one signer
     */
    public static UploadedDocumentId addDocumentWithNosignature(SignitoDocumentRequestBuilder groupBuilder, String signerCode) {
        UploadedDocumentId d3 = groupBuilder.uploadDocumentAsInputStream("filename3.pdf", createDummyInputStream(3), "This is document 3", null, (config) -> {
            config.setStrictVisibility(true); //visible only for signers
            config.setFinalEmailConfig(DocumentAttachmentConfigEnum.IGNORED); //no notifications sent to signers upon docGroup is completely signed
            config.setAllowedVisibilityForSigners(Set.of(signerCode)); //since there are no signatures in this doc, we need to specify doc visibility explicitly
        });
        return d3;
    }

    public static String resolveFascimilePath(SignitoClient signitoClient) {
        try {
            File faksFile = new File(ExampleUtils.class.getResource("/signatures/fascimile.png").toURI());
            InputStream is = new FileInputStream(faksFile);

            //create unique key - it chan be file hash, UUID or something like this:
            String key = "fascimile_" + faksFile.getAbsolutePath().replaceAll("[^a-zA-Z0-9_-]+", "");
            try {
                int v = is.available();
                if (v > 0) key += v;
            } catch (IOException e) {
                // ignore
            }

            // if fascimile under this key does not exist - create it
            SignitoSignatureTemplateBuilder signitoSignatureTemplateBuilder = signitoClient.signatureTemplateBuilderByUserKey(key);
            if (!signitoSignatureTemplateBuilder.exists()) {
                signitoSignatureTemplateBuilder.withFascimile(faksFile.getName(), is).withKey(key).withName("Fascimile " + faksFile.getName()).send();
            }

            return key;
        } catch (URISyntaxException | FileNotFoundException e) {
            //unable to read PNG image
            throw new RuntimeException(e);
        }
    }

}
