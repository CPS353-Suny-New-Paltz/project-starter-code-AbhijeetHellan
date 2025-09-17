package networkapi;

// Interface representing the response from a job submission or status request.
public interface JobResponse {
	String getJobId();

	boolean isSuccess();

	String getMessage();

	JobStatus getStatus();
}