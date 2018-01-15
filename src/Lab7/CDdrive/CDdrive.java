package Lab7.CDdrive;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

public class CDdrive {
    private List<File> listFiles = new LinkedList<>();
    private CDdriveObs watcher;

    public void setWatcher(CDdriveObs watcher) {
        this.watcher = watcher;
    }

    public void addFile(File file) {
        listFiles.add(file);
    }

    private void copyFiles() {
        changeProgress("Copy files to temp directory DiskBuffer");
        for (File file : listFiles) {
            Execute("cp -R " + file.getAbsolutePath() + " /home/DiskBuffer");   //" /home/dasha/DiskBuffer"
        }
    }

    private void createDirectory() {
        changeProgress("Create temp directory DiskBuffer");
        Execute("mkdir /home/DiskBuffer");  //" /home/dasha/DiskBuffer"
    }

    private void umountDisk() {
        changeProgress("Umount disk");
        Execute("umount /dev/sr0"); //"umount /dev/hdc"
    }

    private void createISO() {
        changeProgress("Create ISO Disk.iso");
        Execute("mkisofs -v -J -o Disk.iso /home/DiskBuffer");  //("mkisofs -joliet-long -r -o Disk.iso /home/dasha/DiskBuffer");
    }

    private void formatDisk() {
        changeProgress("Format CD disk");
        Execute("cdrecord -dev=/dev/sr0 -v -blank=fast");   //"cdrecord -dev=1,0,0 -v -blank=fast"
    }

    private void writeFilesToDisk() {
        changeProgress("Record Disk.iso to CD");
        Execute("cdrecord -dev=/dev/sr0 -speed=16 -eject -v Disk.iso"); //"cdrecord -dev=1,0,0 -speed=16 -eject -v Disk.iso"
    }

    private void deleteISO() {
        changeProgress("Delete temp files");
        Execute("rm ./Disk.iso");
    }

    private void deleteFiles() {
        Execute("rm -rf /home/DiskBuffer"); //"rm -rf /home/lorem/DiskBuffer"
    }

    private void changeProgress(String message) {
        watcher.setProgressValue(message);
    }

    public void burnFiles() {
        if (!listFiles.isEmpty()) {
            if (checkDisk()) {
                createDirectory();
                copyFiles();
                umountDisk();
                createISO();
                formatDisk();
                writeFilesToDisk();
                deleteISO();
                deleteFiles();
                changeProgress("Complete!");
            } else {
                watcher.showError("Not founded CD disk");
                changeProgress("Error!");
            }
            watcher.endRecord();
            listFiles.clear();
        }
    }

    private void Execute(String command) {
        Process process;
        BufferedReader reader;
        try {
            process = Runtime.getRuntime().exec(new String[]{"/bin/bash", "-c", "echo "  //?????????????????????
                    + System.getenv("PASSWORD") + " |  " + command});
            reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {

                if (line.contains(" written ")) {
                    watcher.setProgressValue(line.substring(0, line.indexOf(" written ")));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean checkDisk() {
        ProcessBuilder bash = new ProcessBuilder("bash", "-c", "df | grep 'sr0'");
        Process process;
        try {
            process = bash.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            return reader.readLine() != null;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
