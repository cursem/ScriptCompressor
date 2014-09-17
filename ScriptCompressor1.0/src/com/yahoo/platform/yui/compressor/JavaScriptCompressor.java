/*
 * YUI Compressor
 * http://developer.yahoo.com/yui/compressor/
 * Author: Julien Lecomte -  http://www.julienlecomte.net/
 * Copyright (c) 2011 Yahoo! Inc.  All rights reserved.
 * The copyrights embodied in the content of this file are licensed
 * by Yahoo! Inc. under the BSD (revised) open source license.
 */
package com.yahoo.platform.yui.compressor;

import org.mozilla.javascript.*;

import gdut.visual.gui.AnalyseWarnPanel;
import gdut.visual.gui.EditOptionPanel;

import java.io.IOException;
import java.io.Reader;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JTable;

public class JavaScriptCompressor {

	static final ArrayList<String> ones;
	static final ArrayList<String> twos;
	static final ArrayList<String> threes;

	static final Set<String> builtin = new HashSet<String>();
	static final Map<Integer, String> literals = new Hashtable<Integer, String>();
	static final Set<String> reserved = new HashSet<String>();

	static {

		// This list contains all the 3 characters or less built-in global
		// symbols available in a browser. Please add to this list if you
		// see anything missing.
		builtin.add("NaN");
		builtin.add("top");

		ones = new ArrayList<String>();
		for (char c = 'a'; c <= 'z'; c++)
			ones.add(Character.toString(c));
		for (char c = 'A'; c <= 'Z'; c++)
			ones.add(Character.toString(c));

		twos = new ArrayList<String>();
		for (int i = 0; i < ones.size(); i++) {
			String one = ones.get(i);
			for (char c = 'a'; c <= 'z'; c++)
				twos.add(one + Character.toString(c));
			for (char c = 'A'; c <= 'Z'; c++)
				twos.add(one + Character.toString(c));
			for (char c = '0'; c <= '9'; c++)
				twos.add(one + Character.toString(c));
		}

		// Remove two-letter JavaScript reserved words and built-in globals...
		twos.remove("as");
		twos.remove("is");
		twos.remove("do");
		twos.remove("if");
		twos.remove("in");
		twos.removeAll(builtin);

		threes = new ArrayList<String>();
		for (int i = 0; i < twos.size(); i++) {
			String two = twos.get(i);
			for (char c = 'a'; c <= 'z'; c++)
				threes.add(two + Character.toString(c));
			for (char c = 'A'; c <= 'Z'; c++)
				threes.add(two + Character.toString(c));
			for (char c = '0'; c <= '9'; c++)
				threes.add(two + Character.toString(c));
		}

		// Remove three-letter JavaScript reserved words and built-in globals...
		threes.remove("for");
		threes.remove("int");
		threes.remove("new");
		threes.remove("try");
		threes.remove("use");
		threes.remove("var");
		threes.removeAll(builtin);

		// That's up to ((26+26)*(1+(26+26+10)))*(1+(26+26+10))-8
		// (206,380 symbols per scope)

		// The following list comes from
		// org/mozilla/javascript/Decompiler.java...
		// 注意：此处并为认为地设定键与值的acsii码相等，比如Token.ASSIGN是89,而'='为61
		literals.put(new Integer(Token.GET), "get ");
		literals.put(new Integer(Token.SET), "set ");
		literals.put(new Integer(Token.TRUE), "true");
		literals.put(new Integer(Token.FALSE), "false");
		literals.put(new Integer(Token.NULL), "null");
		literals.put(new Integer(Token.THIS), "this");
		literals.put(new Integer(Token.FUNCTION), "function");
		literals.put(new Integer(Token.COMMA), ",");
		literals.put(new Integer(Token.LC), "{");
		literals.put(new Integer(Token.RC), "}");
		literals.put(new Integer(Token.LP), "(");
		literals.put(new Integer(Token.RP), ")");
		literals.put(new Integer(Token.LB), "[");
		literals.put(new Integer(Token.RB), "]");
		literals.put(new Integer(Token.DOT), ".");
		literals.put(new Integer(Token.NEW), "new ");
		literals.put(new Integer(Token.DELPROP), "delete ");
		literals.put(new Integer(Token.IF), "if");
		literals.put(new Integer(Token.ELSE), "else");
		literals.put(new Integer(Token.FOR), "for");
		literals.put(new Integer(Token.IN), " in ");
		literals.put(new Integer(Token.WITH), "with");
		literals.put(new Integer(Token.WHILE), "while");
		literals.put(new Integer(Token.DO), "do");
		literals.put(new Integer(Token.TRY), "try");
		literals.put(new Integer(Token.CATCH), "catch");
		literals.put(new Integer(Token.FINALLY), "finally");
		literals.put(new Integer(Token.THROW), "throw");
		literals.put(new Integer(Token.SWITCH), "switch");
		literals.put(new Integer(Token.BREAK), "break");
		literals.put(new Integer(Token.CONTINUE), "continue");
		literals.put(new Integer(Token.CASE), "case");
		literals.put(new Integer(Token.DEFAULT), "default");
		literals.put(new Integer(Token.RETURN), "return");
		literals.put(new Integer(Token.VAR), "var ");
		literals.put(new Integer(Token.SEMI), ";");
		literals.put(new Integer(Token.ASSIGN), "=");
		literals.put(new Integer(Token.ASSIGN_ADD), "+=");
		literals.put(new Integer(Token.ASSIGN_SUB), "-=");
		literals.put(new Integer(Token.ASSIGN_MUL), "*=");
		literals.put(new Integer(Token.ASSIGN_DIV), "/=");
		literals.put(new Integer(Token.ASSIGN_MOD), "%=");
		literals.put(new Integer(Token.ASSIGN_BITOR), "|=");
		literals.put(new Integer(Token.ASSIGN_BITXOR), "^=");
		literals.put(new Integer(Token.ASSIGN_BITAND), "&=");
		literals.put(new Integer(Token.ASSIGN_LSH), "<<=");
		literals.put(new Integer(Token.ASSIGN_RSH), ">>=");
		literals.put(new Integer(Token.ASSIGN_URSH), ">>>=");
		literals.put(new Integer(Token.HOOK), "?");
		literals.put(new Integer(Token.OBJECTLIT), ":");
		literals.put(new Integer(Token.COLON), ":");
		literals.put(new Integer(Token.OR), "||");
		literals.put(new Integer(Token.AND), "&&");
		literals.put(new Integer(Token.BITOR), "|");
		literals.put(new Integer(Token.BITXOR), "^");
		literals.put(new Integer(Token.BITAND), "&");
		literals.put(new Integer(Token.SHEQ), "===");
		literals.put(new Integer(Token.SHNE), "!==");
		literals.put(new Integer(Token.EQ), "==");
		literals.put(new Integer(Token.NE), "!=");
		literals.put(new Integer(Token.LE), "<=");
		literals.put(new Integer(Token.LT), "<");
		literals.put(new Integer(Token.GE), ">=");
		literals.put(new Integer(Token.GT), ">");
		literals.put(new Integer(Token.INSTANCEOF), " instanceof ");
		literals.put(new Integer(Token.LSH), "<<");
		literals.put(new Integer(Token.RSH), ">>");
		literals.put(new Integer(Token.URSH), ">>>");
		literals.put(new Integer(Token.TYPEOF), "typeof");
		literals.put(new Integer(Token.VOID), "void ");
		literals.put(new Integer(Token.CONST), "const ");
		literals.put(new Integer(Token.NOT), "!");
		literals.put(new Integer(Token.BITNOT), "~");
		literals.put(new Integer(Token.POS), "+");
		literals.put(new Integer(Token.NEG), "-");
		literals.put(new Integer(Token.INC), "++");
		literals.put(new Integer(Token.DEC), "--");
		literals.put(new Integer(Token.ADD), "+");
		literals.put(new Integer(Token.SUB), "-");
		literals.put(new Integer(Token.MUL), "*");
		literals.put(new Integer(Token.DIV), "/");
		literals.put(new Integer(Token.MOD), "%");
		literals.put(new Integer(Token.COLONCOLON), "::");
		literals.put(new Integer(Token.DOTDOT), "..");
		literals.put(new Integer(Token.DOTQUERY), ".(");
		literals.put(new Integer(Token.XMLATTR), "@");
		literals.put(new Integer(Token.LET), "let ");
		literals.put(new Integer(Token.YIELD), "yield ");

		// See
		// http://developer.mozilla.org/en/docs/Core_JavaScript_1.5_Reference:Reserved_Words

		// JavaScript 1.5 reserved words
		reserved.add("break");
		reserved.add("case");
		reserved.add("catch");
		reserved.add("continue");
		reserved.add("default");
		reserved.add("delete");
		reserved.add("do");
		reserved.add("else");
		reserved.add("finally");
		reserved.add("for");
		reserved.add("function");
		reserved.add("if");
		reserved.add("in");
		reserved.add("instanceof");
		reserved.add("new");
		reserved.add("return");
		reserved.add("switch");
		reserved.add("this");
		reserved.add("throw");
		reserved.add("try");
		reserved.add("typeof");
		reserved.add("var");
		reserved.add("void");
		reserved.add("while");
		reserved.add("with");
		// Words reserved for future use
		reserved.add("abstract");
		reserved.add("boolean");
		reserved.add("byte");
		reserved.add("char");
		reserved.add("class");
		reserved.add("const");
		reserved.add("debugger");
		reserved.add("double");
		reserved.add("enum");
		reserved.add("export");
		reserved.add("extends");
		reserved.add("final");
		reserved.add("float");
		reserved.add("goto");
		reserved.add("implements");
		reserved.add("import");
		reserved.add("int");
		reserved.add("interface");
		reserved.add("long");
		reserved.add("native");
		reserved.add("package");
		reserved.add("private");
		reserved.add("protected");
		reserved.add("public");
		reserved.add("short");
		reserved.add("static");
		reserved.add("super");
		reserved.add("synchronized");
		reserved.add("throws");
		reserved.add("transient");
		reserved.add("volatile");
		// These are not reserved, but should be taken into account
		// in isValidIdentifier (See jslint source code)
		reserved.add("arguments");
		reserved.add("eval");
		reserved.add("true");
		reserved.add("false");
		reserved.add("Infinity");
		reserved.add("NaN");
		reserved.add("null");
		reserved.add("undefined");
	}

