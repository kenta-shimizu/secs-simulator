package com.shimizukenta.secssimulator.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.function.Consumer;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import com.shimizukenta.secs.hsmsss.HsmsSsCommunicatorConfig;
import com.shimizukenta.secs.secs1ontcpip.Secs1OnTcpIpCommunicatorConfig;
import com.shimizukenta.secssimulator.SecsSimulatorException;
import com.shimizukenta.secssimulator.SecsSimulatorProtocol;

public class SetConfigDialog extends AbstractSwingDialog {
	
	private static final long serialVersionUID = 3639672355647254120L;
	
	private static class NumberTextField extends JTextField {
		
		private static final long serialVersionUID = -7951884917128959768L;

		public NumberTextField(String text, int columns) {
			super(text, columns);
			this.setHorizontalAlignment(JTextField.RIGHT);
		}
		
		public void setValue(int v) {
			this.setText(String.valueOf(v));
		}
		
		public void setValue(float v) {
			this.setText(String.valueOf(v));
		}
		
		public OptionalInt optionalInt() {
			String s = this.getText();
			if ( (s != null) && (! s.isEmpty()) ) {
				if (! s.contains(".")) {
					try {
						int v = Integer.parseInt(s);
						return OptionalInt.of(v);
					}
					catch ( NumberFormatException giveup ) {
					}
				}
			}
			return OptionalInt.empty();
		}
		
		public OptionalDouble optionalDouble() {
			String s = this.getText();
			if ( (s != null) && (! s.isEmpty()) ) {
				try {
					double v = Double.parseDouble(s);
					return OptionalDouble.of(v);
				}
				catch ( NumberFormatException giveup ) {
				}
			}
			return OptionalDouble.empty();
		}
	};
	
	private static final float defaultLinktestTime = 180.0F;
	
	private final ButtonGroup protocolGroup;
	private final JRadioButton hsmsSsPassiveRadio;
	private final JRadioButton hsmsSsActiveRadio;
	private final JRadioButton secs1OnTcpIpRadio;
	private final JRadioButton secs1OnTcpIpRecvRadio;
	
	private final JTextField ipText;
	private final NumberTextField portText;
	
	private final NumberTextField deviceIdText;
	
	private final ButtonGroup hostEquipGroup;
	private final JRadioButton hostRadio;
	private final JRadioButton equipRadio;
	
	private final JCheckBox masterMode;
	
	private final NumberTextField t1Text;
	private final NumberTextField t2Text;
	private final NumberTextField t3Text;
	private final NumberTextField t4Text;
	private final NumberTextField t5Text;
	private final NumberTextField t6Text;
	private final NumberTextField t7Text;
	private final NumberTextField t8Text;
	private final NumberTextField retryText;
	private final JCheckBox linktestCheck;
	private final NumberTextField linktestText;

	private final JCheckBox autoReply;
	private final JCheckBox autoReplyS9Fy;
	private final JCheckBox autoReplySxF0;
	
	private final JCheckBox darkMode;
	
	private final JButton okButton;
	
	private boolean result;
	
