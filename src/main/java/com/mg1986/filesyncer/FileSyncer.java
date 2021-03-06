package com.mg1986.filesyncer;

import java.io.*;
import java.util.Calendar;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

import org.apache.commons.io.FileUtils;

public class FileSyncer {

    private final static String separator = "----------------------------------";

    public static void main(String[] args) {

        // If two command line arguments supplied at runtime, proceed
        if (args.length == 2) {
            String sourceURL = args[0];
            File sourceDirectory = new File(sourceURL);
            String targetURL = args[1];
            File targetDirectory = new File(targetURL);

            // If both command line argument are valid directories that exists, proceed
            if (sourceDirectory.isDirectory() && targetDirectory.isDirectory()) {

                String beginTimeStamp = new SimpleDateFormat("yyyy/MM/dd k:m").format(Calendar.getInstance().getTime());
                System.out.println("File sync began at: " + beginTimeStamp + "\n" + separator);

                desync(targetURL, sourceURL);
                sync(sourceURL, targetURL);

                String endTimeStamp = new SimpleDateFormat("yyyy/MM/dd k:m").format(Calendar.getInstance().getTime());
                System.out.println("File sync ended at: " + endTimeStamp + "\n" + separator);

            } else {
                System.out.println("FileSyncer requires valid source and target folder paths as program inputs");
            }
        } else {
            System.out.println("FileSyncer requires valid source and target folder paths as program inputs");
        }
    }

    // Adds/replaces files/directories from source to target
    private static void sync(String sourceDirectory, String targetDirectory) {

        File folder = new File(sourceDirectory);

        for (File source : folder.listFiles()) {
            try {
                String targetURL = source.getAbsolutePath().replace(sourceDirectory, targetDirectory);
                File target = new File(targetURL);
                if (source.isDirectory()) {
                    if (target.isDirectory() && target.exists()) {
                        sync(source.getAbsolutePath(), target.getAbsolutePath());
                    } else {
                        Files.copy(source.toPath(), target.toPath(), REPLACE_EXISTING);
                        System.out.println("Directory synced: " + target);
                    }
                } else if (source.isFile()) {
                    if (!target.exists() || !FileUtils.contentEquals(source, target)) {
                        Files.copy(source.toPath(), target.toPath(), REPLACE_EXISTING);
                        System.out.println("File synced: " + target);
                    }
                }
            } catch (IOException ioe) {
                String targetURL = source.getAbsolutePath().replace(sourceDirectory, targetDirectory);
                System.out.println("Sync failed: " + targetURL);
            }
        }
    }

    // Removes files/directories from target if they don't exist in source
    private static void desync(String targetDirectory, String sourceDirectory) {

        File folder = new File(targetDirectory);

        for (File target : folder.listFiles()) {
            try {
                String sourceURL = target.getAbsolutePath().replace(targetDirectory, sourceDirectory);
                File source = new File(sourceURL);
                if (target.exists() && !source.exists()) {
                    System.out.println("File/Directory removed: " + target.getAbsolutePath());
                    if (target.isDirectory()) {
                        FileUtils.deleteDirectory(target);
                    } else {
                        Files.deleteIfExists(target.toPath());
                    }
                } else if (target.isDirectory()) {
                    desync(target.getAbsolutePath(), source.getAbsolutePath());
                }
            } catch (IOException ioe) {
                System.out.println("Desync failed: " + target.getAbsolutePath());
            }
        }
    }
}



