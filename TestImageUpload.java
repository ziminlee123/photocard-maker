import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import java.io.FileInputStream;
import java.io.IOException;

public class TestImageUpload {
    public static void main(String[] args) {
        // Azure Storage 연결 문자열 (실제 값으로 교체 필요)
        String connectionString = "your_azure_storage_connection_string";
        String containerName = "photocards";
        String fileId = "test-123";
        String imagePath = "test-image.jpg"; // 테스트용 이미지 파일 경로
        
        try {
            // Azure Storage 클라이언트 생성
            BlobServiceClient blobServiceClient = new BlobServiceClientBuilder()
                .connectionString(connectionString)
                .buildClient();
            
            BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(containerName);
            
            // 테스트 이미지 업로드
            String fileName = "photocard_" + fileId + ".jpg";
            BlobClient blobClient = containerClient.getBlobClient(fileName);
            
            try (FileInputStream fileInputStream = new FileInputStream(imagePath)) {
                blobClient.upload(fileInputStream, true);
                System.out.println("테스트 이미지 업로드 완료: " + fileName);
            }
            
            // 다운로드 URL 생성
            String downloadUrl = "https://guidely-phtotcardmaker-g9hqaacaadcwdhfn.koreacentral-01.azurewebsites.net/api/photocards/" + fileId + "/download";
            System.out.println("다운로드 URL: " + downloadUrl);
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
