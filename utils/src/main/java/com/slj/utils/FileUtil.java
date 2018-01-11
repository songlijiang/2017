package com.slj.utils;

import com.sun.deploy.util.StringUtils;

import java.io.*;
import java.net.URL;
import java.security.SecureRandom;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileUtil {
    public static final int ONE_KB = 1024;
    public static final int ONE_MB = 1048576;
    public static final int ONE_GB = 1073741824;
    public static String FS = System.getProperty("file.separator");
    private static final String[] INVALID_CHARACTERS_FOR_WINDOWS_FILE_NAME = new String[]{":", "*", "?", "\"", "<", ">", "|"};


    public static String byteCountToDisplaySize(int size) {
        String displaySize;
        if (size / ONE_GB > 0) {
            displaySize = size / ONE_GB + " GB";
        } else if (size / ONE_MB > 0) {
            displaySize = size / ONE_MB + " MB";
        } else if (size / ONE_KB > 0) {
            displaySize = size / ONE_KB + " KB";
        } else {
            displaySize = size + " bytes";
        }

        return displaySize;
    }

    public static String dirname(String filename) {
        int i = filename.lastIndexOf(File.separator);
        return i >= 0 ? filename.substring(0, i) : "";
    }

    public static String filename(String filename) {
        int i = filename.lastIndexOf(File.separator);
        return i >= 0 ? filename.substring(i + 1) : filename;
    }

    public static String basename(String filename) {
        return basename(filename, extension(filename));
    }

    public static String basename(String filename, String suffix) {
        int i = filename.lastIndexOf(File.separator) + 1;
        int lastDot = suffix != null && suffix.length() > 0 ? filename.lastIndexOf(suffix) : -1;
        if (lastDot >= 0) {
            return filename.substring(i, lastDot);
        } else {
            return i > 0 ? filename.substring(i) : filename;
        }
    }

    public static String extension(String filename) {
        int lastSep = filename.lastIndexOf(File.separatorChar);
        int lastDot;
        if (lastSep < 0) {
            lastDot = filename.lastIndexOf(46);
        } else {
            lastDot = filename.substring(lastSep + 1).lastIndexOf(46);
            if (lastDot >= 0) {
                lastDot += lastSep + 1;
            }
        }

        return lastDot >= 0 && lastDot > lastSep ? filename.substring(lastDot + 1) : "";
    }

    public static boolean fileExists(String fileName) {
        File file = new File(fileName);
        return file.exists();
    }

    public static String fileRead(String file) throws IOException {
        return fileRead((String)file, (String)null);
    }

    public static String fileRead(String file, String encoding) throws IOException {
        return fileRead(new File(file), encoding);
    }

    public static String fileRead(File file) throws IOException {
        return fileRead((File)file, (String)null);
    }

    public static String fileRead(File file, String encoding) throws IOException {
        StringBuffer buf = new StringBuffer();
        InputStreamReader reader = null;

        try {
            if (encoding != null) {
                reader = new InputStreamReader(new FileInputStream(file), encoding);
            } else {
                reader = new InputStreamReader(new FileInputStream(file));
            }

            char[] b = new char[512];

            int count;
            while((count = reader.read(b)) > 0) {
                buf.append(b, 0, count);
            }
        } finally {
            IOUtil.close(reader);
        }

        return buf.toString();
    }

    public static void fileAppend(String fileName, String data) throws IOException {
        fileAppend(fileName, (String)null, data);
    }

    public static void fileAppend(String fileName, String encoding, String data) throws IOException {
        FileOutputStream out = null;

        try {
            out = new FileOutputStream(fileName, true);
            if (encoding != null) {
                out.write(data.getBytes(encoding));
            } else {
                out.write(data.getBytes());
            }
        } finally {
            IOUtil.close(out);
        }

    }

    public static void fileWrite(String fileName, String data) throws IOException {
        fileWrite(fileName, (String)null, data);
    }

    public static void fileWrite(String fileName, String encoding, String data) throws IOException {
        FileOutputStream out = null;

        try {
            out = new FileOutputStream(fileName);
            if (encoding != null) {
                out.write(data.getBytes(encoding));
            } else {
                out.write(data.getBytes());
            }
        } finally {
            IOUtil.close(out);
        }

    }

    public static void fileDelete(String fileName) {
        File file = new File(fileName);
        file.delete();
    }

    public static boolean waitFor(String fileName, int seconds) {
        return waitFor(new File(fileName), seconds);
    }

    public static boolean waitFor(File file, int seconds) {
        int timeout = 0;
        int tick = 0;

        while(!file.exists()) {
            if (tick++ >= 10) {
                tick = 0;
                if (timeout++ > seconds) {
                    return false;
                }
            }

            try {
                Thread.sleep(100L);
            } catch (InterruptedException var5) {
                ;
            }
        }

        return true;
    }

    public static File getFile(String fileName) {
        return new File(fileName);
    }

    public static String[] getFilesFromExtension(String directory, String[] extensions) {
        Vector files = new Vector();
        File currentDir = new File(directory);
        String[] unknownFiles = currentDir.list();
        if (unknownFiles == null) {
            return new String[0];
        } else {
            for(int i = 0; i < unknownFiles.length; ++i) {
                String currentFileName = directory + System.getProperty("file.separator") + unknownFiles[i];
                File currentFile = new File(currentFileName);
                if (currentFile.isDirectory()) {
                    if (!currentFile.getName().equals("CVS")) {
                        String[] fetchFiles = getFilesFromExtension(currentFileName, extensions);
                        files = blendFilesToVector(files, fetchFiles);
                    }
                } else {
                    String add = currentFile.getAbsolutePath();
                    if (isValidFile(add, extensions)) {
                        files.addElement(add);
                    }
                }
            }

            String[] foundFiles = new String[files.size()];
            files.copyInto(foundFiles);
            return foundFiles;
        }
    }

    private static Vector blendFilesToVector(Vector v, String[] files) {
        for(int i = 0; i < files.length; ++i) {
            v.addElement(files[i]);
        }

        return v;
    }

    private static boolean isValidFile(String file, String[] extensions) {
        String extension = extension(file);
        if (extension == null) {
            extension = "";
        }

        for(int i = 0; i < extensions.length; ++i) {
            if (extensions[i].equals(extension)) {
                return true;
            }
        }

        return false;
    }

    public static boolean contentEquals(File file1, File file2) throws IOException {
        boolean file1Exists = file1.exists();
        if (file1Exists != file2.exists()) {
            return false;
        } else if (!file1Exists) {
            return true;
        } else if (!file1.isDirectory() && !file2.isDirectory()) {
            InputStream input1 = null;
            FileInputStream input2 = null;

            boolean var5;
            try {
                input1 = new FileInputStream(file1);
                input2 = new FileInputStream(file2);
                var5 = IOUtil.contentEquals(input1, input2);
            } finally {
                IOUtil.close(input1);
                IOUtil.close(input2);
            }

            return var5;
        } else {
            return false;
        }
    }

    public static File toFile(URL url) {
        if (url != null && url.getProtocol().equalsIgnoreCase("file")) {
            String filename = url.getFile().replace('/', File.separatorChar);
            int pos = -1;

            while((pos = filename.indexOf(37, pos + 1)) >= 0) {
                if (pos + 2 < filename.length()) {
                    String hexStr = filename.substring(pos + 1, pos + 3);
                    char ch = (char)Integer.parseInt(hexStr, 16);
                    filename = filename.substring(0, pos) + ch + filename.substring(pos + 3);
                }
            }

            return new File(filename);
        } else {
            return null;
        }
    }

    public static URL[] toURLs(File[] files) throws IOException {
        URL[] urls = new URL[files.length];

        for(int i = 0; i < urls.length; ++i) {
            urls[i] = files[i].toURL();
        }

        return urls;
    }

    public static String removeExtension(String filename) {
        String ext = extension(filename);
        if ("".equals(ext)) {
            return filename;
        } else {
            int index = filename.lastIndexOf(ext) - 1;
            return filename.substring(0, index);
        }
    }

    public static String getExtension(String filename) {
        return extension(filename);
    }

    public static String removePath(String filepath) {
        return removePath(filepath, File.separatorChar);
    }

    public static String removePath(String filepath, char fileSeparatorChar) {
        int index = filepath.lastIndexOf(fileSeparatorChar);
        return -1 == index ? filepath : filepath.substring(index + 1);
    }

    public static String getPath(String filepath) {
        return getPath(filepath, File.separatorChar);
    }

    public static String getPath(String filepath, char fileSeparatorChar) {
        int index = filepath.lastIndexOf(fileSeparatorChar);
        return -1 == index ? "" : filepath.substring(0, index);
    }

    public static void copyFileToDirectory(String source, String destinationDirectory) throws IOException {
        copyFileToDirectory(new File(source), new File(destinationDirectory));
    }

    public static void copyFileToDirectoryIfModified(String source, String destinationDirectory) throws IOException {
        copyFileToDirectoryIfModified(new File(source), new File(destinationDirectory));
    }

    public static void copyFileToDirectory(File source, File destinationDirectory) throws IOException {
        if (destinationDirectory.exists() && !destinationDirectory.isDirectory()) {
            throw new IllegalArgumentException("Destination is not a directory");
        } else {
            copyFile(source, new File(destinationDirectory, source.getName()));
        }
    }

    public static void copyFileToDirectoryIfModified(File source, File destinationDirectory) throws IOException {
        if (destinationDirectory.exists() && !destinationDirectory.isDirectory()) {
            throw new IllegalArgumentException("Destination is not a directory");
        } else {
            copyFileIfModified(source, new File(destinationDirectory, source.getName()));
        }
    }

    public static void copyFile(File source, File destination) throws IOException {
        String message;
        if (!source.exists()) {
            message = "File " + source + " does not exist";
            throw new IOException(message);
        } else if (!source.getCanonicalPath().equals(destination.getCanonicalPath())) {
            copyStreamToFile(new FileInputStream(source), destination);
            if (source.length() != destination.length()) {
                message = "Failed to copy full contents from " + source + " to " + destination;
                throw new IOException(message);
            }
        }
    }

    public static boolean copyFileIfModified(File source, File destination) throws IOException {
        if (destination.lastModified() < source.lastModified()) {
            copyFile(source, destination);
            return true;
        } else {
            return false;
        }
    }

    public static void copyURLToFile(URL source, File destination) throws IOException {
        copyStreamToFile( source.openStream(), destination);
    }

    public static void copyStreamToFile(InputStream  source, File destination) throws IOException {
        if (destination.getParentFile() != null && !destination.getParentFile().exists()) {
            destination.getParentFile().mkdirs();
        }

        if (destination.exists() && !destination.canWrite()) {
            String message = "Unable to open file " + destination + " for writing.";
            throw new IOException(message);
        } else {
            InputStream input = null;
            FileOutputStream output = null;

            try {
                input = source;
                output = new FileOutputStream(destination);
                IOUtil.copy(input, output);
            } finally {
                IOUtil.close(input);
                IOUtil.close(output);
            }

        }
    }

    public static String normalize(String path) {
        String normalized = path;

        while(true) {
            int index = normalized.indexOf("//");
            if (index < 0) {
                while(true) {
                    index = normalized.indexOf("/./");
                    if (index < 0) {
                        while(true) {
                            index = normalized.indexOf("/../");
                            if (index < 0) {
                                return normalized;
                            }

                            if (index == 0) {
                                return null;
                            }

                            int index2 = normalized.lastIndexOf(47, index - 1);
                            normalized = normalized.substring(0, index2) + normalized.substring(index + 3);
                        }
                    }

                    normalized = normalized.substring(0, index) + normalized.substring(index + 2);
                }
            }

            normalized = normalized.substring(0, index) + normalized.substring(index + 1);
        }
    }

    public static String catPath(String lookupPath, String path) {
        int index = lookupPath.lastIndexOf("/");
        String lookup = lookupPath.substring(0, index);

        String pth;
        for(pth = path; pth.startsWith("../"); pth = pth.substring(index)) {
            if (lookup.length() <= 0) {
                return null;
            }

            index = lookup.lastIndexOf("/");
            lookup = lookup.substring(0, index);
            index = pth.indexOf("../") + 3;
        }

        return lookup + "/" + pth;
    }





    public static long sizeOfDirectory(String directory) {
        return sizeOfDirectory(new File(directory));
    }

    public static long sizeOfDirectory(File directory) {
        String message;
        if (!directory.exists()) {
            message = directory + " does not exist";
            throw new IllegalArgumentException(message);
        } else if (!directory.isDirectory()) {
            message = directory + " is not a directory";
            throw new IllegalArgumentException(message);
        } else {
            long size = 0L;
            File[] files = directory.listFiles();

            for(int i = 0; i < files.length; ++i) {
                File file = files[i];
                if (file.isDirectory()) {
                    size += sizeOfDirectory(file);
                } else {
                    size += file.length();
                }
            }

            return size;
        }
    }


    private static void copyDirectoryStructure(File sourceDirectory, File destinationDirectory, File rootDestinationDirectory, boolean onlyModifiedFiles) throws IOException {
        if (sourceDirectory == null) {
            throw new IOException("source directory can't be null.");
        } else if (destinationDirectory == null) {
            throw new IOException("destination directory can't be null.");
        } else if (sourceDirectory.equals(destinationDirectory)) {
            throw new IOException("source and destination are the same directory.");
        } else if (!sourceDirectory.exists()) {
            throw new IOException("Source directory doesn't exists (" + sourceDirectory.getAbsolutePath() + ").");
        } else {
            File[] files = sourceDirectory.listFiles();
            String sourcePath = sourceDirectory.getAbsolutePath();

            for(int i = 0; i < files.length; ++i) {
                File file = files[i];
                if (!file.equals(rootDestinationDirectory)) {
                    String dest = file.getAbsolutePath();
                    dest = dest.substring(sourcePath.length() + 1);
                    File destination = new File(destinationDirectory, dest);
                    if (file.isFile()) {
                        destination = destination.getParentFile();
                        if (onlyModifiedFiles) {
                            copyFileToDirectoryIfModified(file, destination);
                        } else {
                            copyFileToDirectory(file, destination);
                        }
                    } else {
                        if (!file.isDirectory()) {
                            throw new IOException("Unknown file type: " + file.getAbsolutePath());
                        }

                        if (!destination.exists() && !destination.mkdirs()) {
                            throw new IOException("Could not create destination directory '" + destination.getAbsolutePath() + "'.");
                        }

                        copyDirectoryStructure(file, destination, rootDestinationDirectory, onlyModifiedFiles);
                    }
                }
            }

        }
    }

    public static void rename(File from, File to) throws IOException {
        if (to.exists() && !to.delete()) {
            throw new IOException("Failed to delete " + to + " while trying to rename " + from);
        } else {
            File parent = to.getParentFile();
            if (parent != null && !parent.exists() && !parent.mkdirs()) {
                throw new IOException("Failed to create directory " + parent + " while trying to rename " + from);
            } else {
                if (!from.renameTo(to)) {
                    copyFile(from, to);
                    if (!from.delete()) {
                        throw new IOException("Failed to delete " + from + " while trying to rename it.");
                    }
                }

            }
        }
    }

    public static File createTempFile(String prefix, String suffix, File parentDir) {
        File result = null;
        String parent = System.getProperty("java.io.tmpdir");
        if (parentDir != null) {
            parent = parentDir.getPath();
        }

        DecimalFormat fmt = new DecimalFormat("#####");
        SecureRandom secureRandom = new SecureRandom();
        long secureInitializer = secureRandom.nextLong();
        Random rand = new Random(secureInitializer + Runtime.getRuntime().freeMemory());
        synchronized(rand) {
            do {
                result = new File(parent, prefix + fmt.format((long)Math.abs(rand.nextInt())) + suffix);
            } while(result.exists());

            return result;
        }
    }

    public static void copyFile(File from, File to, String encoding, FileUtil.FilterWrapper[] wrappers) throws IOException {
        copyFile(from, to, encoding, wrappers, false);
    }

    public static void copyFile(File from, File to, String encoding, FileUtil.FilterWrapper[] wrappers, boolean overwrite) throws IOException {
        if (wrappers != null && wrappers.length > 0) {
            Reader fileReader = null;
            Object fileWriter = null;

            try {
                if (encoding != null && encoding.length() >= 1) {
                    FileInputStream instream = new FileInputStream(from);
                    FileOutputStream outstream = new FileOutputStream(to);
                    fileReader = new BufferedReader(new InputStreamReader(instream, encoding));
                    fileWriter = new OutputStreamWriter(outstream, encoding);
                } else {
                    fileReader = new BufferedReader(new FileReader(from));
                    fileWriter = new FileWriter(to);
                }

                Reader reader = fileReader;

                for(int i = 0; i < wrappers.length; ++i) {
                    FileUtil.FilterWrapper wrapper = wrappers[i];
                    reader = wrapper.getReader((Reader)reader);
                }

                IOUtil.copy((Reader)reader, (Writer)fileWriter);
            } finally {
                IOUtil.close(fileReader);
                IOUtil.close((Writer)fileWriter);
            }
        } else if (to.lastModified() < from.lastModified() || overwrite) {
            copyFile(from, to);
        }

    }

    public static List loadFile(File file) throws IOException {
        List lines = new ArrayList();
        if (file.exists()) {
            BufferedReader reader = new BufferedReader(new FileReader(file));

            for(String line = reader.readLine(); line != null; line = reader.readLine()) {
                line = line.trim();
                if (!line.startsWith("#") && line.length() != 0) {
                    lines.add(line);
                }
            }

            reader.close();
        }

        return lines;
    }

    public abstract static class FilterWrapper {
        public FilterWrapper() {
        }

        public abstract Reader getReader(Reader var1);
    }
}

