/*
 * Copyright 2009-2013 Aarhus University
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dk.brics.tajs.analysis.dom;

import java.util.Collection;
import java.util.Set;

import dk.brics.tajs.analysis.State;
import dk.brics.tajs.htmlparser.DOMEventHelpers;
import dk.brics.tajs.lattice.ObjectLabel;
import dk.brics.tajs.lattice.Value;
import dk.brics.tajs.util.Collections;

public class DOMEvents {

    /**
     * Create generic Keyboard Event.
     */
    public static Value createAnyKeyboardEvent() {
        return Value.makeObject(DOMRegistry.getKeyboardEventLabel());
    }

    /**
     * Create generic Mouse Event.
     */
    public static Value createAnyMouseEvent() {
        return Value.makeObject(DOMRegistry.getMouseEventLabel());
    }

    /**
     * Creates a generic AJAX Event.
     */
    public static Value createAnyAjaxEvent() {
        return Value.makeObject(DOMRegistry.getAjaxEventLabel());
    }

    /**
     * Create a generic non-mouse, non-keyboard Event.
     */
    public static Value createAnyEvent() {
        Set<ObjectLabel> labels = Collections.newSet();
        labels.add(DOMRegistry.getKeyboardEventLabel());
        labels.add(DOMRegistry.getMouseEventLabel());
        labels.add(DOMRegistry.getMutationEventLabel());
        labels.add(DOMRegistry.getWheelEventLabel());
        return Value.makeObject(labels);
    }

    /**
     * Add Event Handler. (NOT Timeout Event Handlers.)
     */
    public static void addEventHandler(State s, String property, Value v) {
        if (DOMEventHelpers.isKeyboardEventAttribute(property) || DOMEventHelpers.isKeyboardEventProperty(property)) {
            addKeyboardEventHandler(s, v.getObjectLabels());
        } else if (DOMEventHelpers.isMouseEventAttribute(property) || DOMEventHelpers.isMouseEventProperty(property)) {
            addMouseEventHandler(s, v.getObjectLabels());
        } else if (DOMEventHelpers.isLoadEventAttribute(property)) {
            addLoadEventHandler(s, v.getObjectLabels());
        } else if (DOMEventHelpers.isUnloadEventAttribute(property)) {
            addUnloadEventHandler(s, v.getObjectLabels());
        } else {
            addUnknownEventHandler(s, v.getObjectLabels());
        }
    }

    /**
     * Add a Keyboard Event Handler.
     */
    public static void addKeyboardEventHandler(State s, Collection<ObjectLabel> labels) {
        s.getExtras().addToMaySet(DOMRegistry.MaySets.KEYBOARD_EVENT_HANDLER.name(), labels);
    }

    /**
     * Add a Mouse Event Handler.
     */
    public static void addMouseEventHandler(State s, Collection<ObjectLabel> labels) {
        s.getExtras().addToMaySet(DOMRegistry.MaySets.MOUSE_EVENT_HANDLER.name(), labels);
    }

    /**
     * Add an AJAX Event Handler.
     */
    public static void addAjaxEventHandler(State s, Collection<ObjectLabel> labels) {
        s.getExtras().addToMaySet(DOMRegistry.MaySets.AJAX_EVENT_HANDLER.name(), labels);
    }

    /**
     * Add a Timeout Event Handler.
     */
    public static void addTimeoutEventHandler(State s, Collection<ObjectLabel> labels) {
        s.getExtras().addToMaySet(DOMRegistry.MaySets.TIMEOUT_EVENT_HANDLERS.name(), labels);
    }

    /**
     * Add an Unknown Event Handler.
     */
    public static void addUnknownEventHandler(State s, Collection<ObjectLabel> labels) {
        s.getExtras().addToMaySet(DOMRegistry.MaySets.UNKNOWN_EVENT_HANDLERS.name(), labels);
    }

    /**
     * Add a Load Event Handler.
     */
    public static void addLoadEventHandler(State s, Collection<ObjectLabel> labels) {
        s.getExtras().addToMaySet(DOMRegistry.MaySets.LOAD_EVENT_HANDLER.name(), labels);
        s.getExtras().addToMustSet(DOMRegistry.MustSets.LOAD_EVENT_HANDLER.name(), labels);
    }

    /**
     * Add a Unload Event Handler.
     */
    public static void addUnloadEventHandler(State s, Collection<ObjectLabel> labels) {
        s.getExtras().addToMaySet(DOMRegistry.MaySets.UNLOAD_EVENT_HANDLERS.name(), labels);
    }

}
