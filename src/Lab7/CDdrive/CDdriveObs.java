package Lab7.CDdrive;

public interface CDdriveObs {
    void setProgressValue(String message);

    void endRecord();

    void showError(String message);
}
