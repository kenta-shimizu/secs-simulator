package com.shimizukenta.secssimulator.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.shimizukenta.secs.sml.SmlMessage;
import com.shimizukenta.secs.sml.SmlParseException;
import com.shimizukenta.secssimulator.SmlAliasPair;

public class SmlEditorFrame extends AbstractSwingInternalFrame {
	
	private static final long serialVersionUID = -5437511057134301556L;
	
	private final JTextArea textArea = new JTextArea("");
	private final JButton loadButton = new JButton("Load...");
	private final JButton saveAndAddButton = new JButton("Save & Add...");
	private final JButton sendButton = new JButton("Send");
	private final JLabel errorMsg = new JLabel("");
	
	public SmlEditorFrame(SwingSecsSimulator parent) {
		super(parent, "SML Editor", true, true, true, true);
		
		this.setDefaultCloseOperation(HIDE_ON_CLOSE);
		
		this.setBounds(
				(config().screenWidth()  * 25 / 100),
				(config().screenHeight() * 10 / 100),
				(config().screenWidth()  * 50 / 100),
				(config().screenHeight() * 40 / 100));
		
		this.errorMsg.setHorizontalAlignment(JLabel.LEFT);
		this.errorMsg.setVerticalAlignment(JLabel.CENTER);
		this.errorMsg.setForeground(Color.RED);
		this.errorMsg.setOpaque(false);
		
		this.loadButton.addActionListener(ev -> {
			
			try {
				this.errorMsg.setText("");
				
				Optional<SmlMessage> op = simulator().showLoadSmlDialog();
				
				op.map(SmlMessage::toString).ifPresent(this.textArea::setText);
			}
			catch ( SmlParseException | IOException e ) {
				
				String msg = e.getMessage();
				if ( msg != null ) {
					this.errorMsg.setText(msg);
				}
			}
			
			this.repaint();
		});
		
		this.saveAndAddButton.addActionListener(ev -> {
			
			try {
				String text = this.textArea.getText().trim();
				if ( ! text.endsWith(".") ) {
					text += ".";
				}
				
				this.errorMsg.setText("");
				
				final SmlMessage sm = simulator().parseSml(text);
				
				Path path = simulator().showSaveSmlDialog().orElse(null);
				if ( path != null ) {
					
					try (
							BufferedWriter bw = Files.newBufferedWriter(path, StandardCharsets.UTF_8);
							) {
						
						bw.write(sm.toString());
						bw.flush();
					}
					
					SmlAliasPair pair = SmlAliasPair.fromFile(path);
					config().smlAliasPairPool().add(pair);
				}
			}
			catch ( SmlParseException | IOException e ) {
				
				String msg = e.getMessage();
				if ( msg != null ) {
					this.errorMsg.setText(msg);
				}
			}
			
			this.repaint();
		});
		
		this.sendButton.addActionListener(ev -> {
			this.sendSmlDirect();
		});
		
		this.setLayout(defaultBorderLayout());
		
		{
			JPanel p = borderPanel();
			
			{
				final JScrollPane scrollPane = new JScrollPane(
						this.textArea,
						JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
						JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
				
				p.add(scrollPane, BorderLayout.CENTER);
			}
			{
				p.add(this.errorMsg, BorderLayout.SOUTH);
			}
			
			this.add(p, BorderLayout.CENTER);
		}
		{
			JPanel p = borderPanel();
			
			{
				JPanel pp = gridPanel(2, 1);
				
				pp.add(this.loadButton);
				pp.add(this.saveAndAddButton);
				
				p.add(pp, BorderLayout.NORTH);
			}
			{
				JPanel pp = gridPanel(1, 1);
				
				pp.add(this.sendButton);
				
				p.add(pp, BorderLayout.SOUTH);
			}
			
			this.add(p, BorderLayout.EAST);
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
	
	private void sendSmlDirect() {
		
		String text = this.textArea.getText().trim();
		if ( ! text.endsWith(".") ) {
			text += ".";
		}
		
		try {
			this.errorMsg.setText("");
			
			SmlMessage sm = simulator().parseSml(text);
			
			simulator().asyncSend(sm);
		}
		catch ( SmlParseException e ) {
			
			String msg = e.getMessage();
			if ( msg != null ) {
				this.errorMsg.setText(msg);
			}
		}
		
		this.repaint();
	}
	
	@Override
	protected void notifyCommunicateStateChanged(boolean communicated) {
		this.sendButton.setEnabled(communicated);
		this.sendButton.repaint();
	}
	
}