	private static int countChar(String haystack, char needle) {
		int idx = 0;
		int count = 0;
		int length = haystack.length();
		while (idx < length) {
			char c = haystack.charAt(idx++);
			if (c == needle) {
				count++;
			}
		}
		return count;
	}

	private static int printSourceString(String source, int offset,
			StringBuffer sb) {
		int length = source.charAt(offset);
		++offset;
		if ((0x8000 & length) != 0) {
			length = ((0x7FFF & length) << 16) | source.charAt(offset);
			++offset;
		}
		if (sb != null) {
			String str = source.substring(offset, offset + length);
			sb.append(str);
		}
		return offset + length;
	}

	private static int printSourceNumber(String source, int offset,
			StringBuffer sb) {
		double number = 0.0;
		char type = source.charAt(offset);
		++offset;
		if (type == 'S') {
			if (sb != null) {
				number = source.charAt(offset);
			}
			++offset;
		} else if (type == 'J' || type == 'D') {
			if (sb != null) {
				long lbits;
				lbits = (long) source.charAt(offset) << 48;
				lbits |= (long) source.charAt(offset + 1) << 32;
				lbits |= (long) source.charAt(offset + 2) << 16;
				lbits |= (long) source.charAt(offset + 3);
				if (type == 'J') {
					number = lbits;
				} else {
					number = Double.longBitsToDouble(lbits);
				}
			}
			offset += 4;
		} else {
			// Bad source
			throw new RuntimeException();
		}
		if (sb != null) {
			sb.append(ScriptRuntime.numberToString(number, 10));
		}
		return offset;
	}

