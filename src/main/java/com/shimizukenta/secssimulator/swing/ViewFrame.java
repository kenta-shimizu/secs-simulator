package com.shimizukenta.secssimulator.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.LinkedList;
import java.util.stream.Collectors;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.border.EtchedBorder;

import com.shimizukenta.secssimulator.SecsSimulatorLog;

public class ViewFrame extends AbstractSwingInternalFrame {
	
	private static final long serialVersionUID = 4744049507304871891L;
	
	private final JLabel communicateStatus = new JLabel(" ");
	private final Color communicateStatusDefaultBgColor = communicateStatus.getBackground();
	
	private final JTextArea messageLogTextArea = new JTextArea("");
	private final JScrollPane scrollPane;
	private final JScrollBar vScrollBar;
	
	public ViewFrame(SwingSecsSimulator parent) {
		super(parent, "Viewer", true, false, true, true);
		
		this.setLayout(defaultBorderLayout());
		
		{
			JPanel p = gridPanel(1, 3);
			
			{
				communicateStatus.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
				communicateStatus.setHorizontalAlignment(JLabel.CENTER);
				communicateStatus.setVerticalAlignment(JLabel.CENTER);
				communicateStatus.setOpaque(false);
				
				p.add(communicateStatus);
			}
			{
				p.add(emptyPanel());
			}
			{
				p.add(emptyPanel());
			}
			
			this.add(p, BorderLayout.NORTH);
		}
		{
			this.messageLogTextArea.setEditable(false);
			this.messageLogTextArea.setLineWrap(false);
			this.messageLogTextArea.setOpaque(true);
			
			this.scrollPane = new JScrollPane(
					this.messageLogTextArea,
					JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
					JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			
			this.vScrollBar = this.scrollPane.getVerticalScrollBar();
			
			this.add(scrollPane, BorderLayout.CENTER);
		}
		
		config().darkMode().addChangeListener(dark -> {
			
			if ( dark ) {
				
				//HOOK
				
			} else {
				
				//HOOK
			}
		});
	}
	
	@Override
	public void setVisible(boolean aFlag) {
		if ( aFlag ) {
			
			int w = this.getDesktopPane().getWidth();
			int h = this.getDesktopPane().getHeight();
			
			this.setBounds(
					(w *  1 / 100),
					(h *  1 / 100),
					(w * 50 / 100),
					(h * 85 / 100));
		}
		
		super.setVisible(aFlag);
	}
	
	private static final String BR = System.lineSeparator();
	private static final String BRBR = BR + BR;
	
	private final LinkedList<String> msgs = new LinkedList<>();
	
	@Override
	protected void putMessageLog(SecsSimulatorLog log) {
		
		synchronized ( this ) {
			
			String s = log.toString() + BRBR;
			msgs.add(s);
			
			int msgSize = msgs.size();
			if ( msgSize > config().viewerSize() ) {
				
				for ( int i = (msgSize / 2); i > 0; --i ) {
					msgs.pollFirst();
				}
				
				this.messageLogTextArea.setText(msgs.stream().collect(Collectors.joining()));
				
			} else {
				
				this.messageLogTextArea.append(s);
			}
		}
		
		SwingUtilities.invokeLater(() -> {
			this.vScrollBar.setValue(Integer.MAX_VALUE);
		});
	}
	
	@Override
	protected void notifyCommunicateStateChanged(boolean communicated) {
		
		if ( communicated ) {
			
			this.communicateStatus.setText("Communicating");
			this.communicateStatus.setBackground(config().communicatingColor());
			this.communicateStatus.setOpaque(true);
			
		} else {
			
			this.communicateStatus.setText("Not communicate");;
			this.communicateStatus.setBackground(this.communicateStatusDefaultBgColor);
			this.communicateStatus.setOpaque(false);
		}
		
		this.communicateStatus.repaint();
	}
	
}
