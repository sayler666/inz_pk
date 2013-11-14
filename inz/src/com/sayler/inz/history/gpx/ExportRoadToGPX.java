package com.sayler.inz.history.gpx;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import android.os.Environment;

import com.sayler.inz.database.model.Road;
import com.sayler.inz.database.model.Track;

public class ExportRoadToGPX {
	static final String TAG = "ExportRoadToGPX";

	public static String export(Road road) throws ParserConfigurationException,
			IOException {

		DocumentBuilderFactory docFactory = DocumentBuilderFactory
				.newInstance();
		DocumentBuilder docBuilder;
		docBuilder = docFactory.newDocumentBuilder();

		// root elements
		Document doc = docBuilder.newDocument();

		// gpx node
		Element gpx = doc.createElement("gpx");
		gpx.setAttribute("creator", "com.sayler.inz");
		gpx.setAttribute("xmlns:xsi",
				"http://www.w3.org/2001/XMLSchema-instance");
		gpx.setAttribute("xsi:schemaLocation",
				"http://www.topografix.com/gpx/1/1/gpx.xsd");
		doc.appendChild(gpx);

		// creation time node
		Element globalTime = doc.createElement("time");
		SimpleDateFormat dateFormatLocal = new SimpleDateFormat(
				"yyyy-MM-dd'T'HH:mm:ss'Z'");
		globalTime.appendChild(doc.createTextNode(dateFormatLocal.format(road
				.getCreatedAt())));
		gpx.appendChild(globalTime);

		// wpts nodes
		for (Track wpt : road.getTracks()) {
			Element wptNode = doc.createElement("wpt");
			wptNode.setAttribute("lat", String.valueOf(wpt.getLat()));
			wptNode.setAttribute("lon", String.valueOf(wpt.getLng()));

			// ele
			Element elevation = doc.createElement("ele");
			elevation.appendChild(doc.createTextNode(String.valueOf(wpt
					.getAlt())));
			wptNode.appendChild(elevation);
			// time
			Element time = doc.createElement("time");
			String timeString = "0";
			if (wpt.getCreatedAt() != null) {
				timeString = String.valueOf(dateFormatLocal.format(wpt
						.getCreatedAt()));
			}
			time.appendChild(doc.createTextNode(timeString));
			wptNode.appendChild(time);
			
			// append wpt node
			gpx.appendChild(wptNode);

		}

		// string writer
		StringWriter sw = new StringWriter();
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer;

		// saving to file
		try {
			transformer = tf.newTransformer();

			transformer
					.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
			transformer.setOutputProperty(OutputKeys.METHOD, "xml");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

			transformer.transform(new DOMSource(doc), new StreamResult(sw));

			// Log.d(TAG, sw.toString());
			SimpleDateFormat dateFormatFileName = new SimpleDateFormat(
					"yyyy-MM-dd_HH_mm_ss");
			File xmlFile = new File(Environment.getExternalStorageDirectory()
					.getPath()
					+ "/"
					+ dateFormatFileName.format(road.getCreatedAt()) + ".gpx");
			xmlFile.createNewFile();
			FileOutputStream fOut = new FileOutputStream(xmlFile);
			OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
			myOutWriter.append(sw.toString());
			myOutWriter.close();

			return xmlFile.getAbsolutePath();
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}

		return "";
	}

}
