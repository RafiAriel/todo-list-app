package models;

import java.io.Serializable;

public class Mission implements Serializable {
    int deadlineTimeHour,deadlineTimeMin;
    int startTimeHour,startTimeMin;
    String phone;
    String mission_for_today;
    String address;
    String email;


    public Mission(int deadlineTimeHour, int deadlineTimeMin, int startTimeHour, int startTimeMin, String phone, String mission_for_today, String address, String email) {
        this.deadlineTimeHour = deadlineTimeHour;
        this.deadlineTimeMin = deadlineTimeMin;
        this.startTimeHour = startTimeHour;
        this.startTimeMin = startTimeMin;
        this.phone = phone;
        this.mission_for_today = mission_for_today;
        this.address = address;
        this.email = email;
    }

    @Override
    public String toString() {
        return "Mission{" +
                "deadlineTimeHour=" + deadlineTimeHour +
                ", deadlineTimeMin=" + deadlineTimeMin +
                ", startTimeHour=" + startTimeHour +
                ", startTimeMin=" + startTimeMin +
                ", phone='" + phone + '\'' +
                ", mission_for_today='" + mission_for_today + '\'' +
                ", address='" + address + '\'' +
                ", email='" + email + '\'' +
                '}';
    }

    public int getDeadlineTimeHour() {
        return deadlineTimeHour;
    }

    public int getDeadlineTimeMin() {
        return deadlineTimeMin;
    }

    public int getStartTimeHour() {
        return startTimeHour;
    }

    public int getStartTimeMin() {
        return startTimeMin;
    }

    public String getPhone() {
        return phone;
    }

    public String getMission_for_today() {
        return mission_for_today;
    }

    public String getAddress() {
        return address;
    }

    public String getemail() {
        return email;
    }
}
