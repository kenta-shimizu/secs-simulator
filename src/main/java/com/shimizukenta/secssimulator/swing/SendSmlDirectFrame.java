package com.shimizukenta.secssimulator.swing;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.shimizukenta.secs.sml.SmlMessage;
import com.shimizukenta.secs.sml.SmlParseException;
import com.shimizukenta.secssimulator.extendsml.ExtendSmlMessageParser;

public class SendSmlDirectFrame extends AbstractSwingInnerFrame {
	
	private static final long serialVersionUID = -5437511057134301556L;
	
	private final JTextArea textArea = new JTextArea("");
	private final JButton sendButton = new JButton("Send");
	private final JLabel errorMsg = new JLabel("");
	
	public SendSmlDirectFrame(SwingSecsSimulator parent) {
		super(parent, "Send SML Direct", true, true, true, true);
		
		this.setDefaultCloseOperation(HIDE_ON_CLOSE);
		
		this.setBounds(
				(config().screenWidth()  * 30 / 100),
				(config().screenHeight() * 10 / 100),
				(config().screenWidth()  * 40 / 100),
				(config().screenHeight() * 40 / 100));
		
		this.errorMsg.setForeground(Color.RED);
		this.errorMsg.setOpaque(false);
		
		this.sendButton.addActionListener(ev -> {
			this.sendSmlDirect();
		});
		
		this.setLayout(defaultBorderLayout());
		
		{
			final JScrollPane scrollPane = new JScrollPane(
					this.textArea,
					JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
					JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			
			this.add(scrollPane, BorderLayout.CENTER);
		}
		{
			JPanel p = borderPanel();
			
			{
				this.errorMsg.setHorizontalAlignment(JLabel.LEFT);
				this.errorMsg.setVerticalAlignment(JLabel.CENTER);
				this.errorMsg.setOpaque(false);
				p.add(this.errorMsg, BorderLayout.CENTER);
			}
			{
				p.add(this.sendButton, BorderLayout.EAST);
			}
			
			this.add(p, BorderLayout.SOUTH);
		}
	}
	
	@Override
	public void setVisible(boolean aFlag) {
		if ( aFlag ) {
			this.textArea.setText("");
			this.errorMsg.setText("");
		}
		super.setVisible(aFlag);
	}
	
	private static final ExtendSmlMessageParser parser = ExtendSmlMessageParser.getInstance();
	
	private void sendSmlDirect() {
		
		String text = this.textArea.getText().trim();
		if ( ! text.endsWith(".") ) {
			text += ".";
		}
		
		try {
			this.errorMsg.setText("");
			
			SmlMessage sm = parser.parse(text);
			
			try {
				simulator().send(sm);
			}
			catch ( InterruptedException ignore ) {
			}
		}
		catch ( SmlParseException e ) {
			
			String msg = e.getMessage();
			if ( msg != null ) {
				this.errorMsg.setText(msg);
			}
		}
	}
	
	@Override
	protected void notifyCommunicateStateChanged(boolean communicated) {
		this.sendButton.setEnabled(communicated);
		this.sendButton.repaint();
	}
	
}
