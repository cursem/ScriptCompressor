package gdut.visual.gui;

import gdut.tool.FileCode;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTree;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;


public class FileListPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8122394951932516905L;

	public static Vector<String> fileVector;
	public static JList<String> fileList;
	
	public static JTabbedPane listAndSyntaxPane;
	public static JTree syntaxTree;

	public static String[] sourceFiles; // 源文件名（不含路径）
	public static String[] saveToFiles; // 混淆后的文件名（不含路径），形式如：源文件名-obf.js

	public FileListPanel() {
		FileListPanel.fileVector = new Vector<String>();
		init();
	}

	public void init() {
		setBorder(new EmptyBorder(0, 0, 10, 0));
		setLayout(new BorderLayout());
		fileList = new JList<String>(fileVector);
		fileList.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 12));
		JScrollPane fileListScrollPane = new JScrollPane(fileList);
		
		syntaxTree = new JTree();
		DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("Parser Tree");
		syntaxTree.setModel(new DefaultTreeModel(rootNode));
		((DefaultTreeCellRenderer)syntaxTree.getCellRenderer()).setOpenIcon(new ImageIcon("bin/ui/img/node.png"));
		((DefaultTreeCellRenderer)syntaxTree.getCellRenderer()).setClosedIcon(new ImageIcon("bin/ui/img/node.png"));
		JScrollPane syntaxScrollPane = new JScrollPane(syntaxTree);
		
		listAndSyntaxPane = new JTabbedPane(JTabbedPane.TOP);
		listAndSyntaxPane.setFont(new Font("Microsoft YaHei UI",Font.PLAIN,12));
		listAndSyntaxPane.addTab("\u6587\u4ef6\u5217\u8868", fileListScrollPane);// 文件列表
		listAndSyntaxPane.addTab("\u8bed\u6cd5\u6811", syntaxScrollPane);	//语法树
		
		add(listAndSyntaxPane);
		
		fileList.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				@SuppressWarnings("deprecation")
				Object[] selectedObj = fileList.getSelectedValues();
				sourceFiles = new String[selectedObj.length];
				saveToFiles = new String[selectedObj.length];
				String selectedFile;
				for (int i = 0; i < selectedObj.length; ++i) {
					selectedFile = selectedObj[i].toString();
					sourceFiles[i] = selectedFile;
					saveToFiles[i] = selectedFile.substring(0,
							selectedFile.lastIndexOf("."))
							+ "-obf." + EditOptionPanel.type; // 保存的文件名
				}

				String charset;
				File file = null;
				BufferedReader buffReadIn = null;
				try {
					file = new File(MenuBar.sourceFilePath
							+ fileList.getSelectedValue());
					charset = FileCode.getCharsetByFile(file);
					buffReadIn = new BufferedReader(new InputStreamReader(
							new FileInputStream(file), charset));
					PreLaterCodePanel.preCodeTextArea.read(buffReadIn, file);
					PreLaterCodePanel.preCodeTextArea
							.setForeground(Color.BLACK); // 正常显示，黑色
					buffReadIn.close();
					buffReadIn = null;
				} catch (FileNotFoundException e1) { // 删除选定的路径（文件夹或文件）的情况下选定fileList中的多个文件，将会捕获此异常，
														// 但不知为何，此异常会捕获3次，无奈，只能不用弹窗提示
					PreLaterCodePanel.preCodeTextArea.setText("\n    源文件"
							+ fileList.getSelectedValue()
							+ "不存在或被移动\n    或者源文件路径" + MenuBar.sourceFilePath
							+ "不存在或被移动！");
					PreLaterCodePanel.preCodeTextArea.setForeground(Color.RED); // 将提示设为红色

					// JOptionPane.showMessageDialog(MainFrame.frame, "源文件"
					// + fileList.getSelectedValue() + "不存在或被移动\n或者源文件路径"
					// + MenuBar.sourceFilePath + "不存在或被移动",
					// "JavaScript混淆器", JOptionPane.WARNING_MESSAGE);

					return; // 源文件不存在，则不往下执行了
				} catch (UnsupportedEncodingException e1) {
					BufferedReader buffReadInTmp = null;
					try {
						buffReadInTmp = new BufferedReader(
								new InputStreamReader(
										new FileInputStream(file), "GBK"));
						PreLaterCodePanel.preCodeTextArea.read(buffReadInTmp,
								file);
						buffReadInTmp.close();
					} catch (IOException e2) {
						// 不作处理
					}
				} catch (IOException e1) {
					e1.printStackTrace();
				} finally {
					if (buffReadIn != null) {
						try {
							buffReadIn.close();
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					}
				}

				BufferedReader buffReadIn1 = null;
				try {
					if (MenuBar.fileToSavePath == null) {
						PreLaterCodePanel.laterCodeTextArea
								.setText("\n    目标路径下没有混淆后的文件！");
						PreLaterCodePanel.laterCodeTextArea
								.setForeground(Color.RED); // 将提示设为红色
						return;
					}
					file = new File(MenuBar.fileToSavePath + saveToFiles[0]);

					charset = FileCode.getCharsetByFile(file);
					buffReadIn1 = new BufferedReader(new InputStreamReader(
							new FileInputStream(file), charset));
					PreLaterCodePanel.laterCodeTextArea.read(buffReadIn1, file);
					PreLaterCodePanel.laterCodeTextArea
							.setForeground(Color.BLACK); // 正常显示，黑色
					buffReadIn1.close();
					buffReadIn1 = null;
				} catch (FileNotFoundException e1) {
					PreLaterCodePanel.laterCodeTextArea
							.setText("\n    目标路径下没有混淆后的文件！");
					PreLaterCodePanel.laterCodeTextArea
							.setForeground(Color.RED); // 将提示设为红色
				} catch (UnsupportedEncodingException e1) {
					BufferedReader buffReadInTmp = null;
					try {
						buffReadInTmp = new BufferedReader(
								new InputStreamReader(
										new FileInputStream(file), "GBK"));
						PreLaterCodePanel.preCodeTextArea.read(buffReadInTmp,
								file);
						buffReadInTmp.close();
					} catch (IOException e2) {
						// 不作处理
					}

				} catch (IOException e1) {
					// 不作处理
				} finally {
					if (buffReadIn1 != null) {
						try {
							buffReadIn1.close();
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					}
				}

			}

		});
	}
}