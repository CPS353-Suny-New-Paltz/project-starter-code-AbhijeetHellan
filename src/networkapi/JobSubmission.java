package networkapi;
// Class representing a job submission request.
public class JobSubmission {
    private String inputSource;
    private String outputSource;
    private Delimiters delimiters;
    
    public JobSubmission(String inputSource, String outputSource, Delimiters delimiters) {
        this.inputSource = inputSource;
        this.outputSource = outputSource;
        // If no delimiters provided, use default
        if(delimiters != null) {
		    this.delimiters = delimiters;
		} else {
		    this.delimiters = Delimiters.createDefault();
		}
	}
    public JobSubmission(String inputSource, String outputSource) {
        this(inputSource, outputSource, null);
    }
    
    public String getInputSource() {
        return inputSource;
    }
    
    public String getOutputSource() {
        return outputSource;
    }
    
    public Delimiters getDelimiters() {
        return delimiters;
    }
}