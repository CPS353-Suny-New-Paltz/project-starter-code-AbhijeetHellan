package conceptualapi;
// Represents a request to perform a computation task
public class ComputeRequest {
    private int number;
    
    public ComputeRequest(int number) {
        if (number <= 0 || number >= Integer.MAX_VALUE) {
            throw new IllegalArgumentException("Number must be positive and less than Integer.MAX_VALUE");
        }
        this.number = number;
    }
    
    public int getNumber() {
        return number;
    }
}