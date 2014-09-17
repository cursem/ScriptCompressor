package gdut.visual.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.text.JTextComponent;

public class RightMenu {

	public static JPopupMenu createJPopupMenu(final JTextComponent component) {
		JMenuItem cutMenuItem = new JMenuItem("\u526a\u5207(T)");// ¼ôÇÐ
		JMenuItem copyMenuItem = new JMenuItem("\u590D\u5236(C)");// ¸´ÖÆ
		JMenuItem pasteMenuItem = new JMenuItem("\u7C98\u8D34(P)"); // Õ³Ìû
		JMenuItem deleteMenuItem = new JMenuItem("\u5220\u9664(L)");// É¾³ý

		cutMenuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				component.cut();
			}
		});

		copyMenuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				component.copy();

			}
		});

		pasteMenuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				component.paste();
			}
		});

		deleteMenuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				component.replaceSelection("");

			}
		});

		JPopupMenu poupMenu = new JPopupMenu();
		poupMenu.add(cutMenuItem);
		poupMenu.add(copyMenuItem);
		poupMenu.add(pasteMenuItem);
		poupMenu.add(deleteMenuItem);
		return poupMenu;
	}

	public static void addPoupMenu(final JTextComponent component) {
		component.addMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON3) {
					JPopupMenu popupMenu = RightMenu
							.createJPopupMenu(component);
					popupMenu.show(component, e.getX(), e.getY());
					;
				}
			}
		});
	}
}
