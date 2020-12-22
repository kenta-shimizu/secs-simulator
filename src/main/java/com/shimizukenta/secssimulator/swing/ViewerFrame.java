package com.shimizukenta.secssimulator.swing;

import java.awt.BorderLayout;
import java.util.LinkedList;
import java.util.stream.Collectors;

import javax.swing.JLabel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import com.shimizukenta.secssimulator.SecsSimulatorLog;

public class ViewerFrame extends AbstractSwingInnerFrame {
	
	private static final long serialVersionUID = 4744049507304871891L;
	
	private final JLabel communicateStatus = new JLabel(" ");
	private final JTextArea messageLogTextArea = new JTextArea("");
	private final JScrollPane scrollPane;
	private final JScrollBar vScrollBar;

	public ViewerFrame(SwingSecsSimulator parent) {
		super(parent, "Viewer", true, false, true, true);
		
		this.setBounds(
				(config().screenWidth() * 1 / 100),
				(config().screenHeight() * 1 / 100),
				(config().screenWidth() * 50 / 100),
				(config().screenHeight() * 85 / 100));
		
		this.messageLogTextArea.setEditable(false);
		
		this.scrollPane = new JScrollPane(
				messageLogTextArea,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		
		this.vScrollBar = this.scrollPane.getVerticalScrollBar();
		
		this.setLayout(new BorderLayout());
		
		this.add(communicateStatus, BorderLayout.NORTH);
		this.add(scrollPane, BorderLayout.CENTER);
		
		this.setVisible(true);
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
				
				for ( int i = 0, m = (msgSize / 2); i < m; ++i ) {
					msgs.removeFirst();
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
			
		} else {
			
			this.communicateStatus.setText("Not communicate");;
		}
	}
	
}
