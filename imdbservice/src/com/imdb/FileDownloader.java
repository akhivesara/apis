package com.imdb;

import com.imdb.util.PathUtils;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.util.zip.GZIPInputStream;

public class FileDownloader {

    public static void downloadIfNeeded(String identifier) {
        boolean needed = true;
        try {

            Instant now = Instant.now();
            FileTime time = Files.getLastModifiedTime(Paths.get(PathUtils.getLocalFinalPath(identifier)));
            if (time != null) {
                needed = false;
            }

        } catch (IOException e) {
            e.printStackTrace();
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



    /*

    OLD CODE

    private static void readLines(String fileName) {
        try{
            BufferedReader buf = new BufferedReader(new FileReader(fileName));
            ArrayList<String> words = new ArrayList<>();
            String lineJustFetched;
            String[] wordsArray;

            int lineCount = 0;
            while(true) {

                lineJustFetched = buf.readLine();
                if(lineJustFetched == null){
                    //lineCount = 0;
                    break;
                }else{
                    lineCount++;
                    if (lineCount == 1) {
                        continue;
                    }
                    wordsArray = lineJustFetched.split("\t");
                    for(String each : wordsArray){

                        if("\\N".equals(each)) {
                            each = null;
                        }
                        // RATINGS
                        words.add(each);

                    }
                    // write row
                    MySQLStore db = MySQLStore.getInstance("jdbc:mysql://localhost/nflxtakehome", "nflxtakehome", "nflxtakehome");
                    //db.testInsert(input + "", "Title Name " + input);
                    int index = words.size() - 3;
                    //db.ratingsInsert(words.get(0), Double.parseDouble(words.get(1)), Integer.parseInt(words.get(2)));
                    db.ratingsInsert(words.get(index), Double.parseDouble(words.get(index + 1)), Integer.parseInt(words.get(index + 2)));

                }
            }

            for(String each : words){
                System.out.println(each);
            }

            buf.close();

        }catch(Exception e){
            e.printStackTrace();
        }
    }

        public static void downloadMultipleFiles(String[] fileIdentifiers) {
        for (String identifier : fileIdentifiers) {
            FileDownloader.downloadFile(identifier);
        }
    }

    public static void downloadExample() {
        String fileName = "https://datasets.imdbws.com/title.crew.tsv.gz";
        String outName = "/Users/ashishkhivesara/Documents/d1.txt";
        String decodeName = "/Users/ashishkhivesara/Documents/e1.txt";

        download(fileName, outName, decodeName);
    }


 */
}
