package com.shimizukenta.secssimulator.swing;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collection;
import java.util.Vector;
import java.util.stream.Collectors;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import com.shimizukenta.secssimulator.SmlAliasPair;

public class ControlFrame extends AbstractSwingInnerFrame {
	
	private static final long serialVersionUID = 1369410578401942391L;
	
	private final JList<String> smlList;
	
	private final JButton openButton;
	private final JButton closeButton;
	private final JButton addSmlButton;
	private final JButton removeSmlButton;
	private final JButton showSmlButton;
	private final JButton sendSmlButton;
	
	private final JCheckBox autoReply;
	
	public ControlFrame(SwingSecsSimulator parent) {
		super(parent, "Controller", true, false, true, true);
		
		this.setBounds(
				(config().screenWidth()  * 50 / 100),
				(config().screenHeight() *  5 / 100),
				(config().screenWidth()  * 45 / 100),
				(config().screenHeight() * 60 / 100));
		
		this.smlList = new JList<>();
		this.smlList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.smlList.addMouseListener(new MouseAdapter() {
			
			@Override
			public void mouseClicked(MouseEvent ev) {
				if ( ev.getClickCount() == 2 ) {
					sendSmlButton.doClick();
				}
			}
		});
		
		this.openButton = new JButton("Open");
		this.openButton.addActionListener(ev -> {
			simulator().openCommunicator();
		});
		
		this.closeButton = new JButton("Close");
		this.closeButton.addActionListener(ev -> {
			simulator().closeCommunicator();
		});
		
		this.addSmlButton = new JButton("Add...");
		this.addSmlButton.addActionListener(ev -> {
			simulator().showAddSmlDialog();
		});
		
		this.removeSmlButton = new JButton("Remove");
		this.removeSmlButton.addActionListener(ev -> {
			String alias = this.smlList.getSelectedValue();
			if ( alias != null ) {
				simulator().removeSml(alias);
			}
		});
		
		this.showSmlButton = new JButton("Show");
		this.showSmlButton.addActionListener(ev -> {
			
			String alias = this.smlList.getSelectedValue();
			if ( alias != null ) {
				simulator().optionalSmlAlias(alias).ifPresent(sm -> {
					simulator().showSml(sm);
				});
			}
		});
		
		this.sendSmlButton = new JButton("Send");
		this.sendSmlButton.addActionListener(ev -> {
			
			String alias = this.smlList.getSelectedValue();
			if ( alias != null ) {
				simulator().optionalSmlAlias(alias).ifPresent(sm -> {
					try {
						simulator().send(sm);
					}
					catch ( InterruptedException ignore ) {
					}
				});
			}
		});
		
		this.autoReply = new JCheckBox("Auto-reply");
		this.autoReply.addActionListener(ev -> {
			config().autoReply().set(this.autoReply.isSelected());
		});
		
		
		this.setLayout(defaultBorderLayout());
		
		{
			JPanel p = borderPanel();
			
			p.setBorder(defaultTitledBorder("Communicator"));
			
			{
				JPanel pp = borderPanel();
				
				{
					JPanel ppp = lineBoxPanel();
					
					ppp.add(this.openButton);
					ppp.add(this.closeButton);
					
					pp.add(ppp, BorderLayout.WEST);
				}
				
				p.add(pp, BorderLayout.CENTER);
			}
			
			this.add(p, BorderLayout.NORTH);
		}
		{
			JPanel p = borderPanel();
			
			p.setBorder(defaultTitledBorder("SML Alias"));
			
			{
				JPanel pp = borderPanel();
				
				{
					JPanel ppp = borderPanel();
					
					{
						JPanel q = lineBoxPanel();
						
						q.add(this.sendSmlButton);
						
						ppp.add(q, BorderLayout.EAST);
					}
					{
						JPanel q = lineBoxPanel();
						
						q.add(this.addSmlButton);
						q.add(this.removeSmlButton);
						q.add(this.showSmlButton);
						
						ppp.add(q, BorderLayout.WEST);
					}
					
					pp.add(ppp, BorderLayout.NORTH);
				}
				{
					final JScrollPane scrollPane = new JScrollPane(
							this.smlList,
							JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
							JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
					
					pp.add(scrollPane, BorderLayout.CENTER);
				}
				{
					JPanel ppp = borderPanel();
					
					ppp.add(autoReply, BorderLayout.WEST);
					
					pp.add(ppp, BorderLayout.SOUTH);
				}
				
				p.add(pp, BorderLayout.CENTER);
			}
			
			this.add(p, BorderLayout.CENTER);
		}
		
		config().darkMode().addChangeListener(dark -> {
			
			if ( dark ) {
				
				//HOOK
				
			} else {
				
				//HOOK
			}
		});
		
		config().autoReply().addChangeListener(this.autoReply::setSelected);
		
		this.setVisible(true);
	}
	
	@Override
	protected void notifyCommunicateStateChanged(boolean communicated) {
		this.sendSmlButton.setEnabled(communicated);
		this.sendSmlButton.repaint();
	}
	
	@Override
	protected void notifySmlAliasesChanged(Collection<? extends SmlAliasPair> pairs) {
		
		this.smlList.setListData(
				new Vector<>(
						pairs.stream()
						.sorted()
						.map(p -> p.alias())
						.collect(Collectors.toList())
						)
				);
		
		this.smlList.repaint();
	}

}
