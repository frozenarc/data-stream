package org.frozenarc.datastream.xml;

import javax.xml.stream.events.XMLEvent;
import java.util.Stack;

/**
 * Author: mpanchal
 * Date: 2022-12-01 07:07
 */
class StackManager {

    private final Stack<String> stack;
    //private String endedElement;

    public StackManager() {
        stack = new Stack<>();
    }

    public void manage(int event, String elementName) {
        if (event == XMLEvent.START_ELEMENT) {
            //endedElement = null;
            stack.push(elementName);
        }
        if (event == XMLEvent.END_ELEMENT) {
            //endedElement = null;
            if (stack.peek().equals(elementName)) {
                //endedElement = elementName;
                stack.pop();
            } else {
                throw new IllegalStateException("Stack state is illegal, Current stack: " + stack + ", Current event: " + XMLEventUtil.getEventString(event) + ", Element: " + elementName);
            }
        }
    }

    public int getDepth() {
        return stack.size() - 1;
    }

    public boolean isStackEmpty() {
        return getDepth() == -1;
    }

    public String getCurrentPath() {
        StringBuilder path = new StringBuilder();
        for (String ele : stack) {
            path.append("/").append(ele);
        }
        return path.toString(); // + (endedElement == null ? "" : ("/" + endedElement));
    }
}
