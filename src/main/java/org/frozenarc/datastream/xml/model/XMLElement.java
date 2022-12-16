package org.frozenarc.datastream.xml.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Author: mpanchal
 * Date: 2022-12-01 06:56
 * Class to represent xml element
 */
public class XMLElement {

    private String path;

    private String name;

    private String value;

    private final List<XMLAttribute> attributes;

    private final List<XMLElement> elements;

    public XMLElement() {

        attributes = new ArrayList<>();
        elements = new ArrayList<>();
    }

    @Override
    public String toString() {
        return "<" + getName() + (getAttributes().size() > 0 ? " " + toAttrString() : "") + ">"
               + getValue().orElseGet(() -> getElements().stream()
                                                         .map(XMLElement::toString)
                                                         .reduce("",
                                                                 (ele, e) -> ele + e))
               + "</" + getName() + ">";
    }

    public String toAttrString() {
        return getAttributes().stream()
                              .map(XMLAttribute::toString)
                              .reduce("", (attr, a) -> attr + (attr.equals("") ? "" : " ") + a);
    }

    @SuppressWarnings("unused")
    public Optional<String> getPath() {
        return Optional.ofNullable(path);
    }

    public String getName() {
        return name;
    }

    public Optional<String> getValue() {
        return Optional.ofNullable(value);
    }

    public List<XMLAttribute> getAttributes() {
        return attributes;
    }

    public List<XMLElement> getElements() {
        return elements;
    }

    @SuppressWarnings("unused")
    public void setPath(String path) {
        this.path = path;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void addAttribute(XMLAttribute attribute) {
        this.attributes.add(attribute);
    }

    public void addElement(XMLElement element) {
        this.elements.add(element);
    }
}
