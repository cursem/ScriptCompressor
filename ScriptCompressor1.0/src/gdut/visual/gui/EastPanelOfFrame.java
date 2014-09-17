package gdut.visual.gui;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JSplitPane;

public class EastPanelOfFrame extends JPanel{
	public JSplitPane splitPane;
	public AnalyseWarnPanel analyseWarnPanel = new AnalyseWarnPanel(); 
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4587805792284576517L;

	public EastPanelOfFrame() {
		setLayout(new BorderLayout());
		add(OpenSaveToPanel.getInstance(),BorderLayout.NORTH);
		
		splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,true,new PreLaterCodePanel(),analyseWarnPanel);
		splitPane.setDividerLocation((MainFrame.frame.getHeight()-65)*6/10);
		splitPane.setDividerSize(10);
		splitPane.setBorder(null);
		splitPane.setOneTouchExpandable(true);
		add(splitPane,BorderLayout.CENTER);
	}
}