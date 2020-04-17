package billboard.viewer;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;


public class BillboardIO {
    /**
     * Gets the image at the specified url.
     * @param url URL to retrieve the image from.
     * @return The image at the specified URL. Returns null if the URL was not valid.
     */
    public static Image getImageFromURL(URL url) {
        try {
            return ImageIO.read(url);
        } catch (IOException e) {
            System.err.println("Could not get image from url:\n" + url.toString());
        }
        return null;
    }


    /**
     * Decodes an image from Base64 encoding.
     * @param imageData Base64 image data to decode.
     * @return The decoded image. Returns null if the decoding was unsuccessful.
     */
    public static Image decodeImageFromBase64(String imageData) {
        byte[] byteImage = Base64.getDecoder().decode(imageData);
        ByteArrayInputStream inStream = new ByteArrayInputStream(byteImage);
        try {
            return ImageIO.read(inStream);
        } catch (IOException e) {
            System.err.println("Unable to form image from data:\n" + imageData);
        }
        return null;
    }


    /**
     * Converts a DOM tree into XML and saves the file to the given location.
     * @param doc DOM object to convert into XML.
     * @param filePath File location at which to save the XML.
     */
    public static void saveDOCasXML(Document doc, String filePath) {
        try {
            DOMSource source = new DOMSource(doc);
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            FileWriter writer = new FileWriter(filePath);
            StreamResult result = new StreamResult(writer);

            transformer.transform(source, result);
        } catch (Exception e) {
            /*As used in this project, this function should never receive a DOM that is invalid XML.*/
            System.err.println("Unable convert or save document object model to XML.");
            System.exit(-1);
        }
    }


    /**
     * Attempts to parse an input string (assumed to be XML) and turn it into a DOM tree.
     * @param xml XML string to be parsed.
     * @return DOM representation of the input XML. Returns null if the string could not be properly converted.
     */
    public static Document generateDOCfromXML(String xml) {
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            return builder.parse(new InputSource(new StringReader(xml)));
        }
        catch (Exception e) {
            System.err.println("Unable to parse XML:\n" + xml);
            return null;
        }
    }


    /**
     * Attempts to get the contents of the target file as a string.
     * @param filePath Path of the file to be read.
     * @return String contents of the file. Returns null if the file could not be read.
     */
    public static String getFileContentsAsString(String filePath) {
        try {
            return new String(Files.readAllBytes(Paths.get(filePath)));
        }
        catch(Exception e) {
            System.err.println("Unable to read file: " + filePath);
            return null;
        }
    }
}
