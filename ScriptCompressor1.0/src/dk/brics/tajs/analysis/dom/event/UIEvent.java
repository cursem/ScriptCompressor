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

import dk.brics.tajs.analysis.*;
import dk.brics.tajs.analysis.dom.DOMObjects;
import dk.brics.tajs.analysis.dom.DOMWindow;
import dk.brics.tajs.lattice.ObjectLabel;
import dk.brics.tajs.lattice.Value;

import static dk.brics.tajs.analysis.dom.DOMFunctions.createDOMFunction;
import static dk.brics.tajs.analysis.dom.DOMFunctions.createDOMProperty;

/**
 * The UIEvent interface provides specific contextual information associated
 * with User Interface events.
 */
public class UIEvent {

    public static ObjectLabel PROTOTYPE;
    public static ObjectLabel INSTANCES;

    public static void build(State s) {
        PROTOTYPE = new ObjectLabel(DOMObjects.UI_EVENT_PROTOTYPE, ObjectLabel.Kind.OBJECT);
        INSTANCES = new ObjectLabel(DOMObjects.UI_EVENT_INSTANCES, ObjectLabel.Kind.OBJECT);

        // Prototype object.
        s.newObject(PROTOTYPE);
        s.writeInternalPrototype(PROTOTYPE, Value.makeObject(Event.PROTOTYPE));

        // Multiplied object.
        s.newObject(INSTANCES);
        s.writeInternalPrototype(INSTANCES, Value.makeObject(PROTOTYPE));

        /*
         * Properties.
         */
        createDOMProperty(s, PROTOTYPE, "detail", Value.makeAnyNum().setReadOnly());
        createDOMProperty(s, PROTOTYPE, "view", Value.makeObject(DOMWindow.WINDOW).setReadOnly());

        /*
         * Functions.
         */
        createDOMFunction(s, PROTOTYPE, DOMObjects.UI_EVENT_INIT_UI_EVENT, "initUIEvent", 5);
    }

    public static Value evaluate(DOMObjects nativeObject, FunctionCalls.CallInfo call, State s, Solver.SolverInterface c) {
        switch (nativeObject) {
            case UI_EVENT_INIT_UI_EVENT: {
                NativeFunctions.expectParameters(nativeObject, call, c, 5, 5);
                /* Value typeArg =*/ Conversion.toString(NativeFunctions.readParameter(call, s, 0), c);
                /* Value canBubble =*/ Conversion.toBoolean(NativeFunctions.readParameter(call, s, 1));
                /* Value cancelableArg =*/ Conversion.toBoolean(NativeFunctions.readParameter(call, s, 2));
                // View arg not modelled...
                /* Value detailArg =*/ Conversion.toNumber(NativeFunctions.readParameter(call, s, 4), c);
                return Value.makeUndef();
            }
            default:
                throw new UnsupportedOperationException("Unsupported Native Object: "
                        + nativeObject);
        }
    }
}
