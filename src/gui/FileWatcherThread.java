package gui;

import model.Fahrer;
import model.Fahrt;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;


public class FileWatcherThread extends Thread {
    private final Path fileToWatch;
    private final Map<String, Fahrer> fahrerMap;
    private final KilometererfassungGUI gui;
    private final DateTimeFormatter dateFormatter;

    public FileWatcherThread (String fileName, Map <String, Fahrer> fahrerMap, KilometererfassungGUI gui, DateTimeFormatter dateFormatter) {
        this.fileToWatch = Paths.get(fileName);
    this.fahrerMap = fahrerMap;
    this.gui = gui;
    this.dateFormatter = dateFormatter;
    }

    @Override
    public void run(){
        try{
        WatchService watchService = FileSystems.getDefault().newWatchService();
        fileToWatch.getParent().register(watchService, StandardWatchEventKinds.ENTRY_CREATE);

        while (!isInterrupted()){
            WatchKey key = watchService.poll();
            if (key != null){
                for (WatchEvent<?> event : key.pollEvents()) {
                    if (event.context().toString().equals(fileToWatch.getFileName().toString())){
                        processFile();
                    }
                }
                key.reset();
            }
            Thread.sleep(60000);
        }
    } catch (IOException | InterruptedException e){
        e.printStackTrace();
    }
    }

private void processFile() {
            try (BufferedReader reader = new BufferedReader(new FileReader(fileToWatch.toFile()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length == 4) {
                        String personalnummer = parts[0];
                        LocalDate datum = LocalDate.parse(parts[1], dateFormatter);
                        String startort = parts[2];
                        int kilometer = Integer.parseInt(parts[3]);

                        if (kilometer < 0) {
                            throw new IllegalArgumentException("negative Kilometerangabe in der Datei gefunden");
                        }
                        Fahrer fahrer = fahrerMap.get(personalnummer);
                        if (fahrer != null) {
                            Fahrt neueFahrt = new Fahrt(datum, startort, kilometer);
                            fahrer.addFahrt(neueFahrt);
                            SwingUtilities.invokeLater(() -> gui.updateFahrerUI(fahrer));
                        }
                    }
                }
                Files.delete(fileToWatch);
            } catch (IOException | IllegalArgumentException e) {
                e.printStackTrace();
            }
        }
    }
