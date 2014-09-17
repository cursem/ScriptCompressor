/*
 * YUI Compressor
 * http://developer.yahoo.com/yui/compressor/
 * Author: Julien Lecomte -  http://www.julienlecomte.net/
 * Copyright (c) 2011 Yahoo! Inc.  All rights reserved.
 * The copyrights embodied in the content of this file are licensed
 * by Yahoo! Inc. under the BSD (revised) open source license.
 */
package com.yahoo.platform.yui.compressor;

import gdut.visual.gui.AnalyseWarnPanel;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

class ScriptOrFnScope {

	private int braceNesting;
	private ScriptOrFnScope parentScope;
	private ArrayList<ScriptOrFnScope> subScopes; // 用来保存子作用域
	private Hashtable<String, JavaScriptIdentifier> identifiers = new Hashtable<String, JavaScriptIdentifier>();
	private Hashtable<String, String> hints = new Hashtable<String, String>();
	private boolean markedForMunging = true;
	private int varcount = 0;

	ScriptOrFnScope(int braceNesting, ScriptOrFnScope parentScope) {
		this.braceNesting = braceNesting;
		this.parentScope = parentScope;
		this.subScopes = new ArrayList<ScriptOrFnScope>();
		if (parentScope != null) {
			parentScope.subScopes.add(this); // 将自己保存到上级域的列表中
		}
	}

	int getBraceNesting() {
		return braceNesting;
	}

	ScriptOrFnScope getParentScope() {
		return parentScope;
	}

	JavaScriptIdentifier declareIdentifier(String symbol, boolean normalVar) { // 声明本作用域的标识符
		JavaScriptIdentifier identifier = identifiers.get(symbol);
		if (identifier == null) {
			identifier = new JavaScriptIdentifier(symbol, this, normalVar);
			identifiers.put(symbol, identifier);
		}
		return identifier;
	}

	JavaScriptIdentifier getIdentifier(String symbol) {
		return identifiers.get(symbol);
	}

	public Hashtable<String, JavaScriptIdentifier> getIdentifiers() {
		return identifiers;
	}

	void addHint(String variableName, String variableType) {
		hints.put(variableName, variableType);
	}

	void preventMunging() {
		if (parentScope != null) {
			markedForMunging = false;
		}
	}

	private ArrayList<String> getUsedSymbols() {
		ArrayList<String> result = new ArrayList<String>();
		Enumeration<JavaScriptIdentifier> elements = identifiers.elements();
		while (elements.hasMoreElements()) {
			JavaScriptIdentifier identifier = elements.nextElement();
			String mungedValue = identifier.getMungedValue();
			if (mungedValue == null) { // mungedValue为null，说明没有被混淆，所以返回原来的值
				mungedValue = identifier.getValue();
			}
			result.add(mungedValue);
		}
		return result;
	}

	private ArrayList<String> getAllUsedSymbols() { // 获得在JavascriptCompressor.ones,twos,threes中出现的字符
		ArrayList<String> result = new ArrayList<String>();
		ScriptOrFnScope scope = this;
		while (scope != null) {
			result.addAll(scope.getUsedSymbols());
			scope = scope.parentScope;
		}
		return result;
	}

	int incrementVarCount() {
		varcount++;
		return varcount;
	}

	public void getFullMapping(StringBuffer outBuffer, String mungedPrefix) {
		Enumeration<JavaScriptIdentifier> elements = identifiers.elements();
		while (elements.hasMoreElements()) {
			JavaScriptIdentifier identifier = elements.nextElement();
			String mungedValue = identifier.getMungedValue();
			if (mungedValue == null) {
				mungedValue = identifier.getValue();
			}
			outBuffer.append(mungedPrefix + mungedValue);
			outBuffer.append(": ");
			outBuffer.append(identifier.getValue() + "\n");
		}

		for (int i = 0; i < subScopes.size(); i++) {
			ScriptOrFnScope scope = subScopes.get(i);
			scope.getFullMapping(outBuffer, "\t" + mungedPrefix);
		}
	}

	public void getOriginalValue(Vector<Vector<String>> vt,
			Map<ScriptOrFnScope, List<String>> scopeSymbolMaping) {
		Enumeration<JavaScriptIdentifier> elements = identifiers.elements();
		List<String> symbolList = new ArrayList<String>();
		while (elements.hasMoreElements()) {
			JavaScriptIdentifier identifier = elements.nextElement();
			String originalValue = identifier.getValue();

			Vector<String> tmpVT = new Vector<String>();
			tmpVT.add(originalValue);
			tmpVT.add("null");
			vt.add(tmpVT);

			symbolList.add(originalValue);
		}
		scopeSymbolMaping.put(this, symbolList);
		for (int i = 0; i < subScopes.size(); i++) {
			ScriptOrFnScope scope = subScopes.get(i);
			scope.getOriginalValue(vt, scopeSymbolMaping);
		}
	}

