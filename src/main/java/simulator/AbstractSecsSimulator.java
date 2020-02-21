package simulator;

import java.io.IOException;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

import secs.SecsCommunicatableStateChangeListener;
import secs.SecsCommunicator;
import secs.SecsException;
import secs.SecsLogListener;
import secs.SecsMessage;
import secs.SecsMessageReceiveListener;
import secs.SecsSendMessageException;
import secs.SecsWaitReplyMessageException;
import secs.sml.SmlMessage;
import secs.sml.SmlMessageParser;
import secs.sml.SmlParseException;

public abstract class AbstractSecsSimulator {
	
	private final Collection<SecsMessageReceiveListener> recvListeners = new CopyOnWriteArrayList<>();
	private final Collection<SecsCommunicatableStateChangeListener> stateChangeListeners = new CopyOnWriteArrayList<>();
	private final Collection<SecsLogListener> logListeners = new CopyOnWriteArrayList<>();
	
	private SecsCommunicator secsCommunicator;
	
	public AbstractSecsSimulator() {
		secsCommunicator = null;
	}
	
	abstract protected SecsCommunicator createSecsCommunicator();
	
	protected void openCommunicator() throws IOException {
		
		synchronized ( this ) {
			
			if ( secsCommunicator != null ) {
				secsCommunicator.close();
			}
			
			secsCommunicator = createSecsCommunicator();
			
			recvListeners.forEach(secsCommunicator::addSecsMessageReceiveListener);
			stateChangeListeners.forEach(secsCommunicator::addSecsCommunicatableStateChangeListener);
			logListeners.forEach(secsCommunicator::addSecsLogListener);
			
			secsCommunicator.open();
		}
	}
	
	protected void closeCommunicator() throws IOException {
		
		synchronized ( this ) {
			
			if ( secsCommunicator == null ) {
				return;
			}
			
			try {
				secsCommunicator.close();
			}
			finally {
				secsCommunicator = null;
			}
		}
	}
	
	protected boolean addSecsMessageReceiveListener(SecsMessageReceiveListener lstnr) {
		
		synchronized ( this ) {
			
			if ( secsCommunicator != null ) {
				secsCommunicator.addSecsMessageReceiveListener(lstnr);
			}
			
			return recvListeners.add(lstnr);
		}
	}
	
	protected boolean removeSecsMessageReceiveListener(SecsMessageReceiveListener lstnr) {
		
		synchronized ( this ) {
			
			if ( secsCommunicator != null ) {
				secsCommunicator.removeSecsMessageReceiveListener(lstnr);
			}
			
			return recvListeners.remove(lstnr);
		}
	}
	
	protected boolean addSecsCommunicatableStateChangeListener(SecsCommunicatableStateChangeListener lstnr) {
		
		synchronized ( this ) {
			
			if ( secsCommunicator != null ) {
				secsCommunicator.addSecsCommunicatableStateChangeListener(lstnr);
			}
			
			return stateChangeListeners.add(lstnr);
		}
	}
	
	protected boolean removeSecsCommunicatableStateChangeListener(SecsCommunicatableStateChangeListener lstnr) {
		
		synchronized ( this ) {
			
			if ( secsCommunicator != null ) {
				secsCommunicator.removeSecsCommunicatableStateChangeListener(lstnr);
			}
			
			return stateChangeListeners.remove(lstnr);
		}
	}
	
	protected boolean addSecsLogListener(SecsLogListener lstnr) {
		
		synchronized ( this ) {
			
			if ( secsCommunicator != null ) {
				secsCommunicator.addSecsLogListener(lstnr);
			}
			
			return logListeners.add(lstnr);
		}
	}
	
	protected boolean removeSecsLogListener(SecsLogListener lstnr) {
		
		synchronized ( this ) {
			
			if ( secsCommunicator != null ) {
				secsCommunicator.removeSecsLogListener(lstnr);
			}
			
			return logListeners.remove(lstnr);
		}
	}
	
	protected Optional<SecsMessage> sendSml(SmlMessage smlMessage)
			throws InterruptedException
			, SecsSendMessageException, SecsWaitReplyMessageException, SecsException {
		
		synchronized ( this ) {
			
			if ( secsCommunicator == null ) {
				throw new SecsSendMessageException("Secs Communicator not opened");
			}
			
			return secsCommunicator.send(smlMessage);
		}
	}
	
	protected Optional<SecsMessage> sendSml(SecsMessage primaryMsg, SmlMessage smlMessage)
			throws InterruptedException
			, SecsSendMessageException, SecsWaitReplyMessageException, SecsException {
		
		synchronized ( this ) {
			
			if ( secsCommunicator == null ) {
				throw new SecsSendMessageException("Secs Communicator not opened");
			}
			
			return secsCommunicator.send(primaryMsg, smlMessage);
		}
	}
	
	protected Optional<SecsMessage> sendDirectSml(CharSequence smlString)
			throws InterruptedException
			, SecsSendMessageException, SecsWaitReplyMessageException, SecsException
			, SmlParseException {
		
		return sendSml(SmlMessageParser.getInstance().parse(smlString.toString()));
	}
	
	protected Optional<SecsMessage> sendDirectSml(SecsMessage primaryMsg, CharSequence smlString)
			throws InterruptedException
			, SecsSendMessageException, SecsWaitReplyMessageException, SecsException
			, SmlParseException {
		
		return sendSml(primaryMsg, SmlMessageParser.getInstance().parse(smlString.toString()));
	}
	
}
