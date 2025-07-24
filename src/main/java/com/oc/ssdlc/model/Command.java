package com.oc.ssdlc.model;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Command {

    private String command;
    private String value;
    private String sender;

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }


}