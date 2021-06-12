package asaad;

import com.cdancy.jenkins.rest.JenkinsClient;
import com.cdancy.jenkins.rest.domain.common.Error;
import com.cdancy.jenkins.rest.domain.common.IntegerResponse;
import com.cdancy.jenkins.rest.domain.job.BuildInfo;
import com.cdancy.jenkins.rest.domain.queue.QueueItem;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

public class JenkinsJob {

	private	static String JENKINS_URI = "http://localhost:8080";
	private	static String USERNAME = "firstAdmin";
	private	static String PASSWORD = "1191e8798b94ac79434a686b0070a4c956";   //password should be token api, otherwise crumb error 401

	private String job_name;
	private	String xml_job_config_path = "src/main/java/asaad/job.xml";

	public JenkinsJob(String job_name, String xml_job_config_path) {
		this.job_name = job_name;
		this.xml_job_config_path = xml_job_config_path;
		try {
			createJob(JENKINS_URI, job_name, xml_job_config_path);
		} catch (IOException | SAXException | ParserConfigurationException | TransformerException e) {
			e.printStackTrace();
			System.err.println(e.toString());
		}
	}



	private boolean areErrors(List<Error> errors){
		int num_errors = 0;
		for (Error error : errors){
			num_errors++;
			System.out.println("error : " + error.exceptionName());
		}

		return num_errors > 0;
	}

	private String xmlToString(Document document) throws TransformerException {
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer t = tf.newTransformer();
		StringWriter sw = new StringWriter();
		t.transform(new DOMSource(document), new StreamResult(sw));


		return sw.toString();
	}

	private String createJob(String url, String newJobName, String xmlFileUri) throws IOException, SAXException, ParserConfigurationException, TransformerException {
		DocumentBuilderFactory documentBuilder = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = documentBuilder.newDocumentBuilder();
		Document doc = docBuilder.parse(xmlFileUri);

		String xmlString = xmlToString(doc);
		Client client = Client.create();
		client.addFilter(new com.sun.jersey.api.client.filter.HTTPBasicAuthFilter(USERNAME, PASSWORD));
		WebResource webResource = client.resource(url + "/createItem?name=" + newJobName);
		ClientResponse response = webResource.type("application/xml").post(ClientResponse.class, xmlString);
		String jsonResponse = response.getEntity(String.class);
		client.destroy();
		//System.out.println("Response createJob:::::" + jsonResponse);

		return jsonResponse;

	}

	public static List<String> listJobs() {
		Client client = Client.create();
		client.addFilter(new com.sun.jersey.api.client.filter.HTTPBasicAuthFilter(USERNAME, PASSWORD));
		WebResource webResource = client.resource(JENKINS_URI + "/api/xml");
		ClientResponse response = webResource.get(ClientResponse.class);
		String jsonResponse = response.getEntity(String.class);
		client.destroy();

		List<String> jobList = new ArrayList<>();
		String[] jobs = jsonResponse.split("job>");
		for (String job : jobs) {
			String[] names = job.split("name>");
			if (names.length == 3) {
				String name = names[1];
				name = name.substring(0, name.length() - 2); // Take off </ for the closing name tag: </name>
				jobList.add(name);

			}

		}
		return jobList;
	}

	public  String deleteJob() {
		Client client = Client.create();
		client.addFilter(new com.sun.jersey.api.client.filter.HTTPBasicAuthFilter(USERNAME, PASSWORD));
		WebResource webResource = client.resource(JENKINS_URI + "/job/" + job_name + "/doDelete");
		ClientResponse response = webResource.post(ClientResponse.class);
		String jsonResponse = response.getEntity(String.class);
		client.destroy();
		//System.out.println("Response deleteJobs:::::" + jsonResponse);

		return jsonResponse;
	}

	public String buildJob() throws InterruptedException {
		JenkinsClient client = JenkinsClient.builder()
				.endPoint(JENKINS_URI) //
				.credentials(USERNAME+":"+PASSWORD)
				.build();
		IntegerResponse queueId = client.api().jobsApi().build(null,job_name);
		List<Error> errorList = queueId.errors();
		if(!areErrors(errorList))
			System.out.println("Build successfuly submitted with queue id: " + queueId.value());

		QueueItem queueItem = client.api().queueApi().queueItem(queueId.value());
		while(true){
			if (queueItem.cancelled())
				System.out.println("Queue item cancelled");
			if (queueItem.executable() != null){
				System.out.println("Build is executing with build number: " + queueItem.executable().number());
				break;
			}
			Thread.sleep(10000);
			queueItem = client.api().queueApi().queueItem(queueId.value());
		}
		BuildInfo buildInfo = client.api().jobsApi().buildInfo(null, job_name, queueItem.executable().number());
		while (buildInfo.result() == null) {
			Thread.sleep(10000);
			buildInfo = client.api().jobsApi().buildInfo(null, job_name, queueItem.executable().number());
		}
		//System.out.println("Build status : " + buildInfo.result());

		return buildInfo.result();

	}

	public String readJob() {
		Client client = Client.create();
		client.addFilter(new com.sun.jersey.api.client.filter.HTTPBasicAuthFilter(USERNAME, PASSWORD));
		WebResource webResource = client.resource(JENKINS_URI + "/job/" + job_name + "/config.xml");
		ClientResponse response = webResource.get(ClientResponse.class);
		String jsonResponse = response.getEntity(String.class);
		client.destroy();
//		System.out.println("Response readJob:::::"+jsonResponse);
		return jsonResponse;
	}

	public String crumbCredentialGetter() {
		Client client = Client.create();

		client.addFilter(new com.sun.jersey.api.client.filter.HTTPBasicAuthFilter(USERNAME, PASSWORD));

		WebResource webResource = client
				.resource(JENKINS_URI + "/crumbIssuer/api/json");

		ClientResponse response = webResource.accept("application/json")
				.get(ClientResponse.class);
		if (response.getStatus() != 200) {
			throw new RuntimeException("Failed : HTTP error code : "
					+ response.getStatus());
		}
		String output = response.getEntity(String.class);
		System.out.println("Output from Server .... \n");
		System.out.println(output);

		return output.split(",")[1];
	}

	public String getJob_name() {
		return job_name;
	}

	public void setJob_name(String job_name) {
		this.job_name = job_name;
	}

	public String getXml_job_config_path() {
		return xml_job_config_path;
	}

	public void setXml_job_config_path(String xml_job_config_path) {
		this.xml_job_config_path = xml_job_config_path;
	}


}
