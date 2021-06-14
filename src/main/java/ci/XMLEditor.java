package ci;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;

public class XMLEditor {

	public static void setGithubXml(String inputXmlConfigUri, String outputXmlConfigUri, String githubLink) {
		try {
			File file = new File(inputXmlConfigUri);
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(file);

			Node url = doc.getElementsByTagName("url").item(0);
			url.setTextContent(githubLink);
			System.out.println(url.getTextContent());

			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			File outputFile = new File(outputXmlConfigUri);
			StreamResult result = new StreamResult(outputFile);

			//write data
			transformer.transform(source, result);
			System.out.println("Config XML Created DONE in " + outputXmlConfigUri);

		} catch (ParserConfigurationException | IOException | SAXException | TransformerException e) {
			System.out.println("error while parsing xml " + e.getMessage());
		}
	}
}