	public SetConfigDialog(Frame owner, SwingSecsSimulator simm) {
		super(simm, owner, "Set Config", true);
		
		this.result = false;
		
		this.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
		
		this.hsmsSsPassiveRadio = defaultRadioButton("HSMS-SS-Passive", true);
		this.hsmsSsActiveRadio = defaultRadioButton("HSMS-SS-Active", false);
		this.secs1OnTcpIpRadio = defaultRadioButton("SECS-I-on-TCP/IP", false);
		this.secs1OnTcpIpRecvRadio = defaultRadioButton("SECS-I-on-TCP/IP-Receiver", false);
		this.protocolGroup = new ButtonGroup();
		this.protocolGroup.add(this.hsmsSsPassiveRadio);
		this.protocolGroup.add(this.hsmsSsActiveRadio);
		this.protocolGroup.add(this.secs1OnTcpIpRadio);
		this.protocolGroup.add(this.secs1OnTcpIpRecvRadio);
		
		this.ipText = new JTextField("", 15);
		this.portText = new NumberTextField("5000", 5);
		
		this.deviceIdText = new NumberTextField("10", 5);
		
		this.equipRadio = defaultRadioButton("Equip", true);
		this.hostRadio = defaultRadioButton("Host", false);
		this.hostEquipGroup = new ButtonGroup();
		this.hostEquipGroup.add(this.equipRadio);
		this.hostEquipGroup.add(this.hostRadio);
		
		this.masterMode = defaultCheckBox("Master-mode (SECS-I)", true);
		
		this.t1Text = new NumberTextField("1", 4);
		this.t2Text = new NumberTextField("1", 4);
		this.t3Text = new NumberTextField("1", 4);
		this.t4Text = new NumberTextField("1", 4);
		this.t5Text = new NumberTextField("1", 4);
		this.t6Text = new NumberTextField("1", 4);
		this.t7Text = new NumberTextField("1", 4);
		this.t8Text = new NumberTextField("1", 4);
		
		this.retryText = new NumberTextField("3", 3);
		
		this.linktestText = new NumberTextField("1", 4);
		this.linktestCheck = defaultCheckBox("Linktest (HSMS-SS): ", false);
		this.linktestCheck.addChangeListener(ev -> {
			this.linktestText.setEditable(this.linktestCheck.isSelected());
			this.linktestText.setEnabled(this.linktestCheck.isSelected());
		});
		
		this.autoReply = defaultCheckBox("Auto-reply", true);
		this.autoReplyS9Fy = defaultCheckBox("Auto-reply-S9Fy", false);
		this.autoReplySxF0 = defaultCheckBox("Auto-reply-SxF0", false);
		
		this.darkMode = defaultCheckBox("Dark-mode", false);
		
		this.okButton = new JButton("OK");
		this.okButton.addActionListener(ev -> {
			synchronized ( this ) {
				setConfig();
				this.result = true;
				this.setVisible(false);
			}
		});
		
		
		this.setLayout(defaultBorderLayout());
		
		{
			final JTabbedPane tabbedpane = new JTabbedPane();
			
			{
				List<Component> comps = new ArrayList<>();
				
				{
					JPanel p = borderPanel();
					p.setBorder(defaultTitledBorder("Protocol"));
					
					{
						JPanel pp = gridPanel(4, 1);
						
						pp.add(this.hsmsSsPassiveRadio);
						pp.add(this.hsmsSsActiveRadio);
						pp.add(this.secs1OnTcpIpRadio);
						pp.add(this.secs1OnTcpIpRecvRadio);
						
						p.add(pp, BorderLayout.WEST);
					}
					
					comps.add(p);
				}
				{
					JPanel p = borderPanel();
					p.setBorder(defaultTitledBorder("Socket-Address"));
					
					{
						List<Component> ll = new ArrayList<>();
						
						{
							JPanel pp = gridPanel(2, 1);
							pp.add(defaultLabel("IP: ", JLabel.RIGHT));
							pp.add(defaultLabel("Port: ", JLabel.RIGHT));
							ll.add(pp);
						}
						{
							JPanel pp = gridPanel(2, 1);
							
							{
								JPanel ppp = flowPanel(FlowLayout.LEFT);
								ppp.add(this.ipText);
								pp.add(ppp);
							}
							{
								JPanel ppp = flowPanel(FlowLayout.LEFT);
								ppp.add(this.portText);
								pp.add(ppp);
							}
							
							ll.add(pp);
						}
						
						p.add(compactStackPanel(BorderLayout.WEST, ll));
					}
					
					comps.add(p);
				}
				{
					JPanel p = flowPanel(FlowLayout.LEFT);
					p.add(defaultLabel("Device-ID: ", JLabel.RIGHT));
					p.add(this.deviceIdText);
					comps.add(p);
				}
				{
					JPanel p = borderPanel();
					p.setBorder(defaultTitledBorder("Equip/Host"));
					
					{
						JPanel pp = gridPanel(2, 1);
						
						pp.add(this.equipRadio);
						pp.add(this.hostRadio);
						
						p.add(pp, BorderLayout.WEST);
					}
					
					comps.add(p);
				}
				{
					JPanel p = flowPanel(FlowLayout.LEFT);
					p.add(this.masterMode);
					comps.add(p);
				}
				
				JPanel p = compactStackPanel(BorderLayout.NORTH, comps);
				p.setOpaque(true);
				
				{
					final Color bgColor = p.getBackground();
					
					this.config().darkMode().addChangeListener(dark -> {
						if ( dark ) {
							p.setBackground(this.config().defaultDarkPanelBackGroundColor());
						} else {
							p.setBackground(bgColor);
						}
					});
				}
				
				tabbedpane.add("General", defaultScrollPane(p));
			}
			{
				List<Component> comps = new ArrayList<>();
				
				{
					
					JPanel p = borderPanel();
					p.setBorder(defaultTitledBorder("Timeout"));
					
					{
						JPanel pp = gridPanel(8, 1);
						
						pp.add(timeoutPanel(t1Text, "T1: ", "sec."));
						pp.add(timeoutPanel(t2Text, "T2: ", "sec."));
						pp.add(timeoutPanel(t3Text, "T3: ", "sec."));
						pp.add(timeoutPanel(t4Text, "T4: ", "sec."));
						pp.add(timeoutPanel(t5Text, "T5: ", "sec."));
						pp.add(timeoutPanel(t6Text, "T6: ", "sec."));
						pp.add(timeoutPanel(t7Text, "T7: ", "sec."));
						pp.add(timeoutPanel(t8Text, "T8: ", "sec."));
						
						p.add(pp, BorderLayout.WEST);
					}
					
					comps.add(p);
				}
				{
					JPanel p = flowPanel(FlowLayout.LEFT);
					
					p.add(defaultLabel("Retry (SECS-I): ", JLabel.RIGHT));
					p.add(this.retryText);
					
					comps.add(p);
				}
				{
					JPanel p = flowPanel(FlowLayout.LEFT);
					
					p.add(linktestCheck);
					p.add(linktestText);
					p.add(defaultLabel("sec.", JLabel.LEFT));
					
					comps.add(p);
				}
				
				JPanel p = compactStackPanel(BorderLayout.NORTH, comps);
				p.setOpaque(true);
				
				{
					final Color bgColor = p.getBackground();
					
					this.config().darkMode().addChangeListener(dark -> {
						if ( dark ) {
							p.setBackground(this.config().defaultDarkPanelBackGroundColor());
						} else {
							p.setBackground(bgColor);
						}
					});
				}
				
				tabbedpane.add("Timer", defaultScrollPane(p));
			}
			{
				List<Component> comps = new ArrayList<>();
				
				{
					JPanel p = borderPanel();
					p.setBorder(defaultTitledBorder("Auto-Replies"));
					
					{
						JPanel pp = gridPanel(3, 1);
						
						pp.add(this.autoReply);
						pp.add(this.autoReplyS9Fy);
						pp.add(this.autoReplySxF0);
						
						p.add(pp, BorderLayout.WEST);
					}
					
					comps.add(p);
				}
				{
					JPanel p = flowPanel(FlowLayout.LEFT);
					p.add(this.darkMode);
					comps.add(p);
				}
				
				JPanel p = compactStackPanel(BorderLayout.NORTH, comps);
				p.setOpaque(true);
				
				{
					final Color bgColor = p.getBackground();
					
					this.config().darkMode().addChangeListener(dark -> {
						if ( dark ) {
							p.setBackground(this.config().defaultDarkPanelBackGroundColor());
						} else {
							p.setBackground(bgColor);
						}
					});
				}
				
				tabbedpane.add("Other", defaultScrollPane(p));
			}
			
			this.add(tabbedpane, BorderLayout.CENTER);
		}
		{
			JPanel p = flowPanel(FlowLayout.CENTER);
			
			p.add(this.okButton);
			
			this.add(p, BorderLayout.SOUTH);
		}
	}
	
