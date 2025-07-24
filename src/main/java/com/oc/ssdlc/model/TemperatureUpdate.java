package com.oc.ssdlc.model;



public class TemperatureUpdate {
    private String device_id;
    private String type;
    private double temperature;
    private double target_temperature;
    private long timestamp;

    public String getDevice_id() {
        return device_id;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public double getTarget_temperature() {
        return target_temperature;
    }

    public void setTarget_temperature(double target_temperature) {
        this.target_temperature = target_temperature;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
