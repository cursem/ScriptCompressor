package com.yahoo.platform.yui.compressor;

import gdut.visual.gui.EditOptionPanel;
import gdut.visual.gui.LinkGUIToCompress;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomArray {

	//����5������ӵ�js�ļ��ܵ������switch�����õ��ı���
	public static String a = "_$g";
	public static String b = "_m$";
	public static String c = "_p$";
	public static String d = "_x$";
	public static String f = "__e_";
	//��������ӵ�js�ļ��еĺ�����ʹ�õ��ı���
	public static String g = "_y$";
	public static String h = "_s$";
	public static String i = "_r$";

	public static String randomArray() {

		int array[] = new int[20];
		Random random = new Random();

		for (int i = 0; i <= 18; i += 3) {
			array[i] = 5 * random.nextInt(14) + 3; // �����3%5
		}

		for (int i = 1; i <= 19; i += 3) {
			array[i] = 7 * random.nextInt(14) + 1; // �����1%7
		}
		// �����еĳ���
		array[2] = 3;
		array[5] = 5;
		array[8] = 1;
		array[11] = 7;

		StringBuilder sb = new StringBuilder();
		sb.append("var " + a + "=[");
		for (int i = 0; i < array.length; i++) {
			sb.append(array[i] + ",");
		}
		sb.deleteCharAt(sb.length() - 1); // ɾ�����һ������
		sb.append("],"
				+ b
				+ "=0;var "
				+ c
				+ "=function("+g+","+h+"){var "+i+";var a="+g+"["+h+"-1];for("+i+"="+h+"-2;"+i+">=0;"+i+"--){"+g+"["+i+"+1]="+g+"["+i+"]}"+g+"[0]=a;"
				+ b + "=(" + b + "+1)%"+h+"};");
		return sb.toString();
	}

	public static void setAddVarName() {
		ScriptOrFnScope globalScope = LinkGUIToCompress.compressor
				.getGlobalScope();
		List<String> usedSymbols = new ArrayList<String>();
		if (EditOptionPanel.checkBoxMunge.isSelected()) {
			globalScope.getAllMungeValue(usedSymbols);
		} else {
			globalScope.getAllOriginalValue(usedSymbols);
		}

		@SuppressWarnings("unused")
		int pickFromSet = 1;
		ArrayList<String> freeSymbols = new ArrayList<String>();

		freeSymbols.addAll(JavaScriptCompressor.ones); // ���������������ַ�(����)
		freeSymbols.removeAll(usedSymbols);
		if (freeSymbols.size() == 0) {
			pickFromSet = 2;
			freeSymbols.addAll(JavaScriptCompressor.twos);
			freeSymbols.removeAll(usedSymbols);
		}
		if (freeSymbols.size() == 0) {
			pickFromSet = 3;
			freeSymbols.addAll(JavaScriptCompressor.threes);
			freeSymbols.removeAll(usedSymbols);
		}
		if (freeSymbols.size() == 0) {
			throw new IllegalStateException("������ʹ���������б��ñ���. ֹͣ����...");
		}

		a = freeSymbols.remove(0);
		if (freeSymbols.size() == 0) {
			return;
		}
		b = freeSymbols.remove(0);
		if (freeSymbols.size() == 0) {
			return;
		}
		c = freeSymbols.remove(0);
		if (freeSymbols.size() == 0) {
			return;
		}
		d = freeSymbols.remove(0);
		if (freeSymbols.size() == 0) {
			return;
		}
		f = freeSymbols.remove(0);
		if (freeSymbols.size() == 0) {
			return;
		}
		
		
		g = freeSymbols.remove(0);
		if (freeSymbols.size() == 0) {
			return;
		}
		h = freeSymbols.remove(0);
		if (freeSymbols.size() == 0) {
			return;
		}
		i = freeSymbols.remove(0);
	}

}
