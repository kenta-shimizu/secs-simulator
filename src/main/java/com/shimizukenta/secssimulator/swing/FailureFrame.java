package com.shimizukenta.secssimulator.swing;

import java.awt.BorderLayout;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.Vector;
import java.util.stream.Collectors;

import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class FailureFrame extends AbstractSwingInternalFrame {
	
	private static final long serialVersionUID = 2858548529315956501L;
	
	private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("uuuu-MM-dd'T'HH:mm:ss.SSS");
	
	private static class ThrowablePair implements Comparable<ThrowablePair> {
		
		private final LocalDateTime timestamp;
		private final Throwable cause;
		private String cache;
		
		public ThrowablePair(Throwable t) {
			this.timestamp = LocalDateTime.now();
			this.cause = t;
			this.cache = null;
		}
		
		@Override
		public String toString() {
			synchronized ( this ) {
				if ( this.cache == null ) {
					StringBuilder sb = new StringBuilder(timestamp.format(dtf))
							.append(" ");
					
					String msg = this.cause.getMessage();
					if ( msg == null ) {
						
						sb.append(cause.getClass().getSimpleName());
						
					} else {
						
						sb.append(msg);
					}
					
					this.cache = sb.toString();
				}
				
				return this.cache;
			}
		}

		@Override
		public int compareTo(ThrowablePair other) {
			return this.timestamp.compareTo(other.timestamp);
		}
	}
	
	
	private final JList<String> list;
	private final JButton clearButton;
	
	public FailureFrame(SwingSecsSimulator parent) {
		super(parent, "Failure", true, true, true, true);
		
		this.list = new JList<>();
		this.clearButton = new JButton("Clear All");
		this.clearButton.addActionListener(ev -> {
			this.clear();
		});
		
		this.setDefaultCloseOperation(HIDE_ON_CLOSE);
		
		this.setLayout(defaultBorderLayout());
		
		{
			this.add(defaultScrollPane(this.list), BorderLayout.CENTER);
		}
		{
			JPanel p = borderPanel();
			
			{
				JPanel pp = gridPanel(1, 1);
				pp.add(this.clearButton);
				p.add(pp, BorderLayout.SOUTH);
			}
			
			this.add(p, BorderLayout.EAST);
		}
	}
	
	private final LinkedList<ThrowablePair> pairs = new LinkedList<>();
	
	public void put( Throwable t ) {
		synchronized ( this ) {
			this.pairs.addFirst(new ThrowablePair(t));
			this.updateView();
		}
	}
	
	public void clear() {
		synchronized ( this ) {
			this.pairs.clear();
			this.updateView();
		}
	}
	
	@Override
	public void setVisible(boolean aFlag) {
		
		synchronized ( this ) {
			
			if ( aFlag ) {
				
				if ( ! this.isVisible() ) {
					
					int w = this.getDesktopPane().getWidth();
					int h = this.getDesktopPane().getHeight();
					
					this.setBounds(
							(w * 25 / 100),
							(h * 10 / 100),
							(w * 50 / 100),
							(h * 50 / 100));
				}
			}
			
			super.setVisible(aFlag);
			
			this.moveToFront();
		}
	}
	
	private void updateView() {
		SwingUtilities.invokeLater(() -> {
			
			synchronized ( this ) {
				
				this.list.setListData(
						new Vector<>(
								this.pairs.stream()
								.map(v -> v.toString())
								.collect(Collectors.toList())
								)
						);
				
				this.setVisible(true);
				this.repaint();
			}
		});
	}
	
}
