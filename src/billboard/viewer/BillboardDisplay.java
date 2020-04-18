package billboard.viewer;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import javax.swing.*;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.net.URL;
import java.util.ArrayList;


/**
 * Creates a resizable JPanel showing the graphical elements of the billboard loaded from XML. This can either server
 * as a preview for a billboard designer, or as the billboard viewer when used by the BillboardFrame class.
 * @see BillboardFrame This class uses BillboardDisplay to create the billboard viewer.
 */
public class BillboardDisplay extends JPanel {
    // Configuration constants
    static private final String default_bg_colour = "#F5F5F5";  // TODO change default background back to F5F5F5
    static private final String default_text_colour = "#000000";
    static private final Font default_font = new Font(Font.SANS_SERIF, Font.PLAIN, 40);

    // Cached references
    private Dimension displaySize;
    private Document doc;   // DOM representation of the loaded XML file.
    private ArrayList<Element> billboardElements;   // List of billboard elements in the DOM.

    public BillboardDisplay(String xmlPath, Dimension displaySize) {
        this.displaySize = displaySize;

        setLayout(new GridBagLayout());
        // Display size set to be static so that pack() will not mess up the layout.
        setPreferredSize(displaySize);
        
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

        setBackgroundColour();
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
        billboardElements.add(getDocElement(doc, "message"));
        billboardElements.add(getDocElement(doc, "picture"));
        billboardElements.add(getDocElement(doc, "information"));

        // Check that the billboard has something to display
        if (billboardElements.get(1) == null && billboardElements.get(2) == null && billboardElements.get(3) == null) {
            return null;
        }

        return billboardElements;
    }


    /**
     * Sets the billboard background colour.
     */
    private void setBackgroundColour() {
        Color bg_colour = getBackgroundColour(billboardElements.get(0));
        setBackground(bg_colour);
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
            message.setFont(default_font);    //TODO: make this dynamic size

            GridBagConstraints c = new GridBagConstraints();
            c.fill = GridBagConstraints.HORIZONTAL;
            c.anchor = GridBagConstraints.CENTER;
            c.weighty = 0.1;
            c.weightx = 0.1;
            c.gridx = 0;
            c.gridy = 0;
            add(message, c);
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
                return;     //TODO make this case go to the error screen
            }

            // TODO: Clean up and move this to a separate class
            Dimension currentImageSize = new Dimension(image.getWidth(null), image.getHeight(null));
            Dimension imageBoundarySize = new Dimension(displaySize.width / 3, displaySize.height / 3);
            Dimension newImageSize = scaleImageDimensions(imageBoundarySize, currentImageSize);
            Image scaledImage = image.getScaledInstance(newImageSize.width, newImageSize.height, Image.SCALE_SMOOTH);

            //TODO: Add image scaling, perhaps extend JLabel to create a new type
            JLabel picture = new JLabel(new ImageIcon(scaledImage), SwingConstants.CENTER);

            GridBagConstraints c = new GridBagConstraints();
            c.fill = GridBagConstraints.HORIZONTAL;
            c.anchor = GridBagConstraints.CENTER;
            c.weighty = 0.1;
            c.weightx = 0.1;
            c.gridx = 0;
            c.gridy = 1;
            add(picture, c);
        }
    }


    /**
     * Adds an information panel to the billboard GUI.
     */
    private void addInfoToBillboard()
    {
        Element infoElement = billboardElements.get(3);
        if (infoElement != null) {
            String text = getElementText(infoElement);
            JTextPane info = new JTextPane();
            info.setText(text);

            StyledDocument infoText = info.getStyledDocument();
            SimpleAttributeSet textAttr = new SimpleAttributeSet();
            StyleConstants.setAlignment(textAttr, StyleConstants.ALIGN_CENTER);
            infoText.setParagraphAttributes(0, infoText.getLength(), textAttr, false);

            info.setEditable(false);
            info.setFocusable(false);
            info.setFont(default_font);    //TODO make this dynamic size

            Color bg_colour = getBackgroundColour(billboardElements.get(0));
            info.setBackground(bg_colour);
            Color fg_colour = getTextColour(infoElement);
            info.setForeground(fg_colour);

            GridBagConstraints c = new GridBagConstraints();
            c.fill = GridBagConstraints.HORIZONTAL;
            c.anchor = GridBagConstraints.CENTER;
            c.weighty = 0.1;
            c.weightx = 0.1;
            c.gridx = 0;
            c.gridy = 2;
            add(info, c);
        }
    }


    /**
     * Gets the the element with name 'tagName' from the specified document object model (DOM).
     * @param doc Document object model to retrieve the element from.
     * @param tagName Tag name of the element to retrieve.
     * @return The first matching requested element, or null if none exist.
     */
    private Element getDocElement(Document doc, String tagName) {
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


    /**
     * Finds the dimensions (in pixels) of an image once it has been scaled to fit inside a bounding box of given size.
     * The new dimensions retain the original aspect ratio.
     * @param imageBoundarySize The size of the rectangular boundary inside which the image needs to fit.
     * @param currentImageSize The current image dimensions.
     * @return The scaled image dimensions with aspect ratio maintained.
     */
    private Dimension scaleImageDimensions(Dimension imageBoundarySize, Dimension currentImageSize) {
        int currentWidth = currentImageSize.width;
        int currentHeight = currentImageSize.height;
        int boundaryWidth = imageBoundarySize.width;
        int boundaryHeight = imageBoundarySize.height;

        int newWidth = boundaryWidth;
        int newHeight = (newWidth * currentHeight) / currentWidth;

        if (newHeight > boundaryHeight) {
            newHeight = boundaryHeight;
            newWidth = (newHeight * currentWidth) / currentHeight;
        }

        return new Dimension(newWidth, newHeight);
    }
}