	// 得到的tokens包含源代码的由词法分析器分析出的所有单词，但不包含注释
	private static ArrayList<JavaScriptToken> parse(Reader in,
			ErrorReporter reporter)// 返回tokens（保存的是源文件中出现的javascript关键字和NAME，REGEXP，STRING等类型）
			throws IOException, EvaluatorException {

		CompilerEnvirons env = new CompilerEnvirons(); // 创建编译环境对象
		env.setLanguageVersion(Context.VERSION_1_7); // 设置语言版本
		Parser parser = new Parser(env, reporter); // 创建解释器对象
		parser.parse(in, null, 1); // 解释输入流
		String source = parser.getEncodedSource(); // 获得已编码的源码（词法分析阶段通常是把从源程序中识别出的各个单词的词文
													// 转换为某种内部表示
		int offset = 0;
		int length = source.length();
		ArrayList<JavaScriptToken> tokens = new ArrayList<JavaScriptToken>();
		StringBuffer sb = new StringBuffer();

		while (offset < length) {
			int tt = source.charAt(offset++); // 获取特定位置上的字符，并转化为ASCII编码
			switch (tt) {

			case Token.CONDCOMMENT: // 条件注释
			case Token.KEEPCOMMENT: // 注释
			case Token.NAME: //
			case Token.REGEXP: // 正则表达式类型
			case Token.STRING: // String类型,js程序中双引号或单引号括起来的字符串
				sb.setLength(0);
				offset = printSourceString(source, offset, sb);
				tokens.add(new JavaScriptToken(tt, sb.toString()));
				break;

			case Token.NUMBER: // Number类型
				sb.setLength(0);
				offset = printSourceNumber(source, offset, sb);
				tokens.add(new JavaScriptToken(tt, sb.toString()));
				break;

			default:
				String literal = literals.get(new Integer(tt));
				if (literal != null) { // 若不为空，说明哈希表literals中含有键new
										// Integer(tt)所对应的值
					tokens.add(new JavaScriptToken(tt, literal)); // 将此关键字保存到数组列表tokens中
				}
				break;
			}
		}

		/*
		 * //begin Iterator<JavaScriptToken> iterator = tokens.iterator();
		 * JavaScriptToken token; while(iterator.hasNext()) { token =
		 * iterator.next();
		 * System.out.println(token.getType()+"\t"+token.getValue()); } //end
		 */
		return tokens;
	}

	// 此函数是将表达式优化,形式参数中的tokens是诸如'+','-','*'等单字符的操作符（当然也是关键字）和NAME，REGEXP，STRING等类型
	public void processStringLiterals(boolean merge) {

		String tv;
		int i, length = tokens.size();
		JavaScriptToken token, prevToken, nextToken;

		if (merge) { // 如果优化

			// Concatenate string literals(连接字符串常量) that are being appended
			// wherever
			// it is safe to do so. Note that we take care of the case:
			// "a" + "b".toUpperCase()

			for (i = 0; i < length; i++) {
				token = tokens.get(i);
				switch (token.getType()) {

				case Token.ADD:
					if (i > 0 && i < length) {
						prevToken = tokens.get(i - 1);
						nextToken = tokens.get(i + 1);
						if (prevToken.getType() == Token.STRING
								&& nextToken.getType() == Token.STRING
								&& (i == length - 1 || tokens.get(i + 2)
										.getType() != Token.DOT)) {
							tokens.set(
									i - 1,
									new JavaScriptToken(Token.STRING, prevToken
											.getValue() + nextToken.getValue()));// 设置对应索引处的值
							tokens.remove(i + 1); // 移除之前的值
							tokens.remove(i); // 移除之前的值
							i = i - 1;
							length = length - 2;
							break;
						}
					}
				}
			}

		}

		// Second
		// pass...Token中保存的STRING类型形如abc，经以下处理后变为"abc"，a'b'c-->"a'b'c",a"b"c-->'a"b"c'

		for (i = 0; i < length; i++) {
			token = tokens.get(i);
			if (token.getType() == Token.STRING) {
				tv = token.getValue();

				// Finally, add the quoting characters（引号字符） and escape the
				// string（滤过字符串）. We use
				// the quoting character that minimizes the amount of escaping
				// to save
				// a few additional bytes.

				char quotechar;
				int singleQuoteCount = countChar(tv, '\''); // 计算单引号数量
				int doubleQuoteCount = countChar(tv, '"'); // 计算双引号数量
				if (doubleQuoteCount <= singleQuoteCount) {
					quotechar = '"';
				} else {
					quotechar = '\'';
				}

				tv = quotechar + escapeString(tv, quotechar) + quotechar;

				// String concatenation transforms the old script scheme:
				// '<scr'+'ipt ...><'+'/script>'
				// into the following:
				// '<script ...></script>'
				// which breaks if this code is embedded inside an HTML
				// document.
				// Since this is not the right way to do this, let's fix the
				// code by
				// transforming all "</script" into "<\/script"

				if (tv.indexOf("</script") >= 0) {
					tv = tv.replaceAll("<\\/script", "<\\\\/script");
				}

				tokens.set(i, new JavaScriptToken(Token.STRING, tv));
			}
		}
	}

	// Add necessary escaping that was removed in Rhino's tokenizer（词法分析器）.
	private static String escapeString(String s, char quotechar) {

		assert quotechar == '"' || quotechar == '\''; // 如果启用了断言，则表达式为假时，程序抛出AssertionError（这是一个错误，不是异常，所以不能被捕获）

		if (s == null) {
			return null;
		}

		StringBuffer sb = new StringBuffer();
		for (int i = 0, L = s.length(); i < L; i++) {
			int c = s.charAt(i);
			if (c == quotechar) {
				sb.append("\\");
			}
			sb.append((char) c);
		}

		return sb.toString();
	}

	/*
	 * Simple check to see whether a string is a valid identifier name. If a
	 * string matches this pattern, it means it IS a valid identifier name. If a
	 * string doesn't match it, it does not necessarily mean it is not a valid
	 * identifier name.
	 */
	private static final Pattern SIMPLE_IDENTIFIER_NAME_PATTERN = Pattern
			.compile("^[a-zA-Z_][a-zA-Z0-9_]*$");

	private static boolean isValidIdentifier(String s) {
		Matcher m = SIMPLE_IDENTIFIER_NAME_PATTERN.matcher(s);
		return (m.matches() && !reserved.contains(s));
	}

