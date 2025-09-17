package conceptualapi;
import project.annotations.ConceptualAPIPrototype;
public class JobHandlerPrototype {
@ConceptualAPIPrototype
    public void prototype(ComputationAPI logic) {


    	// In the real application, this number would be read from an input source.
        int numberToCompute = 12345;

        // 1. Create a request for the number.
        System.out.println("Creating compute request for: " + numberToCompute);
        ComputeRequest request = new ComputeRequest(numberToCompute);

        // 2. Call the calculator to get the result.
        ComputeResponse response = logic.compute(request);

        // 3. Print the result based on the response.
        if (response.isSuccess()) {
            System.out.println("Input: " + request.getNumber() + " -> Result: '" + response.getResult() + "'");
        } else {
            System.out.println("Computation failed: " + response.getMessage());
        }
    }
}