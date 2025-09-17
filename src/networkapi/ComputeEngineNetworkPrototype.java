package networkapi;
import project.annotations.NetworkAPIPrototype;
public class ComputeEngineNetworkPrototype {
	@NetworkAPIPrototype
	public void prototype(ComputeEngine engine) {
		// Create custom delimiters
		Delimiters customDelimiters = new Delimiters(',', ':');

		// Create a job submission request with input, output, and custom delimiters
		JobSubmission request = new JobSubmission("input stream", "output stream", customDelimiters);

		// 3. Submit the job to the compute engine
		JobResponse response = engine.submitJob(request);

		// 4. Check if the job submission was successful
		if (!response.isSuccess()) {
			System.out.println("Job submission failed: " + response.getMessage());
			return;
		}
		System.out.println("Job submitted successfully with ID: " + response.getJobId());

		// Check the job status
		JobResponse statusResponse = engine.getJobStatus(response.getJobId());
		System.out.println("Job status: " + statusResponse.getStatus());
		System.out.println("Message: " + statusResponse.getMessage());
	}
}