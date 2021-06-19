package ci;


import com.offbytwo.jenkins.JenkinsServer;
import com.offbytwo.jenkins.model.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;

public class Main {
	private static final String JENKINS_URI = "http://localhost:8080";
	private static final String USERNAME = "firstAdmin";
	private static final String PASSWORD = "1191e8798b94ac79434a686b0070a4c956";

	private static final String XML_JOB_CONFIG = "src/main/java/ci/jobi.xml";

	public static void main(String[] args) throws InterruptedException {

/*		System.out.println("creating the job");
		JenkinsJob job = new JenkinsJob("monPipeline", XML_JOB_CONFIG);
		System.out.println("reading the job");
		//System.out.println(job.readJob());
		Thread.sleep(2000);

		System.out.println("building the job");
		System.out.println(job.buildJob());
		Thread.sleep(2000);
		//System.out.println("deleting the job");
		//job.deleteJob();
		Thread.sleep(2000);
		System.out.println("listing the jobs");
		System.out.println(JenkinsJob.listJobs());*/



		try {
			JenkinsServer jenkins = new JenkinsServer(new URI(JENKINS_URI), USERNAME, PASSWORD);
			Map<String, Job> jobs = jenkins.getJobs();
			JobWithDetails jobWithDetails = jobs.get("lol").details();
			//System.out.println(jobWithDetails.getLastSuccessfulBuild().details().getConsoleOutputText());
			Build build = jobWithDetails.getLastSuccessfulBuild();
			List<Artifact> 	artifacts = build.details().getArtifacts();
			for (Artifact art : artifacts){
				System.out.println(art.getFileName());
				System.out.println(art.getDisplayPath());
				System.out.println(art.getRelativePath());
				System.out.println("downloading");
				InputStream inputStream = build.details().downloadArtifact(art);
				URI uri = new URI(build.getUrl());
				String artifactPath = uri.getPath() + "artifact/" + art.getRelativePath();
				URI artifactUri = new URI(uri.getScheme(), uri.getUserInfo(), uri.getHost(), uri.getPort(), artifactPath, "", "");
				System.out.println(artifactUri);
				//Files.copy(inputStream, Path.of("src/main/java/ci/art.txt"), StandardCopyOption.REPLACE_EXISTING);
			}
		} catch (URISyntaxException | IOException e) {
			e.printStackTrace();
		}


	}


}
