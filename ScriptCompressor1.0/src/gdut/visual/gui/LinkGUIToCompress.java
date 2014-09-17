package gdut.visual.gui;

import gdut.tool.AutoRecompose;
import gdut.tool.FileCode;
import gdut.tool.Packer;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.Vector;

import javax.swing.JOptionPane;

import org.mozilla.javascript.EvaluatorException;

import com.yahoo.platform.yui.compressor.CssCompressor;
import com.yahoo.platform.yui.compressor.ErrorReporterImpl;
import com.yahoo.platform.yui.compressor.JavaScriptCompressor;
import com.yahoo.platform.yui.compressor.RandomArray;

public class LinkGUIToCompress {

	// 编辑选项
	private static String type;
	private static int linebreakpos;
	private static boolean munge;
	private static boolean verbose;
	private static boolean preserveAllSemiColons;
	private static boolean optimizations;
	private static boolean autoRecompose;
	private static boolean packer;

	public static void goToCompress(String analyzeOrCompress, String single) {
		// 先清空各种显示面板
		AnalyseWarnPanel.resultTextArea.setText("");
		AnalyseWarnPanel.warnTextPane.setText("");
		type = EditOptionPanel.type;
		linebreakpos = Integer.parseInt(EditOptionPanel.lineBreakField
				.getText());
		if (EditOptionPanel.checkBoxMunge.isSelected()) {
			munge = true;
		} else {
			munge = false;
		}

		if (EditOptionPanel.checkBoxVerbose.isSelected()) {
			verbose = true;
		} else {
			verbose = false;
		}
		if (EditOptionPanel.chkDeleteSemi.isSelected()) {
			preserveAllSemiColons = false;
		} else {
			preserveAllSemiColons = true;
		}
		if (EditOptionPanel.chkOptimizations.isSelected()) {
			optimizations = true;
		} else {
			optimizations = false;
		}
		if(EditOptionPanel.checkAutoRecompose.isSelected()) {
			autoRecompose = true;
		}else {
			autoRecompose = false;
		}
		if (EditOptionPanel.checkBoxPacker.isSelected()) {
			packer = true;
		} else {
			packer = false;
		}
		if (single.equals("single")) {
			AnalyzeOrCompress(analyzeOrCompress);
		} else {
			multiCompress();
		}
	}

	public static JavaScriptCompressor compressor = null;
	private static String charset = "GB18030";
	private static boolean analyzed = false;// 用于记录是否使用了分析功能
	private static Reader in = null;
	private static Writer out = null;

