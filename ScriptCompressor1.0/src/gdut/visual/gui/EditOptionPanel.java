package gdut.visual.gui;

import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

public class EditOptionPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1968126058893315404L;

	private static final String TOOLTIP_FOR_TYPE = "Specifies the type of input file";

	public static JCheckBox checkBoxVerbose;
	public static JCheckBox checkBoxMunge;
	public static JCheckBox chkOptimizations;
	public static JCheckBox chkDeleteSemi;
	public static JCheckBox checkBoxPacker;
	public static JCheckBox checkBoxOpacity;
	public static JCheckBox checkBoxFlatten;
	public static JCheckBox checkAutoRecompose;
	public static JTextField lineBreakField;

	public Box editOptionBox;

	public static String type = "js";

	public EditOptionPanel() {
		init();
	}

	public void init() {

		setLayout(new GridLayout(12, 2, 0, 0));
		setBorder(new TitledBorder(null, "\u7F16\u8F91\u9009\u9879",
				TitledBorder.LEADING, TitledBorder.TOP, new Font(
						"Microsoft YaHei UI", Font.PLAIN, 13), null));

		// type
		JLabel lblType = new JLabel("\u811a\u672c\u7c7b\u578b");// �ű�����
		lblType.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 12));
		lblType.setToolTipText(TOOLTIP_FOR_TYPE);
		add(lblType);
		final JRadioButton radioJavaScript = new JRadioButton("JavaScript");
		radioJavaScript.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 12));
		radioJavaScript.setToolTipText(TOOLTIP_FOR_TYPE);
		radioJavaScript.setSelected(true);
		add(radioJavaScript);

		add(new JLabel());
		JRadioButton radioCss = new JRadioButton("CSS");
		radioCss.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 12));
		radioCss.setToolTipText(TOOLTIP_FOR_TYPE);
		add(radioCss);

		ButtonGroup typeGroup = new ButtonGroup();
		typeGroup.add(radioJavaScript);
		typeGroup.add(radioCss);

		// ����
		final JLabel lblVerbose = new JLabel("\u663e\u793a\u8b66\u544a");// ��ʾ����
		lblVerbose.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 12));
		add(lblVerbose);
		checkBoxVerbose = new JCheckBox();
		checkBoxVerbose.setSelected(true);
		add(checkBoxVerbose);

		// ����
		final JLabel lblNomunge = new JLabel("\u6df7\u6dc6\u53d8\u91cf"); // ��������
		lblNomunge.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 12));
		add(lblNomunge);
		checkBoxMunge = new JCheckBox();
		add(checkBoxMunge);

		// �����Ż�
		final JLabel lblDisableOptimizations = new JLabel(
				"\u4ee3\u7801\u4f18\u5316");// �����Ż�
		lblDisableOptimizations.setFont(new Font("Microsoft YaHei UI",
				Font.PLAIN, 12));
		add(lblDisableOptimizations);
		chkOptimizations = new JCheckBox();
		add(chkOptimizations);

		// ȥ���ֺ�
		final JLabel lblDeleteSemi = new JLabel("\u53bb\u9664\u5206\u53f7"); // ȥ���ֺ�
		lblDeleteSemi.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 12));
		add(lblDeleteSemi);
		chkDeleteSemi = new JCheckBox();
		add(chkDeleteSemi);

		// ��͸�����ʽ
		final JLabel opacityExp = new JLabel("\u4e0d\u900f\u660e\u8c13\u8bcd");// ��͸��ν��
		opacityExp.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 12));
		add(opacityExp);
		checkBoxOpacity = new JCheckBox();
		add(checkBoxOpacity);

		// ѹ�������
		final JLabel flattenFlow = new JLabel("\u538b\u6241\u63a7\u5236\u6d41");// ѹ�������
		flattenFlow.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 12));
		add(flattenFlow);
		checkBoxFlatten = new JCheckBox();
		add(checkBoxFlatten);
		
		//�������޸�
		final JLabel autoRecompose = new JLabel("\u7a0b\u5e8f\u81ea\u4fee\u6539");//�������޸�
		flattenFlow.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 12));
		add(autoRecompose);
		checkAutoRecompose = new JCheckBox();
		add(checkAutoRecompose);
		// Packerѹ��
		final JLabel lblRedundant = new JLabel("Packer\u538b\u7f29");// Packerѹ��
		lblRedundant.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 12));
		add(lblRedundant);
		checkBoxPacker = new JCheckBox();
		add(checkBoxPacker);

		// ���뻻�з�
		final JLabel lblLinebreak = new JLabel("\u63d2\u5165\u6362\u884c\u7b26");// ���뻻�з�
		lblLinebreak.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 12));
		lineBreakField = new JTextField();
		lineBreakField.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 12));
		lineBreakField.setColumns(1);
		lineBreakField.setText("-1");
		add(lineBreakField);
		add(lblLinebreak);
		add(lineBreakField);

		radioJavaScript.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (radioJavaScript.isSelected()) {
					type = "js";
				} else
					type = "css";
			}
		});

		radioCss.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (radioJavaScript.isSelected()) {
					type = "js";
				} else
					type = "css";
			}
		});

	}
}