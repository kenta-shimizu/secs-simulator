package com.shimizukenta.secssimulator.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.util.Objects;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class TextViewDialog extends AbstractSwingDialog {
	
	private static final long serialVersionUID = -3343395080369099743L;
	
	private final JTextArea textarea;
	private final JButton okButton;
	
	public TextViewDialog(Frame owner, String title, SwingSecsSimulator simm) {
		super(simm, owner, title, true);
		
		{
			this.textarea = new JTextArea("");
			this.textarea.setEditable(false);
			
			final Color bgColor = textarea.getBackground();
			final Color fgColor = textarea.getForeground();
			
			this.config().darkMode().addChangeListener(dark -> {
				if ( dark ) {
					this.textarea.setBackground(this.config().defaultDarkAreaBackGroundColor());
					this.textarea.setForeground(this.config().defaultDarkAreaForeGroundColor());
				} else {
					this.textarea.setBackground(bgColor);
					this.textarea.setForeground(fgColor);
				}
			});
		}
		
		this.okButton = new JButton("OK");
		this.okButton.addActionListener(ev -> {
			this.setVisible(false);
		});
		
		this.setLayout(defaultBorderLayout());
		
		{
			this.add(defaultScrollPane(this.textarea), BorderLayout.CENTER);
		}
		{
			JPanel p = flowPanel(FlowLayout.CENTER);
			p.add(okButton);
			this.add(p, BorderLayout.SOUTH);
		}
		
		{
			final Color bgColor = this.getContentPane().getBackground();
			this.config().darkMode().addChangeListener(dark -> {
				if ( dark ) {
					this.getContentPane().setBackground(this.config().defaultDarkPanelBackGroundColor());
				} else {
					this.getContentPane().setBackground(bgColor);
				}
			});
		}
	}
	
	public void showText(Object o) {
		textarea.setText(Objects.toString(o, ""));
		this.setVisible(true);
	}
	
	@Override
	public void setVisible(boolean aFlag) {
		if ( aFlag ) {
			this.setSize(
					(this.getOwner().getWidth()  * 50 / 100),
					(this.getOwner().getHeight() * 60 / 100)
					);
			
			this.setLocationRelativeTo(this.getOwner());
		}
		super.setVisible(aFlag);
	}
	
}
