package billboard.viewer;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.ArrayList;


public class BillboardDisplay extends JPanel {
    // Configuration constants
    static private final String default_bg_colour = "#F5F5F5";  // TODO change default background back to F5F5F5
    static private final String default_text_colour = "#000000";

    // Cached references
    Document doc;   // DOM representation of the loaded XML file.
    ArrayList<Element> billboardElements;   // List of billboard elements in the DOM.

    public BillboardDisplay(String xmlPath) {
        setLayout(new GridLayout(0, 1));  //TODO: PROBABLY MAKE THIS GRID BAG LAYOUT

        String xml = BillboardIO.getFileContentsAsString(xmlPath);
        if (xml == null) {
            System.exit(0);  //TODO: implement error screen when not working instead of exiting.
        }

        doc = BillboardIO.generateDOCfromXML(xml);
        if (doc == null) {
            System.exit(0);  //TODO: implement error screen when not working instead of exiting.
        }

        billboardElements = getBillboardElements();
        if (billboardElements == null) {
            System.err.println("The billboard is either empty or invalid.");
            System.exit(0);  //TODO: implement error screen when not working instead of exiting.
        }

        Color bg_colour = getBackgroundColour(billboardElements.get(0));
        setBackground(bg_colour);

        addMessageToBillboard();
        addImageToBillboard();
        addInfoToBillboard();
    }


    /**
     * Creates a list of the four billboard elements from the DOM. These elements are defined by tag names: 'billboard',
     * 'message', 'picture' and 'information'. The element positions in the list store a null if that element is not
     * defined by the billboard XML.
     * @return A list of the four billboard elements from the DOM. Returns null if the billboard would have nothing to
     * display, or if the billboard is empty.
     */
    private ArrayList<Element> getBillboardElements() {
        ArrayList<Element> billboardElements = new ArrayList<>();

        // Check that the loaded xml is a billboard
        if (!doc.getDocumentElement().getTagName().equals("billboard")) {
            return null;
        }

        billboardElements.add(doc.getDocumentElement());
        billboardElements.add(getDOCelement(doc, "message"));
        billboardElements.add(getDOCelement(doc, "picture"));
        billboardElements.add(getDOCelement(doc, "information"));

        // Check that the billboard has something to display
        if (billboardElements.get(1) == null && billboardElements.get(2) == null && billboardElements.get(3) == null) {
            return null;
        }

        return billboardElements;
    }


    /**
     * Adds a message panel to the billboard GUI.
     */
    private void addMessageToBillboard() {
        Element messageElement = billboardElements.get(1);
        if (messageElement != null) {
            String msg = getElementText(messageElement);
            JLabel message = new JLabel(msg, SwingConstants.CENTER);
            message.setForeground(getTextColour(messageElement));
            message.setFont(new Font("Arial", Font.PLAIN, 40));    //TODO: remove this
            add(message);
        }
    }


    /**
     * Adds an image panel to the billboard GUI.
     */
    private void addImageToBillboard() {
        Element pictureElement = billboardElements.get(2);
        if (pictureElement != null) {
            Image image = getElementImage(pictureElement);
            if (image == null) {
                return;
            }
            JLabel picture = new JLabel(new ImageIcon(image), SwingConstants.CENTER);
            add(picture);
        }
    }


    /**
     * Adds an information panel to the billboard GUI.
     */
    private void addInfoToBillboard()
    {
        Element infoElement = billboardElements.get(3);
        if (infoElement != null) {
            String info = getElementText(infoElement);
            JTextArea text = new JTextArea(info);
            text.setLineWrap(true);
            text.setWrapStyleWord(true);
            text.setEditable(false);
            text.setFocusable(false);
            text.setFont(new Font("Arial", Font.PLAIN, 40));    //TODO remove this

            Color bg_colour = getBackgroundColour(billboardElements.get(0));
            text.setBackground(bg_colour);
            Color fg_colour = getTextColour(infoElement);
            text.setForeground(fg_colour);

            add(text);
        }
    }


    /**
     * Gets the the element with name 'tagName' from the specified document object model (DOM).
     * @param doc Document object model to retrieve the element from.
     * @param tagName Tag name of the element to retrieve.
     * @return The first matching requested element, or null if none exist.
     */
    private Element getDOCelement(Document doc, String tagName) {
        try {
            return (Element) doc.getElementsByTagName(tagName).item(0);
        }
        catch (Exception ignored) {}
        return null;
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
                return BillboardIO.getImageFromURL(url);
            }
            catch (Exception e) {
                System.err.println("Invalid url:\n" + picture_url);
                return null;
            }
        }
        else {
            String picture_data = element.getAttribute("data");
            if (!picture_data.equals("")) {
                return BillboardIO.decodeImageFromBase64(picture_data);
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
}
