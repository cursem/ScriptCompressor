package gdut.visual.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.plaf.ColorUIResource;

public class AnalyseWarnPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8351042190364882621L;

	public static JTable table;
	public static JScrollPane analyseScPane;
	public static JTabbedPane mungeTabbedPane;
	public static JTextArea analyseTextArea;
	public static JTextArea resultTextArea;
	public static JTextPane warnTextPane;
	public JSplitPane splitPane;

	public AnalyseWarnPanel() {
		init();
	}

	public void init() {
		analyseTextArea = new JTextArea(8, 10);	//没有用处的，仅是为了初始显示时的协调
		analyseTextArea.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 12));
		analyseScPane = new JScrollPane();
		analyseScPane.setViewportView(analyseTextArea);
		analyseScPane.setBorder(new TitledBorder(null, "词法分析",
				TitledBorder.LEADING, TitledBorder.TOP, new Font(
						"Microsoft YaHei UI", Font.PLAIN, 13), null));

		resultTextArea = new JTextArea(8, 10);
		resultTextArea.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 12));
		RightMenu.addPoupMenu(resultTextArea);
		JScrollPane resultScPane = new JScrollPane();
		resultScPane.setViewportView(resultTextArea);
		resultScPane.setBorder(new TitledBorder(null, "词法分析",
				TitledBorder.LEADING, TitledBorder.TOP, new Font(
						"Microsoft YaHei UI", Font.PLAIN, 13), null));

		mungeTabbedPane = new JTabbedPane(JTabbedPane.BOTTOM);
		mungeTabbedPane.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 12));
		mungeTabbedPane.add("\u8f93\u5165", analyseScPane);//输入
		mungeTabbedPane.add("\u7ed3\u679c", resultScPane);//结果

		warnTextPane = new JTextPane();
		warnTextPane.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 12));
		RightMenu.addPoupMenu(warnTextPane);
		warnTextPane.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if(e.getClickCount()==2) {
					String symbol = warnTextPane.getSelectedText();
					String preCode = PreLaterCodePanel.preCodeTextArea
							.getText();
					int start = preCode.indexOf(symbol);
					if(start==-1) {
						return;
					}
					PreLaterCodePanel.preCodeTextArea.select(start,start
							+ symbol.length());
					PreLaterCodePanel.preCodeTextArea.setSelectedTextColor(Color.WHITE);
					PreLaterCodePanel.preCodeTextArea.requestFocus();
					
					PreLaterCodePanel.ioTabbledPane.setSelectedIndex(0);
				}
			}
		});
		JScrollPane warnScPane = new JScrollPane(warnTextPane);
		warnScPane.setBorder(new TitledBorder(null, "\u8b66\u544a\u4fe1\u606f",		//警告信息
				TitledBorder.LEADING, TitledBorder.TOP, new Font(
						"Microsoft YaHei UI", Font.PLAIN, 13), null));

		splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true,
				mungeTabbedPane, warnScPane);
		splitPane.setDividerLocation((MainFrame.frame.getWidth() - 250) / 2);
		splitPane.setDividerSize(10);
		splitPane.setBorder(null);
		splitPane.setOneTouchExpandable(true);

		// 设置分割条的颜色了，此处设置为绿色
		UIDefaults uidefs = UIManager.getLookAndFeelDefaults();
		uidefs.put("SplitPane.background", new ColorUIResource(Color.GRAY));
		// uidefs.put("SplitPane.oneTouchButtonSize", 10);

		// setBorder(new EmptyBorder(10,0, 5, 0));
		setLayout(new BorderLayout());
		add(splitPane, BorderLayout.CENTER);
	}

	public static void addTable(Vector<Vector<String>> rowData) {
		Vector<String> columnNames = new Vector<String>();
		columnNames.add("\u539f\u59cb\u53d8\u91cf");//原始变量
		columnNames.add("\u6df7\u6dc6\u53d8\u91cf");//混淆变量
		table = new JTable(rowData, columnNames);
		table.getTableHeader().setFont(
				new Font("Microsoft YaHei UI", Font.PLAIN, 12));
		table.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 12));
		table.setRowHeight(20);
		analyseScPane.setViewportView(table);
		
		mungeTabbedPane.setSelectedIndex(0);
		table.getSelectionModel().addListSelectionListener(
				new ListSelectionListener() {

					@Override
					public void valueChanged(ListSelectionEvent arg0) {
						String symbol = table.getValueAt(
								table.getSelectedRow(), 0).toString();
						String preCode = PreLaterCodePanel.preCodeTextArea
								.getText();
						int start = preCode.indexOf(symbol);
						if(start==-1) {
							return;
						}
						PreLaterCodePanel.preCodeTextArea.select(start,start
								+ symbol.length());
						PreLaterCodePanel.preCodeTextArea.setSelectedTextColor(Color.WHITE);
						PreLaterCodePanel.preCodeTextArea.requestFocus();
						
						PreLaterCodePanel.ioTabbledPane.setSelectedIndex(0);
					}
				});
	}
}