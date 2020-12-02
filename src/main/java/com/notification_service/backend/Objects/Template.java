package com.notification_service.backend.Objects;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Template {

    public int id;
    public String template;

    public Template(int id, String template) {
        this.id = id;
        this.template = template;
    }
}
