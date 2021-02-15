package com.triplogs.model;

public class LocationData {

    String tripId;
    String speed;
    String altitude;
    String heading;
    String latitude;
    String longitude;
    String accuracy;
    String timestamp;

    public LocationData(String tripId, String speed, String altitude, String heading, String latitude, String longitude, String accuracy, String timestamp) {
        this.tripId = tripId;
        this.speed = speed;
        this.altitude = altitude;
        this.heading = heading;
        this.latitude = latitude;
        this.longitude = longitude;
        this.accuracy = accuracy;
        this.timestamp = timestamp;
    }

    public String getTripId() {
        return tripId;
    }

    public void setTripId(String tripId) {
        this.tripId = tripId;
    }

    public String getSpeed() {
        return speed;
    }

    public void setSpeed(String speed) {
        this.speed = speed;
    }

    public String getAltitude() {
        return altitude;
    }

    public void setAltitude(String altitude) {
        this.altitude = altitude;
    }

    public String getHeading() {
        return heading;
    }

    public void setHeading(String heading) {
        this.heading = heading;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(String accuracy) {
        this.accuracy = accuracy;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "{" +
                "tripId='" + tripId + '\'' +
                ", speed='" + speed + '\'' +
                ", altitude='" + altitude + '\'' +
                ", heading='" + heading + '\'' +
                ", latitude='" + latitude + '\'' +
                ", longitude='" + longitude + '\'' +
                ", accuracy='" + accuracy + '\'' +
                ", timestamp='" + timestamp + '\'' +
                '}';
    }
}
