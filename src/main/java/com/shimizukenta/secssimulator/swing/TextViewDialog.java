package com.shimizukenta.secssimulator.swing;

import java.awt.BorderLayout;
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
			this.textarea = defaultTextArea();
			this.textarea.setEditable(false);
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
