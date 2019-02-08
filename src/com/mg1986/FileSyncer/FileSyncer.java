package com.mg1986.FileSyncer;

import java.io.*;
import java.nio.file.*;
import java.util.Arrays;

import static java.nio.file.StandardCopyOption.*;

public class FileSyncer {

    private final static String separator = "----------------------------------";

    public static void main(String [] args) {

        if (args.length == 2) {
            String sourceURL = args[0];
            File sourceDirectory = new File(sourceURL);
            String targetURL = args[1];
            File targetDirectory = new File(targetURL);

            if (sourceDirectory.isDirectory() && targetDirectory.isDirectory()) {
                desync(targetURL, sourceURL);
                sync(sourceURL, targetURL);
            }
        }
    }

    private static void sync(String sourceDirectory, String targetDirectory) {

        File folder = new File(sourceDirectory);

        for (File source : folder.listFiles()) {
            try {
                String targetURL = source.getAbsolutePath().replace(sourceDirectory, targetDirectory);
                File target = new File(targetURL);
                if (source.isDirectory()) {
                    if (target.isDirectory()) {
                        sync(source.getAbsolutePath(), target.getAbsolutePath());
                    } else {
                        Files.copy(source.toPath(), target.toPath(), REPLACE_EXISTING);
                        System.out.println("Directory synced: " + target);
                    }
                } else if (source.isFile()) {
                    if (!target.exists()) {
                        Files.copy(source.toPath(), target.toPath(), REPLACE_EXISTING);
                        System.out.println("File synced: " + target);
                    } else {
                        byte[] f1 = Files.readAllBytes(source.toPath());
                        byte[] f2 = Files.readAllBytes(target.toPath());
                        if (!Arrays.equals(f1, f2)) {
                            Files.copy(source.toPath(), target.toPath(), REPLACE_EXISTING);
                            System.out.println("File synced: " + target);
                        }
                    }
                }
            } catch (IOException ioe) {
                ioe.printStackTrace(System.out);
                String targetURL = source.getAbsolutePath().replace(sourceDirectory, targetDirectory);
                System.out.println("Sync failed: " + targetURL);
            }
        }
    }

    private static void desync(String targetDirectory, String sourceDirectory) {

        File folder = new File(targetDirectory);

        for (File target : folder.listFiles()) {
            try {
                String sourceURL = target.getAbsolutePath().replace(targetDirectory, sourceDirectory);
                File source = new File(sourceURL);
                if (target.exists() && !source.exists()) {
                    Files.deleteIfExists(target.toPath());
                } else if (target.isDirectory()){
                    desync(target.getAbsolutePath(), source.getAbsolutePath());
                }
            } catch (IOException ioe){
                System.out.println("Sync failed: " + target.getAbsolutePath());
            }
        }
    }
}
