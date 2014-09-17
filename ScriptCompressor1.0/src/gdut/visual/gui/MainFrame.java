package gdut.visual.gui;

import gdut.visual.gui.MenuBar;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class MainFrame {
	
	public static JFrame frame; 
	
	public static void createAndShowGUI() {
		frame = new JFrame("JavaScript\u6df7\u6dc6\u5668");//JavaScript»ìÏýÆ÷
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		frame.setLocation(400, 200);
		frame.setMinimumSize(new Dimension(Toolkit.getDefaultToolkit().getScreenSize().width/2,Toolkit.getDefaultToolkit().getScreenSize().height/2));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setIconImage(new ImageIcon("bin/ui/img/systemIco.jpg").getImage());
		frame.setVisible(true);
			
		JPanel listEditPanel = new JPanel(new BorderLayout());
		listEditPanel.add(new FileListPanel(),BorderLayout.CENTER);
		listEditPanel.add(new EditOptionPanel(),BorderLayout.SOUTH);
		listEditPanel.setPreferredSize(new Dimension(240,Toolkit.getDefaultToolkit().getScreenSize().height));
		listEditPanel.setMinimumSize(new Dimension(240,Toolkit.getDefaultToolkit().getScreenSize().height));
		listEditPanel.setMaximumSize(new Dimension(240,Toolkit.getDefaultToolkit().getScreenSize().height));
		
		Box box = Box.createHorizontalBox();
		final EastPanelOfFrame eastPanelOfFrame = new EastPanelOfFrame();
		box.add(listEditPanel);box.add(eastPanelOfFrame);
		
		frame.setLayout(new BorderLayout());
		frame.setJMenuBar(MenuBar.getMenuBar());
		frame.getContentPane().add(new ToolBar(),BorderLayout.NORTH);
		frame.getContentPane().add(box);
		
	}
	
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException
				| IllegalAccessException | UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				createAndShowGUI();
				OpenSaveToPanel.inputTextField.requestFocusInWindow();
			}
			
		});
	}
}