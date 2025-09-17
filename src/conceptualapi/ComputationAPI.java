package conceptualapi;

import project.annotations.ConceptualAPI;

@ConceptualAPI

public interface ComputationAPI {
	ComputeResponse compute(ComputeRequest request);
}