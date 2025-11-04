package emptyimplementations;

import java.util.List;
import java.util.UUID;

import conceptualapi.ComputationAPI;
import networkapi.ComputeEngine;
import networkapi.Delimiters;
import networkapi.JobResponse;
import networkapi.JobStatus;
import networkapi.JobSubmission;
import processapi.DataStorageAPI;
import processapi.ReadRequest;
import processapi.ReadResponse;
import processapi.WriteRequest;
import processapi.WriteResponse;

public abstract class AbstractNetworkAPIImpl implements ComputeEngine {
	protected DataStorageAPI dataStore;
	protected ComputationAPI computation;

	public AbstractNetworkAPIImpl(DataStorageAPI dataStore, ComputationAPI computation) {
		this.dataStore = dataStore;
		this.computation = computation;
	}

	protected abstract List<String> processNumbers(List<Integer> numbers, char separator) throws Exception;

	@Override
	public JobResponse submitJob(JobSubmission request) {
	    if (request == null) {
	        String jobId = UUID.randomUUID().toString();
	        return new JobResponseImpl(jobId, false, "Job request cannot be null", JobStatus.FAILED);
	    }
	    
	    if (request.getInputSource() == null) {
	        String jobId = UUID.randomUUID().toString();
	        return new JobResponseImpl(jobId, false, "Input source cannot be null", JobStatus.FAILED);
	    }
	    
	    if (request.getOutputSource() == null) {
	        String jobId = UUID.randomUUID().toString();
	        return new JobResponseImpl(jobId, false, "Output source cannot be null", JobStatus.FAILED);
	    }
		
		try {
			String jobId = UUID.randomUUID().toString();

			ReadRequest readRequest = new ReadRequest(request.getInputSource());
			ReadResponse readResponse = dataStore.readData(readRequest);
			if (!readResponse.isSuccess()) {
				return new JobResponseImpl(jobId, false, "Failed to read input data", JobStatus.FAILED);
			}

			List<Integer> numbers = readResponse.getIntegerStream().getNumbers();

			Delimiters delimiters = request.getDelimiters();
			char separator = delimiters.getKeyValueSeparator();

			List<String> results = processNumbers(numbers, separator);

			WriteRequest writeRequest = new WriteRequest(request.getOutputSource(), results);
			WriteResponse writeResponse = dataStore.writeData(writeRequest);
			if (!writeResponse.isSuccess()) {
				return new JobResponseImpl(jobId, false, "Failed to write output data", JobStatus.FAILED);
			}
			return new JobResponseImpl(jobId, true, "Job completed successfully", JobStatus.COMPLETED);

		} catch (Exception e) {
			String jobId = UUID.randomUUID().toString();
			return new JobResponseImpl(jobId, false, "Job execution failed: " + e.getMessage(), JobStatus.FAILED);
		}
	}

    @Override
    public JobResponse getJobStatus(String jobId) {
        if (jobId == null || jobId.trim().isEmpty()) {
            return new JobResponseImpl("", false, "Job ID cannot be null or empty", JobStatus.FAILED);
        }

        try {
            return new JobResponseImpl(jobId, true, "Job status retrieved", JobStatus.COMPLETED);
        } catch (Exception e) {
            return new JobResponseImpl(jobId, false, "Failed to retrieve job status: " + e.getMessage(), 
                    JobStatus.FAILED);
        }
    }

    @Override
    public JobResponse cancelJob(String jobId) {
        if (jobId == null || jobId.trim().isEmpty()) {
            return new JobResponseImpl("", false, "Job ID cannot be null or empty", JobStatus.FAILED);
        }

        try {
            return new JobResponseImpl(jobId, true, "Job cancelled successfully", JobStatus.CANCELLED);
        } catch (Exception e) {
            return new JobResponseImpl(jobId, false, "Failed to cancel job: " + e.getMessage(), 
                    JobStatus.FAILED);
        }
    }
}
