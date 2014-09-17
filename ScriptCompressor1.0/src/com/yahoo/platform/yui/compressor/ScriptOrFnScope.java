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
	private ArrayList<ScriptOrFnScope> subScopes; // ����������������
	private Hashtable<String, JavaScriptIdentifier> identifiers = new Hashtable<String, JavaScriptIdentifier>();
	private Hashtable<String, String> hints = new Hashtable<String, String>();
	private boolean markedForMunging = true;
	private int varcount = 0;

	ScriptOrFnScope(int braceNesting, ScriptOrFnScope parentScope) {
		this.braceNesting = braceNesting;
		this.parentScope = parentScope;
		this.subScopes = new ArrayList<ScriptOrFnScope>();
		if (parentScope != null) {
			parentScope.subScopes.add(this); // ���Լ����浽�ϼ�����б���
		}
	}

	int getBraceNesting() {
		return braceNesting;
	}

	ScriptOrFnScope getParentScope() {
		return parentScope;
	}

	JavaScriptIdentifier declareIdentifier(String symbol, boolean normalVar) { // ������������ı�ʶ��
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
			if (mungedValue == null) { // mungedValueΪnull��˵��û�б����������Է���ԭ����ֵ
				mungedValue = identifier.getValue();
			}
			result.add(mungedValue);
		}
		return result;
	}

	private ArrayList<String> getAllUsedSymbols() { // �����JavascriptCompressor.ones,twos,threes�г��ֵ��ַ�
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

		int pickFromSet = 1; // pickFromSet��Ӧ��ones,twos,threes

		// ������ȫ�ֱ���
		if (parentScope != null) { // �ϼ���Ϊ�գ�˵������ȫ����ȫ����ı������ܻᱻHTMLӦ�ã����Բ����л�����

			ArrayList<String> freeSymbols = new ArrayList<String>();

			freeSymbols.addAll(JavaScriptCompressor.ones); // ���������������ַ�(����)
			freeSymbols.removeAll(getAllUsedSymbols());// ȥ����ʹ�õ�symbols������ǰ����ϼ����(�������Ҳ����Ϊȥ������)
														// ��70��73�п�֪
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
				throw new IllegalStateException("������ʹ���������б��ñ���. ֹͣ����...");
			}

			Enumeration<JavaScriptIdentifier> elements = identifiers.elements(); // �г���ǰ�������еı�ʶ��
			while (elements.hasMoreElements()) {
				if (freeSymbols.size() == 0) { // �жϵ�ǰ�ĵ�ǰ�ı��ñ�ʶ���Ƿ��Ѿ�ʹ����
												// ��Ϊÿ����һ�����ʹ�freeSymbols��ɾ��һ��������ÿ��ѭ����Ҫ���
					pickFromSet++;
					if (pickFromSet == 2) {
						freeSymbols.addAll(JavaScriptCompressor.twos);
					} else if (pickFromSet == 3) {
						freeSymbols.addAll(JavaScriptCompressor.threes);
					} else {
						throw new IllegalStateException(
								"������ʹ���������б��ñ���. ֹͣ����...");
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
					continue;// mungedValue��Ϊ�գ�˵��ǰ���Ѿ�ʹ�����ֶ�����
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
				doc.insertString(doc.getLength(), "ͬһ�����������뾡��ʹ��һ��var��������\r\n\t"
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
			if (identifier.getMungedValue() == null) { // ȫ�ֱ������п���Ϊ��
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
