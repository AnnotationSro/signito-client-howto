package sk.annotation.signito.examples;

import sk.annotation.projects.signito.client.SignitoClient;
import sk.annotation.projects.signito.data.dto.documents.group.DocumentGroupDetailDTO;
import sk.annotation.projects.signito.data.enums.DocumentItemTypeEnum;
import sk.annotation.projects.signito.data.enums.DocumentStatusEnum;
import sk.annotation.projects.signito.utils.HttpCallbacks;
import sk.annotation.projects.signito.web.HttpRequest;
import sk.annotation.projects.signito.web.HttpResponse;
import sk.annotation.signito.examples.utils.ExampleUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Map;
import java.util.Objects;

/**
 * download signed documents together with signature protocol
 */
public class Example_5_Download {

    public static void main(String[] args) {
        new Example_5_Download();
    }

    public Example_5_Download(){
        //----------
        String docGroupId = "123";
        //---------


        SignitoClient signitoClient = ExampleUtils.createSignitoClient();

        //request documentGroup detail
        DocumentGroupDetailDTO detailDTO = signitoClient.documentGroupDetail(docGroupId);

        //if document is signed, download all signed documents
        if (Objects.equals(detailDTO.getStatus(), DocumentStatusEnum.DONE)){
            detailDTO.getDocumentItems().forEach(doc -> {
                String filename = getFilenameFromPath(doc.getFile().getPath());

                if (DocumentItemTypeEnum.DOC.equals(doc.getType())){
                    signitoClient.downloadFileToPath(doc.getFile(), Path.of("/tmp/"+filename));
                }

                if (DocumentItemTypeEnum.PROTOCOL.equals(doc.getType())){
                    signitoClient.downloadFileToPath(doc.getFile(), Path.of("/tmp/protocol.pdf"));
                }
            });
        }
    }

    private String getFilenameFromPath(String path) {
        return path.substring(path.lastIndexOf("/") + 1);
    }
}
