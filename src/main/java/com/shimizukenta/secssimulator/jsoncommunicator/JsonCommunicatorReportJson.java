package com.shimizukenta.secssimulator.jsoncommunicator;

import com.shimizukenta.jsonhub.JsonHub;

public class JsonCommunicatorReportJson {
	
	public Boolean communicating;
	public String receive;
	public LogReport log;
	public JsonHub config;
	
	public JsonCommunicatorReportJson() {
		this.communicating = null;
		this.receive = null;
		this.log = null;
		this.config = null;
	}
	
}
