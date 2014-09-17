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

	public static String[] sourceFiles; // Դ�ļ���������·����
	public static String[] saveToFiles; // ��������ļ���������·��������ʽ�磺Դ�ļ���-obf.js

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
		listAndSyntaxPane.addTab("\u6587\u4ef6\u5217\u8868", fileListScrollPane);// �ļ��б�
		listAndSyntaxPane.addTab("\u8bed\u6cd5\u6811", syntaxScrollPane);	//�﷨��
		
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
							+ "-obf." + EditOptionPanel.type; // ������ļ���
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
							.setForeground(Color.BLACK); // ������ʾ����ɫ
					buffReadIn.close();
					buffReadIn = null;
				} catch (FileNotFoundException e1) { // ɾ��ѡ����·�����ļ��л��ļ����������ѡ��fileList�еĶ���ļ������Ჶ����쳣��
														// ����֪Ϊ�Σ����쳣�Ჶ��3�Σ����Σ�ֻ�ܲ��õ�����ʾ
					PreLaterCodePanel.preCodeTextArea.setText("\n    Դ�ļ�"
							+ fileList.getSelectedValue()
							+ "�����ڻ��ƶ�\n    ����Դ�ļ�·��" + MenuBar.sourceFilePath
							+ "�����ڻ��ƶ���");
					PreLaterCodePanel.preCodeTextArea.setForeground(Color.RED); // ����ʾ��Ϊ��ɫ

					// JOptionPane.showMessageDialog(MainFrame.frame, "Դ�ļ�"
					// + fileList.getSelectedValue() + "�����ڻ��ƶ�\n����Դ�ļ�·��"
					// + MenuBar.sourceFilePath + "�����ڻ��ƶ�",
					// "JavaScript������", JOptionPane.WARNING_MESSAGE);

					return; // Դ�ļ������ڣ�������ִ����
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
						// ��������
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
								.setText("\n    Ŀ��·����û�л�������ļ���");
						PreLaterCodePanel.laterCodeTextArea
								.setForeground(Color.RED); // ����ʾ��Ϊ��ɫ
						return;
					}
					file = new File(MenuBar.fileToSavePath + saveToFiles[0]);

					charset = FileCode.getCharsetByFile(file);
					buffReadIn1 = new BufferedReader(new InputStreamReader(
							new FileInputStream(file), charset));
					PreLaterCodePanel.laterCodeTextArea.read(buffReadIn1, file);
					PreLaterCodePanel.laterCodeTextArea
							.setForeground(Color.BLACK); // ������ʾ����ɫ
					buffReadIn1.close();
					buffReadIn1 = null;
				} catch (FileNotFoundException e1) {
					PreLaterCodePanel.laterCodeTextArea
							.setText("\n    Ŀ��·����û�л�������ļ���");
					PreLaterCodePanel.laterCodeTextArea
							.setForeground(Color.RED); // ����ʾ��Ϊ��ɫ
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
						// ��������
					}

				} catch (IOException e1) {
					// ��������
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