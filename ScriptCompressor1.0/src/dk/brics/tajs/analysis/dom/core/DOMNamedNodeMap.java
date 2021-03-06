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

package dk.brics.tajs.analysis.dom.core;

import dk.brics.tajs.analysis.Conversion;
import dk.brics.tajs.analysis.FunctionCalls;
import dk.brics.tajs.analysis.InitialStateBuilder;
import dk.brics.tajs.analysis.NativeFunctions;
import dk.brics.tajs.analysis.Solver;
import dk.brics.tajs.analysis.State;
import dk.brics.tajs.analysis.dom.*;
import dk.brics.tajs.lattice.ObjectLabel;
import dk.brics.tajs.lattice.Value;
import dk.brics.tajs.util.AnalysisException;

import static dk.brics.tajs.analysis.dom.DOMFunctions.createDOMFunction;
import static dk.brics.tajs.analysis.dom.DOMFunctions.createDOMProperty;

/**
 * Objects implementing the NamedNodeMap interface are used to represent
 * collections of nodes that can be accessed by name. Note that NamedNodeMap
 * does not inherit from NodeList; NamedNodeMaps are not maintained in any
 * particular order. Objects contained in an object implementing NamedNodeMap
 * may also be accessed by an ordinal index, but this is simply to allow
 * convenient enumeration of the contents of a NamedNodeMap, and does not imply
 * that the DOM specifies an order to these Nodes.
 */
public class DOMNamedNodeMap {

    public static ObjectLabel CONSTRUCTOR;
    public static ObjectLabel PROTOTYPE;
    public static ObjectLabel INSTANCES;

    public static void build(State s) {
        CONSTRUCTOR = new ObjectLabel(DOMObjects.NAMEDNODEMAP_CONSTRUCTOR, ObjectLabel.Kind.FUNCTION);
        PROTOTYPE = new ObjectLabel(DOMObjects.NAMEDNODEMAP_PROTOTYPE, ObjectLabel.Kind.OBJECT);
        INSTANCES = new ObjectLabel(DOMObjects.NAMEDNODEMAP_INSTANCES, ObjectLabel.Kind.OBJECT);

        // Constructor Object
        s.newObject(CONSTRUCTOR);
        s.writePropertyWithAttributes(CONSTRUCTOR, "length", Value.makeNum(0).setAttributes(true, true, true));
        s.writePropertyWithAttributes(CONSTRUCTOR, "prototype", Value.makeObject(PROTOTYPE).setAttributes(true, true, true));
        s.writeInternalPrototype(CONSTRUCTOR, Value.makeObject(InitialStateBuilder.OBJECT_PROTOTYPE));
        s.writeProperty(DOMWindow.WINDOW, "NamedNodeMap", Value.makeObject(CONSTRUCTOR));

        // Prototype object.
        s.newObject(PROTOTYPE);
        s.writeInternalPrototype(PROTOTYPE, Value.makeObject(InitialStateBuilder.OBJECT_PROTOTYPE));

        // Multiplied object.
        s.newObject(INSTANCES);
        s.writeInternalPrototype(INSTANCES, Value.makeObject(PROTOTYPE));

        /*
         * Properties.
         */
        createDOMProperty(s, INSTANCES, "length", Value.makeNum(0).setReadOnly());

        s.multiplyObject(INSTANCES);
        INSTANCES = INSTANCES.makeSingleton().makeSummary();

        /*
         * Functions.
         */
        // DOM Level 1
        createDOMFunction(s, PROTOTYPE, DOMObjects.NAMEDNODEMAP_PROTOTYPE_GETNAMEDITEM, "getNamedItem", 1);
        createDOMFunction(s, PROTOTYPE, DOMObjects.NAMEDNODEMAP_PROTOTYPE_SETNAMEDITEM, "setNamedItem", 1);
        createDOMFunction(s, PROTOTYPE, DOMObjects.NAMEDNODEMAP_PROTOTYPE_REMOVENAMEDITEM, "removeNamedItem", 1);
        createDOMFunction(s, PROTOTYPE, DOMObjects.NAMEDNODEMAP_PROTOTYPE_ITEM, "item", 1);

        createDOMFunction(s, PROTOTYPE, DOMObjects.NAMEDNODEMAP_PROTOTYPE_GETNAMEDITEMNS, "getNamedItemNS", 2);
        createDOMFunction(s, PROTOTYPE, DOMObjects.NAMEDNODEMAP_PROTOTYPE_SETNAMEDITEMNS, "setNamedItemNS", 2);
        createDOMFunction(s, PROTOTYPE, DOMObjects.NAMEDNODEMAP_PROTOTYPE_REMOVEDNAMEDITEMNS, "removeNamedItemNS", 2);
    }

