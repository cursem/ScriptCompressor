package com.yahoo.platform.yui.compressor;

import java.awt.Color;

import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import org.mozilla.javascript.ErrorReporter;
import org.mozilla.javascript.EvaluatorException;

import gdut.visual.gui.AnalyseWarnPanel;

public class ErrorReporterImpl implements ErrorReporter {

	public void warning(String message, String sourceName, int line,
			String lineSource, int lineOffset) {
		SimpleAttributeSet attrSet = new SimpleAttributeSet();
		StyleConstants.setForeground(attrSet, Color.RED);
		Document doc = AnalyseWarnPanel.warnTextPane.getDocument();
		try {
			if (line < 0) {
				doc.insertString(doc.getLength(), "[WARNING] ", attrSet);
				doc.insertString(doc.getLength(), message + "\r\n\r\n", null);
			} else {
				doc.insertString(doc.getLength(), "[WARNING] ", attrSet);
				doc.insertString(doc.getLength(), line + ':' + lineOffset + ':'
						+ message + "\r\n\r\n", null);
			}
		} catch (BadLocationException e) {
			e.printStackTrace();
		}

	}

	public void error(String message, String sourceName, int line,
			String lineSource, int lineOffset) {
		SimpleAttributeSet attrSet = new SimpleAttributeSet();
		StyleConstants.setForeground(attrSet, Color.RED);
		Document doc = AnalyseWarnPanel.warnTextPane.getDocument();
		try {
			if (line < 0) {
				doc.insertString(doc.getLength(), "[ERROR] " + message+"\r\n\r\n",
						attrSet);
			} else {
				doc.insertString(doc.getLength(), "[ERROR] " + line + ':'
						+ lineOffset + ':' + message+"\r\n\r\n", attrSet);
			}
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}

	public EvaluatorException runtimeError(String message, String sourceName,
			int line, String lineSource, int lineOffset) {
		error(message, sourceName, line, lineSource, lineOffset);
		return new EvaluatorException(message);
	}
}
