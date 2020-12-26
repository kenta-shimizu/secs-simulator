package com.shimizukenta.secssimulator.swing;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.io.IOException;
import java.nio.file.Path;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class LoggingFrame extends AbstractSwingInternalFrame {
	
	private static final long serialVersionUID = -903877971739628916L;
	
	private final JTextArea textarea = new JTextArea("");
	private final JButton stopButton = new JButton("Stop");
	
	public LoggingFrame(SwingSecsSimulator parent) {
		super(parent, "Logging", true, false, true, true);
		
		this.setBounds(
				(config().screenWidth()  * 30 / 100),
				(config().screenHeight() * 10 / 100),
				(config().screenWidth()  * 40 / 100),
				(config().screenHeight() * 40 / 100));
		
		this.textarea.setEditable(false);
		this.textarea.setLineWrap(true);
		
		this.setLayout(defaultBorderLayout());
		
		{
			this.add(textarea, BorderLayout.CENTER);
		}
		{
			JPanel p = flowPanel(FlowLayout.CENTER);
			
			p.add(stopButton);
			
			this.add(p, BorderLayout.SOUTH);
		}
		
		this.stopButton.addActionListener(ev -> {
		
			try {
				simulator().stopLogging();
			}
			catch ( IOException giveup ) {
			}
			catch ( InterruptedException ignore ) {
			}
		});
	}
	
	@Override
	protected void notifyLoggingPropertyChanged(Path path) {
		
		boolean visible = (path != null);
		
		if ( visible ) {
			this.textarea.setText(path.toAbsolutePath().normalize().toString());
		}
		
		this.setVisible(visible);
	}
	
}