    /**
     * Transfer Functions.
     */
    // TODO: Figure out how to modely this correctly
    public static Value evaluate(DOMObjects nativeobject, FunctionCalls.CallInfo call, State s, Solver.SolverInterface c) {
        switch (nativeobject) {
            case NAMEDNODEMAP_PROTOTYPE_GETNAMEDITEM: {
                NativeFunctions.expectParameters(nativeobject, call, c, 1, 1);
                /* Value name =*/ Conversion.toString(NativeFunctions.readParameter(call, s, 0), c);
                return Value.makeObject(DOMNode.INSTANCES);
            }
            case NAMEDNODEMAP_PROTOTYPE_SETNAMEDITEM: {
                NativeFunctions.expectParameters(nativeobject, call, c, 1, 1);
                /* Value node =*/ DOMConversion.toNode(NativeFunctions.readParameter(call, s, 0), c);
                return Value.makeObject(DOMNode.INSTANCES);
            }
            case NAMEDNODEMAP_PROTOTYPE_REMOVENAMEDITEM: {
                NativeFunctions.expectParameters(nativeobject, call, c, 1, 1);
                /* Value name =*/ Conversion.toString(NativeFunctions.readParameter(call, s, 0), c);
                return Value.makeObject(DOMNode.INSTANCES);
            }
            case NAMEDNODEMAP_PROTOTYPE_ITEM: {
                NativeFunctions.expectParameters(nativeobject, call, c, 1, 1);
                /* Value index =*/ Conversion.toNumber(NativeFunctions.readParameter(call, s, 0), c);
                return Value.makeObject(DOMNode.INSTANCES);
            }
            case NAMEDNODEMAP_PROTOTYPE_GETNAMEDITEMNS: {
                NativeFunctions.expectParameters(nativeobject, call, c, 2, 2);
                /* Value namespaceURI =*/ Conversion.toString(NativeFunctions.readParameter(call, s, 0), c);
                /* Value localName =*/ Conversion.toString(NativeFunctions.readParameter(call, s, 1), c);
                return Value.makeObject(DOMNode.INSTANCES);
            }
            case NAMEDNODEMAP_PROTOTYPE_SETNAMEDITEMNS: {
                NativeFunctions.expectParameters(nativeobject, call, c, 1, 1);
                /* Value node =*/ DOMConversion.toNode(NativeFunctions.readParameter(call, s, 0), c);
                return Value.makeObject(DOMNode.INSTANCES);
            }
            case NAMEDNODEMAP_PROTOTYPE_REMOVEDNAMEDITEMNS: {
                NativeFunctions.expectParameters(nativeobject, call, c, 2, 2);
                /* Value namespaceURI =*/ Conversion.toString(NativeFunctions.readParameter(call, s, 0), c);
                /* Value localName =*/ Conversion.toString(NativeFunctions.readParameter(call, s, 1), c);
                return Value.makeObject(DOMNode.INSTANCES);
            }
            default: {
                throw new AnalysisException("Unsupported Native Object: " + nativeobject);
            }
        }
    }

}