	private static void AnalyzeOrCompress(String analyzeOrCompress) { // 分析阶段做了简单的字符常量处理和建立语法树
		String beforeObfFile = OpenSaveToPanel.beforeObfFile;
		String afterObfFile = OpenSaveToPanel.afterObfFile;
		if (type.equals("css")) {
			if (analyzeOrCompress.equals("analyzeOnly")) {
				JOptionPane.showMessageDialog(MainFrame.frame, "CSS文件不能进行变量分析",
						"JavaScript混淆器", JOptionPane.WARNING_MESSAGE);
				return;
			} else {
				if(beforeObfFile==null) {
					JOptionPane.showMessageDialog(MainFrame.frame, "没有选择源文件",
							"JavaScript混淆器", JOptionPane.WARNING_MESSAGE);
					return;
				}
				if(afterObfFile==null) {
					JOptionPane.showMessageDialog(MainFrame.frame, "没有选择保存路径",
							"JavaScript混淆器", JOptionPane.WARNING_MESSAGE);
					return;
				}
				
				Reader in = null;
				try {
					charset = FileCode.getCharsetByString(beforeObfFile);
					in = new InputStreamReader(new FileInputStream(
							beforeObfFile), charset);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
					JOptionPane.showMessageDialog(MainFrame.frame, "源文件"
							+ beforeObfFile + "不存在或被移动", "JavaScript混淆器",
							JOptionPane.WARNING_MESSAGE);
					return;
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
					JOptionPane.showMessageDialog(MainFrame.frame, "源文件"
							+ beforeObfFile + "的编码不被支持", "JavaScript混淆器",
							JOptionPane.WARNING_MESSAGE);
					return;
				}
				Writer out;
				try {
					out = new OutputStreamWriter(new FileOutputStream(
							afterObfFile), charset);
					CssCompressor compressor = new CssCompressor(in);
					String objCSS = compressor.compress(out, linebreakpos);
					PreLaterCodePanel.laterCodeTextArea.setText(objCSS);
					PreLaterCodePanel.ioTabbledPane.setSelectedIndex(1);
					out.close();
					out = null;
					return;
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
					JOptionPane.showMessageDialog(MainFrame.frame, "目标文件"
							+ afterObfFile + "的编码不被支持", "JavaScript混淆器",
							JOptionPane.WARNING_MESSAGE);
					return;
				} catch (FileNotFoundException e) {
					e.printStackTrace();
					JOptionPane.showMessageDialog(MainFrame.frame, "目标路径（"
							+ afterObfFile + "）不存在", "JavaScript混淆器",
							JOptionPane.WARNING_MESSAGE);
					return;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}
		
		/*
		 * 以上是对css代码的处理，以下是对js代码的处理
		 */
		
		if (analyzed == false||analyzeOrCompress.equals("analyzeOnly")) { // 没有经历分析阶段（或用户点击分析）,则进行分析（包含了代码的初步处理和建立语法树）
			try {
				if (beforeObfFile == null) {
					JOptionPane.showMessageDialog(MainFrame.frame, "没有选择源文件",
							"JavaScript混淆器", JOptionPane.WARNING_MESSAGE);
					return;
				}
				charset = FileCode.getCharsetByString(beforeObfFile);
				in = new InputStreamReader(new FileInputStream(beforeObfFile),
						charset);
				compressor = new JavaScriptCompressor(in, new ErrorReporterImpl(),
						munge, verbose);
				compressor.processStringLiterals(optimizations);
				if (optimizations) {
					compressor.optimizeObjectMemberAccess();
					compressor.optimizeObjLitMemberDecl();
				}
				compressor.buildSymbolTree();

				in.close();

				analyzed = true; // 标记为已经经过分析阶段
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(MainFrame.frame, "源文件"
						+ beforeObfFile + "不存在或被移动", "JavaScript混淆器",
						JOptionPane.WARNING_MESSAGE);
				return;
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(MainFrame.frame, "源文件"
						+ beforeObfFile + "的编码不被支持", "JavaScript混淆器",
						JOptionPane.WARNING_MESSAGE);
				return;
			} catch (EvaluatorException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (analyzeOrCompress.equals("analyzeOnly")) { // 仅分析处理
			// 输出分析得到的变量名函数名等标识符
			Vector<Vector<String>> originalValue = compressor
					.getOriginalValue();
			AnalyseWarnPanel.addTable(originalValue);
			return;
		}
		try {
			if (afterObfFile == null) {
				JOptionPane.showMessageDialog(MainFrame.frame, "没有选择保存路径",
						"JavaScript混淆器", JOptionPane.WARNING_MESSAGE);
				return;
			}
			out = new OutputStreamWriter(new FileOutputStream(afterObfFile),
					charset);
			// 其实并没有用ones,twos,threes中的字符替换源代码中的变量名或函数名，而是对应地将他们存入到JavaScriptIdentifier的mungedValue中
			compressor.mungeSymboltree(analyzed);
			if (EditOptionPanel.checkBoxOpacity.isSelected()
					|| EditOptionPanel.checkBoxFlatten.isSelected()) {
				RandomArray.setAddVarName();//设置好RandomArray类中和本类中要添加到源js程序中的变量
			}
			StringBuffer sb = null;
			sb = compressor
					.printSymbolTree(linebreakpos, preserveAllSemiColons);
			String tmp;
			if (EditOptionPanel.checkBoxOpacity.isSelected()
					|| EditOptionPanel.checkBoxFlatten.isSelected()) {// 加入不透明谓词或压扁控制流需要的一些额外代码
				tmp = RandomArray.randomArray() + sb.toString();//加入随机数组
			} else {
				tmp = sb.toString();
			}
			if (packer) { // packer压缩
				tmp = new Packer().packer(tmp);
			}
			if(autoRecompose){//自修改
	        	tmp = AutoRecompose.autoRecompose(tmp);
	        }
			out.write(tmp);
			out.close();
			PreLaterCodePanel.ioTabbledPane.setSelectedIndex(1);
			AnalyseWarnPanel.mungeTabbedPane.setSelectedIndex(1);
			// 显示混淆前后变量名和函数名
			AnalyseWarnPanel.resultTextArea.setText(compressor
					.printMungeMapping());
			// 显示混淆后的文件
			PreLaterCodePanel.laterCodeTextArea.setText(tmp);
			PreLaterCodePanel.laterCodeTextArea.setForeground(Color.BLACK);
			analyzed = false; // 进行了完整的处理过程，重新设置分析未进行的标志
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(MainFrame.frame, "目标路径（"
					+ afterObfFile + "）不存在", "JavaScript混淆器",
					JOptionPane.WARNING_MESSAGE);
			return;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(MainFrame.frame, "目标文件"
					+ afterObfFile + "的编码不被支持", "JavaScript混淆器",
					JOptionPane.WARNING_MESSAGE);
			return;
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	
	
	
	
	private static void multiCompress() { // 批量混淆
		try {
			String[] fileToSave = FileListPanel.saveToFiles; // 批量混淆时的输出文件名
			String[] sourceFiles = FileListPanel.sourceFiles;
			if (sourceFiles != null && sourceFiles.length != 0
					&& MenuBar.sourceFilePath != null
					&& MenuBar.fileToSavePath != null) {
				int i = 0;
				String charset;
				Reader in = null;
				Writer out = null;
				for (String sourceFile : FileListPanel.sourceFiles) {
					charset = FileCode
							.getCharsetByString(MenuBar.sourceFilePath
									+ sourceFile);
					in = new InputStreamReader(new FileInputStream(
							MenuBar.sourceFilePath + sourceFile), charset);
					out = new OutputStreamWriter(new FileOutputStream(
							MenuBar.fileToSavePath + fileToSave[i++]), charset);
					/*
					 * 此if块都是对源代码的各种处理（包括混淆，压缩等）
					 */
					if (type.equals("js")) {
						compressor = new JavaScriptCompressor(
								in, new ErrorReporterImpl(), munge, verbose);
						in.close();
						in = null;

						compressor
								.processStringLiterals(optimizations);
						if (optimizations) {
							compressor.optimizeObjectMemberAccess();
							compressor.optimizeObjLitMemberDecl();
						}
						compressor.buildSymbolTree();
						compressor.mungeSymboltree(false);// analyzed默认为false，即此多文件处理
																// 不让用户指定混淆变量

						if (EditOptionPanel.checkBoxOpacity.isSelected()
								|| EditOptionPanel.checkBoxFlatten.isSelected()) {
							RandomArray.setAddVarName();//设置好RandomArray类中和本类中要添加到源js程序中的变量
						}
						StringBuffer sb = null;
						sb = compressor.printSymbolTree(linebreakpos,
								preserveAllSemiColons);
						String tmp;
						if (EditOptionPanel.checkBoxOpacity.isSelected()
								|| EditOptionPanel.checkBoxFlatten.isSelected()) { // 加入不透明谓词或压扁控制流需要的一些额外代码
							tmp = RandomArray.randomArray()
									+ sb.toString();
						} else {
							tmp = sb.toString();
						}
						if (packer) { // packer压缩
							tmp = new Packer().packer(tmp);
						}
						if(autoRecompose){//自修改
				        	tmp = AutoRecompose.autoRecompose(tmp);
				        }
						out.write(tmp);
						out.close();
						out = null;

						// 显示混淆后的变量名和函数名
						AnalyseWarnPanel.resultTextArea.append(sourceFile
								+ "\r\n" + compressor.printMungeMapping()
								+ "\r\n\r\n");
					} else {
						CssCompressor compressor = new CssCompressor(in);
						compressor.compress(out, linebreakpos);
						out.close();
						out = null;
					}
				}
				AnalyseWarnPanel.mungeTabbedPane.setSelectedIndex(1);
				// 显示混淆后的文件（仅第一个文件）
				charset = FileCode.getCharsetByString(MenuBar.fileToSavePath
						+ fileToSave[0]);
				BufferedReader buffReader = new BufferedReader(
						new InputStreamReader(new FileInputStream(
								MenuBar.fileToSavePath + fileToSave[0]),
								charset));
				PreLaterCodePanel.laterCodeTextArea.read(buffReader,
						MenuBar.fileToSavePath + fileToSave[0]);
				PreLaterCodePanel.laterCodeTextArea.setForeground(Color.BLACK); // 正常显示，黑色
				PreLaterCodePanel.ioTabbledPane.setSelectedIndex(1);
				buffReader.close();
				buffReader = null;

			} else {
				JOptionPane.showMessageDialog(MainFrame.frame,
						"您没有选择源文件或文件保存路径", "JavaScript混淆器",
						JOptionPane.WARNING_MESSAGE);
			}
		} catch (IOException e) {
			JOptionPane.showMessageDialog(MainFrame.frame, "您没有选择源文件或文件保存路径",
					"JavaScript混淆器", JOptionPane.WARNING_MESSAGE);
		}
	}
}