	/*
	 * Transforms obj["foo"] into obj.foo whenever possible, saving 3 bytes.
	 */
	public void optimizeObjectMemberAccess() {

		String tv;
		int i, length;
		JavaScriptToken token;

		for (i = 0, length = tokens.size(); i < length; i++) {

			if (tokens.get(i).getType() == Token.LB && i > 0 && i < length - 2
					&& tokens.get(i - 1).getType() == Token.NAME
					&& tokens.get(i + 1).getType() == Token.STRING
					&& tokens.get(i + 2).getType() == Token.RB) {
				token = tokens.get(i + 1);
				tv = token.getValue();
				tv = tv.substring(1, tv.length() - 1);
				if (isValidIdentifier(tv)) {
					tokens.set(i, new JavaScriptToken(Token.DOT, "."));
					tokens.set(i + 1, new JavaScriptToken(Token.NAME, tv));
					tokens.remove(i + 2);
					i = i + 2;
					length = length - 1;
				}
			}
		}
	}

	/*
	 * Transforms 'foo': ... into foo: ... whenever possible, saving 2 bytes.
	 */
	public void optimizeObjLitMemberDecl() {

		String tv;
		int i, length;
		JavaScriptToken token;

		for (i = 0, length = tokens.size(); i < length; i++) {
			if (tokens.get(i).getType() == Token.OBJECTLIT && i > 0
					&& tokens.get(i - 1).getType() == Token.STRING) {
				token = tokens.get(i - 1);
				tv = token.getValue();
				tv = tv.substring(1, tv.length() - 1);
				if (isValidIdentifier(tv)) {
					tokens.set(i - 1, new JavaScriptToken(Token.NAME, tv));
				}
			}
		}
	}

	private ErrorReporter logger;

	private boolean munge;
	private boolean verbose;

	private static final int BUILDING_SYMBOL_TREE = 1;
	private static final int CHECKING_SYMBOL_TREE = 2;

	private int mode;
	private int offset;
	private int braceNesting;
	private ArrayList<JavaScriptToken> tokens;
	private Stack<ScriptOrFnScope> scopes = new Stack<ScriptOrFnScope>();
	private ScriptOrFnScope globalScope = new ScriptOrFnScope(-1, null);
	private Hashtable<Integer, ScriptOrFnScope> indexedScopes = new Hashtable<Integer, ScriptOrFnScope>();

	public JavaScriptCompressor(Reader in, ErrorReporter reporter, boolean munge,boolean verbose)
			throws IOException, EvaluatorException {

		this.logger = reporter;
		this.munge = munge;
		this.verbose = verbose;
		this.tokens = parse(in, reporter); // 解释输入流,返回源文件中出现的js关键字
	}

	private ScriptOrFnScope getCurrentScope() {
		return scopes.peek();
	}

	private void enterScope(ScriptOrFnScope scope) {
		scopes.push(scope);
	}

	private void leaveCurrentScope() {
		scopes.pop();
	}

	private JavaScriptToken consumeToken() {
		return tokens.get(offset++);
	}

	private JavaScriptToken getToken(int delta) {
		return tokens.get(offset + delta);
	}

	/*
	 * Returns the identifier for the specified symbol defined in the specified
	 * scope or in any scope above it. Returns null if this symbol does not have
	 * a corresponding identifier.
	 */
	private JavaScriptIdentifier getIdentifier(String symbol,
			ScriptOrFnScope scope) {
		JavaScriptIdentifier identifier;
		while (scope != null) {
			identifier = scope.getIdentifier(symbol);
			if (identifier != null) {
				return identifier;
			}
			scope = scope.getParentScope(); // ??此代码不知何意
		}
		return null;
	}

	/*
	 * If either 'eval' or 'with' is used in a local scope, we must make sure
	 * that all containing local scopes don't get munged. Otherwise, the
	 * obfuscation would potentially introduce bugs.
	 */
	private void protectScopeFromObfuscation(ScriptOrFnScope scope) {
		assert scope != null;

		if (scope == globalScope) {
			// The global scope does not get obfuscated,
			// so we don't need to worry about it...
			return;
		}

		// Find the highest local scope containing the specified scope.
		while (scope.getParentScope() != globalScope) {
			scope = scope.getParentScope();
		}

		assert scope.getParentScope() == globalScope;
		scope.preventMunging();
	}

	private String getDebugString(int max) {
		assert max > 0;
		StringBuffer result = new StringBuffer();
		int start = Math.max(offset - max, 0);
		int end = Math.min(offset + max, tokens.size());
		for (int i = start; i < end; i++) {
			JavaScriptToken token = tokens.get(i);
			if (i == offset - 1) {
				result.append(" ---> ");
			}
			result.append(token.getValue());
			if (i == offset - 1) {
				result.append(" <--- ");
			}
		}
		return result.toString();
	}

	private void warn(String message, boolean showDebugString) {
		if (verbose) {
			if (showDebugString) {
				message = message + "\r\n" + getDebugString(10);
			}
			logger.warning(message, null, -1, null, -1);
		}
	}

