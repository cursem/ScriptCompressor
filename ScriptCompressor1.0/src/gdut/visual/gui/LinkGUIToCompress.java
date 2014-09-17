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

	// �༭ѡ��
	private static String type;
	private static int linebreakpos;
	private static boolean munge;
	private static boolean verbose;
	private static boolean preserveAllSemiColons;
	private static boolean optimizations;
	private static boolean autoRecompose;
	private static boolean packer;

	public static void goToCompress(String analyzeOrCompress, String single) {
		// ����ո�����ʾ���
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
	private static boolean analyzed = false;// ���ڼ�¼�Ƿ�ʹ���˷�������
	private static Reader in = null;
	private static Writer out = null;

	private static void AnalyzeOrCompress(String analyzeOrCompress) { // �����׶����˼򵥵��ַ���������ͽ����﷨��
		String beforeObfFile = OpenSaveToPanel.beforeObfFile;
		String afterObfFile = OpenSaveToPanel.afterObfFile;
		if (type.equals("css")) {
			if (analyzeOrCompress.equals("analyzeOnly")) {
				JOptionPane.showMessageDialog(MainFrame.frame, "CSS�ļ����ܽ��б�������",
						"JavaScript������", JOptionPane.WARNING_MESSAGE);
				return;
			} else {
				if(beforeObfFile==null) {
					JOptionPane.showMessageDialog(MainFrame.frame, "û��ѡ��Դ�ļ�",
							"JavaScript������", JOptionPane.WARNING_MESSAGE);
					return;
				}
				if(afterObfFile==null) {
					JOptionPane.showMessageDialog(MainFrame.frame, "û��ѡ�񱣴�·��",
							"JavaScript������", JOptionPane.WARNING_MESSAGE);
					return;
				}
				
				Reader in = null;
				try {
					charset = FileCode.getCharsetByString(beforeObfFile);
					in = new InputStreamReader(new FileInputStream(
							beforeObfFile), charset);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
					JOptionPane.showMessageDialog(MainFrame.frame, "Դ�ļ�"
							+ beforeObfFile + "�����ڻ��ƶ�", "JavaScript������",
							JOptionPane.WARNING_MESSAGE);
					return;
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
					JOptionPane.showMessageDialog(MainFrame.frame, "Դ�ļ�"
							+ beforeObfFile + "�ı��벻��֧��", "JavaScript������",
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
					JOptionPane.showMessageDialog(MainFrame.frame, "Ŀ���ļ�"
							+ afterObfFile + "�ı��벻��֧��", "JavaScript������",
							JOptionPane.WARNING_MESSAGE);
					return;
				} catch (FileNotFoundException e) {
					e.printStackTrace();
					JOptionPane.showMessageDialog(MainFrame.frame, "Ŀ��·����"
							+ afterObfFile + "��������", "JavaScript������",
							JOptionPane.WARNING_MESSAGE);
					return;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}
		
		/*
		 * �����Ƕ�css����Ĵ��������Ƕ�js����Ĵ���
		 */
		
		if (analyzed == false||analyzeOrCompress.equals("analyzeOnly")) { // û�о��������׶Σ����û����������,����з����������˴���ĳ�������ͽ����﷨����
			try {
				if (beforeObfFile == null) {
					JOptionPane.showMessageDialog(MainFrame.frame, "û��ѡ��Դ�ļ�",
							"JavaScript������", JOptionPane.WARNING_MESSAGE);
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

				analyzed = true; // ���Ϊ�Ѿ����������׶�
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(MainFrame.frame, "Դ�ļ�"
						+ beforeObfFile + "�����ڻ��ƶ�", "JavaScript������",
						JOptionPane.WARNING_MESSAGE);
				return;
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(MainFrame.frame, "Դ�ļ�"
						+ beforeObfFile + "�ı��벻��֧��", "JavaScript������",
						JOptionPane.WARNING_MESSAGE);
				return;
			} catch (EvaluatorException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (analyzeOrCompress.equals("analyzeOnly")) { // ����������
			// ��������õ��ı������������ȱ�ʶ��
			Vector<Vector<String>> originalValue = compressor
					.getOriginalValue();
			AnalyseWarnPanel.addTable(originalValue);
			return;
		}
		try {
			if (afterObfFile == null) {
				JOptionPane.showMessageDialog(MainFrame.frame, "û��ѡ�񱣴�·��",
						"JavaScript������", JOptionPane.WARNING_MESSAGE);
				return;
			}
			out = new OutputStreamWriter(new FileOutputStream(afterObfFile),
					charset);
			// ��ʵ��û����ones,twos,threes�е��ַ��滻Դ�����еı����������������Ƕ�Ӧ�ؽ����Ǵ��뵽JavaScriptIdentifier��mungedValue��
			compressor.mungeSymboltree(analyzed);
			if (EditOptionPanel.checkBoxOpacity.isSelected()
					|| EditOptionPanel.checkBoxFlatten.isSelected()) {
				RandomArray.setAddVarName();//���ú�RandomArray���кͱ�����Ҫ��ӵ�Դjs�����еı���
			}
			StringBuffer sb = null;
			sb = compressor
					.printSymbolTree(linebreakpos, preserveAllSemiColons);
			String tmp;
			if (EditOptionPanel.checkBoxOpacity.isSelected()
					|| EditOptionPanel.checkBoxFlatten.isSelected()) {// ���벻͸��ν�ʻ�ѹ���������Ҫ��һЩ�������
				tmp = RandomArray.randomArray() + sb.toString();//�����������
			} else {
				tmp = sb.toString();
			}
			if (packer) { // packerѹ��
				tmp = new Packer().packer(tmp);
			}
			if(autoRecompose){//���޸�
	        	tmp = AutoRecompose.autoRecompose(tmp);
	        }
			out.write(tmp);
			out.close();
			PreLaterCodePanel.ioTabbledPane.setSelectedIndex(1);
			AnalyseWarnPanel.mungeTabbedPane.setSelectedIndex(1);
			// ��ʾ����ǰ��������ͺ�����
			AnalyseWarnPanel.resultTextArea.setText(compressor
					.printMungeMapping());
			// ��ʾ��������ļ�
			PreLaterCodePanel.laterCodeTextArea.setText(tmp);
			PreLaterCodePanel.laterCodeTextArea.setForeground(Color.BLACK);
			analyzed = false; // �����������Ĵ�����̣��������÷���δ���еı�־
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(MainFrame.frame, "Ŀ��·����"
					+ afterObfFile + "��������", "JavaScript������",
					JOptionPane.WARNING_MESSAGE);
			return;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(MainFrame.frame, "Ŀ���ļ�"
					+ afterObfFile + "�ı��벻��֧��", "JavaScript������",
					JOptionPane.WARNING_MESSAGE);
			return;
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	
	
	
	
	private static void multiCompress() { // ��������
		try {
			String[] fileToSave = FileListPanel.saveToFiles; // ��������ʱ������ļ���
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
					 * ��if�鶼�Ƕ�Դ����ĸ��ִ�������������ѹ���ȣ�
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
						compressor.mungeSymboltree(false);// analyzedĬ��Ϊfalse�����˶��ļ�����
																// �����û�ָ����������

						if (EditOptionPanel.checkBoxOpacity.isSelected()
								|| EditOptionPanel.checkBoxFlatten.isSelected()) {
							RandomArray.setAddVarName();//���ú�RandomArray���кͱ�����Ҫ��ӵ�Դjs�����еı���
						}
						StringBuffer sb = null;
						sb = compressor.printSymbolTree(linebreakpos,
								preserveAllSemiColons);
						String tmp;
						if (EditOptionPanel.checkBoxOpacity.isSelected()
								|| EditOptionPanel.checkBoxFlatten.isSelected()) { // ���벻͸��ν�ʻ�ѹ���������Ҫ��һЩ�������
							tmp = RandomArray.randomArray()
									+ sb.toString();
						} else {
							tmp = sb.toString();
						}
						if (packer) { // packerѹ��
							tmp = new Packer().packer(tmp);
						}
						if(autoRecompose){//���޸�
				        	tmp = AutoRecompose.autoRecompose(tmp);
				        }
						out.write(tmp);
						out.close();
						out = null;

						// ��ʾ������ı������ͺ�����
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
				// ��ʾ��������ļ�������һ���ļ���
				charset = FileCode.getCharsetByString(MenuBar.fileToSavePath
						+ fileToSave[0]);
				BufferedReader buffReader = new BufferedReader(
						new InputStreamReader(new FileInputStream(
								MenuBar.fileToSavePath + fileToSave[0]),
								charset));
				PreLaterCodePanel.laterCodeTextArea.read(buffReader,
						MenuBar.fileToSavePath + fileToSave[0]);
				PreLaterCodePanel.laterCodeTextArea.setForeground(Color.BLACK); // ������ʾ����ɫ
				PreLaterCodePanel.ioTabbledPane.setSelectedIndex(1);
				buffReader.close();
				buffReader = null;

			} else {
				JOptionPane.showMessageDialog(MainFrame.frame,
						"��û��ѡ��Դ�ļ����ļ�����·��", "JavaScript������",
						JOptionPane.WARNING_MESSAGE);
			}
		} catch (IOException e) {
			JOptionPane.showMessageDialog(MainFrame.frame, "��û��ѡ��Դ�ļ����ļ�����·��",
					"JavaScript������", JOptionPane.WARNING_MESSAGE);
		}
	}
}