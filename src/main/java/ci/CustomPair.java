package ci;

import java.net.URI;

public class CustomPair {
	public String artifact_name;
	public URI url;

	public CustomPair(String artifact_name, URI url) {
		this.artifact_name = artifact_name;
		this.url = url;
	}
}
