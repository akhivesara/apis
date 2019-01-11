package com.imdb;

import com.imdb.util.PathUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileTime;
import java.util.zip.GZIPInputStream;

public class FileDownloader {

    /**
     * Downloads file if file is not yet downloaded, unless forceDownload is set.
     * File paths are all retrieved  using {@link PathUtils}
     *
     *
     * TODO: Add cache policy to determine if file should be downloaded again
     * @param identifier    file identifier
     * @param forceDownload
     */
    public static void downloadIfNeeded(String identifier, boolean forceDownload) {
        boolean needed = true;
        if (!forceDownload) {
            try {
                FileTime time = Files.getLastModifiedTime(Paths.get(PathUtils.getLocalFinalPath(identifier)));
                if (time != null) {
                    needed = false;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (needed) {
            downloadFile(identifier);
        }
    }

    private static void downloadFile(String identifier) {
        System.out.println("Being downloadFile for "+identifier);
        download(PathUtils.getImdbURI(identifier), PathUtils.getLocalCopyPath(identifier), PathUtils.getLocalFinalPath(identifier));
    }

    private static void download(String fileName, String outName, String decodeName) {
        try {

            // Copy gzipped File
            InputStream inputStream = new URL(fileName).openStream();
            Files.copy(inputStream, Paths.get(outName), StandardCopyOption.REPLACE_EXISTING);

            // decode file
            GZIPInputStream gzip =
                    new GZIPInputStream(new FileInputStream(outName));

            // copy decoded file
            Files.copy(gzip, Paths.get(decodeName), StandardCopyOption.REPLACE_EXISTING);

            gzip.close();

            // delete encoded file
            Files.delete(Paths.get(outName));

            System.out.println("File Download Done for at "+decodeName);
        } catch (Exception e) {
            System.out.println("Exception : "+e);
        }
    }

}
