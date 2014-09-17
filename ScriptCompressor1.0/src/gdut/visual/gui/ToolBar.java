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
	public static JButton beGraphButton;// ����ǰ����ͼ
	public static JButton afGraphButton;// ���������ͼ
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
				chooser.setDialogTitle("ѡ���ļ���");
				int option = chooser.showDialog(null, "ȷ��");
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
							.getAbsolutePath() + "/"; // �õ��ļ���·��
					OpenSaveToPanel.inputTextField
							.setText(MenuBar.sourceFilePath);
					String[] files = new File(MenuBar.sourceFilePath)
							.list(new FilenameFilter() {
								@Override
								public boolean accept(File dir, String name) {
									int idx = name.lastIndexOf('.');
									String suffix = "";
									if (idx >= 0 && idx < name.length() - 1) {
										suffix = name.substring(idx + 1); // ��ȡ�ļ�����׺
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
					// ���ʷ�����������
					AnalyseWarnPanel.analyseScPane
							.setViewportView(AnalyseWarnPanel.analyseTextArea);
					AnalyseWarnPanel.resultTextArea.setText("");
				}
			}
		});
		openButton.setFocusPainted(false);
		openButton.setToolTipText("ѡ��Դ���������ļ���");
		add(openButton);

		saveButton = new JButton(new ImageIcon("bin/ui/img/saveTo.png"));
		saveButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				chooser.setDialogTitle("ѡ���ļ���");
				int result = chooser.showDialog(null, "ȷ��");
				if (result == JFileChooser.APPROVE_OPTION) {
					MenuBar.fileToSavePath = chooser.getSelectedFile()
							.getAbsolutePath() + "/";
					OpenSaveToPanel.outTextField
							.setText(MenuBar.fileToSavePath);
				}
			}

		});
		saveButton.setFocusPainted(false);
		saveButton.setToolTipText("ѡ�������ļ���");
		add(saveButton);

		addSeparator();

		newFileButton = new JButton(new ImageIcon("bin/ui/img/newFile.gif"));
		newFileButton.setFocusPainted(false);
		newFileButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				FileDialog saveFileDialog = new FileDialog(MainFrame.frame,
						"�����ļ�", FileDialog.SAVE);
				saveFileDialog.setVisible(true);
				String direct = saveFileDialog.getDirectory(); // �˷����õ���·���������б�ܵ�
				String fileName = saveFileDialog.getFile();
				if (direct != null) { // ����˱��水ť
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

					// ���ʷ�����������
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
		newFileButton.setToolTipText("�½��ļ�");
		add(newFileButton);
		saveFileButton = new JButton(new ImageIcon("bin/ui/img/save.gif"));
		saveFileButton.setFocusPainted(false);
		saveFileButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (MenuBar.isNewFile) { // �½����ļ��Ż�ִ�б������
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
					JOptionPane.showMessageDialog(MainFrame.frame, "�빴ѡ��������",
							"��ʾ", JOptionPane.WARNING_MESSAGE);
					return;
				}
				LinkGUIToCompress.goToCompress("analyzeOnly", "single");
			}
		});
		analysistButton.setToolTipText("����Դ����");
		add(analysistButton);
		mungeButton = new JButton(new ImageIcon("bin/ui/img/obfu.gif"));
		mungeButton.setFocusPainted(false);
		mungeButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				LinkGUIToCompress.goToCompress("munge", "single");
			}
		});
		mungeButton.setToolTipText("ִ�л���");
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
		beGraphButton.setToolTipText("����ǰ��JS�ֽ�������ͼ");
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
		afGraphButton.setToolTipText("�������JS�ֽ�������ͼ");
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
								"�������ʵ�ֶ�JavaScript��CSS�ű���ѹ���ͻ�����\r\n���ж���JavaScript�ű��Ļ���֧�ֽ�Ϊ���ƣ�����\r\n���ͳ����﷨�����û�����ָ�����������",
								"����", JOptionPane.INFORMATION_MESSAGE);
			}
		});
		helpButton.setToolTipText("����");
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