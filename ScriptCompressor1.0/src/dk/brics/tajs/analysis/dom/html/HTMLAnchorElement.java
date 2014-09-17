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

package dk.brics.tajs.analysis.dom.html;

import dk.brics.tajs.analysis.*;
import dk.brics.tajs.analysis.dom.DOMObjects;
import dk.brics.tajs.analysis.dom.DOMWindow;
import dk.brics.tajs.lattice.ObjectLabel;
import dk.brics.tajs.lattice.Value;
import dk.brics.tajs.util.AnalysisException;

import static dk.brics.tajs.analysis.dom.DOMFunctions.createDOMFunction;
import static dk.brics.tajs.analysis.dom.DOMFunctions.createDOMProperty;

/**
 * The anchor element. See the A element definition in HTML 4.01
 */
public class HTMLAnchorElement {

    public static ObjectLabel CONSTRUCTOR;
    public static ObjectLabel PROTOTYPE;
    public static ObjectLabel INSTANCES;

    public static void build(State s) {
        CONSTRUCTOR = new ObjectLabel(DOMObjects.HTMLANCHORELEMENT_CONSTRUCTOR, ObjectLabel.Kind.FUNCTION);
        INSTANCES = new ObjectLabel(DOMObjects.HTMLANCHORELEMENT_INSTANCES, ObjectLabel.Kind.OBJECT);
        PROTOTYPE = new ObjectLabel(DOMObjects.HTMLANCHORELEMENT_PROTOTYPE, ObjectLabel.Kind.OBJECT);

        // Constructor Object
        s.newObject(CONSTRUCTOR);
        s.writePropertyWithAttributes(CONSTRUCTOR, "length", Value.makeNum(0).setAttributes(true, true, true));
        s.writePropertyWithAttributes(CONSTRUCTOR, "prototype", Value.makeObject(PROTOTYPE).setAttributes(true, true, true));
        s.writeInternalPrototype(CONSTRUCTOR, Value.makeObject(InitialStateBuilder.FUNCTION_PROTOTYPE));
        s.writeProperty(DOMWindow.WINDOW, "HTMLAnchorElement", Value.makeObject(CONSTRUCTOR));

        // Prototype Object
        s.newObject(PROTOTYPE);
        s.writeInternalPrototype(PROTOTYPE, Value.makeObject(HTMLElement.ELEMENT_PROTOTYPE));

        // Multiplied Object
        s.newObject(INSTANCES);
        s.writeInternalPrototype(INSTANCES, Value.makeObject(PROTOTYPE));

        /*
         * Properties.
         */
        // DOM LEVEL 1
        createDOMProperty(s, INSTANCES, "accessKey", Value.makeAnyStr());
        createDOMProperty(s, INSTANCES, "charset", Value.makeAnyStr());
        createDOMProperty(s, INSTANCES, "coords", Value.makeAnyStr());
        createDOMProperty(s, INSTANCES, "href", Value.makeAnyStr());
        createDOMProperty(s, INSTANCES, "hreflang", Value.makeAnyStr());
        createDOMProperty(s, INSTANCES, "name", Value.makeAnyStr());
        createDOMProperty(s, INSTANCES, "rel", Value.makeAnyStr());
        createDOMProperty(s, INSTANCES, "rev", Value.makeAnyStr());
        createDOMProperty(s, INSTANCES, "shape", Value.makeAnyStr());
        createDOMProperty(s, INSTANCES, "tabIndex", Value.makeAnyNum());
        createDOMProperty(s, INSTANCES, "target", Value.makeAnyStr());
        createDOMProperty(s, INSTANCES, "type", Value.makeAnyStr());

        s.multiplyObject(INSTANCES);
        INSTANCES = INSTANCES.makeSingleton().makeSummary();

        /*
         * Functions.
         */
        // DOM LEVEL 1
        // TODO: Remove, not present on this object, but on HTMLElement
        createDOMFunction(s, PROTOTYPE, DOMObjects.HTMLANCHORELEMENT_BLUR, "blur", 0);
        createDOMFunction(s, PROTOTYPE, DOMObjects.HTMLANCHORELEMENT_FOCUS, "focus", 0);
    }

    /**
     * Transfer Functions.
     */
    public static Value evaluate(DOMObjects nativeObject, FunctionCalls.CallInfo call, Solver.SolverInterface c) {
        switch (nativeObject) {
            case HTMLANCHORELEMENT_BLUR: {
                NativeFunctions.expectParameters(nativeObject, call, c, 0, 0);
                return Value.makeUndef();
            }
            case HTMLANCHORELEMENT_FOCUS: {
                NativeFunctions.expectParameters(nativeObject, call, c, 0, 0);
                return Value.makeUndef();
            }
            default: {
                throw new AnalysisException("Unsupported Native Object: " + nativeObject);
            }
        }
    }

}
