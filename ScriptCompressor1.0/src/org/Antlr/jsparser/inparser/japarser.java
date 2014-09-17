package org.Antlr.jsparser.inparser;

import java.io.InputStream;

import org.antlr.runtime.*;
import org.antlr.runtime.tree.*;

public class japarser {
	public static Tree parser(InputStream in) throws Exception{
		//导入输入流
		ANTLRInputStream input = new ANTLRInputStream(in);
		//开始解析
		//javascriptLexer lexer = new javascriptLexer(input);
		//CommonTokenStream tokens = new CommonTokenStream(lexer);
		//javascriptParser parser = new javascriptParser(tokens);
		//javascriptParser.program_return r = parser.program();
		//返回AST树
		JavaScriptLexer lexer = new JavaScriptLexer(input);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		JavaScriptParser parser = new JavaScriptParser(tokens);
		JavaScriptParser.program_return r = parser.program();
		return (BaseTree)r.getTree();
	}
}
