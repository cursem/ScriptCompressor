package gdut.visual.gui;

import java.awt.BorderLayout;
import java.awt.Font;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.undo.UndoManager;

public class PreLaterCodePanel extends JPanel{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7319434521217160040L;

	public static JTabbedPane ioTabbledPane;
	public static JTextArea preCodeTextArea;
	public static JTextArea laterCodeTextArea;
	
	public static UndoManager um;// 撤销管理类
	
	public PreLaterCodePanel() {
		init();
	}
	
	public void init() {
		
		um = new UndoManager();
		
		preCodeTextArea = new JTextArea();
		preCodeTextArea.setFont(new Font("Microsoft YaHei UI",Font.BOLD,12));
		preCodeTextArea.getDocument().addUndoableEditListener(new UndoableEditListener() {
			
			@Override
			public void undoableEditHappened(UndoableEditEvent e) {
				um.addEdit(e.getEdit());
			}
		});
		RightMenu.addPoupMenu(preCodeTextArea);
		JScrollPane preCodeScroPane = new JScrollPane(preCodeTextArea);
		
		laterCodeTextArea = new JTextArea();
		laterCodeTextArea.setFont(new Font("Microsoft YaHei UI",Font.BOLD,12));
		laterCodeTextArea.setLineWrap(true);
		RightMenu.addPoupMenu(laterCodeTextArea);
		JScrollPane laterCodeScroPane = new JScrollPane(laterCodeTextArea);
		
		ioTabbledPane = new JTabbedPane(JTabbedPane.TOP);
		ioTabbledPane.setFont(new Font("Microsoft YaHei UI",Font.PLAIN,12));
		ioTabbledPane.addTab("\u5904\u7406\u524d\u4ee3\u7801", preCodeScroPane);//处理前代码
		ioTabbledPane.addTab("\u5904\u7406\u540e\u4ee3\u7801", laterCodeScroPane);//处理后代码
		
		setLayout(new BorderLayout());
		add(ioTabbledPane,BorderLayout.CENTER);
	}
}