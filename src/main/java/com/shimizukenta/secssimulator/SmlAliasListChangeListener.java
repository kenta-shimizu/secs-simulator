package com.shimizukenta.secssimulator;

import java.util.EventListener;
import java.util.List;

public interface SmlAliasListChangeListener extends EventListener {
	public void changed(List<String> aliases);
}
