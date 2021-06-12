package ci;


public class Main {

	private static final String XML_JOB_CONFIG = "src/main/java/ci/job.xml";

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
