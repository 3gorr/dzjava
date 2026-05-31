package com.csv.io;

import java.io.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class StreamFactory {

    public static InputStream inputStream(String filename) throws IOException {
        File file = new File(filename);
        if (filename.endsWith(".zip")) {
            ZipInputStream zis = new ZipInputStream(new FileInputStream(file));
            zis.getNextEntry();
            return zis;
        }
        if (filename.endsWith(".csv.gz") || filename.endsWith(".gz")) {
            return new GZIPInputStream(new FileInputStream(file));
        }
        return new FileInputStream(file);
    }

    public static OutputStream outputStream(String filename) throws IOException {
        File file = new File(filename);
        if (filename.endsWith(".zip")) {
            ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(file));
            String entryName = file.getName().replace(".zip", ".csv");
            zos.putNextEntry(new ZipEntry(entryName));
            return zos;
        }
        if (filename.endsWith(".csv.gz") || filename.endsWith(".gz")) {
            return new GZIPOutputStream(new FileOutputStream(file));
        }
        return new FileOutputStream(file);
    }
}
