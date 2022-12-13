package org.frozenarc.datastream.json;

import com.fasterxml.jackson.core.JsonToken;

import java.util.Stack;

/**
 * Author: mpanchal
 * Date: 2022-12-01 06:43
 */
class StackManager {

    private final Stack<JsonToken> stack;
    private final int workingDepth;

    public StackManager(int workingDepth) {
        stack = new Stack<>();
        this.workingDepth = workingDepth;
    }

    public void manage(JsonToken token) {
        if (token == JsonToken.START_OBJECT) {
            stack.push(token);
        }
        if (token == JsonToken.END_OBJECT) {
            if (stack.peek() == JsonToken.START_OBJECT) {
                stack.pop();
            } else {
                throw new IllegalStateException("Stack state is illegal, Current stack: " + stack + ", Current token: " + token);
            }
        }
        if (token == JsonToken.START_ARRAY) {
            stack.push(token);
        }
        if (token == JsonToken.END_ARRAY) {
            if (stack.peek() == JsonToken.START_ARRAY) {
                stack.pop();
            } else {
                throw new IllegalStateException("Stack state is illegal, Current stack: " + stack + ", Current token: " + token);
            }
        }
    }

    public int getDepth() {
        return stack.size() - 1;
    }

    public boolean isStackEmpty() {
        return getDepth() == -1;
    }

    public boolean isOnWorkingDepth() {
        return getDepth() == getWorkingDepth();
    }

    public boolean isLessThanWorkingDepthBy(int diff) {
        return getDepth() == getWorkingDepth() - diff;
    }

    public int getWorkingDepth() {
        return workingDepth;
    }
}
