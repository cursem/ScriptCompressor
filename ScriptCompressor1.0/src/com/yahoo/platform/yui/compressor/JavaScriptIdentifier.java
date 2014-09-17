/*
 * YUI Compressor
 * http://developer.yahoo.com/yui/compressor/
 * Author: Julien Lecomte -  http://www.julienlecomte.net/
 * Copyright (c) 2011 Yahoo! Inc.  All rights reserved.
 * The copyrights embodied in the content of this file are licensed
 * by Yahoo! Inc. under the BSD (revised) open source license.
 */
package com.yahoo.platform.yui.compressor;

import org.mozilla.javascript.Token;

/**
 * JavaScriptIdentifier represents a variable/function identifier.
 */
class JavaScriptIdentifier extends JavaScriptToken {

	private int refcount = 0;
	private String mungedValue;
	private ScriptOrFnScope declaredScope;
	private boolean markedForMunging = true; // 此变量用来标识成员变量value（继承自JavaScriptToken）是否可以混淆

	private boolean normalVar = false; // 为true说明这是一个普通变量。如，不是函数参数，不是
										// for(var i in object){}里的变量等等

	public boolean isNormalVar() {
		return normalVar;
	}

	JavaScriptIdentifier(String value, ScriptOrFnScope declaredScope,
			boolean normalVar) {
		super(Token.NAME, value);
		this.declaredScope = declaredScope;
		this.normalVar = normalVar;
	}

	ScriptOrFnScope getDeclaredScope() {
		return declaredScope;
	}

	void setMungedValue(String value) {
		mungedValue = value;
	}

	String getMungedValue() {
		return mungedValue;
	}

	void preventMunging() {
		markedForMunging = false;
	}

	boolean isMarkedForMunging() {
		return markedForMunging;
	}

	void incrementRefcount() {
		refcount++;
	}

	int getRefcount() {
		return refcount;
	}
}
