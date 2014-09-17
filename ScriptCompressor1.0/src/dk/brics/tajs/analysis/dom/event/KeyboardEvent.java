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

package dk.brics.tajs.analysis.dom.event;

import dk.brics.tajs.analysis.Conversion;
import dk.brics.tajs.analysis.FunctionCalls;
import dk.brics.tajs.analysis.NativeFunctions;
import dk.brics.tajs.analysis.Solver;
import dk.brics.tajs.analysis.State;
import dk.brics.tajs.analysis.dom.DOMObjects;
import dk.brics.tajs.analysis.dom.DOMRegistry;
import dk.brics.tajs.lattice.ObjectLabel;
import dk.brics.tajs.lattice.Value;

import static dk.brics.tajs.analysis.dom.DOMFunctions.createDOMFunction;
import static dk.brics.tajs.analysis.dom.DOMFunctions.createDOMProperty;

/**
 * The KeyboardEvent interface provides specific contextual information
 * associated with keyboard devices. Each keyboard event references a key using
 * an identifier. Keyboard events are commonly directed at the element that has
 * the focus.
 */
public class KeyboardEvent {

    public static ObjectLabel PROTOTYPE;
    public static ObjectLabel INSTANCES;

    public static void build(State s) {
        PROTOTYPE = new ObjectLabel(DOMObjects.KEYBOARD_EVENT_PROTOTYPE, ObjectLabel.Kind.OBJECT);
        INSTANCES = new ObjectLabel(DOMObjects.KEYBOARD_EVENT_INSTANCES, ObjectLabel.Kind.OBJECT);

        // Prototype object
        s.newObject(PROTOTYPE);
        s.writeInternalPrototype(PROTOTYPE, Value.makeObject(UIEvent.PROTOTYPE));

        // Multiplied object
        s.newObject(INSTANCES);
        s.writeInternalPrototype(INSTANCES, Value.makeObject(PROTOTYPE));

        /*
         * Constants (KeyLocation code)
         */
        createDOMProperty(s, PROTOTYPE, "DOM_KEY_LOCATION_STANDARD", Value.makeNum(0));
        createDOMProperty(s, PROTOTYPE, "DOM_KEY_LOCATION_LEFT", Value.makeNum(1));
        createDOMProperty(s, PROTOTYPE, "DOM_KEY_LOCATION_RIGHT", Value.makeNum(2));
        createDOMProperty(s, PROTOTYPE, "DOM_KEY_LOCATION_NUMPAD", Value.makeNum(3));
        createDOMProperty(s, PROTOTYPE, "DOM_KEY_LOCATION_MOBILE", Value.makeNum(4));
        createDOMProperty(s, PROTOTYPE, "DOM_KEY_LOCATION_JOYSTICK", Value.makeNum(5));

        /*
         * Properties
         */
        createDOMProperty(s, INSTANCES, "keyIdentifier", Value.makeAnyStr().setReadOnly());
        createDOMProperty(s, INSTANCES, "keyLocation", Value.makeAnyNumUInt().setReadOnly());
        createDOMProperty(s, INSTANCES, "ctrlKey", Value.makeAnyBool().setReadOnly());
        createDOMProperty(s, INSTANCES, "shiftKey", Value.makeAnyBool().setReadOnly());
        createDOMProperty(s, INSTANCES, "altKey", Value.makeAnyBool().setReadOnly());
        createDOMProperty(s, INSTANCES, "metaKey", Value.makeAnyBool().setReadOnly());
        createDOMProperty(s, INSTANCES, "repeat", Value.makeAnyBool().setReadOnly());

        // DOM LEVEL 0:
        createDOMProperty(s, PROTOTYPE, "charCode", Value.makeAnyNumUInt());
        createDOMProperty(s, PROTOTYPE, "key", Value.makeAnyNumUInt());
        createDOMProperty(s, PROTOTYPE, "keyCode", Value.makeAnyNumUInt());

        /*
         * Functions
         */

        // DOM Level 3:
        createDOMFunction(s, PROTOTYPE, DOMObjects.KEYBOARD_EVENT_GET_MODIFIER_STATE, "getModifierState", 1);
        createDOMFunction(s, PROTOTYPE, DOMObjects.KEYBOARD_EVENT_INIT_KEYBOARD_EVENT, "initKeyboardEvent", 7);
        createDOMFunction(s, PROTOTYPE, DOMObjects.KEYBOARD_EVENT_INIT_KEYBOARD_EVENT_NS, "initKeyboardEventNS", 8);

        // Multiplied object
        s.multiplyObject(INSTANCES);
        INSTANCES = INSTANCES.makeSingleton().makeSummary();

        // DOM Registry
        DOMRegistry.registerKeyboardEventLabel(INSTANCES);
    }

    /*
     * Transfer functions
     */

    public static Value evaluate(DOMObjects nativeObject, FunctionCalls.CallInfo call, State s, Solver.SolverInterface c) {
        switch (nativeObject) {
            case KEYBOARD_EVENT_GET_MODIFIER_STATE: {
                NativeFunctions.expectParameters(nativeObject, call, c, 1, 1);
                /* Value keyIdentifierArg =*/ Conversion.toString(NativeFunctions.readParameter(call, s, 0), c);
                return Value.makeAnyBool();
            }
            case KEYBOARD_EVENT_INIT_KEYBOARD_EVENT: {
                NativeFunctions.expectParameters(nativeObject, call, c, 7, 7);
                /* Value typeArg =*/ Conversion.toString(NativeFunctions.readParameter(call, s, 0), c);
                /* Value canBubbleArg =*/ Conversion.toBoolean(NativeFunctions.readParameter(call, s, 1));
                /* Value cancelableArg =*/ Conversion.toBoolean(NativeFunctions.readParameter(call, s, 2));
                // viewArg not checked...
                /* Value keyIdentifierArg =*/ Conversion.toString(NativeFunctions.readParameter(call, s, 4), c);
                /* Value keyLocationArg =*/ Conversion.toNumber(NativeFunctions.readParameter(call, s, 5), c);
                /* Value modifiersListArg =*/ Conversion.toString(NativeFunctions.readParameter(call, s, 6), c);
                return Value.makeUndef();
            }
            case KEYBOARD_EVENT_INIT_KEYBOARD_EVENT_NS: {
                throw new UnsupportedOperationException("KeyboardEvent.initKeyboardEventNS not supported!");
            }
            default: {
                throw new UnsupportedOperationException("Unsupported Native Object");
            }
        }
    }

}
