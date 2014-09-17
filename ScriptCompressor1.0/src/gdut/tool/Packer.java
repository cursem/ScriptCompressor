package gdut.tool;

import java.io.FileNotFoundException;
import java.io.FileReader;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class Packer {
	
	public String packer(String infile) {
		ScriptEngineManager mgr = new ScriptEngineManager();
		ScriptEngine JSEngine = mgr.getEngineByExtension("js");
		String packeStr = "";
		try {
			JSEngine.eval(new FileReader("lib/packer.js"));
			Invocable inv = (Invocable)JSEngine;
			packeStr = inv.invokeFunction("main",infile).toString();
		} catch (ScriptException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return packeStr;
	}
}
