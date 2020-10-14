package com.shimizukenta.secssimulator;

import java.nio.file.Path;

public class SecsSimulatorSmlEntryFailedException extends SecsSimulatorException {

	private static final long serialVersionUID = -3914992269775138025L;

	public SecsSimulatorSmlEntryFailedException() {
		super();
	}

	public SecsSimulatorSmlEntryFailedException(Path path) {
		super(path.toString());
	}

	public SecsSimulatorSmlEntryFailedException(Throwable cause) {
		super(cause);
	}

	public SecsSimulatorSmlEntryFailedException(Path path, Throwable cause) {
		super(path.toString(), cause);
	}

}
