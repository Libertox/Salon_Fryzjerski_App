package com.example.salon_fryzjerski_projekt;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.stage.Stage;
import javafx.scene.text.Text;


import java.io.IOException;
import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class BookingFinalController {
    @FXML Text bookingDesc, bookingTypeTime, bookingDate, bookingTime, bookingType, bookingEmployee, bookingBranch, bookingCity, bookingPrice, bookingName, bookingSurname, bookingPhone;
    Integer userId, cityId, branchId, typeId, employeeId;
    String cityName, branchName, branchStreet, typeTextName, typeDescName, typePriceName, typeTimeName, employeeName, dateName, timeName, priceName, name, surname, phone;
    SharedDataModel data = SharedDataModel.getInstance();

    public void handleSubmit(ActionEvent event){
        try {

            String endTime = calculateEndTime(timeName, Client.getDurationOfServices(typeId));

            if(userId == null)
                Client.sendRequest(RequestType.AddReservation,dateName,timeName,endTime, name,surname,phone,null, employeeId.toString(),typeId.toString(),branchId.toString());
            else
                Client.sendRequest(RequestType.AddReservation,dateName,timeName,endTime, name,surname,data.getPhone(),userId.toString(),employeeId.toString(),typeId.toString(),branchId.toString());

            removeDataFromDataModel();
            switchToEnd(event);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
        cityId = data.getCityId();
        branchId = data.getBranchId();
        typeId = data.getTypeId();
        employeeId = data.getEmployeeId();

        cityName = data.getCityName();
        branchName = data.getBranchName();
        branchStreet = data.getBranchStreet();
        typeTextName = data.getTypeTextName();
        typeDescName = data.getTypeDescName();
        typePriceName = data.getTypePriceName();
        typeTimeName = data.getTypeTimeName();
        if(data.getEmployeeName() == null){
            employeeName = "Dowolny pracownik";
        } else{
            employeeName = data.getEmployeeName();
        }
        dateName = data.getDateName();
        timeName = data.getTimeName();
        priceName = data.getTypePriceName();
        if (!data.getIsLogged()) {
            name = data.getName();
            surname = data.getSurname();
            phone = data.getPhone();
        }
        else{
            getUserInfo();
        }

        changeTextValue();
    }

    private String formatTimeForDisplay(String time) {
        LocalTime localTime = LocalTime.parse(time, DateTimeFormatter.ofPattern("HH:mm:ss"));
        long hours = Duration.between(LocalTime.MIDNIGHT, localTime).toHours();
        long minutes = Duration.between(LocalTime.MIDNIGHT, localTime).toMinutesPart();

        if (hours > 0) {
            return String.format("%d:%02d h", hours, minutes).trim();
        } else {
            return minutes + " min";
        }

    }

    public void getUserInfo(){
        name = data.getName();
        surname = data.getSurname();
        phone = data.getPhone();
        userId = data.getUserId();
    }


    public void removeDataFromDataModel(){
        data.setCityName(null);
        data.setBranchName(null);
        data.setTypeTextName(null);
        data.setTypeDescName(null);
        data.setTimeName(null);
        data.setDateName(null);
        data.setTimeName(null);
        data.setDateName(null);
        data.setEmployeeName(null);
        data.setTypePriceName(null);
    }

    public void changeTextValue() {
        bookingBranch.setText(branchName + " - " + branchStreet);
        bookingCity.setText(cityName);
        bookingType.setText(typeTextName);
        bookingDesc.setText(typeDescName);
        bookingTypeTime.setText(formatTimeForDisplay(typeTimeName));
        bookingEmployee.setText(employeeName);
        bookingDate.setText(dateName);
        bookingTime.setText(timeName);
        bookingPrice.setText(priceName);
        bookingName.setText(name);
        bookingSurname.setText(surname);
        bookingPhone.setText(phone);
    }

    public void goBackButton(ActionEvent event){
        if(data.getIsLogged()){
            switchToBooking(event);
        }
        else {
            switchToBookingUserInfo(event);
        }
    }
    public void switchToBookingUserInfo(ActionEvent event) {
        switchScene(event, "booking-user-info-view.fxml");
    }
    public void switchToBooking(ActionEvent event) {
        switchScene(event, "booking-view.fxml");
    }

    private void switchScene(ActionEvent event, String fxmlFile) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlFile));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.getScene().setRoot(root);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void switchToEnd(ActionEvent event) throws IOException {
        switchScene(event, "booking-end-view.fxml");
    }
}