	private JPanel timeoutPanel(Component comp, String header, String footer) {
		JPanel p = flowPanel(FlowLayout.RIGHT);
		p.add(defaultLabel(header, JLabel.RIGHT));
		p.add(comp);
		p.add(defaultLabel(footer, JLabel.LEFT));
		return p;
	}
	
	@Override
	public void setVisible(boolean aFlag) {
		if ( aFlag ) {
			this.result = false;
			
			updateView();
			
			this.setSize(
					(this.getOwner().getWidth()  * 50 / 100),
					(this.getOwner().getHeight() * 90 / 100)
					);
			
			this.setLocationRelativeTo(this.getOwner());
			
			this.repaint();
		}
		
		super.setVisible(aFlag);
	}
	
	/**
	 * Returns {@code true} if set success.
	 * 
	 * @return {@code true} if set success
	 */
	public boolean getResult() {
		return this.result;
	}
	
	private void updateView() {
		
		final HsmsSsCommunicatorConfig hsmsSs = config().hsmsSsCommunicatorConfig();
		final Secs1OnTcpIpCommunicatorConfig secs1 = config().secs1OnTcpIpCommunicatorConfig();
		
		switch ( config().protocol().get()) {
		case HSMS_SS_PASSIVE: {
			this.hsmsSsPassiveRadio.setSelected(true);
			break;
		}
		case HSMS_SS_ACTIVE: {
			this.hsmsSsActiveRadio.setSelected(true);
			break;
		}
		case SECS1_ON_TCP_IP: {
			this.secs1OnTcpIpRadio.setSelected(true);
			break;
		}
		case SECS1_ON_TCP_IP_RECEIVER: {
			this.secs1OnTcpIpRecvRadio.setSelected(true);
			break;
		}
		default: {
			/* Nothing */
		}
		}
		
		try {
			this.ipText.setText("");
			this.portText.setValue(5000);
			
			SocketAddress a = hsmsSs.socketAddress().getSocketAddress();
			if ( a instanceof InetSocketAddress ) {
				InetSocketAddress i = (InetSocketAddress)a;
				this.ipText.setText(i.getAddress().getHostAddress());
				this.portText.setValue(i.getPort());
			}
		}
		catch (IllegalStateException giveup) {
		}
		
		this.deviceIdText.setValue(hsmsSs.deviceId().intValue());
		
		if ( hsmsSs.isEquip().booleanValue() ) {
			this.equipRadio.setSelected(true);
		} else {
			this.hostRadio.setSelected(true);
		}
		
		this.masterMode.setSelected(secs1.isMaster().booleanValue());
		
		this.t1Text.setValue(hsmsSs.timeout().t1().floatValue());
		this.t2Text.setValue(hsmsSs.timeout().t2().floatValue());
		this.t3Text.setValue(hsmsSs.timeout().t3().floatValue());
		this.t4Text.setValue(hsmsSs.timeout().t4().floatValue());
		this.t5Text.setValue(hsmsSs.timeout().t5().floatValue());
		this.t6Text.setValue(hsmsSs.timeout().t6().floatValue());
		this.t7Text.setValue(hsmsSs.timeout().t7().floatValue());
		this.t8Text.setValue(hsmsSs.timeout().t8().floatValue());
		
		this.retryText.setValue(secs1.retry().intValue());
		
		{
			float v = hsmsSs.linktest().floatValue();
			if ( v > 0.0F ) {
				
				this.linktestCheck.setSelected(true);
				this.linktestText.setValue(v);
				this.linktestText.setEditable(true);
				this.linktestText.setEnabled(true);
				
			} else {
				
				this.linktestCheck.setSelected(false);
				this.linktestText.setValue(defaultLinktestTime);
				this.linktestText.setEditable(false);
				this.linktestText.setEnabled(false);
			}
		}
		
		this.autoReply.setSelected(config().autoReply().booleanValue());
		this.autoReplyS9Fy.setSelected(config().autoReplyS9Fy().booleanValue());
		this.autoReplySxF0.setSelected(config().autoReplySxF0().booleanValue());
		
		this.darkMode.setSelected(config().darkMode().booleanValue());
		
	}
	
