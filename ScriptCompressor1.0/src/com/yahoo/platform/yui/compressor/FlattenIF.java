package com.yahoo.platform.yui.compressor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Stack;

import org.mozilla.javascript.Token;

public class FlattenIF {
	/*
	 * ����Դ����if(){...}else if(){...}else{...}��tokens�еı�����ʽΪif(){...}else {if(){...}else{...}}
	 * ������Ѷ����Ƕ�׻�����ȥ��,elseLCcount��¼���������������
	 */
	private int excessLCcount = 0;
	public int offset;
	private int length;
	private ArrayList<String> expression = new ArrayList<String>();	//��ű��ʽ
	private ArrayList<String> sentence = new ArrayList<String>();	//�������
	private ArrayList<String> caseBranchList = new ArrayList<String>();	//���case��֧
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
		offset += excessLCcount;	//���������������
		
//		if(sentence.size()==1) {	//���û��else...if��else��֧�Ļ����Ͳ�����ѹ��
//			return null;
//		}
		
		
		String a = RandomArray.a;
		String b = RandomArray.b;
		String c = RandomArray.c;
		String d = RandomArray.d;
		String f = RandomArray.f;
		
		Iterator<String> iteratorExp = expression.iterator();
		Iterator<String> iteratorSen = sentence.iterator();
		
		//ֵ�ֱ���1,2,3,4,5,6,
		String[] dispatchValue = {"","7%"+a+"[("+b+"+5)%20]-"+a+"[("+b+"+1)%20]%7",""+a+"[("+b+"+11)%20]%"+a+"[("+b+"+5)%20]",
				""+a+"[("+b+"+13)%20]%7+"+a+"[("+b+"+11)%20]%"+a+"[("+b+"+5)%20]","4*("+a+"[("+b+"+7)%20]%7)",""+a+"[("+b+"+5)%20]%"+a+"[("+b+"+11)%20]","2*("+a+"[("+b+"+15)%20]%"+a+"[("+b+"+5)%20])"};
		//ֵ�ֱ���0,1,2,3
		String[] noAlpha = {"+[]","++[[]][+[]]","++[++[[]][+[]]][+[]]","++[++[++[[]][+[]]][+[]]][+[]]"};
		
		String dispatchTmp1,dispatchTmp2;
		String noAlphaTmp;
		int next=2;  //next��Ϊcase��֧��ֵ
		/*
		 * ����case��֧,����ֵΪ2,3��case��֧�������ַ�������
		 */
		while(iteratorExp.hasNext()) {	
			//��ǲ����<=6���õ�ֵ�ı��ʽ����
			dispatchTmp1 = (next+1)<=6?dispatchValue[next+1]:String.valueOf((next+1));
			dispatchTmp2 = (next+2)<=6?dispatchValue[next+2]:String.valueOf((next+2));
			
			//�����ַ���������
			noAlphaTmp = (next>1&&next<4)?noAlpha[next]:String.valueOf(next);
			caseBranchList.add("case "+noAlphaTmp+":"+iteratorExp.next()+d+"="+dispatchTmp1+";"+"else "+d+"="+dispatchTmp2+";"+"break;");
			next++;
			noAlphaTmp = (next>1&&next<4)?noAlpha[next]:String.valueOf(next);
			caseBranchList.add("case "+noAlphaTmp+":"+iteratorSen.next()+d+"="+a+"[(8+"+b+")%20]%"+a+"[(11+"+b+")%20];break;");
			next++;
		}
		if(iteratorSen.hasNext()) {	//�������һ��case��֧
			caseBranchList.add("case "+(next)+":"+iteratorSen.next()+d+"="+a+"[(0+"+b+")%5]%"+a+"[(1+"+b+")%5];break;");
		}
		//����case 0:��case 1:��֧
		caseBranchList.add("case +[]:"+d+"="+a+"[("+b+"+11)%20]%"+a+"[("+b+"+5)%20];break;");
		caseBranchList.add("case ++[[]][+[]]:"+f+"="+(caseBranchList.size()+2)*2+";break;");//��e��ֵΪ��case��֧������������ִ֤�е��˷�֧ʱ�ܽ���ѭ��
		//��������case��֧
		caseBranchList.add("case ![]:"+a+"[3]=4;"+a+"[0]=7;"+d+"=2*"+a+"[(2+"+b+")%5];break;");
		caseBranchList.add("case ''==0:"+sentence.get(0)+d+"="+a+"[("+a+"[(2+"+b+")%5]+"+b+")%5];break;");
		
		Collections.shuffle(caseBranchList);	//����case��֧��˳��
		caseBranchList.add("default:break;");	//����һ��default��֧
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
	 *	����ж�������(expression)
	 */
	private void getExpression() {
		String tmpStr = "";
		while(getToken(0).getType()!=Token.LC) {	//��whileѭ������ж�������(expression)
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
	 * �õ�ԭif��else...if��else���е�����,δ�������һ�����
	 */
	private void getSentence() {	
		String tmpStr= "";
		Stack<Integer> LCRC = new Stack<Integer>();
		LCRC.push(consumeToken().getType());	//������ѹ��ջ
		
		while(!LCRC.isEmpty()) {	//�õ�ԭif��else...if��else���е�����,δ�������һ�����
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
		
		consumeToken();			//���һ�����������
		//�жϺ����Ƿ���else...if���else��
		if(offset==length||getToken(0).getType()!=Token.ELSE) {	//�ѵ����ı�ĩβ�����û��else�ؼ���
			return ;
		}else if(getToken(1).getType()==Token.LC&&getToken(2).getType()==Token.IF) {	//������else...if��
			excessLCcount++;
			consumeToken();	//������else
			consumeToken();	//������{
			consumeToken();	//������if
			getExpression();
		}else {	//������else��
			consumeToken(); //������else
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

