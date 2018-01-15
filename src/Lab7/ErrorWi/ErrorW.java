package Lab7.ErrorWi;

import javafx.scene.control.Alert;

public class ErrorW {
    public static void showErrorAlert(String error) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(error);
        alert.showAndWait();
    }
}