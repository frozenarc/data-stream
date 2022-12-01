package org.frozenarc.datastream.xml.model;

import java.util.Optional;

/**
 * Author: mpanchal
 * Date: 2022-12-01 06:56
 */
public class XMLAttribute {

    private String name;

    private String value;

    public String getName() {
        return name;
    }

    public Optional<String> getValue() {
        return Optional.ofNullable(value);
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String toString() {
        return getValue().map(val -> getName() + "=" + "\"" + val + "\"").orElse("");
    }
}
