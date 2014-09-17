package com.yahoo.platform.yui.compressor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Stack;

import org.mozilla.javascript.Token;

public class FlattenIF {
	/*
	 * 对于源码中if(){...}else if(){...}else{...}在tokens中的保存形式为if(){...}else {if(){...}else{...}}
	 * 所以需把多余的嵌套花括号去掉,elseLCcount记录多余的左花括号数量
	 */
	private int excessLCcount = 0;
	public int offset;
	private int length;
	private ArrayList<String> expression = new ArrayList<String>();	//存放表达式
	private ArrayList<String> sentence = new ArrayList<String>();	//存放语句块
	private ArrayList<String> caseBranchList = new ArrayList<String>();	//存放case分支
	private ArrayList<JavaScriptToken> tokens;
	private JavaScriptToken token;
	private JavaScriptIdentifier identifier;
	private ScriptOrFnScope currentScope;
	 
	public FlattenIF(int offset,int length,ArrayList<JavaScriptToken> tokens,ScriptOrFnScope currentScope) {
		this.length = length;
		this.offset = offset;
		this.tokens = tokens;
		this.currentScope = currentScope;
	}
	
	public String flattenIF() {
		
		getExpression();
		offset += excessLCcount;	//跳过多余的右括号
		
//		if(sentence.size()==1) {	//如果没有else...if或else分支的话，就不进行压扁
//			return null;
//		}
		
		
		String a = RandomArray.a;
		String b = RandomArray.b;
		String c = RandomArray.c;
		String d = RandomArray.d;
		String f = RandomArray.f;
		
		Iterator<String> iteratorExp = expression.iterator();
		Iterator<String> iteratorSen = sentence.iterator();
		
		//值分别是1,2,3,4,5,6,
		String[] dispatchValue = {"","7%"+a+"[("+b+"+5)%20]-"+a+"[("+b+"+1)%20]%7",""+a+"[("+b+"+11)%20]%"+a+"[("+b+"+5)%20]",
				""+a+"[("+b+"+13)%20]%7+"+a+"[("+b+"+11)%20]%"+a+"[("+b+"+5)%20]","4*("+a+"[("+b+"+7)%20]%7)",""+a+"[("+b+"+5)%20]%"+a+"[("+b+"+11)%20]","2*("+a+"[("+b+"+15)%20]%"+a+"[("+b+"+5)%20])"};
		//值分别是0,1,2,3
		String[] noAlpha = {"+[]","++[[]][+[]]","++[++[[]][+[]]][+[]]","++[++[++[[]][+[]]][+[]]][+[]]"};
		
		String dispatchTmp1,dispatchTmp2;
		String noAlphaTmp;
		int next=2;  //next作为case分支的值
		/*
		 * 构造case分支,其中值为2,3的case分支的用无字符串代替
		 */
		while(iteratorExp.hasNext()) {	
			//派遣变量<=6的用等值的表达式代替
			dispatchTmp1 = (next+1)<=6?dispatchValue[next+1]:String.valueOf((next+1));
			dispatchTmp2 = (next+2)<=6?dispatchValue[next+2]:String.valueOf((next+2));
			
			//用无字符代替数字
			noAlphaTmp = (next>1&&next<4)?noAlpha[next]:String.valueOf(next);
			caseBranchList.add("case "+noAlphaTmp+":"+iteratorExp.next()+d+"="+dispatchTmp1+";"+"else "+d+"="+dispatchTmp2+";"+"break;");
			next++;
			noAlphaTmp = (next>1&&next<4)?noAlpha[next]:String.valueOf(next);
			caseBranchList.add("case "+noAlphaTmp+":"+iteratorSen.next()+d+"="+a+"[(8+"+b+")%20]%"+a+"[(11+"+b+")%20];break;");
			next++;
		}
		if(iteratorSen.hasNext()) {	//加入最后一个case分支
			caseBranchList.add("case "+(next)+":"+iteratorSen.next()+d+"="+a+"[(0+"+b+")%5]%"+a+"[(1+"+b+")%5];break;");
		}
		//加入case 0:和case 1:分支
		caseBranchList.add("case +[]:"+d+"="+a+"[("+b+"+11)%20]%"+a+"[("+b+"+5)%20];break;");
		caseBranchList.add("case ++[[]][+[]]:"+f+"="+(caseBranchList.size()+2)*2+";break;");//让e的值为总case分支数的两倍，保证执行到此分支时能结束循环
		//加入冗余case分支
		caseBranchList.add("case ![]:"+a+"[3]=4;"+a+"[0]=7;"+d+"=2*"+a+"[(2+"+b+")%5];break;");
		caseBranchList.add("case ''==0:"+sentence.get(0)+d+"="+a+"[("+a+"[(2+"+b+")%5]+"+b+")%5];break;");
		
		Collections.shuffle(caseBranchList);	//打乱case分支的顺序
		caseBranchList.add("default:break;");	//加入一个default分支
		StringBuilder caseBranch = new StringBuilder();
		Iterator<String> iteratorBranchList = caseBranchList.iterator();
		while(iteratorBranchList.hasNext()) {
			caseBranch.append(iteratorBranchList.next());
		}
		
		String switchBlock ="for(var "+f+"=0,"+d+"=0;"+f+"<"
						+caseBranchList.size()
						+";"+f+"++){switch("+d+"){"+caseBranch.toString()+"}"
						+ ""+c+"("+a+",20);}";
		
		return switchBlock;
	}
	/*
	 *	获得判断条件：(expression)
	 */
	private void getExpression() {
		String tmpStr = "";
		while(getToken(0).getType()!=Token.LC) {	//此while循环获得判断条件：(expression)
			token = consumeToken();
			identifier = getIdentifier(token.getValue(),
					currentScope);
			if(identifier != null&&identifier.getMungedValue() != null) {
				tmpStr += identifier.getMungedValue();
			}else {
				tmpStr += token.getValue();
			}
		}
		expression.add("if"+tmpStr);
		
		getSentence();
	}
	/*
	 * 得到原if或else...if或else块中的内容,未包含左右花括号
	 */
	private void getSentence() {	
		String tmpStr= "";
		Stack<Integer> LCRC = new Stack<Integer>();
		LCRC.push(consumeToken().getType());	//左花括号压入栈
		
		while(!LCRC.isEmpty()) {	//得到原if或else...if或else块中的内容,未包含左右花括号
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
		sentence.add(tmpStr);
		
		consumeToken();			//把右花括号消化掉
		//判断后面是否有else...if块或else块
		if(offset==length||getToken(0).getType()!=Token.ELSE) {	//已到达文本末尾或后面没有else关键字
			return ;
		}else if(getToken(1).getType()==Token.LC&&getToken(2).getType()==Token.IF) {	//后面是else...if块
			excessLCcount++;
			consumeToken();	//消化掉else
			consumeToken();	//消化掉{
			consumeToken();	//消化掉if
			getExpression();
		}else {	//后面是else块
			consumeToken(); //消化掉else
			getSentence();
		}
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

