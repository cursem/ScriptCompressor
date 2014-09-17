package com.yahoo.platform.yui.compressor;

import java.util.ArrayList;
import java.util.Stack;

import org.mozilla.javascript.Token;


public class OpacityPredicate {
	public int offset;
	private ArrayList<JavaScriptToken> tokens;
	private JavaScriptToken token;
	private JavaScriptIdentifier identifier;
	private ScriptOrFnScope currentScope;
	
	public String a = "_g$";
	public String b = "_m$";
	public OpacityPredicate(int offset,ArrayList<JavaScriptToken> tokens,ScriptOrFnScope currentScope) {
		
		this.offset = offset;
		this.tokens = tokens;
		this.currentScope = currentScope;
		
		this.a = RandomArray.a;
		this.b = RandomArray.b;
	}
	
	public String opacityPredicateWhile() {
			return "("+"!("+a+"[("+b+"+5)%20]%"+a+"[("+b+"+2)%20]-2*("+a+"[("+b+"+4)%20]%7))"+"&&"+"("+getExpression()+")"+")"
			+"{"+a+"[("+b+"+5)%20]="+a+"[("+b+"+11)%20]-2*"+a+"[("+b+"+8)%20];"+getWhileBody()+"}";
	}
	public String opacityPredicateDoWhile() {
		String doWhileBody = getWhileBody();
		consumeToken();
		return "{"+doWhileBody+""+a+"[("+b+"+5)%20]="+a+"[("+b+"+11)%20]-2*"+a+"[("+b+"+8)%20];}"
				+"while("+"!("+a+"[("+b+"+5)%20]%"+a+"[("+b+"+2)%20]-2*("+a+"[("+b+"+4)%20]%7))"+"&&"+"("+getExpression()+")"+")";
	}
	/*
	 * 获得while循环的判断条件：expression,未包括括號
	 */
	private String getExpression() {
		String tmpStr = "";
		Stack<Integer> LPRP= new Stack<Integer>();
		LPRP.push(consumeToken().getType());
		while(!LPRP.isEmpty()) {	//获得while循环的判断条件：expression
			token = consumeToken();
			identifier = getIdentifier(token.getValue(),
					currentScope);
			if(identifier != null&&identifier.getMungedValue() != null) {
				tmpStr += identifier.getMungedValue();
			}else {
				tmpStr += token.getValue();
			}
			if(getToken(0).getType() == Token.LP){
				LPRP.push(token.getType());
			}
			if(getToken(0).getType() == Token.RP) {
				LPRP.pop();
			}	
		}
		consumeToken();	//消費掉右圓括號
		return tmpStr;
	}
	
	/*
	 * 得到while循環體,未包含左右花括号
	 */
	private String getWhileBody() {
		String tmpStr= "";
		Stack<Integer> LCRC = new Stack<Integer>();
		LCRC.push(consumeToken().getType());	//左花括号压入栈
		
		while(!LCRC.isEmpty()) {	//得到while循環體,未包含左右花括号
			token = consumeToken();
			identifier = getIdentifier(token.getValue(),
					currentScope);
			if(identifier != null&&identifier.getMungedValue() != null) {
				tmpStr += identifier.getMungedValue();
			}else {
				tmpStr += token.getValue();
			}
			if(getToken(0).getType() == Token.LC){
				LCRC.push(token.getType());
			}
			if(getToken(0).getType() == Token.RC) {
				LCRC.pop();
			}						
		}
		consumeToken();
		return tmpStr;
	}
	
	private JavaScriptToken consumeToken() {
		return tokens.get(offset++);
	}

	private JavaScriptToken getToken(int delta) {
		return tokens.get(offset + delta);
	}
	
	private JavaScriptIdentifier getIdentifier(String symbol,
			ScriptOrFnScope scope) {
		JavaScriptIdentifier identifier;
		while (scope != null) {
			identifier = scope.getIdentifier(symbol);
			if (identifier != null) {
				return identifier;
			}
			scope = scope.getParentScope();
		}
		return null;
	}
}
