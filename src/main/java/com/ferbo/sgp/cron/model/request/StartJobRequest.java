package com.ferbo.sgp.cron.model.request;

import java.util.Objects;

import com.google.gson.annotations.SerializedName;

public class StartJobRequest {
	@SerializedName(value = "jobName")
	protected String jobName = null;
	
	@SerializedName(value = "jobGroup")
	protected String jobGroup = null;
	
	@SerializedName(value = "cron")
	protected String cron = null;
	
	public StartJobRequest() {
		
	}
	
	public StartJobRequest(String jobName, String jobGroup, String cron) {
		super();
		this.jobName = jobName;
		this.jobGroup = jobGroup;
		this.cron = cron;
	}

	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	public String getJobGroup() {
		return jobGroup;
	}

	public void setJobGroup(String jobGroup) {
		this.jobGroup = jobGroup;
	}

	public String getCron() {
		return cron;
	}

	public void setCron(String cron) {
		this.cron = cron;
	}

	@Override
	public int hashCode() {
		return Objects.hash(cron, jobGroup, jobName);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		StartJobRequest other = (StartJobRequest) obj;
		return Objects.equals(cron, other.cron) && Objects.equals(jobGroup, other.jobGroup)
				&& Objects.equals(jobName, other.jobName);
	}

	@Override
	public String toString() {
		return "StartJobRequest [jobName=" + jobName + ", jobGroup=" + jobGroup + ", cron=" + cron + "]";
	}
}