	private void parseFunctionDeclaration() {

		String symbol;
		JavaScriptToken token;
		ScriptOrFnScope currentScope, fnScope;
		JavaScriptIdentifier identifier;

		currentScope = getCurrentScope(); // 得到此函数声明所在的域

		token = consumeToken();

		// 如果是函数名（说明这种函数的声明方式是：function funName(){}）
		// 否则是：var varName = function(){}
		if (token.getType() == Token.NAME) {
			if (mode == BUILDING_SYMBOL_TREE) {
				// Get the name of the function and declare it in the current
				// scope.
				symbol = token.getValue(); // 得到的是函数名
				if (currentScope.getIdentifier(symbol) != null) {
					warn("函数" + symbol + "已经在相同的作用域中声明。", true);
				}
				currentScope.declareIdentifier(symbol,false); // 在本作用域中声明标识符
			}
			token = consumeToken(); // 得到左圆括号
		}

		assert token.getType() == Token.LP;
		if (mode == BUILDING_SYMBOL_TREE) {
			fnScope = new ScriptOrFnScope(braceNesting, currentScope); // 新建一个作用域，并把当前域传递过去最为父作用域
			indexedScopes.put(new Integer(offset), fnScope); // 圆括号的下一个索引映射到函数作用域
		} else {
			fnScope = indexedScopes.get(new Integer(offset));
		}

		// Parse function arguments.解释函数参数
		int argpos = 0;
		while ((token = consumeToken()).getType() != Token.RP) {
			assert token.getType() == Token.NAME
					|| token.getType() == Token.COMMA;
			if (token.getType() == Token.NAME && mode == BUILDING_SYMBOL_TREE) {
				symbol = token.getValue();
				identifier = fnScope.declareIdentifier(symbol,false);
				if (symbol.equals("$super") && argpos == 0) {
					// Exception for Prototype 1.6...
					identifier.preventMunging();
				}
				argpos++;
			}
		}

		token = consumeToken(); // 得到函数的左花括号
		assert token.getType() == Token.LC;
		braceNesting++;

		token = getToken(0);
		if (token.getType() == Token.STRING
				&& getToken(1).getType() == Token.SEMI) {
			// This is a hint. Hints are empty statements that look like
			// "localvar1:nomunge, localvar2:nomunge"; They allow developers
			// to prevent specific symbols from getting obfuscated (some heretic
			// implementations, such as Prototype 1.6, require specific variable
			// names, such as $super for example, in order to work
			// appropriately.
			// Note: right now, only "nomunge" is supported in the right hand
			// side
			// of a hint. However, in the future, the right hand side(右边) may
			// contain
			// other values.
			consumeToken();
			String hints = token.getValue();
			// Remove the leading and trailing quotes...
			hints = hints.substring(1, hints.length() - 1).trim();
			StringTokenizer st1 = new StringTokenizer(hints, ","); // Tokenizer分词器,以','为分词界限
			while (st1.hasMoreTokens()) {
				String hint = st1.nextToken();
				int idx = hint.indexOf(':');
				if (idx <= 0 || idx >= hint.length() - 1) {
					if (mode == BUILDING_SYMBOL_TREE) {
						// No need to report the error twice, hence the test...
						warn("提示语句有语法错误：" + hint, true);
					}
					break;
				}
				String variableName = hint.substring(0, idx).trim();
				String variableType = hint.substring(idx + 1).trim();
				if (mode == BUILDING_SYMBOL_TREE) {
					fnScope.addHint(variableName, variableType);
				} else if (mode == CHECKING_SYMBOL_TREE) {
					identifier = fnScope.getIdentifier(variableName);
					if (identifier != null) {
						if (variableType.equals("nomunge")) {
							identifier.preventMunging();
						} else {
							warn("不支持的提示指令： " + hint, true);
						}
					} else {
						warn("提示指令指向未知的标识符: " + hint,
								true);
					}
				}
			}
		}

		parseScope(fnScope); // 解释函数域
	}

	private void parseCatch() {

		String symbol;
		JavaScriptToken token;
		ScriptOrFnScope currentScope;
		JavaScriptIdentifier identifier;

		token = getToken(-1);
		assert token.getType() == Token.CATCH;
		token = consumeToken();
		assert token.getType() == Token.LP;
		token = consumeToken();
		assert token.getType() == Token.NAME;

		symbol = token.getValue();
		currentScope = getCurrentScope();

		if (mode == BUILDING_SYMBOL_TREE) {
			// We must declare the exception identifier in the containing
			// function
			// scope to avoid errors related to the obfuscation process. No need
			// to
			// display a warning if the symbol was already declared here...
			currentScope.declareIdentifier(symbol,false);
		} else {
			identifier = getIdentifier(symbol, currentScope);
			identifier.incrementRefcount();
		}

		token = consumeToken();
		assert token.getType() == Token.RP;
	}

	private void parseExpression() {

		// Parse the expression until we encounter a comma or a semi-colon
		// in the same brace nesting, bracket nesting（方括号嵌套） and paren nesting.
		// Parse functions if any...

		String symbol;
		JavaScriptToken token;
		ScriptOrFnScope currentScope;
		JavaScriptIdentifier identifier;

		int expressionBraceNesting = braceNesting; // 花括号嵌套
		int bracketNesting = 0; // 方括号嵌套
		int parensNesting = 0; //

		int length = tokens.size();

		while (offset < length) {

			token = consumeToken();

			currentScope = getCurrentScope();// return scopes.peek();

			switch (token.getType()) {

			case Token.SEMI:
			case Token.COMMA:
				if (braceNesting == expressionBraceNesting
						&& bracketNesting == 0 && parensNesting == 0) {
					return;
				}
				break;

			case Token.FUNCTION:
				parseFunctionDeclaration();
				break;

			case Token.LC:
				braceNesting++;
				break;

			case Token.RC:
				braceNesting--;
				assert braceNesting >= expressionBraceNesting;
				break;

			case Token.LB:
				bracketNesting++;
				break;

			case Token.RB:
				bracketNesting--;
				break;

			case Token.LP:
				parensNesting++;
				break;

			case Token.RP:
				parensNesting--;
				break;

			case Token.CONDCOMMENT:
				if (mode == BUILDING_SYMBOL_TREE) {
					protectScopeFromObfuscation(currentScope);
					warn("不推荐使用JScript条件注释。"
							+ (munge ? "使用JScript条件注释会降低压缩水平！" : ""), true);
				}
				break;

			case Token.NAME:
				symbol = token.getValue();

				if (mode == BUILDING_SYMBOL_TREE) {

					if (symbol.equals("eval")) {

						protectScopeFromObfuscation(currentScope);
						warn("不推荐使用'eval'关键字。"
								+ (munge ? "使用'eval'会降低压缩水平！" : ""), true);

					}

				} else if (mode == CHECKING_SYMBOL_TREE) {

					if ((offset < 2 || (getToken(-2).getType() != Token.DOT
							&& getToken(-2).getType() != Token.GET && getToken(
							-2).getType() != Token.SET))
							&& getToken(0).getType() != Token.OBJECTLIT) {

						identifier = getIdentifier(symbol, currentScope);

						if (identifier == null) {

							if (symbol.length() <= 3
									&& !builtin.contains(symbol)) {
								// Here, we found an undeclared and
								// un-namespaced symbol that is
								// 3 characters or less in length. Declare it in
								// the global scope.
								// We don't need to declare longer symbols since
								// they won't cause
								// any conflict with other munged symbols.
								globalScope.declareIdentifier(symbol,false);

								// I removed the warning since was only being
								// done when
								// for identifiers 3 chars or less, and was just
								// causing
								// noise for people who happen to rely on an
								// externally
								// declared variable that happen to be that
								// short. We either
								// should always warn or never warn -- the fact
								// that we
								// declare the short symbols in the global space
								// doesn't
								// change anything.
								// warn("Found an undeclared symbol: " + symbol,
								// true);
							}

						} else {

							identifier.incrementRefcount();
						}
					}
				}
				break;
			}
		}
	}

