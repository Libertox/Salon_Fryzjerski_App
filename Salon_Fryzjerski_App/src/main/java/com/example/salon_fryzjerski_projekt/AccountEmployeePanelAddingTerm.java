package com.example.salon_fryzjerski_projekt;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

public class AccountEmployeePanelAddingTerm {

    @FXML Label yearLabel, monthLabel, dayLabel, startTimeLabel;
    @FXML TextField year, month, day, startTime;
    @FXML Button reservationButton, termListButton, addTernButton, addReservationTerm;

    String yearString, monthString, dayString, startTimeString;

    String dataRegex = "^\\d+$";
    String timeRegex = "^(?:[0-1]\\d|2[0-3]):[0-5]\\d$";

    Pattern dataPattern = Pattern.compile(dataRegex);
    Pattern timePattern = Pattern.compile(timeRegex);

    SharedDataModel data = SharedDataModel.getInstance();

    public void handleAddTermButton(){
        yearString = year.getText().trim();
        monthString = month.getText().trim();
        dayString = day.getText().trim();
        startTimeString = startTime.getText().trim();

        if(!handleTermsErrors()){
            LocalDate inputDate = LocalDate.of(Integer.parseInt(yearString), Integer.parseInt(monthString), Integer.parseInt(dayString));
            String date = inputDate.format(DateTimeFormatter.ISO_LOCAL_DATE);

            String endTime = calculateEndTime(startTimeString,"00:30");
            Client.sendRequest(RequestType.AddReservationTerm,date,startTimeString,endTime,data.getUserId().toString());

            yearLabel.setText("");
            monthLabel.setText("");
            dayLabel.setText("");
            startTimeLabel.setText("");

            year.setText("");
            month.setText("");
            day.setText("");
            startTime.setText("");
        }
    }

    public boolean handleTermsErrors(){
        yearLabel.setText("");
        monthLabel.setText("");
        dayLabel.setText("");
        startTimeLabel.setText("");

        boolean isError = checkYearError() && checkMonthError() && checkDayError() && checkStartTimeError();

        if(!isError)
            isError = checkTimeElapsed();

        return isError;
    }

    private boolean checkTimeElapsed() {
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
            LocalTime timeValue = LocalTime.parse(startTimeString, timeFormatter);

            LocalDate inputDate = LocalDate.of(Integer.parseInt(yearString), Integer.parseInt(monthString), Integer.parseInt(dayString));
            LocalDateTime inputDateTime = LocalDateTime.of(inputDate, timeValue);

            if (inputDateTime.isBefore(LocalDateTime.now())) {
                startTimeLabel.setText("Termin musi być w przyszłości");
                return true;
            }
        return false;
    }

    private boolean checkStartTimeError() {
        if(startTimeString.isEmpty()){
            startTimeLabel.setText("Pole jest wymagane");
            return true;
        }
        else if (!timePattern.matcher(startTimeString).matches()) {
            startTimeLabel.setText("Nieprawidłowa wartość");
            return true;
        }
        return false;
    }

    private boolean checkYearError() {
        if(yearString.isEmpty()){
            yearLabel.setText("Pole jest wymagane");
            return true;
        }
        else if(!dataPattern.matcher(yearString).matches()){
            yearLabel.setText("Nieprawidłowa wartość");
            return true;
        }else {
            int yearValue = Integer.parseInt(yearString);
            if (yearValue < LocalDate.now().getYear()) {
                yearLabel.setText("Nieprawidłowy rok");
                return true;
            }
        }

        return false;
    }

    private boolean checkDayError() {
        if(dayString.isEmpty()){
            dayLabel.setText("Pole jest wymagane");
            return true;
        }
        else if (!dataPattern.matcher(dayString).matches()) {
            dayLabel.setText("Nieprawidłowa wartość");
            return true;
        } else {
            int dayValue = Integer.parseInt(dayString);
            int yearValue = Integer.parseInt(yearString);
            int monthValue = Integer.parseInt(monthString);

            if (dayValue < 1 || dayValue > 31) {
                dayLabel.setText("Nieprawidłowy dzień");
                return true;
            } else if (monthValue == 2) {
                boolean isLeapYear = (yearValue % 4 == 0 && (yearValue % 100 != 0 || yearValue % 400 == 0));
                if (isLeapYear && dayValue > 29 || !isLeapYear && dayValue > 28) {
                    dayLabel.setText("Nieprawidłowy dzień dla lutego");
                    return true;
                }
            } else if ((monthValue == 4 || monthValue == 6 || monthValue == 9 || monthValue == 11) && dayValue > 30) {
                dayLabel.setText("Nieprawidłowy dzień dla miesiąca");
                return true;
            }
        }
        return false;
    }

    private boolean checkMonthError() {
        if(monthString.isEmpty()){
            monthLabel.setText("Pole jest wymagane");
            return true;
        }
        else if (!dataPattern.matcher(monthString).matches()) {
            monthLabel.setText("Nieprawidłowa wartość");
            return true;
        } else {
            int monthValue = Integer.parseInt(monthString);
            if (monthValue < 1 || monthValue > 12) {
                monthLabel.setText("Nieprawidłowy miesiąc");
                return true;
            }
        }
        return false;
    }

    public String calculateEndTime(String startTime, String duration) {
        String[] startTimeParts = startTime.split(":");
        int startHours = Integer.parseInt(startTimeParts[0]);
        int startMinutes = Integer.parseInt(startTimeParts[1]);

        String[] durationParts = duration.split(":");
        int durationHours = Integer.parseInt(durationParts[0]);
        int durationMinutes = Integer.parseInt(durationParts[1]);

        int endMinutes = startMinutes + durationMinutes;
        int endHours = startHours + durationHours + endMinutes / 60;
        endMinutes = endMinutes % 60;
        endHours = endHours % 24;

        String endTime = String.format("%02d:%02d", endHours, endMinutes);

        return endTime;
    }


    public void initialize() {
        if (areAllObjectsNonNull()) {

            addTernButton.getStyleClass().add("panel-selected");
            termListButton.getStyleClass().add("default-button");
            reservationButton.getStyleClass().add("default-button");
        }
    }

    private boolean areAllObjectsNonNull() {
        return year != null && month != null && day != null && startTime != null
                && yearLabel != null && monthLabel != null && dayLabel != null && startTimeLabel != null;
    }

    private void switchScene(ActionEvent event, String fxmlFile) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlFile));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.getScene().setRoot(root);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void switchToAccountBookingDetails(ActionEvent event) {
        switchScene(event, "account-booking-history-details.fxml");
    }

    public void switchToAccountReservation(ActionEvent event){
        switchScene(event, "account-employee-panel.fxml");
    }

    public void switchToAccountTermList(ActionEvent event){
        switchScene(event, "account-employee-term-list-panel.fxml");
    }

    public void switchToAccountTermAdding(ActionEvent event){
        switchScene(event,"account-employee-panel-adding-term.fxml");
    }


}