	void munge() {

		if (!markedForMunging) {
			// Stop right here if this scope was flagged as unsafe for munging.
			return;
		}

		int pickFromSet = 1; // pickFromSet对应着ones,twos,threes

		// 不混淆全局变量
		if (parentScope != null) { // 上级域不为空，说明不是全局域（全局域的变量可能会被HTML应用，所以不进行混淆）

			ArrayList<String> freeSymbols = new ArrayList<String>();

			freeSymbols.addAll(JavaScriptCompressor.ones); // 加入用来混淆的字符(单个)
			freeSymbols.removeAll(getAllUsedSymbols());// 去除已使用的symbols包括当前域和上级域的(混淆后的也会作为去除对象)
														// 由70～73行可知
			if (freeSymbols.size() == 0) {
				pickFromSet = 2;
				freeSymbols.addAll(JavaScriptCompressor.twos);
				freeSymbols.removeAll(getAllUsedSymbols());
			}
			if (freeSymbols.size() == 0) {
				pickFromSet = 3;
				freeSymbols.addAll(JavaScriptCompressor.threes);
				freeSymbols.removeAll(getAllUsedSymbols());
			}
			if (freeSymbols.size() == 0) {
				throw new IllegalStateException("混淆器使用完了所有备用变量. 停止工作...");
			}

			Enumeration<JavaScriptIdentifier> elements = identifiers.elements(); // 列出当前域中所有的标识符
			while (elements.hasMoreElements()) {
				if (freeSymbols.size() == 0) { // 判断当前的当前的备用标识符是否已经使用完
												// 因为每混淆一个，就从freeSymbols中删除一个，所以每趟循环都要检查
					pickFromSet++;
					if (pickFromSet == 2) {
						freeSymbols.addAll(JavaScriptCompressor.twos);
					} else if (pickFromSet == 3) {
						freeSymbols.addAll(JavaScriptCompressor.threes);
					} else {
						throw new IllegalStateException(
								"混淆器使用完了所有备用变量. 停止工作...");
					}
					// It is essential to remove the symbols already used in
					// the containing scopes, or some of the variables declared
					// in the containing scopes will be redeclared, which can
					// lead to errors.
					freeSymbols.removeAll(getAllUsedSymbols());
				}

				String mungedValue;
				JavaScriptIdentifier identifier = elements.nextElement();
				if (identifier.getMungedValue() != null) {
					continue;// mungedValue不为空，说明前面已经使用了手动混淆
				}
				if (identifier.isMarkedForMunging()) {

					mungedValue = (String) freeSymbols.remove(0);
				} else {
					mungedValue = identifier.getValue();
				}
				identifier.setMungedValue(mungedValue);
			}

		}

		for (int i = 0; i < subScopes.size(); i++) {
			ScriptOrFnScope scope = subScopes.get(i);
			scope.munge();
		}
	}

	void warning() {
		if (varcount > 1) {
			List<String> symbols = new ArrayList<String>();
			Enumeration<JavaScriptIdentifier> elements = identifiers.elements();
			while (elements.hasMoreElements()) {
				JavaScriptIdentifier identifier = elements.nextElement();
				if (identifier.isNormalVar()) {
					symbols.add(identifier.getValue());
				}
			}
			SimpleAttributeSet attrSet = new SimpleAttributeSet();
			StyleConstants.setForeground(attrSet, Color.RED);
			Document doc = AnalyseWarnPanel.warnTextPane.getDocument();
			try {
				doc.insertString(doc.getLength(), "[WARNING] ", attrSet);
				doc.insertString(doc.getLength(), "同一个作用域中请尽量使用一个var声明变量\r\n\t"
						+ symbols + "\r\n\r\n", null);
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
		}

		for (int i = 0; i < subScopes.size(); i++) {
			ScriptOrFnScope nextScope = subScopes.get(i);
			nextScope.warning();
		}
	}

	void getAllOriginalValue(List<String> usedSymbols) {
		Enumeration<JavaScriptIdentifier> elements = identifiers.elements();
		while (elements.hasMoreElements()) {
			JavaScriptIdentifier identifier = elements.nextElement();
			usedSymbols.add(identifier.getValue());
		}
		for (int i = 0; i < subScopes.size(); i++) {
			ScriptOrFnScope nextScope = subScopes.get(i);
			nextScope.getAllOriginalValue(usedSymbols);
		}
	}

	void getAllMungeValue(List<String> usedSymbols) {
		Enumeration<JavaScriptIdentifier> elements = identifiers.elements();
		while (elements.hasMoreElements()) {
			JavaScriptIdentifier identifier = elements.nextElement();
			if (identifier.getMungedValue() == null) { // 全局变量就有可能为空
				usedSymbols.add(identifier.getValue());
			}
			usedSymbols.add(identifier.getMungedValue());
		}
		for (int i = 0; i < subScopes.size(); i++) {
			ScriptOrFnScope nextScope = subScopes.get(i);
			nextScope.getAllMungeValue(usedSymbols);
		}
	}
}