	private void parseScope(ScriptOrFnScope scope) {

		String symbol;
		JavaScriptToken token;
		JavaScriptIdentifier identifier;

		int length = tokens.size();

		enterScope(scope); // 进入范围(将scope压入栈scopes中)

		while (offset < length) { // 在此函数调用前已设置为0

			token = consumeToken(); // 消费tokens,offset自增

			switch (token.getType()) {

			case Token.VAR: // 注意后面没有break;

				if (mode == BUILDING_SYMBOL_TREE) { 
					scope.incrementVarCount(); // 当前范围内的var数量加1
				}

				/* FALLSTHROUGH(falls through) 失败 */

			case Token.CONST:

				// The var keyword is followed by at least one symbol name.
				// If several symbols follow, they are comma(逗号) separated.
				for (;;) {
					token = consumeToken();

					assert token.getType() == Token.NAME;
					// 判断关键字var、const后的变量是否已经定义，未定义则定义，已定义则输出警告
					if (mode == BUILDING_SYMBOL_TREE) {
						symbol = token.getValue();
						if (scope.getIdentifier(symbol) == null) {
							scope.declareIdentifier(symbol,true); // 声明标识符
						} else {
							warn("变量" + symbol + "已经在相同的作用域中声明", true);
						}
					}

					token = getToken(0);// offset不自增,token.getValue()的值一般为'='或';'

					assert token.getType() == Token.SEMI
							|| token.getType() == Token.ASSIGN
							|| token.getType() == Token.COMMA
							|| token.getType() == Token.IN;

					if (token.getType() == Token.IN) {
						break;
					} else {
						parseExpression(); // 解释表达式（例如解释var a = 'u';或var a;）
						token = getToken(-1); // 得到的是';'
						if (token.getType() == Token.SEMI) { // 变量声明完成
							break;
						}
					}
				}
				break;

			case Token.FUNCTION:
				parseFunctionDeclaration();
				break;

			case Token.LC:
				braceNesting++;
				break;

			case Token.RC:
				braceNesting--;
				assert braceNesting >= scope.getBraceNesting();
				if (braceNesting == scope.getBraceNesting()) { // 说明要离开当前作用域了
					leaveCurrentScope(); // 离开当前作用域
					return;
				}
				break;

			case Token.WITH:
				if (mode == BUILDING_SYMBOL_TREE) {
					// Inside a 'with' block, it is impossible to figure out
					// statically whether a symbol is a local variable or an
					// object member. As a consequence, the only thing we can
					// do is turn the obfuscation off for the highest scope
					// containing the 'with' block.
					protectScopeFromObfuscation(scope);
					warn("不推荐使用with关键字。" + (munge ? " 使用with会降低压缩水平" : ""),
							true);
				}
				break;

			case Token.CATCH:
				parseCatch();
				break;

			case Token.CONDCOMMENT:
				if (mode == BUILDING_SYMBOL_TREE) {
					protectScopeFromObfuscation(scope);
					warn("不推荐使用JScript条件注释。"
							+ (munge ? "使用JScript条件解释会降低压缩水平。" : ""), true);
				}
				break;

			case Token.NAME:
				symbol = token.getValue();

				if (mode == BUILDING_SYMBOL_TREE) {

					if (symbol.equals("eval")) {

						protectScopeFromObfuscation(scope);
						warn("不推荐使用'eval'关键字。"
								+ (munge ? "使用'eval'会降低压缩水平！" : ""), true);

					}

				} else if (mode == CHECKING_SYMBOL_TREE) {

					if ((offset < 2 || getToken(-2).getType() != Token.DOT)
							&& getToken(0).getType() != Token.OBJECTLIT) {

						identifier = getIdentifier(symbol, scope);

						if (identifier == null) {

							if (symbol.length() <= 3
									&& !builtin.contains(symbol)) {
								// Here, we found an undeclared and
								// un-namespaced symbol that is
								// 3 characters or less in length. Declare it in
								// the global scope.
								// We don't need to declare longer symbols since
								// they won't cause
								// any conflict with other munged symbols.
								globalScope.declareIdentifier(symbol,false);
								// warn("Found an undeclared symbol: " + symbol,
								// true);
							}

						} else {

							identifier.incrementRefcount();
						}
					}
				}
				break;
			}
		}
	}

	// 建立语法树
	public void buildSymbolTree() {
		offset = 0;
		braceNesting = 0; // 花括号嵌套
		scopes.clear(); // 清空栈
		indexedScopes.clear(); // 清空哈希表
		indexedScopes.put(new Integer(0), globalScope); // gloabalScope为ScriptOrFnScope对象，构造函数参数为-1,null
		mode = BUILDING_SYMBOL_TREE; // （BUILDING_SYMBOL_TREE值为1）
		parseScope(globalScope);
		
		globalScope.warning();
	}

