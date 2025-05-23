package com.ferbo.sgp.cron.model.response;

import java.util.Objects;

import com.ferbo.sgp.cron.model.request.StartJobRequest;
import com.google.gson.annotations.SerializedName;

public class StartJobResponse extends StartJobRequest {
	
	@SerializedName(value = "status")
	protected String status = null;
	
	@SerializedName(value = "message")
	protected String message = null;
	
	public StartJobResponse(String jobName, String jobGroup, String cron) {
		super(jobName, jobGroup, cron);
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(message, status);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		StartJobResponse other = (StartJobResponse) obj;
		return Objects.equals(message, other.message) && Objects.equals(status, other.status);
	}

	@Override
	public String toString() {
		return "StartJobResponse [status=" + status + ", message=" + message + "]";
	}
}
