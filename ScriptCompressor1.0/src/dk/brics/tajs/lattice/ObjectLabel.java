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

package dk.brics.tajs.lattice;

import java.util.Map;

import dk.brics.tajs.flowgraph.Function;
import dk.brics.tajs.flowgraph.AbstractNode;
import dk.brics.tajs.flowgraph.SourceLocation;
import dk.brics.tajs.js2flowgraph.RhinoASTToFlowgraph.SourceLocationMaker;
import dk.brics.tajs.options.Options;

/**
 * Label of abstract object.
 * Immutable.
 */
public final class ObjectLabel implements Comparable<ObjectLabel> { // TODO: canonicalize?
	
	/**
	 * Source location used for host functions.
	 */
	public static final SourceLocation initial_source = SourceLocationMaker.makeUnknown("<initial state>");
	
	/**
	 * Object kinds.
	 */
	public static enum Kind {
		OBJECT {@Override public String toString() { return "Object"; }}, 
		FUNCTION {@Override public String toString() { return "Function"; }}, 
		ARRAY {@Override public String toString() { return "Array"; }}, 
		REGEXP {@Override public String toString() { return "RegExp"; }}, 
		DATE {@Override public String toString() { return "Date"; }}, 
		STRING {@Override public String toString() { return "String"; }}, 
		NUMBER {@Override public String toString() { return "Number"; }}, 
		BOOLEAN {@Override public String toString() { return "Boolean"; }}, 
		ERROR {@Override public String toString() { return "Error"; }}, 
		MATH {@Override public String toString() { return "Math"; }}, 
		ACTIVATION {@Override public String toString() { return "activation"; }},
		ARGUMENTS {@Override public String toString() { return "arguments"; }},
	}
	
	/**
	 * If set, this abstract object represents a single concrete object.
	 * (If not set, it can represent any number of concrete objects.)
	 */
	private boolean singleton;
	
	private Kind kind; // [[Class]]
	
	private AbstractNode node; // non-null for user defined non-Function objects
	
	private HostObject hostobject; // non-null for host objects
	
	private Function function; // non-null for user defined functions
	
	/**
	 * Values of special variables at function entry in the context where the object was created, or null if none.
	 */
	private Map<String,Value> specialentryvars;

	/**
	 * Values of special variables in the context where the object was created, or null if none.
	 */
	private Map<String,Value> specialvars;

	/**
	 * Values of special registers in the context where the object was created, or null if none.
	 */
	private Map<Integer,Value> specialregs;
	

	private int hashcode;
	
	private ObjectLabel(HostObject hostobject, AbstractNode node, Function function, Kind kind) {
		this.hostobject = hostobject;
		this.node = node;
		this.function = function;
		this.kind = kind;
		if (!Options.isRecencyDisabled())
			this.singleton = true;
		hashcode = (hostobject != null ? hostobject.toString().hashCode() : 0) + 
				(function != null ? function.hashCode() : 0) +
				(node != null ? node.getIndex() : 0) +
				(singleton ? 123 : 0) +
				(specialentryvars == null ? 0 : specialentryvars.hashCode()) +
				(specialvars == null ? 0 : specialvars.hashCode()) +
				(specialregs == null ? 0 : specialregs.hashCode()) +
				kind.ordinal() * 117; // avoids using enum hashcodes
	}
	
	/**
	 * Constructs a new object label for a user defined non-function object.
	 * @see ObjectLabel#ObjectLabel(AbstractNode, Kind, Map, Map, Map)
	 */
	public ObjectLabel(AbstractNode n, Kind kind) {
		this(null, n, null, kind);
	}

	/**
	 * Constructs a new object label for a user defined non-function object.
	 * If {@link Options#isRecencyDisabled()} is disabled, the object label 
	 * represents a single concrete object (otherwise, it may represent any 
	 * number of concrete objects).
	 * @param specialvars special variables, or null if none
	 */
	public ObjectLabel(AbstractNode n, Kind kind, Map<String,Value> specialentryvars, Map<String,Value> specialvars, Map<Integer,Value> specialregs) {
		this(null, n, null, kind);
		if(Options.isContextSensitiveHeapEnabled()){
			this.specialentryvars = specialentryvars;
			this.specialvars = specialvars;
			this.specialregs = specialregs;
		}
	}

	/**
	 * Constructs a new object label for a user defined function object.
	 * @see ObjectLabel#ObjectLabel(Function, Map, Map, Map)
	 */
	public ObjectLabel(Function f) {
		this(null, null, f, Kind.FUNCTION);
	}
	
	/**
	 * Constructs a new object label for a user defined function object.
	 * If {@link Options#isRecencyDisabled()} is disabled, the object label 
	 * represents a single concrete object (otherwise, it may represent any 
	 * number of concrete objects).
	 * @param specialvars special variables, or null if none
	 */
	public ObjectLabel(Function f, Map<String,Value> specialentryvars, Map<String,Value> specialvars, Map<Integer,Value> specialregs) {
		this(null, null, f, Kind.FUNCTION);
		if (Options.isContextSensitiveHeapEnabled()) {
			this.specialentryvars = specialentryvars;
			this.specialvars = specialvars;
			this.specialregs = specialregs;
		}
	}
	
