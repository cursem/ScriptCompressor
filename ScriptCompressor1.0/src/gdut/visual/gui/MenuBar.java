package gdut.visual.gui;

import java.awt.FileDialog;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.print.PrinterException;
import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.Writer;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

public class MenuBar {

	private JMenuBar menuBar;
	private JMenu fileMenu;
	private JMenuItem newFileMenuItem;
	private JMenuItem saveFileMenuItem;
	public static JMenuItem openMenuItem;
	public static JMenuItem saveMenuItem;
	private JMenuItem printMenuItem;
	private JMenuItem exitMenuItem;
	private JMenu editMenu;
	private JMenuItem undoMenuItem;
	private JMenuItem redoMenuItem;
	private JMenuItem cutMenuItem;
	private JMenuItem copyMenuItem;
	private JMenuItem pasteMenuItem;
	private JMenuItem deleteMenuItem;
	private JMenu mungeMenu;
	public static JMenuItem analyzeMenuItem;
	public static JMenuItem mungeMenuItem;
	private JMenu helpMenu;
	private JMenuItem helpMenuItem;
	private JMenuItem aboutMenuItem;

	public static String sourceFilePath; // 源文件路径,形式如：C:\Users\wangwensheng\Desktop/
	public static String fileToSavePath; // 混淆后文件的保存路径,形式如：C:\Users\wangwensheng\Desktop/
	public static boolean isNewFile = false;// 是否为新建的文件

