package com.shimizukenta.secssimulator;

import java.util.EventListener;

public interface AutoReplyStateChangedListener extends EventListener {
	public void changed(boolean f);
}
