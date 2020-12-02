package com.notification_service.backend.Objects;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Notification {
    public Employee employee = new Employee();
    public Customer customer = new Customer();
    public int sendTime = 0;
    public String type = "";
    int templateId = 0;
    int id = 0;
}