	public void mungeSymboltree(boolean analyzed) {

		if (!munge) {
			return;
		}

		// One problem with obfuscation resides in the use of undeclared
		// and un-namespaced global symbols that are 3 characters or less
		// in length. Here is an example:
		//
		// var declaredGlobalVar;
		//
		// function declaredGlobalFn() {
		// var localvar;
		// localvar = abc; // abc is an undeclared global symbol
		// }
		//
		// In the example above, there is a slim chance that localvar may be
		// munged to 'abc', conflicting with the undeclared global symbol
		// abc, creating a potential bug. The following code detects such
		// global symbols. This must be done AFTER the entire file has been
		// parsed, and BEFORE munging the symbol tree. Note that declaring
		// extra symbols in the global scope won't hurt.
		//
		// Note: Since we go through all the tokens to do this, we also use
		// the opportunity to count how many times each identifier is used.

		offset = 0;
		braceNesting = 0;
		scopes.clear();
		mode = CHECKING_SYMBOL_TREE;
		parseScope(globalScope);

		if (analyzed) { // 进行了分析，说明用户想主动输入混淆变量，优先使用用户输入的混淆变量
			manMung();
		}
		globalScope.munge();
	}

	public StringBuffer printSymbolTree(int linebreakpos,
			boolean preserveAllSemiColons) throws IOException {

		offset = 0;
		braceNesting = 0;
		scopes.clear();

		String symbol;
		JavaScriptToken token;
		// begin
		if (tokens.size() == 0) {
			StringBuffer result = new StringBuffer();
			return result;
		}
		// end
		JavaScriptToken lastToken = getToken(0);
		ScriptOrFnScope currentScope;
		JavaScriptIdentifier identifier;

		int length = tokens.size(); // 文本的长度
		StringBuffer result = new StringBuffer();

		int linestartpos = 0;

		enterScope(globalScope); // 将globalScope压入栈scopes中

		while (offset < length) {

			token = consumeToken();
			symbol = token.getValue();
			currentScope = getCurrentScope();
			switch (token.getType()) {
			case Token.GET:
			case Token.SET:
				lastToken = token; // 注意没有break;

			case Token.NAME:

				if (offset >= 2 && getToken(-2).getType() == Token.DOT
						|| getToken(0).getType() == Token.OBJECTLIT) {

					result.append(symbol);

				} else {
					identifier = getIdentifier(symbol, currentScope);
					if (identifier != null) {
						if (identifier.getMungedValue() != null) {
							result.append(identifier.getMungedValue());
						} else {
							result.append(symbol);
						}
						if (currentScope != globalScope // 全局域中的变量可能被HTML文档使用，不需要发出警告
								&& identifier.getRefcount() == 0) {
							warn("标识符" + symbol + "已经声明但未使用 ", true);
						}
					} else {
						result.append(symbol);
					}
				}
				break;

			case Token.REGEXP:
			case Token.STRING:
				result.append(symbol);
				break;

			case Token.NUMBER:
				if (getToken(0).getType() == Token.DOT) {
					// calling methods on int requires a leading dot so JS
					// doesn't
					// treat the method as the decimal component of a float
					result.append('(');
					result.append(symbol);
					result.append(')');
				} else {
					result.append(symbol);
				}
				break;

			case Token.ADD:
			case Token.SUB:
				result.append(literals.get(new Integer(token.getType())));
				if (offset < length) {
					token = getToken(0);
					if (token.getType() == Token.INC
							|| token.getType() == Token.DEC
							|| token.getType() == Token.ADD
							|| token.getType() == Token.DEC) {
						// Handle the case x +/- ++/-- y
						// We must keep a white space here. Otherwise, x +++ y
						// would be
						// interpreted as x ++ + y by the compiler, which is a
						// bug (due
						// to the implicit(隐式的) assignment being done on the
						// wrong variable)
						result.append(' ');
					} else if (token.getType() == Token.POS
							&& getToken(-1).getType() == Token.ADD
							|| token.getType() == Token.NEG
							&& getToken(-1).getType() == Token.SUB) {
						// Handle the case x + + y and x - - y
						result.append(' ');
					}
				}
				break;

			case Token.FUNCTION:
				if (lastToken.getType() != Token.GET
						&& lastToken.getType() != Token.SET) {
					result.append("function");
				}
				lastToken = token;
				token = consumeToken();
				if (token.getType() == Token.NAME) {
					result.append(' ');
					symbol = token.getValue();
					identifier = getIdentifier(symbol, currentScope);
					assert identifier != null;
					if (identifier.getMungedValue() != null) {
						result.append(identifier.getMungedValue());
					} else {
						result.append(symbol);
					}
					if (currentScope != globalScope
							&& identifier.getRefcount() == 0) {
						warn("标识符" + symbol + "已经声明但未使用", true);
					}
					token = consumeToken();
				}
				assert token.getType() == Token.LP;
				result.append('(');
				currentScope = indexedScopes.get(new Integer(offset)); // 根据左圆括号的下一个索引映射得到函数作用域
				enterScope(currentScope);
				while ((token = consumeToken()).getType() != Token.RP) {
					assert token.getType() == Token.NAME
							|| token.getType() == Token.COMMA;
					if (token.getType() == Token.NAME) {
						symbol = token.getValue();
						identifier = getIdentifier(symbol, currentScope);
						assert identifier != null;
						if (identifier.getMungedValue() != null) {
							result.append(identifier.getMungedValue());
						} else {
							result.append(symbol);
						}
					} else if (token.getType() == Token.COMMA) {
						result.append(',');
					}
				}
				result.append(')');
				token = consumeToken(); // 得到左花括号
				assert token.getType() == Token.LC;
				result.append("{"); // nomodify
				braceNesting++;
				token = getToken(0);
				if (token.getType() == Token.STRING
						&& getToken(1).getType() == Token.SEMI) {
					// This is a hint. Skip it!
					consumeToken();
					consumeToken();
				}
				break;

			case Token.RETURN:
			case Token.TYPEOF:
				result.append(literals.get(new Integer(token.getType())));
				// No space needed after 'return' and 'typeof' when followed
				// by '(', '[', '{', a string or a regexp.
				if (offset < length) {
					token = getToken(0);
					if (token.getType() != Token.LP
							&& token.getType() != Token.LB
							&& token.getType() != Token.LC
							&& token.getType() != Token.STRING
							&& token.getType() != Token.REGEXP
							&& token.getType() != Token.SEMI) {
						result.append(' ');
					}
				}
				break;

			case Token.CASE:
			case Token.THROW:
				result.append(literals.get(new Integer(token.getType())));
				// White-space needed after 'case' and 'throw' when not followed
				// by a string.
				if (offset < length && getToken(0).getType() != Token.STRING) {
					result.append(' ');
				}
				break;

			case Token.BREAK:
			case Token.CONTINUE:
				result.append(literals.get(new Integer(token.getType())));
				if (offset < length && getToken(0).getType() != Token.SEMI) {
					// If 'break' or 'continue' is not followed by a
					// semi-colon（分号）, it must
					// be followed by a label, hence(因此) the need for a white
					// space.
					result.append(' ');
				}
				break;

			case Token.LC:
				result.append("{"); // nomodify
				braceNesting++;
				break;

			case Token.RC:
				result.append('}');
				braceNesting--;
				assert braceNesting >= currentScope.getBraceNesting();
				if (braceNesting == currentScope.getBraceNesting()) {
					leaveCurrentScope();
				}
				break;

			case Token.SEMI:
				// No need to output a semi-colon if the next character is a
				// right-curly...
				if (preserveAllSemiColons || offset < length
						&& getToken(0).getType() != Token.RC) {
					result.append(';');
				}

				if (linebreakpos >= 0
						&& result.length() - linestartpos > linebreakpos) {
					// Some source control tools don't like it when files
					// containing lines longer
					// than, say 8000 characters, are checked in. The linebreak
					// option is used in
					// that case to split long lines after a specific column.
					result.append('\n');
					linestartpos = result.length();
				}
				break;

			case Token.COMMA:
				// No need to output a comma if the next character is a
				// right-curly or a right-square bracket
				if (offset < length && getToken(0).getType() != Token.RC
						&& getToken(0).getType() != Token.RB) {
					result.append(',');
				}
				break;

			case Token.CONDCOMMENT:
			case Token.KEEPCOMMENT:
				if (result.length() > 0
						&& result.charAt(result.length() - 1) != '\n') {
					result.append("\n");
				}
				result.append("/*");
				if (token.getType() == Token.KEEPCOMMENT) {
					result.append("!");
				}
				result.append(symbol);
				result.append("*/\n");
				break;

			// begin--这个分支用于实现压扁控制流
			// 此分支将if(expression){...}else if(expression){...}else{...}转化为
			// switch(expression){case true:原if块;break;case false:原else块;break;}
			case Token.IF:
				if (!EditOptionPanel.checkBoxFlatten.isSelected()) {
					result.append(symbol);
					break;
				}
				FlattenIF flattenIF = new FlattenIF(offset, length, tokens,
						currentScope);
				String switchBlock = flattenIF.flattenIF();
				result.append(switchBlock);
				offset = flattenIF.offset;
				break;
			case Token.WHILE:
				if (!EditOptionPanel.checkBoxOpacity.isSelected()) {
					result.append(symbol);
					break;
				}
				result.append(symbol);
				OpacityPredicate opacityPredicateWhile = new OpacityPredicate(
						offset, tokens, currentScope);
				String whileBlock = opacityPredicateWhile
						.opacityPredicateWhile();
				result.append(whileBlock);
				offset = opacityPredicateWhile.offset;
				break;
			case Token.DO:
				if (!EditOptionPanel.checkBoxOpacity.isSelected()) {
					result.append(symbol);
					break;
				}
				result.append(symbol);
				OpacityPredicate opacityPredicateDoWhile = new OpacityPredicate(
						offset, tokens, currentScope);
				String doWhileBlock = opacityPredicateDoWhile
						.opacityPredicateDoWhile();
				result.append(doWhileBlock);
				offset = opacityPredicateDoWhile.offset;
				break;
			// end
			default:
				String literal = literals.get(new Integer(token.getType()));
				if (literal != null) {
					result.append(literal);
				} else {
					warn("此标志符不能被打印出来：" + symbol, true);
				}
				break;
			}
		}

		// Append a semi-colon at the end, even if unnecessary semi-colons are
		// supposed to be removed. This is especially useful when concatenating
		// several minified files (the absence of an ending semi-colon at the
		// end of one file may very likely cause a syntax error)
		/*if (!preserveAllSemiColons && result.length() > 0
				&& getToken(-1).getType() != Token.CONDCOMMENT
				&& getToken(-1).getType() != Token.KEEPCOMMENT) {
			if (result.charAt(result.length() - 1) == '\n') {
				result.setCharAt(result.length() - 1, ';');
			} else {
				result.append(';');
			}
		}*///暂时用不上，注释掉

		return result;
	}