	/**
	 * Constructs a new object label for a host object. 
	 * If {@link Options#isRecencyDisabled()} is disabled, the object label 
	 * represents a single concrete object (otherwise, it may represent any 
	 * number of concrete objects).
	 */
	public ObjectLabel(HostObject hostobject, Kind kind) {
		this(hostobject, null, null, kind);
	}
	
	/**
	 * Returns the object label kind.
	 */
	public Kind getKind() {
		return kind;
	}
	
	/**
	 * Returns the source location.
	 */
	public SourceLocation getSourceLocation() {
		if (node != null)
			return node.getSourceLocation();
		else if (function != null)
			return function.getSourceLocation();
		else
			return initial_source;
	}	
	
	/**
	 * Returns the node of this non-function object label.
	 * Fails if this is a user-defined function.
	 */
	public AbstractNode getNode() {
		return node;
	}

	/**
	 * Returns true if this object label represents a host object.
	 */
	public boolean isHostObject() {
		return hostobject != null;
	}

	/**
	 * Returns the descriptor for this object label.
	 * It is assumed that this object label represents a host function.
	 */
	public HostObject getHostObject() {
		return hostobject;
	}
	
	/**
	 * Returns true if this object label definitely represents a single concrete object.
	 */
	public boolean isSingleton() {
		return singleton;
	}
	
	/**
	 * Returns the function of this function object label.
	 * Fails if this is not a user-defined function.
	 */
	public Function getFunction() {
		if (function == null)
			throw new IllegalArgumentException("Non-Function object label: " + this);
		return function;
	}
	
	/**
	 * Returns the values for the special entry variables of this object label, or null if none.
	 */
	public Map<String,Value> getSpecialEntryVars() {
		return specialentryvars;
	}
	
	/**
	 * Returns the values for the special variables of this object label, or null if none.
	 */
	public Map<String,Value> getSpecialVars() {
		return specialvars;
	}
	
	/**
	 * Returns the values for the special registers of this object label, or null if none.
	 */
	public Map<Integer,Value> getSpecialRegisters() {
		return specialregs;
	}
	
	/**
	 * Returns the summary object label associated with this singleton object label.
	 */
	public ObjectLabel makeSummary() {
		if (!singleton && !Options.isRecencyDisabled())
			throw new IllegalStateException("attempt to obtain summary of non-singleton");
		ObjectLabel obj =  new ObjectLabel(hostobject, node, function, kind);
		obj.specialentryvars = specialentryvars;
		obj.specialvars = specialvars;
		obj.specialregs = specialregs;
		obj.singleton = false;
		return obj;
	}

	/**
	 * Returns the singleton object label associated with this object label, or this object if that is singleton.
	 */
	public ObjectLabel makeSingleton() {
		if (singleton)
			return this;
		ObjectLabel obj = new ObjectLabel(hostobject, node, function, kind);
		obj.specialentryvars = specialentryvars;
		obj.specialvars = specialvars;
		obj.specialregs = specialregs;
		return obj;
	}

	/**
	 * Produces a string representation of this object label.
	 */
	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		if (singleton)
			b.append('@');
		else
			b.append('*');
		if (function != null) {
			String f = function.getName();
			if (f == null)
				f = "<anonymous>";
			b.append(f).append("#fun").append(function.getIndex());
		} else if (hostobject != null) 
			b.append(hostobject).append('[').append(hostobject.getAPI().getShortName()).append(']');
		else
			b.append(describe()).append("#node").append(node.getIndex());
		if (specialentryvars != null)
			b.append(specialentryvars);
		if (specialvars != null)
			b.append(specialvars);
		if (specialregs != null)
			b.append(specialregs);
		return b.toString();
	}
	
	/**
	 * Produces a human readable description of this object label.
	 */
	public String describe() {
		if (hostobject != null)
			return "Primitive";
		else if (function != null)
			return "Function";
		else
			return kind.toString();
	}

	/**
	 * Checks whether the given object label is equal to this one.
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		if (!(obj instanceof ObjectLabel))
			return false;
		ObjectLabel x = (ObjectLabel) obj;
		if ((hostobject == null) != (x.hostobject == null))
			return false;
		if ((specialentryvars == null) != (x.specialentryvars == null))
			return false;
		if (specialentryvars != null && !specialentryvars.equals(x.specialentryvars)) // using collection equality
			return false;
		if ((specialvars == null) != (x.specialvars == null))
			return false;
		if (specialvars != null && !specialvars.equals(x.specialvars)) // using collection equality
			return false;
		if ((specialregs == null) != (x.specialregs == null))
			return false;
		if (specialregs != null && !specialregs.equals(x.specialregs)) // using collection equality
			return false;
		return (hostobject == null || hostobject.equals(x.hostobject)) && 
		function == x.function && node == x.node &&
		singleton == x.singleton && kind == x.kind; 
	}

	/**
	 * Returns the hash code for this object label.
	 */
	@Override
	public int hashCode() {
		return hashcode;
	}

	/**
	 * Compares this and the given object label.
	 * The source location is used as primary key, and toString is used as secondary key.
	 */
	@Override
	public int compareTo(ObjectLabel objlabel) {
		int c = getSourceLocation().compareTo(objlabel.getSourceLocation());
		if (c != 0)
			return c;
		if (equals(objlabel))
			return 0;
		return toString().compareTo(objlabel.toString());
	}
}