	private boolean setConfig() {
		
		boolean f = true;
		
		if ( this.hsmsSsPassiveRadio.isSelected() ) {
			
			config().protocol(SecsSimulatorProtocol.HSMS_SS_PASSIVE);
			
		} else if ( this.hsmsSsActiveRadio.isSelected() ) {
			
			config().protocol(SecsSimulatorProtocol.HSMS_SS_ACTIVE);
			
		} else if ( this.secs1OnTcpIpRadio.isSelected() ) {
			
			config().protocol(SecsSimulatorProtocol.SECS1_ON_TCP_IP);
			
		} else if ( this.secs1OnTcpIpRecvRadio.isSelected() ) {
			
			config().protocol(SecsSimulatorProtocol.SECS1_ON_TCP_IP_RECEIVER);
		}
		
		try {
			String ip = this.ipText.getText().trim();
			if ( ip.isEmpty() ) {
				throw new SecsSimulatorException("Config Socket-Address IP is empty");
			}
			
			int port = this.portText.optionalInt()
					.orElseThrow(() -> new SecsSimulatorException("Config Socket-Address Port invalid"));
			
			if ( port <= 0 || port >= 65536 ) {
				throw new SecsSimulatorException("Config Socket-Address Port(" + port + ") invalid");
			}
			
			try {
				config().socketAddress(new InetSocketAddress(ip, port));
			}
			catch ( IllegalArgumentException e ) {
				throw new SecsSimulatorException("Config Socket-Address parse failed", e);
			}
		}
		catch ( SecsSimulatorException e ) {
			simulator().putFailure(e);
		}
		
		try {
			int v = this.deviceIdText.optionalInt()
					.orElseThrow(() -> new SecsSimulatorException("Config Device-ID invalid"));
			
			if ( v < 0 || v >= 32768 ) {
				throw new SecsSimulatorException("Config Device-ID (" + v + ") invalid");
			}
			
			config().deviceId(v);
		}
		catch ( SecsSimulatorException e ) {
			simulator().putFailure(e);
		}
		
		if ( this.equipRadio.isSelected() ) {
			
			config().isEquip(true);
			
		} else if ( this.hostRadio.isSelected() ) {
			
			config().isEquip(false);
		}
		
		config().isMaster(this.masterMode.isSelected());
		
		setTime(this.t1Text, config()::timeoutT1, "Config Timeout-T1");
		setTime(this.t2Text, config()::timeoutT2, "Config Timeout-T2");
		setTime(this.t3Text, config()::timeoutT3, "Config Timeout-T3");
		setTime(this.t4Text, config()::timeoutT4, "Config Timeout-T4");
		setTime(this.t5Text, config()::timeoutT5, "Config Timeout-T5");
		setTime(this.t6Text, config()::timeoutT6, "Config Timeout-T6");
		setTime(this.t7Text, config()::timeoutT7, "Config Timeout-T7");
		setTime(this.t8Text, config()::timeoutT8, "Config Timeout-T8");
		
		try {
			int v = this.retryText.optionalInt()
					.orElseThrow(() -> new SecsSimulatorException("Config Retry invalid"));
			
			if ( v < 0 ) {
				
				throw new SecsSimulatorException("Config Retry (" + v + ") invalid < 0");
			}
			
			config().retry(v);
		}
		catch ( SecsSimulatorException e ) {
			simulator().putFailure(e);
		}
		
		if ( this.linktestCheck.isSelected() ) {
			
			setTime(this.linktestText, config()::linktest, "Config Linktest");
			
		} else {
			
			config().notLinktest();
		}
		
		config().autoReply().set(this.autoReply.isSelected());
		config().autoReplyS9Fy().set(this.autoReplyS9Fy.isSelected());
		config().autoReplySxF0().set(this.autoReplySxF0.isSelected());
		
		config().darkMode().set(this.darkMode.isSelected());
		
		return f;
	}
	
	private void setTime(NumberTextField t, Consumer<Float> c, String timeName) {
		try {
			double v = t.optionalDouble()
					.orElseThrow(() -> new SecsSimulatorException(timeName + " invalid"));
			
			if ( v <= 0.0D ) {
				throw new SecsSimulatorException(timeName + " invalid <= 0.0");
			}
			
			c.accept(Double.valueOf(v).floatValue());
		}
		catch ( SecsSimulatorException e ) {
			simulator().putFailure(e);
		}
	}
	
}
