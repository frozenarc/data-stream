package org.frozenarc.datastream.xml;

import org.frozenarc.datastream.DataStream;
import org.frozenarc.datastream.DataStreamException;
import org.frozenarc.datastream.xml.model.XMLAttribute;
import org.frozenarc.datastream.xml.model.XMLElement;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;
import java.io.InputStream;
import java.util.List;

/**
 * Author: mpanchal
 * Date: 2022-12-01 07:05
 * Class to handle xml data stream
 */
public class XMLStream implements DataStream<XMLElement> {

    private final XMLStreamReader reader;
    private final List<String> xPaths;

    private final StackManager manager;

    private int event;
    private boolean nextRead = true;
    private boolean hasNextEvent = false;

    public XMLStream(InputStream inputStream, List<String> xPaths) throws XMLStreamException {
        this.manager = new StackManager();
        this.xPaths = xPaths;
        this.reader = XMLInputFactory.newFactory().createXMLStreamReader(inputStream);
    }

    /**
     * to be used to check whether next node is available or not
     * @return true if node available to be read or false
     * @throws DataStreamException
     */
    @Override
    public boolean hasNext() throws DataStreamException {
        try {
            if (nextRead) {
                nextRead = false;
                hasNextEvent = false;
                while (event != XMLEvent.END_DOCUMENT) {
                    if (event == XMLEvent.START_ELEMENT
                        && xPaths.stream()
                                 .map(xPath -> xPath.endsWith("/@") ? xPath.substring(0, xPath.indexOf("/@")) : xPath)
                                 .anyMatch(xPath -> manager.getCurrentPath().equals(xPath))) {
                        hasNextEvent = true;
                        break;
                    }
                    nextEvent();
                }
            }
            return hasNextEvent;
        } catch (XMLStreamException ex) {
            throw new DataStreamException(ex);
        }
    }

    /**
     * @return next node
     * @throws DataStreamException
     */
    @Override
    public XMLElement next() throws DataStreamException {
        try {
            if (event != XMLEvent.START_ELEMENT) {
                throw new IllegalStateException("Current event is not a START element, Event: " + XMLEventUtil.getEventString(event));
            }
            nextRead = true;
            XMLElement element = null;
            while (event != XMLEvent.END_ELEMENT) {
                if (event == XMLEvent.START_ELEMENT) {
                    if (element == null) {
                        element = new XMLElement();
                        element.setName(reader.getLocalName());
                        int attCount = reader.getAttributeCount();
                        for (int i = 0; i < attCount; i++) {
                            XMLAttribute attribute = new XMLAttribute();
                            attribute.setName(reader.getAttributeLocalName(i));
                            attribute.setValue(reader.getAttributeValue(i));
                            element.addAttribute(attribute);
                        }
                    } else {
                        element.addElement(next());
                    }
                }
                if (event == XMLEvent.CHARACTERS) {
                    if (reader.getText().trim().length() > 0) {
                        element.setValue(reader.getText());
                    }
                }
                nextEvent();
            }
            return element;
        } catch (XMLStreamException ex) {
            throw new DataStreamException(ex);
        }
    }

    private void nextEvent() throws XMLStreamException {
        int event = reader.next();
        manager.manage(event, (event == XMLEvent.START_ELEMENT
                               || event == XMLEvent.END_ELEMENT)
                              ? reader.getName().getLocalPart()
                              : "");
        this.event = event;
    }
}
