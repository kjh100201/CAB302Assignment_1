package billboard.viewer;

import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;

public class BillboardFormatter {
    static final String default_bg_colour = "#F5F5F5";
    static final String default_text_colour = "#000000";

    public static void format(JFrame frame, String xmlPath) {
        String xml = readFileToString(xmlPath);
        if (xml == null) {
            System.exit(0);  //TODO: handle continued operation when server request loop is operational.
        }

        // Temporary text demo
        JTextArea text = new JTextArea();
        text.setLineWrap(true);
        text.setWrapStyleWord(true);
        text.setEditable(false);
        text.setFocusable(false);
        text.append(xml);
        frame.getContentPane().add(text);

        Document doc = generateDOMfromXML(xml);
        if (doc == null) {
            System.exit(0);  //TODO: handle continued operation when server request loop is operational.
        }


        // Get the elements of the billboard
        Element billboard = null;
        Element message = null;
        Element picture = null;
        Element info = null;
        try {
            billboard = doc.getDocumentElement();
            message = (Element) doc.getElementsByTagName("message").item(0);
            picture = (Element) doc.getElementsByTagName("picture").item(0);
            info = (Element) doc.getElementsByTagName("information").item(0);
        }
        catch (Exception e) {
            System.out.println("Invalid billboard XML format");
            System.exit(0);  //TODO set the error billboard and continue operation
        }
    }


    /**
     * Gets the the element with name 'tagName' from the specified document object model (DOM).
     * @param dom Document object model to retrieve the element from.
     * @param tagName Tag name of the element to retrieve.
     * @return The first matching requested element, or null if none exist.
     */
    private Element getDOMElement(Document dom, String tagName) {
        try {
            return (Element) dom.getElementsByTagName(tagName).item(0);
        }
        catch (Exception ignored) {}
        return null;
    }


    /**
     * Creates a list of the four billboard elements from the DOM. These elements are defined by tag names: 'billboard',
     * 'message', 'picture' and 'information'. The element positions in the list store a null if that element is not
     * defined by the billboard XML.
     * @param doc DOM representation of the XML billboard layout
     * @return A list of the four billboard elements from the DOM. Returns null if the billboard would have nothing to
     * display, or if the billboard is empty.
     */
    private ArrayList<Element> getBillboardElements(Document doc) {
        ArrayList<Element> billboardElements = new ArrayList<>();

        // Check that the loaded xml is a billboard
        if (!doc.getDocumentElement().getTagName().equals("billboard")) {
            return null;
        }

        billboardElements.add(doc.getDocumentElement());
        billboardElements.add(getDOMElement(doc, "message"));
        billboardElements.add(getDOMElement(doc, "picture"));
        billboardElements.add(getDOMElement(doc, "information"));

        // Check that the billboard has something to display
        if (billboardElements.get(1) == null & billboardElements.get(2) == null & billboardElements.get(0) == null) {
            return null;
        }

        return billboardElements;
    }


    /**
     * Gets the text content of the specified element.
     * @param element Element to retrieve the text from.
     * @return The text content of the element.
     */
    private String getElementText(Element element) {
        return element.getTextContent();
    }


    /**
     * Gets the image at the specified url.
     * @param url URL to retrieve the image from.
     * @return The image at the specified URL. Returns null if the URL was not valid.
     */
    private Image getImageFromURL(URL url) {
        try {
            BufferedImage image = ImageIO.read(url);
            return (Image) image;
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
    private Image decodeImageFromBase64(String imageData) {
        byte[] byteImage = Base64.getDecoder().decode(imageData);
        ByteArrayInputStream inStream = new ByteArrayInputStream(byteImage);
        try {
            BufferedImage image = ImageIO.read(inStream);
            return (Image) image;
        } catch (IOException e) {
            System.err.println("Unable to form image from data:\n" + imageData);
        }
        return null;
    }


    /**
     * Gets the image from the specified DOM element. This may either be a URL or a Base64 encoded string.
     * @param element Element to retrieve the image for.
     * @return The image. Returns null if the image could not be decoded, or if no image could be retrieved from the
     * specified url.
     */
    private Image getElementImage(Element element) {
        String picture_url = element.getAttribute("url");
        if (!picture_url.equals("")) {
            try {
                URL url = new URL(picture_url);
                return getImageFromURL(url);
            }
            catch (Exception e) {
                System.err.println("Invalid url:\n" + picture_url);
                return null;
            }
        }
        else {
            String picture_data = element.getAttribute("data");
            if (!picture_data.equals("")) {
                return decodeImageFromBase64(picture_data);
            }
            return null;
        }
    }


    /**
     * Gets the background colour for the specified element. Sets a default if no colour is specified.
     * @param element Element to get the background colour for.
     * @return Colour of the background.
     */
    private Color getBackgroundColour(Element element) {
        String billboard_background = element.getAttribute("background");
        if (billboard_background.equals("")) {
            billboard_background = default_bg_colour;
        }
        return Color.decode(billboard_background);
    }


    /**
     * Gets the text colour for the specified element. Sets a default if no colour is specified.
     * @param element Element to get the text colour for.
     * @return Colour of the text.
     */
    private Color getTextColour(Element element) {
        String message_colour = element.getAttribute("colour");
        if (message_colour.equals("")) {
            message_colour = default_text_colour;
        }
        return Color.decode(message_colour);
    }


    /**
     * Attempts to parse an input XML string and turn it into a DOM tree.
     * @param xml XML string to be parsed.
     * @return DOM representation of the input XML. Returns null if the string could not be properly converted.
     */
    private static Document generateDOMfromXML(String xml) {
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            return builder.parse(new InputSource(new StringReader(xml)));
        }
        catch (Exception e) {
            System.err.println("Unable to parse to DOM:\n" + xml);
            return null;
        }
    }


    /**
     * Attempts to read the specified file to a String.
     * @param filePath Path of the file to be read.
     * @return String contents of the file. Returns null if the file could not be read.
     */
    private static String readFileToString(String filePath) {
        try {
            return new String(Files.readAllBytes(Paths.get(filePath)));
        }
        catch(Exception e) {
            System.err.println("Unable to read file: " + filePath);
            return null;
        }
    }
}
