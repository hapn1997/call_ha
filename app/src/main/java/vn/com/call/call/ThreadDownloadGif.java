package vn.com.call.call;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import vn.com.call.call.model.CallFlashItem;

public class ThreadDownloadGif extends Thread {

    private CallFlashItem item;
    private String filepath;
    private DownloadGifCallback callback;

    public ThreadDownloadGif(CallFlashItem item, String filepath, DownloadGifCallback callback) {
        this.item = item;
        this.filepath = filepath;
        this.callback = callback;
    }

    @Override
    public void run() {
        try {
            URL url = new URL(item.getVideo());
            URLConnection connection = url.openConnection();
            connection.connect();

            // Detect the file lenghth
            int fileLength = connection.getContentLength();

            // Download the file
            InputStream input = new BufferedInputStream(url.openStream());

            // Save the downloaded file
            OutputStream output = new FileOutputStream(filepath);

            byte data[] = new byte[1024];
            long total = 0;
            int count;
            while ((count = input.read(data)) != -1) {
                total += count;
                callback.onProgress((int) (total * 100 / fileLength));
                output.write(data, 0, count);
            }

            // Close connection
            output.flush();
            output.close();
            input.close();

            callback.onSuccess();
        } catch (Exception e) {
            e.printStackTrace();
            callback.onError();
        }
    }

    public interface DownloadGifCallback {
        void onProgress(int progress);

        void onSuccess();

        void onError();
    }
}
