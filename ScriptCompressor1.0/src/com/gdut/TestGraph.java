package com.gdut;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

public class TestGraph {

	public static void main(String[] args) {
		String[] cmd = { "Graphviz2.36/bin/dot.exe", "-Tpng",
				"-o", "out/flowgraphs/flowgraph.png",
				"out/flowgraphs/final.dot" };
		Runtime rt = Runtime.getRuntime();
		try {
			Process p = rt.exec(cmd);
			int exitValue = p.waitFor();
			System.out.println(exitValue);
			p.destroy();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		System.out.println("wwww");
	}
}
