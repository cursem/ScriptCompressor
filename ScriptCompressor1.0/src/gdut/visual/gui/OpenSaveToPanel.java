package gdut.visual.gui;

import gdut.tool.FileCode;

import java.awt.BorderLayout;
import java.awt.FileDialog;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import org.Antlr.jsparser.inparser.japarser;
import org.antlr.runtime.tree.Tree;

public class OpenSaveToPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4482363031203900103L;
	public static OpenSaveToPanel openSaveToPanel = null;

	public static JButton browseButton;
	public static JButton singleMungeButton;
	public static JButton multiMungeButton;
	public static JTextField inputTextField;
	public static JTextField outTextField;

	public static JButton saveToButton;

	// 混淆前后的文件名（包含路径）
	public static String beforeObfFile;
	public static String afterObfFile;

	public static String obfFileSavePath;

	public OpenSaveToPanel() {
		init();
	}

	public void init() {
		setSize(MainFrame.frame.getWidth() - 240, 65);
		JLabel openLabel = new JLabel("\u6253\u5f00\u5730\u5740\uff1a");// 打开地址：
		openLabel.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 14));
		inputTextField = new JTextField();
		inputTextField.setEditable(false);
		inputTextField.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 12));
		browseButton = new JButton("\u6d4f\u89c8"); // 浏览，打开单个文件
		browseButton.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 14));
		browseButton.setFocusPainted(false);

		JLabel saveLabel = new JLabel("\u4fdd\u5b58\u5730\u5740\uff1a");// 保存地址：
		saveLabel.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 14));
		outTextField = new JTextField();
		outTextField.setEditable(false);
		outTextField.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 12));
		saveToButton = new JButton("\u4fdd\u5b58");// 保存
		saveToButton.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 14));
		saveToButton.setFocusPainted(false);

		singleMungeButton = new JButton("\u6df7\u6dc6");// 混淆
		singleMungeButton
				.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 14));
		singleMungeButton.setFocusPainted(false);
		multiMungeButton = new JButton("\u6279\u91cf\u6df7\u6dc6");// 批量混淆
		multiMungeButton
				.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 14));
		multiMungeButton.setFocusPainted(false);

		JPanel openSavePanel = new JPanel(new BorderLayout());
		openSavePanel.setBorder(new EmptyBorder(0, 0, 0, 5));
		JPanel mungeButtonPanel = new JPanel(new BorderLayout());

		JPanel openPanel = new JPanel(new BorderLayout());
		openPanel.add(openLabel, BorderLayout.WEST);
		openPanel.add(inputTextField, BorderLayout.CENTER);
		openPanel.add(browseButton, BorderLayout.EAST);
		openPanel.setBorder(new EmptyBorder(0, 0, 3, 0));

		JPanel savePanel = new JPanel(new BorderLayout());
		savePanel.add(saveLabel, BorderLayout.WEST);
		savePanel.add(outTextField, BorderLayout.CENTER);
		savePanel.add(saveToButton, BorderLayout.EAST);

		openSavePanel.add(openPanel, BorderLayout.NORTH);
		openSavePanel.add(savePanel, BorderLayout.SOUTH);
		openSavePanel.setBorder(new EmptyBorder(0, 0, 3, 0));

		mungeButtonPanel.add(singleMungeButton, BorderLayout.NORTH);
		mungeButtonPanel.add(multiMungeButton, BorderLayout.SOUTH);
		mungeButtonPanel.setBorder(new EmptyBorder(0, 0, 3, 0));

		setLayout(new BorderLayout());
		add(openSavePanel, BorderLayout.CENTER);
		add(mungeButtonPanel, BorderLayout.EAST);

		browseButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				FileDialog openFileDialog = new FileDialog(MainFrame.frame,
						"选定文件", FileDialog.LOAD);
				openFileDialog.setFilenameFilter(new FilenameFilter() { // 此文件过滤器对Windows不起作用
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
				openFileDialog.setVisible(true);
				String direct = openFileDialog.getDirectory(); // 此方法得到的路径最后是有斜杠的
				String fileName = openFileDialog.getFile();
				if (direct != null && fileName != null) { // 点了打开按钮
					multiMungeButton.setEnabled(false);
					singleMungeButton.setEnabled(true);
					MenuBar.analyzeMenuItem.setEnabled(true);
					MenuBar.mungeMenuItem.setEnabled(true);
					ToolBar.analysistButton.setEnabled(true);
					ToolBar.mungeButton.setEnabled(true);
					MenuBar.saveMenuItem.setEnabled(false);
					saveToButton.setEnabled(true);
					ToolBar.saveButton.setEnabled(false);

					FileListPanel.fileVector.clear();
					FileListPanel.fileList.updateUI();

					beforeObfFile = direct + fileName;
					File file = new File(direct, fileName);
					Reader in;
					try {
						String charset = FileCode.getCharsetByFile(file);
						in = new BufferedReader(new InputStreamReader(
								new FileInputStream(file), charset));
						// 展示混淆前的代码
						PreLaterCodePanel.preCodeTextArea.read(in, file);
						in.close();
						PreLaterCodePanel.ioTabbledPane.setSelectedIndex(0);

						inputTextField.setText(direct + fileName);
					} catch (UnsupportedEncodingException
							| FileNotFoundException e2) {
						e2.printStackTrace();
					} catch (IOException e1) {
						e1.printStackTrace();
					}

					// 将词法分析面板清空
					AnalyseWarnPanel.analyseScPane
							.setViewportView(AnalyseWarnPanel.analyseTextArea);
					AnalyseWarnPanel.resultTextArea.setText("");
					AnalyseWarnPanel.warnTextPane.setText("");

					MenuBar.isNewFile = false;

					// 创建语法树
					try {
						InputStream inTree = new FileInputStream(new File(
								direct+fileName));
						Tree ptree = japarser.parser(inTree);
						DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(fileName);
						FileListPanel.syntaxTree.setModel(new DefaultTreeModel(rootNode));
						showASTJTree(rootNode,ptree);
						FileListPanel.listAndSyntaxPane.setSelectedIndex(1);
						FileListPanel.syntaxTree.expandRow(0);
					} catch (FileNotFoundException e1) {
						e1.printStackTrace();
					} catch (Exception e1) {
						e1.printStackTrace();
					}

				}
			}

		});

		saveToButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				FileDialog saveFileDialog = new FileDialog(MainFrame.frame,
						"保存文件", FileDialog.SAVE);
				saveFileDialog.setVisible(true);
				String direct = saveFileDialog.getDirectory(); // 此方法得到的路径最后是有斜杠的
				String fileName = saveFileDialog.getFile();
				if (direct != null) { // 点击了保存按钮
					outTextField.setText(direct + fileName);
					afterObfFile = direct + fileName;
					obfFileSavePath = direct;
				}
			}
		});

		singleMungeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				LinkGUIToCompress.goToCompress("munge", "single");
			}
		});

		multiMungeButton.setEnabled(false);
		multiMungeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				LinkGUIToCompress.goToCompress("munge", "multi");
			}
		});
	}

	// --具体的AST导入JTree的操作
	private void showASTJTree(DefaultMutableTreeNode root, Tree ptree) {
		DefaultMutableTreeNode child;
		for (int count = 0; count < ptree.getChildCount(); count++) {
			child = new DefaultMutableTreeNode(ptree.getChild(count));
			root.add(child);
			if (ptree.getChild(count).getChildCount() > 0) {
				showASTJTree(child, ptree.getChild(count));
			}
		}
	}

	
	public static OpenSaveToPanel getInstance() {
		if (openSaveToPanel == null) {
			openSaveToPanel = new OpenSaveToPanel();
		}
		return openSaveToPanel;
	}
}