	public MenuBar() {
		menuBar = new JMenuBar();
		menuBar.setSize(MainFrame.frame.getWidth(), 380);
		menuBar.setBorderPainted(false);
		fileMenu = new JMenu("\u6587\u4EF6(F)");// 文件
		fileMenu.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 13));
		menuBar.add(fileMenu);

		newFileMenuItem = new JMenuItem("\u65b0\u5efa(N)", new ImageIcon(
				"bin/ui/img/newFile.gif")); // 新建
		newFileMenuItem.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 13));
		newFileMenuItem.addActionListener(new ActionListener() {
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
					isNewFile = true;
				}
			}
		});
		newFileMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N,
				InputEvent.CTRL_MASK));
		fileMenu.add(newFileMenuItem);

		saveFileMenuItem = new JMenuItem("\u4fdd\u5b58(S)", new ImageIcon(
				"bin/ui/img/save.gif")); // 保存
		saveFileMenuItem
				.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 13));
		saveFileMenuItem.addActionListener(new ActionListener() {

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
		saveFileMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
				InputEvent.CTRL_MASK));
		fileMenu.add(saveFileMenuItem);

		fileMenu.addSeparator();

		openMenuItem = new JMenuItem("\u6253\u5f00\u6587\u4ef6\u5939(O)",
				new ImageIcon("bin/ui/img/folder_add.png"));// 打开文件夹
		openMenuItem.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 13));
		openMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				JFileChooser chooser = new JFileChooser();
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				chooser.setDialogTitle("选定文件夹");
				int option = chooser.showDialog(null, "确定");
				if (option == JFileChooser.APPROVE_OPTION) {
					// 进行以下设置以保证按钮的组合应用，如“浏览”与“保存”
					OpenSaveToPanel.singleMungeButton.setEnabled(false);
					OpenSaveToPanel.multiMungeButton.setEnabled(true);
					OpenSaveToPanel.saveToButton.setEnabled(false);
					saveMenuItem.setEnabled(true);
					ToolBar.saveButton.setEnabled(true);

					sourceFilePath = chooser.getSelectedFile()
							.getAbsolutePath() + "/"; // 得到文件的路径
					OpenSaveToPanel.inputTextField.setText(sourceFilePath);
					String[] files = new File(sourceFilePath)
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

					// 将词法分析面板清空
					AnalyseWarnPanel.analyseScPane
							.setViewportView(AnalyseWarnPanel.analyseTextArea);
					AnalyseWarnPanel.resultTextArea.setText("");
					isNewFile = false;
				}
			}
		});
		openMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,
				InputEvent.CTRL_MASK));
		fileMenu.add(openMenuItem);

		saveMenuItem = new JMenuItem("\u4fdd\u5b58\u8def\u5f84(SP)",
				new ImageIcon("bin/ui/img/saveTo.png"));// 保存路径
		saveMenuItem.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 13));
		saveMenuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				chooser.setDialogTitle("\u9009\u5b9a\u6587\u4ef6\u5939");// 选定文件夹
				int result = chooser.showDialog(null, "确定");
				if (result == JFileChooser.APPROVE_OPTION) {
					fileToSavePath = chooser.getSelectedFile()
							.getAbsolutePath() + "/";
					OpenSaveToPanel.outTextField.setText(fileToSavePath);
				}
			}

		});
		saveMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L,
				InputEvent.CTRL_MASK));
		fileMenu.add(saveMenuItem);

		fileMenu.addSeparator();
		printMenuItem = new JMenuItem("\u6253\u5370(P)", new ImageIcon(
				"bin/ui/img/print.gif"));// 打印
		printMenuItem.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 13));
		printMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P,
				InputEvent.CTRL_MASK));
		printMenuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					if (PreLaterCodePanel.ioTabbledPane.getSelectedIndex() == 0) {

						PreLaterCodePanel.preCodeTextArea.print();

					} else {
						PreLaterCodePanel.laterCodeTextArea.print();
					}
				} catch (PrinterException e) {
					e.printStackTrace();
				}
			}
		});
		fileMenu.add(printMenuItem);

		fileMenu.addSeparator();

		exitMenuItem = new JMenuItem("\u9000\u51fa(X)"); // 退出
		exitMenuItem.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 13));
		exitMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				System.exit(0);
			}
		});
		exitMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E,
				InputEvent.CTRL_MASK));
		fileMenu.add(exitMenuItem);

		editMenu = new JMenu("\u7F16\u8F91(E)"); // 编辑
		editMenu.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 13));
		menuBar.add(editMenu);

		undoMenuItem = new JMenuItem("\u64a4\u9500(U)", new ImageIcon(
				"bin/ui/img/undo.gif")); // 撤销
		undoMenuItem.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 13));
		undoMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z,
				InputEvent.CTRL_MASK));
		editMenu.add(undoMenuItem);
		undoMenuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (PreLaterCodePanel.um.canUndo()) {
					PreLaterCodePanel.um.undo();
				}
			}
		});
		
		redoMenuItem = new JMenuItem("\u91cd\u505a(R)", new ImageIcon(
				"bin/ui/img/redo.gif")); // 重做
		redoMenuItem.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 13));
		redoMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y,
				InputEvent.CTRL_MASK));
		editMenu.add(redoMenuItem);
		redoMenuItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(PreLaterCodePanel.um.canRedo()){
					PreLaterCodePanel.um.redo();
				}
			}
		});

		editMenu.addSeparator();

		cutMenuItem = new JMenuItem("\u526a\u5207(T)", new ImageIcon(
				"bin/ui/img/cut.gif"));// 剪切
		cutMenuItem.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 13));
		cutMenuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (PreLaterCodePanel.preCodeTextArea.getSelectedText() != null) {
					PreLaterCodePanel.preCodeTextArea.cut();
				} else if (PreLaterCodePanel.laterCodeTextArea
						.getSelectedText() != null) {
					PreLaterCodePanel.laterCodeTextArea.cut();
				} else if (AnalyseWarnPanel.resultTextArea.getSelectedText() != null) {

				} else {
					AnalyseWarnPanel.warnTextPane.cut();
				}

			}
		});
		cutMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X,
				InputEvent.CTRL_MASK));
		editMenu.add(cutMenuItem);

		editMenu.addSeparator();

		copyMenuItem = new JMenuItem("\u590D\u5236(C)", new ImageIcon(
				"bin/ui/img/copy.gif")); // 复制
		copyMenuItem.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 13));
		copyMenuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (PreLaterCodePanel.preCodeTextArea.getSelectedText() != null) {
					PreLaterCodePanel.preCodeTextArea.copy();
				} else if (PreLaterCodePanel.laterCodeTextArea
						.getSelectedText() != null) {
					PreLaterCodePanel.laterCodeTextArea.copy();
				} else if (AnalyseWarnPanel.resultTextArea.getSelectedText() != null) {
					AnalyseWarnPanel.resultTextArea.copy();
				} else {
					AnalyseWarnPanel.warnTextPane.copy();
				}

			}
		});
		copyMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C,
				InputEvent.CTRL_MASK));
		editMenu.add(copyMenuItem);

		pasteMenuItem = new JMenuItem("\u7C98\u8D34(P)", new ImageIcon(
				"bin/ui/img/paste.gif")); // 粘帖
		pasteMenuItem.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 13));
		pasteMenuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

			}
		});
		pasteMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V,
				InputEvent.CTRL_MASK));
		editMenu.add(pasteMenuItem);

		deleteMenuItem = new JMenuItem("\u5220\u9664(L)", new ImageIcon(
				"bin/ui/img/delete.gif"));// 删除
		deleteMenuItem.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 13));
		deleteMenuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (PreLaterCodePanel.preCodeTextArea.getSelectedText() != null) {
					PreLaterCodePanel.preCodeTextArea.replaceSelection("");
				} else if (PreLaterCodePanel.laterCodeTextArea
						.getSelectedText() != null) {
					PreLaterCodePanel.laterCodeTextArea.replaceSelection("");
				} else if (AnalyseWarnPanel.resultTextArea.getSelectedText() != null) {
					AnalyseWarnPanel.resultTextArea.replaceSelection("");
				} else {
					AnalyseWarnPanel.warnTextPane.replaceSelection("");
				}

			}
		});
		deleteMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D,
				InputEvent.CTRL_MASK));
		editMenu.add(deleteMenuItem);

		// 添加混淆工具菜单
		mungeMenu = new JMenu("\u624b\u5de5\u6df7\u6dc6(O)"); // 手工混淆
		mungeMenu.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 13));
		menuBar.add(mungeMenu);
		analyzeMenuItem = new JMenuItem("\u5206\u6790(A)", new ImageIcon(
				"bin/ui/img/analysis.gif")); // 分析
		analyzeMenuItem.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 13));
		analyzeMenuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(!EditOptionPanel.checkBoxMunge.isSelected()) {
					JOptionPane.showMessageDialog(MainFrame.frame, "请勾选混淆变量",
							"提示", JOptionPane.WARNING_MESSAGE);
					return;
				}
				LinkGUIToCompress.goToCompress("analyzeOnly", "single");
			}
		});
		mungeMenu.add(analyzeMenuItem);
		mungeMenuItem = new JMenuItem("\u6df7\u6dc6(O)", new ImageIcon(
				"bin/ui/img/obfu.gif"));// 混淆
		mungeMenuItem.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 13));
		mungeMenuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				LinkGUIToCompress.goToCompress("munge", "single");
			}
		});
		mungeMenu.add(mungeMenuItem);

		helpMenu = new JMenu("\u5E2E\u52A9(H)"); // 帮助
		helpMenu.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 13));
		menuBar.add(helpMenu);

		helpMenuItem = new JMenuItem("\u5E2E\u52A9(H)", new ImageIcon(
				"bin/ui/img/help.gif")); // 帮助
		helpMenuItem.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 13));
		helpMenu.add(helpMenuItem);
		helpMenuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				JOptionPane
						.showMessageDialog(
								null,
								"本软件可实现对JavaScript、CSS脚本的压缩和混淆。\r\n其中对于JavaScript脚本的混淆支持较为完善，可以\r\n解释出其语法树，用户可以指定其混淆变量",
								"帮助", JOptionPane.INFORMATION_MESSAGE);
			}
		});

		aboutMenuItem = new JMenuItem("\u5173\u4e8e(A)");// 关于
		aboutMenuItem.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 13));
		helpMenu.add(aboutMenuItem);
		aboutMenuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				JOptionPane.showMessageDialog(null,
						"ScriptObfuscator,1.0版\r\n  Copyright(C)2014",
						"关于ScriptObfuscator", JOptionPane.PLAIN_MESSAGE,
						new ImageIcon("bin/ui/img/about.jpg"));
			}
		});
	}

	public static JMenuBar getMenuBar() {
		return new MenuBar().menuBar;
	}
}