import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.IOUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class CoupaUtils {
	protected static String deleteDTDRow(InputStream inputStream) {
		String finalString = "";
		try {
			String input = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
			String[] lines = input.split(System.getProperty("line.separator"));
			lines[1] = "";
			StringBuilder finalStringBuilder= new StringBuilder("");
			for(String s:lines){
				if(!s.equals("")){
					finalStringBuilder.append(s).append(System.getProperty("line.separator"));
				}
			}
			finalString = finalStringBuilder.toString();
			inputStream = IOUtils.toInputStream(finalString);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return finalString;
	}

	protected static String responseKO() {
		Double payloadRandom = Math.random();
		String payload = payloadRandom.toString();
		payload = payload.substring(payload.length()-9);
		Date now = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");
		String formattedDate = sdf.format(now);
		String response = "<?xml version=\"1.0\"?>\r\n"
				+ "<cXML payloadID=\""+payload+"\" xml:lang=\"en\" timestamp=\""+formattedDate+"\">\r\n"
				+ "  <Response>\r\n"
				+ "     <Status code=\"500\" text=\"KO\"/>\r\n"
				+ "  </Response>\r\n"
				+ "</cXML>";
		return response;
	}

	protected static String responseOK() {
		Double payloadRandom = Math.random();
		String payload = payloadRandom.toString();
		payload = payload.substring(payload.length()-9);
		Date now = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");
		String formattedDate = sdf.format(now);
		String response = "<?xml version=\"1.0\"?>\r\n"
				+ "<cXML payloadID=\""+payload+"\" xml:lang=\"en\" timestamp=\""+formattedDate+"\">\r\n"
				+ "  <Response>\r\n"
				+ "     <Status code=\"200\" text=\"OK\"/>\r\n"
				+ "  </Response>\r\n"
				+ "</cXML>";
		return response;
	}

	protected static boolean saveFileFTP(String input, String host, int port, String user, String pass) {

		Date now = new Date();
		long t = now.getTime();
		String fileName = "cXML_"+t+".txt";
		String path = "C:\\Users\\caponetto\\Documents\\CoupaXML\\";
		File file = new File(path+fileName);
		try  {
			FileOutputStream localeFile = new FileOutputStream(file, false);
			localeFile.write(input.getBytes());
			localeFile.close();

			FTPClient ftpClient = new FTPClient();
			ftpClient.connect(host, port);
			ftpClient.login(user, pass);
			ftpClient.enterLocalPassiveMode();

			ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

			OutputStream outputStream = ftpClient.storeFileStream(fileName);
			outputStream.write(input.getBytes());
			outputStream.close();
		} catch(IOException e) {
			System.out.println(e.getMessage());
			return false;
		}

		file.delete();
		return true;
	}
	protected static String xmlParser(InputStream inputStream) {
		// Instantiate the Factory
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		String input = "";
		try {

			dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);

			// parse XML file
			DocumentBuilder db = dbf.newDocumentBuilder();

			input = deleteDTDRow(inputStream);
			//InputStream inputStreamForFTP = inputStream;
			inputStream = IOUtils.toInputStream(input);
			Document doc = db.parse(inputStream);
			doc.getDocumentElement().normalize();

			System.out.println("Root Element :" + doc.getDocumentElement().getNodeName());
			System.out.println("------");

			NodeList headerlist = doc.getElementsByTagName("Header");
			for (int hl = 0; hl < headerlist.getLength(); hl++) {
				Node header = headerlist.item(hl);
				if (header.getNodeType() == Node.ELEMENT_NODE) {
					Element elementHeader = (Element) header;
					NodeList headerChilds = elementHeader.getChildNodes();
					for (int h=0; h<headerChilds.getLength(); h++)
					{
						Node fromToSender = headerChilds.item(h);
						if (fromToSender.getNodeType() == Node.ELEMENT_NODE) {
							Element elementFromToSender = (Element) fromToSender;
							if(elementFromToSender!=null && elementFromToSender.getNodeName().equals("From")) {
								NodeList fromChilds = elementFromToSender.getChildNodes();
								for (int fts=0; fts<fromChilds.getLength(); fts++)
								{
									Node credential = fromChilds.item(fts);
									if (credential.getNodeType() == Node.ELEMENT_NODE) {
										Element elementCredential = (Element) credential;
										if(elementCredential!=null && elementCredential.getNodeName().equals("Credential")) {
											String fromIdentity = elementCredential.getElementsByTagName("Identity").item(0).getTextContent().trim();
										}
									}
								}
							}else if(elementFromToSender!=null && elementFromToSender.getNodeName().equals("To")) {
								NodeList toChilds = elementFromToSender.getChildNodes();
								for (int fts=0; fts<toChilds.getLength(); fts++)
								{
									Node credential = toChilds.item(fts);
									if (credential.getNodeType() == Node.ELEMENT_NODE) {
										Element elementCredential = (Element) credential;
										if(elementCredential!=null && elementCredential.getNodeName().equals("Credential")) {
											String toIdentity = elementCredential.getElementsByTagName("Identity").item(0).getTextContent().trim();
										}
									}
								}
							}else if(elementFromToSender!=null && elementFromToSender.getNodeName().equals("Sender")) {
								NodeList senderChilds = elementFromToSender.getChildNodes();
								for (int fts=0; fts<senderChilds.getLength(); fts++)
								{
									Node credential = senderChilds.item(fts);
									if (credential.getNodeType() == Node.ELEMENT_NODE) {
										Element elementCredential = (Element) credential;
										if(elementCredential!=null && elementCredential.getNodeName().equals("Credential")) {
											String senderIdentity = elementCredential.getElementsByTagName("Identity").item(0).getTextContent().trim();
											String senderSharedSecret = elementCredential.getElementsByTagName("SharedSecret").item(0).getTextContent().trim();
										}
									}
								}
							}
						}
					}
				}
			}

			inputStream.close();
			return input;
			//return saveFileFTP(input);

		} catch (ParserConfigurationException | SAXException | IOException e) {
			e.printStackTrace();
			return "";
		}

	}
}
