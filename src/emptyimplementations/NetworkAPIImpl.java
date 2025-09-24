package emptyimplementations;

import conceptualapi.ComputationAPI;
import networkapi.ComputeEngine;
import networkapi.JobResponse;
import networkapi.JobSubmission;
import processapi.DataStorageAPI;

// Implementation of the ComputeEngine interface with unimplemented methods.
public class NetworkAPIImpl implements ComputeEngine {
	// Dependencies on DataStorageAPI and ComputationAPI
	private DataStorageAPI dataStore;
	private ComputationAPI computation;

	public NetworkAPIImpl(DataStorageAPI dataStore, ComputationAPI computation) {
		this.dataStore = dataStore;
		this.computation = computation;
	}

	@Override
	public JobResponse submitJob(JobSubmission request) {
		// Method is not implemented yet, return a default failure response.
		return new JobResponseImpl();
	}

	@Override
	public JobResponse getJobStatus(String jobId) {
		// Method is not implemented yet, return a default failure response.
		return new JobResponseImpl();
	}

	@Override
	public JobResponse cancelJob(String jobId) {
		// Method is not implemented yet, return a default failure response.
		return new JobResponseImpl();
	}
}
