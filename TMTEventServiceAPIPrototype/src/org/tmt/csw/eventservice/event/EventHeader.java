package org.tmt.csw.eventservice.event;

import java.util.Date;

/**
 * Represents the message headers passed along with the payload
 * 
 * @author amit_harsola 
 *
 */
public class EventHeader {
	
	// Event Message Headers
	private String source;
	private Date createTimestamp;
	private Date publishTimestamp;
	
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public Date getCreateTimestamp() {
		return createTimestamp;
	}
	public void setCreateTimestamp(Date createTimestamp) {
		this.createTimestamp = createTimestamp;
	}
	public Date getPublishTimestamp() {
		return publishTimestamp;
	}
	public void setPublishTimestamp(Date publishTimestamp) {
		this.publishTimestamp = publishTimestamp;
	}
}
