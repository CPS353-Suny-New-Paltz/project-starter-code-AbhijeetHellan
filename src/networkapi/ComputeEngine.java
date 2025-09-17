package networkapi;

import project.annotations.NetworkAPI;

@NetworkAPI
public interface ComputeEngine {
	// Submits a new computation job.
	JobResponse submitJob(JobSubmission request);
	// Gets the current status of a submitted job.
	JobResponse getJobStatus(String jobId);
	// Cancels a job that is currently processing or pending.
	JobResponse cancelJob(String jobId);
}