	public String printMungeMapping() {
		StringBuffer sb = new StringBuffer();
		globalScope.getFullMapping(sb, "");
		return sb.toString();
	}

	private Map<ScriptOrFnScope, List<String>> scopeSymbolMaping = new LinkedHashMap<ScriptOrFnScope, List<String>>();

	public Vector<Vector<String>> getOriginalValue() {
		Vector<Vector<String>> vt = new Vector<Vector<String>>();
		globalScope.getOriginalValue(vt, scopeSymbolMaping);
		return vt;
	}

	private void manMung() { // 手动混淆，即由用户输入混淆变量
		JTable table = AnalyseWarnPanel.table;
		Set<ScriptOrFnScope> scopes = scopeSymbolMaping.keySet();
		Iterator<ScriptOrFnScope> scopesSetIt = scopes.iterator();
		int i = 0;
		while (scopesSetIt.hasNext()) {
			ScriptOrFnScope tmpScope = scopesSetIt.next();
			List<String> symbolList = scopeSymbolMaping.get(tmpScope);
			Iterator<String> symbolListIt = symbolList.iterator();
			while (symbolListIt.hasNext()) {
				String symbol = symbolListIt.next();
				JavaScriptIdentifier identifier = tmpScope
						.getIdentifier(symbol);
				String mungedValue = table.getValueAt(i, 1).toString().trim();
				if (identifier.isMarkedForMunging() && !mungedValue.equals("null")) {
					identifier.setMungedValue(mungedValue);
				}
				i++;
			}
		}
	}
	
	public ScriptOrFnScope getGlobalScope() {
		return globalScope;
	}
}
