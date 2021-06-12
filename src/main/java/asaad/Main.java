package asaad;

import com.cdancy.jenkins.rest.JenkinsClient;
import com.cdancy.jenkins.rest.domain.common.Error;
import com.cdancy.jenkins.rest.domain.common.IntegerResponse;
import com.cdancy.jenkins.rest.domain.job.BuildInfo;
import com.cdancy.jenkins.rest.domain.queue.QueueItem;
import com.cdancy.jenkins.rest.domain.system.SystemInfo;
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


public class Main {

	private static final String XML_JOB_CONFIG = "src/main/java/asaad/job.xml";

	public static void main(String[] args) throws InterruptedException {

		System.out.println("creating the job");
		JenkinsJob job = new JenkinsJob("asaadJobSecond", XML_JOB_CONFIG);
		System.out.println("reading the job");
		System.out.println(job.readJob());
		Thread.sleep(2000);
		System.out.println("building the job");
		System.out.println(job.buildJob());
		Thread.sleep(2000);
		System.out.println("deleting the job");
		job.deleteJob();
		Thread.sleep(2000);
		System.out.println("listing the jobs");
		System.out.println(JenkinsJob.listJobs());

	}


}
