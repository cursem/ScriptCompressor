package gdut.visual.gui;

import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.Writer;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToolBar;

import dk.brics.tajs.Main;

;

public class ToolBar extends JToolBar {
	private static final long serialVersionUID = -3076031869227224700L;

	public static JButton openButton;
	public static JButton saveButton;
	private JButton newFileButton;
	private JButton saveFileButton;
	public static JButton analysistButton;
	public static JButton mungeButton;
	public static JButton beGraphButton;// 处理前流程图
	public static JButton afGraphButton;// 处理后流程图
	private JButton helpButton;

	public ToolBar() {
		init();
	}

	public void init() {
		setRequestFocusEnabled(false);
		openButton = new JButton(new ImageIcon("bin/ui/img/folder_add.png"));
		openButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				JFileChooser chooser = new JFileChooser();
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				chooser.setDialogTitle("选定文件夹");
				int option = chooser.showDialog(null, "确定");
				if (option == JFileChooser.APPROVE_OPTION) {
					OpenSaveToPanel.singleMungeButton.setEnabled(false);
					MenuBar.analyzeMenuItem.setEnabled(false);
					MenuBar.mungeMenuItem.setEnabled(false);
					analysistButton.setEnabled(false);
					mungeButton.setEnabled(false);
					OpenSaveToPanel.multiMungeButton.setEnabled(true);

					OpenSaveToPanel.saveToButton.setEnabled(false);
					MenuBar.saveMenuItem.setEnabled(true);
					saveButton.setEnabled(true);

					MenuBar.sourceFilePath = chooser.getSelectedFile()
							.getAbsolutePath() + "/"; // 得到文件的路径
					OpenSaveToPanel.inputTextField
							.setText(MenuBar.sourceFilePath);
					String[] files = new File(MenuBar.sourceFilePath)
							.list(new FilenameFilter() {
								@Override
								public boolean accept(File dir, String name) {
									int idx = name.lastIndexOf('.');
									String suffix = "";
									if (idx >= 0 && idx < name.length() - 1) {
										suffix = name.substring(idx + 1); // 获取文件名后缀
									}
									return suffix.equals(EditOptionPanel.type) ? true
											: false;
								}

							});
					FileListPanel.fileVector.clear();
					for (int i = 0; i < files.length; ++i) {
						FileListPanel.fileVector.add(files[i]);
					}
					FileListPanel.fileList.updateUI();

					MenuBar.isNewFile = false;
					// 将词法分析面板清空
					AnalyseWarnPanel.analyseScPane
							.setViewportView(AnalyseWarnPanel.analyseTextArea);
					AnalyseWarnPanel.resultTextArea.setText("");
				}
			}
		});
		openButton.setFocusPainted(false);
		openButton.setToolTipText("选定源程序所在文件夹");
		add(openButton);

		saveButton = new JButton(new ImageIcon("bin/ui/img/saveTo.png"));
		saveButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				chooser.setDialogTitle("选定文件夹");
				int result = chooser.showDialog(null, "确定");
				if (result == JFileChooser.APPROVE_OPTION) {
					MenuBar.fileToSavePath = chooser.getSelectedFile()
							.getAbsolutePath() + "/";
					OpenSaveToPanel.outTextField
							.setText(MenuBar.fileToSavePath);
				}
			}

		});
		saveButton.setFocusPainted(false);
		saveButton.setToolTipText("选定保存文件夹");
		add(saveButton);

		addSeparator();

		newFileButton = new JButton(new ImageIcon("bin/ui/img/newFile.gif"));
		newFileButton.setFocusPainted(false);
		newFileButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				FileDialog saveFileDialog = new FileDialog(MainFrame.frame,
						"保存文件", FileDialog.SAVE);
				saveFileDialog.setVisible(true);
				String direct = saveFileDialog.getDirectory(); // 此方法得到的路径最后是有斜杠的
				String fileName = saveFileDialog.getFile();
				if (direct != null) { // 点击了保存按钮
					OpenSaveToPanel.inputTextField.setText(direct + fileName);
					OpenSaveToPanel.beforeObfFile = direct + fileName;

					OpenSaveToPanel.multiMungeButton.setEnabled(false);
					OpenSaveToPanel.singleMungeButton.setEnabled(true);
					MenuBar.analyzeMenuItem.setEnabled(true);
					MenuBar.mungeMenuItem.setEnabled(true);
					MenuBar.saveMenuItem.setEnabled(false);
					OpenSaveToPanel.saveToButton.setEnabled(true);
					ToolBar.saveButton.setEnabled(false);

					FileListPanel.fileVector.clear();
					FileListPanel.fileList.updateUI();

					// 将词法分析面板清空
					AnalyseWarnPanel.analyseScPane
							.setViewportView(AnalyseWarnPanel.analyseTextArea);
					AnalyseWarnPanel.resultTextArea.setText("");
					AnalyseWarnPanel.warnTextPane.setText("");

					PreLaterCodePanel.preCodeTextArea.setText("");
					PreLaterCodePanel.preCodeTextArea.setText("");
					MenuBar.isNewFile = true;
				}
			}
		});
		newFileButton.setToolTipText("新建文件");
		add(newFileButton);
		saveFileButton = new JButton(new ImageIcon("bin/ui/img/save.gif"));
		saveFileButton.setFocusPainted(false);
		saveFileButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (MenuBar.isNewFile) { // 新建了文件才会执行保存操作
					String fileName = OpenSaveToPanel.beforeObfFile;
					try {
						Writer out = new FileWriter(fileName);
						out.write(PreLaterCodePanel.preCodeTextArea.getText());
						out.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		});
		add(saveFileButton);

		addSeparator();

		analysistButton = new JButton(new ImageIcon("bin/ui/img/analysis.gif"));
		analysistButton.setFocusPainted(false);
		analysistButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (!EditOptionPanel.checkBoxMunge.isSelected()) {
					JOptionPane.showMessageDialog(MainFrame.frame, "请勾选混淆变量",
							"提示", JOptionPane.WARNING_MESSAGE);
					return;
				}
				LinkGUIToCompress.goToCompress("analyzeOnly", "single");
			}
		});
		analysistButton.setToolTipText("分析源代码");
		add(analysistButton);
		mungeButton = new JButton(new ImageIcon("bin/ui/img/obfu.gif"));
		mungeButton.setFocusPainted(false);
		mungeButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				LinkGUIToCompress.goToCompress("munge", "single");
			}
		});
		mungeButton.setToolTipText("执行混淆");
		add(mungeButton);
		addSeparator();

		beGraphButton = new JButton(new ImageIcon("bin/ui/img/beforeGraph.png"));
		beGraphButton.setFocusPainted(false);
		beGraphButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				getGraph(OpenSaveToPanel.beforeObfFile,"beflowgraph.png");
				try {
					File file = new File("out/beflowgraph.png");
					String absolutePath = file.getAbsolutePath();
					System.out.println(absolutePath);
					Runtime.getRuntime().exec(
							"rundll32 c:/Windows/System32/shimgvw.dll ImageView_Fullscreen "
									+ absolutePath);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		beGraphButton.setToolTipText("混淆前的JS字节码流程图");
		add(beGraphButton);

		afGraphButton = new JButton(new ImageIcon("bin/ui/img/afterGraph.png"));
		afGraphButton.setFocusPainted(false);
		afGraphButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				getGraph(OpenSaveToPanel.afterObfFile,"afflowgraph.png");
				try {
					File file = new File("out/afflowgraph.png");
					String absolutePath = file.getAbsolutePath();
					System.out.println(absolutePath);
					Runtime.getRuntime().exec(
							"rundll32 c:/Windows/System32/shimgvw.dll ImageView_Fullscreen "
									+ absolutePath);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		afGraphButton.setToolTipText("混淆后的JS字节码流程图");
		add(afGraphButton);
		addSeparator();

		helpButton = new JButton(new ImageIcon("bin/ui/img/help.gif"));
		helpButton.setFocusPainted(false);
		helpButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JOptionPane
						.showMessageDialog(
								new JPanel(),
								"本软件可实现对JavaScript、CSS脚本的压缩和混淆。\r\n其中对于JavaScript脚本的混淆支持较为完善，可以\r\n解释出其语法树，用户可以指定其混淆变量",
								"帮助", JOptionPane.INFORMATION_MESSAGE);
			}
		});
		helpButton.setToolTipText("帮助");
		add(helpButton);
	}

	private void getGraph(String fileName,String imgName) {
		if (fileName != null && new File(fileName).exists()) {
			String[] graphOption = { "-flowgraph", fileName };
			Main.main(graphOption);
			String[] cmd = { "Graphviz2.36/bin/dot.exe", "-Tpng", "-o",
					"out/"+imgName, "out/flowgraphs/final.dot" };
			Runtime rt = Runtime.getRuntime();
			try {
				Process p = rt.exec(cmd);
				p.waitFor();
				p.destroy();
			} catch (IOException e0) {
				e0.printStackTrace();
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			File file = new File("out/flowgraphs");
			String[] files = file.list();
			File deleteFile = null;
			if (files.length != 0) {
				for (String tmpStr : files) {
					deleteFile = new File("out/flowgraphs/" + tmpStr);
					if (deleteFile != null) {
						deleteFile.delete();
					}
				}
			}
		}
	}
}