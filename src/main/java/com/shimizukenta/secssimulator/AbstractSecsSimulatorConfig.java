package com.shimizukenta.secssimulator;

import java.io.Serializable;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Collection;

import com.shimizukenta.secs.hsmsss.HsmsSsCommunicatorConfig;
import com.shimizukenta.secs.hsmsss.HsmsSsProtocol;
import com.shimizukenta.secs.secs1ontcpip.Secs1OnTcpIpCommunicatorConfig;

public abstract class AbstractSecsSimulatorConfig implements Serializable {
	
	private static final long serialVersionUID = 7451456701694504127L;
	
	private boolean autoReply;
	private boolean autoReplySxF0;
	private boolean autoReplyS9Fy;
	private SecsSimulatorProtocol protocol;
	private final HsmsSsCommunicatorConfig hsmsSsCommConfig = new HsmsSsCommunicatorConfig();
	private final Secs1OnTcpIpCommunicatorConfig secs1OnTcpIpCommConfig = new Secs1OnTcpIpCommunicatorConfig();
	private final Secs1OnTcpIpCommunicatorConfig secs1OnTcpIpRecvCommConfig = new Secs1OnTcpIpCommunicatorConfig();
	private SocketAddress secs1AdapterSocketAddress;
	
	public AbstractSecsSimulatorConfig() {
		autoReply = true;
		autoReplySxF0 = false;
		autoReplyS9Fy = false;
		protocol = SecsSimulatorProtocol.HSMS_SS_PASSIVE;
		secs1AdapterSocketAddress = null;
	}
	
	
	private final Collection<AutoReplyStateChangedListener> autoReplyStateChangedListeners = new ArrayList<>();
	
	public boolean addAutoReplyStateChangedListener(AutoReplyStateChangedListener l) {
		synchronized ( autoReplyStateChangedListeners ) {
			l.changed(autoReply());
			return autoReplyStateChangedListeners.add(l);
		}
	}
	
	public boolean removeAutoReplyStateChangedListener(AutoReplyStateChangedListener l) {
		synchronized ( autoReplyStateChangedListeners ) {
			return autoReplyStateChangedListeners.remove(l);
		}
	}
	
	public void autoReply(boolean f) {
		synchronized ( autoReplyStateChangedListeners ) {
			this.autoReply = f;
			autoReplyStateChangedListeners.forEach(l -> {l.changed(f);});
		}
	}
	
	public boolean autoReply() {
		synchronized ( autoReplyStateChangedListeners ) {
			return autoReply;
		}
	}
	
	
	private final Collection<AutoReplySxF0StateChangedListener> autoReplySxF0StateChangedListeners = new ArrayList<>();
	
	public boolean addAutoReplySxF0StateChangedListener(AutoReplySxF0StateChangedListener l) {
		synchronized ( autoReplySxF0StateChangedListeners ) {
			l.changed(autoReplySxF0());
			return autoReplySxF0StateChangedListeners.add(l);
		}
	}
	
	public boolean removeAutoReplySxF0StateChangedListener(AutoReplySxF0StateChangedListener l) {
		synchronized ( autoReplySxF0StateChangedListeners ) {
			return autoReplySxF0StateChangedListeners.remove(l);
		}
	}
	
	public void autoReplySxF0(boolean f) {
		synchronized ( autoReplySxF0StateChangedListeners ) {
			autoReplySxF0 = f;
			autoReplySxF0StateChangedListeners.forEach(l -> {l.changed(f);});
		}
	}
	
	public boolean autoReplySxF0() {
		synchronized ( autoReplySxF0StateChangedListeners ) {
			return autoReplySxF0;
		}
	}
	
	
	private final Collection<AutoReplyS9FyStateChangedListener> autoReplyS9FyStateChangedListeners = new ArrayList<>();
	
	public boolean addAutoReplyS9FyStateChangedListener(AutoReplyS9FyStateChangedListener l) {
		synchronized ( autoReplyS9FyStateChangedListeners ) {
			l.changed(autoReplyS9Fy());
			return autoReplyS9FyStateChangedListeners.add(l);
		}
	}
	
	public boolean removeAutoReplyS9FyStateChangedListener(AutoReplyS9FyStateChangedListener l) {
		synchronized ( autoReplyS9FyStateChangedListeners ) {
			return autoReplyS9FyStateChangedListeners.remove(l);
		}
	}
	
	public void autoReplyS9Fy(boolean f) {
		synchronized ( autoReplyS9FyStateChangedListeners ) {
			autoReplyS9Fy = f;
			autoReplyS9FyStateChangedListeners.forEach(l -> {l.changed(f);});
		}
	}
	
	public boolean autoReplyS9Fy() {
		synchronized ( autoReplyS9FyStateChangedListeners ) {
			return autoReplyS9Fy;
		}
	}
	
	
	public SecsSimulatorProtocol protocol() {
		synchronized ( this ) {
			return protocol;
		}
	}
	
	public void protocol(SecsSimulatorProtocol protocol) {
		synchronized ( this ) {
			this.protocol = protocol;
			
			switch ( protocol ) {
			case HSMS_SS_PASSIVE: {
				hsmsSsCommConfig.protocol(HsmsSsProtocol.PASSIVE);
				break;
			}
			case HSMS_SS_ACTIVE: {
				hsmsSsCommConfig.protocol(HsmsSsProtocol.ACTIVE);
				break;
			}
			default: {
				/* nothing */
			}
			}
		}
	}
	
	public HsmsSsCommunicatorConfig hsmsSsCommunicatorConfig() {
		return hsmsSsCommConfig;
	}
	
	public Secs1OnTcpIpCommunicatorConfig secs1OnTcpIpCommunicatorConfig() {
		return secs1OnTcpIpCommConfig;
	}
	
	public Secs1OnTcpIpCommunicatorConfig secs1OnTcpIpReceiverCommunicatorConfig() {
		return secs1OnTcpIpRecvCommConfig;
	}
	
	public void secs1AdapterSocketAddress(SocketAddress addr) {
		synchronized ( this ) {
			secs1AdapterSocketAddress = addr;
		}
	}
	
	public SocketAddress secs1AdapterSocketAddress() {
		synchronized ( this ) {
			return secs1AdapterSocketAddress;
		}
	}
}
