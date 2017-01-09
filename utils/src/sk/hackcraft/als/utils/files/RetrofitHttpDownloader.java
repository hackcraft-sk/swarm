package sk.hackcraft.als.utils.files;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.http.GET;
import retrofit2.http.Url;

import java.io.*;
import java.nio.file.Path;

public class RetrofitHttpDownloader {

    private final RetrofitFileDownloader retrofitFileDownloader;

    public RetrofitHttpDownloader(OkHttpClient client) {
        Retrofit retrofit = new Retrofit.Builder()
                .client(client)
                .build();

        retrofitFileDownloader = retrofit.create(RetrofitFileDownloader.class);
    }

    public void download(String fileUrl, String checksum, Path destinationPath) throws IOException {
        boolean success = false;

        // three attempts
        for (int i = 0; i < 3; i++) {
            Call<ResponseBody> call = retrofitFileDownloader.downloadFile(fileUrl);
            Response<ResponseBody> response = call.execute();
            ResponseBody body = response.body();

            try (
                    BufferedInputStream urlInputStream = new BufferedInputStream(body.byteStream());
                    FileOutputStream fileOutputStream = new FileOutputStream(destinationPath.toFile())
            ) {
                byte data[] = new byte[1024];
                int count;
                while ((count = urlInputStream.read(data, 0, 1024)) != -1) {
                    fileOutputStream.write(data, 0, count);
                }

                File downloadedFile = destinationPath.toFile();

                if (!downloadedFile.exists()) {
                    throw new IOException("BotInfo file doesn't exists after download.");
                }

                try (FileInputStream fis = new FileInputStream(downloadedFile); BufferedInputStream bis = new BufferedInputStream(fis)) {
                    ChecksumCreator checksumCreator = new MD5ChecksumCreator(bis);
                    String fileChecksum = checksumCreator.create();

                    if (!fileChecksum.equals(checksum)) {
                        System.out.println("Calculated checksum doesn't match " + fileChecksum);
                        System.out.println("Received checksum: " + checksum);
                        continue;
                    }

                    success = true;
                    break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (!success) {
            throw new IOException("Can't download file " + fileUrl);
        }
    }

    private interface RetrofitFileDownloader {

        @GET
        Call<ResponseBody> downloadFile(@Url String fileUrl);
    }
}
