package ch.reinhard;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class FotoSorter {

    private String fotosVerzeichnis = "";

    public FotoSorter(String fotosVerzeichnis) {
        this.fotosVerzeichnis = fotosVerzeichnis;
    }

    public void sortiereFotos() {
        List<File> fotoFileList = getZuSortierendeFotos();
        erstelleOrdnerFuerFotos(fotoFileList);
        kopiereFotosInNeueOrdner(fotoFileList);
    }

    private void erstelleOrdnerFuerFotos(List<File> fotoFileList) {
        Set<String> newDirectoryList = new HashSet<>();

        for (File file : fotoFileList) {
            // Liste mit den Ordner-Namen (Datum) erstellen
            //Date fileDate = getFileCreationTime(file);
            // Date fileDate = getFileModifiedTime(file);
            Date dateFromFilename = getDateFromFilename(file);
            if (dateFromFilename != null) {
                String formattedDate = getFormattedDate(dateFromFilename);
                newDirectoryList.add(formattedDate);
            } else {
                System.out.println("Datum konnte nicht aus FileName konvertiert werden: " + file.getName());
            }
        }

        // Neue Ordner erstellen
        for (String datum : newDirectoryList) {
            File newDirectory = new File(fotosVerzeichnis + datum);
            if (!newDirectory.exists()) {
                newDirectory.mkdir();
                System.out.println(datum);
            }
        }
    }

    private List<File> getZuSortierendeFotos() {
        List<File> fotoFileList = new ArrayList<>();

        if (fotosVerzeichnis != null && !fotosVerzeichnis.isEmpty()) {
            File folder = new File(fotosVerzeichnis);
            File[] listOfFiles = folder.listFiles();

            for (File file : listOfFiles) {
                if (file.isFile() && !file.isHidden()) {
                    if (!file.getName().endsWith(".iml") && !file.getName().endsWith(".jar")) {
                        // File zum Kopieren sammeln
                        fotoFileList.add(file);
                    }
                }
            }
        }

        return fotoFileList;
    }

    private void kopiereFotosInNeueOrdner(List<File> fotoFileList) {
        // Fotos in die neuen Ordner kopieren
        for (File file : fotoFileList) {
            Date fileCreationDate = getDateFromFilename(file);

            if (fileCreationDate != null) {
                String formattedDate = getFormattedDate(fileCreationDate);
                try {
                    Files.move(Paths.get(fotosVerzeichnis + file.getName()), Paths.get(fotosVerzeichnis + formattedDate + "/" + file.getName()), StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    private Date getFileModifiedTime(File file) {
        FileTime fileModifiedTime = null;
        try {
            BasicFileAttributes attrs = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
            fileModifiedTime = attrs.lastModifiedTime();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (fileModifiedTime != null) {
            return new Date(fileModifiedTime.toMillis());
        } else {
            return null;
        }
    }

    private Date getFileCreationTime(File file) {
        FileTime fileCreationTime = null;
        try {
            BasicFileAttributes attrs = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
            fileCreationTime = attrs.creationTime();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (fileCreationTime != null) {
            return new Date(fileCreationTime.toMillis());
        } else {
            return null;
        }
    }

    private Date getDateFromFilename(File file) {

        String filename = file.getName();

        // Whatsapp-Images haben IMG- vorab
        String filenameOhnePrefix = filename.replace("IMG-", "");

        String datumAusFilename = filenameOhnePrefix.substring(0, 8);

        // Erstelle Date aus String
        Date dateFromFilename = null;
        String pattern = "yyyyMMdd";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        try {
            dateFromFilename = simpleDateFormat.parse(datumAusFilename);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return dateFromFilename;
    }

    private String getFormattedDate(Date date) {
        String pattern = "yyyy-MM-dd";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        String formattedCreationTime = simpleDateFormat.format(date);
        return  formattedCreationTime;
    }
}
