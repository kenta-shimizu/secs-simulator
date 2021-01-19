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
	
	private final JTextArea textarea;
	private final JButton stopButton;
	
	public LoggingFrame(SwingSecsSimulator parent) {
		super(parent, "Logging", true, false, true, true);
		
		{
			this.textarea = defaultTextArea();
			this.textarea.setEditable(false);
			this.textarea.setLineWrap(true);
		}
		
		this.stopButton = new JButton("Stop");
		
		this.setLayout(defaultBorderLayout());
		
		{
			this.add(defaultScrollPane(textarea), BorderLayout.CENTER);
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
	public void setVisible(boolean aFlag) {
		if ( aFlag ) {
			
			int w = this.getDesktopPane().getWidth();
			int h = this.getDesktopPane().getHeight();
			
			this.setBounds(
					(w * 30 / 100),
					(h * 10 / 100),
					(w * 40 / 100),
					(h * 40 / 100));
		}
		super.setVisible(aFlag);
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
