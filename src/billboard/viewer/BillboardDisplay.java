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
    static final int MESSAGE = 1;
    static final int PICTURE = 2;
    static final int INFORMATION = 3;
    static private final String DEFAULT_BG_COLOUR = "#F4F4F4";
    static private final String DEFAULT_TEXT_COLOUR = "#000000";

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
        int fontSize = addMessageToBillboard();
        addImageToBillboard();
        addInfoToBillboard(fontSize);
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
        if (billboardElements.get(MESSAGE) == null && billboardElements.get(PICTURE) == null && billboardElements.get(INFORMATION) == null) {
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
     * Adds the message component to the billboard GUI.
     */
    private int addMessageToBillboard() {
        Element messageElement = billboardElements.get(1);
        if (messageElement != null) {
            String msg = getElementText(messageElement);
            JLabel message = new JLabel(msg, SwingConstants.CENTER);
            message.setForeground(getTextColour(messageElement));
            Dimension messageDimensions = getMessageDimensions();
            message.setPreferredSize(messageDimensions);

            Font font = scaleSingleLineFont(msg, messageDimensions);
            message.setFont(font);

            GridBagConstraints c = getStandardGridConstraints(0, 0);
            add(message, c);

            return font.getSize();
        }
        return -1;
    }


    /**
     * Adds the image component to the billboard GUI.
     */
    private void addImageToBillboard() {
        Element pictureElement = billboardElements.get(2);
        if (pictureElement != null) {
            Image image = getElementImage(pictureElement);
            if (image == null) {
                return;     //TODO make this case go to the error screen
            }

            Dimension currentImageSize = new Dimension(image.getWidth(null), image.getHeight(null));
            Dimension imageBoundarySize = getPictureImageDimensions();
            Dimension newImageSize = scaleImageDimensions(imageBoundarySize, currentImageSize);
            Image scaledImage = image.getScaledInstance(newImageSize.width, newImageSize.height, Image.SCALE_SMOOTH);

            JLabel picture = new JLabel(new ImageIcon(scaledImage), SwingConstants.CENTER);
            picture.setPreferredSize(getPictureDimensions());

            GridBagConstraints c = getStandardGridConstraints(0, 1);
            add(picture, c);
        }
    }


    /**
     * Adds the information component to the billboard GUI.
     */
    private void addInfoToBillboard(int maxFontSize)
    {
        Element infoElement = billboardElements.get(3);
        if (infoElement != null) {
            JPanel panel = new JPanel(new GridBagLayout());
            Dimension infoDimensions = getInfoDimensions();
            panel.setPreferredSize(infoDimensions);

            // Align the text content to the center
            String text = getElementText(infoElement);
            JTextPane info = new JTextPane();
            info.setText(text);
            StyledDocument infoText = info.getStyledDocument();
            SimpleAttributeSet textAttr = new SimpleAttributeSet();
            StyleConstants.setAlignment(textAttr, StyleConstants.ALIGN_CENTER);
            infoText.setParagraphAttributes(0, infoText.getLength(), textAttr, false);

            info.setEditable(false);
            info.setFocusable(false);
            Font font = scaleMultilineFont(text, infoDimensions, maxFontSize - 1);
            info.setFont(font);

            // Set colours
            Color bg_colour = getBackgroundColour(billboardElements.get(0));
            info.setBackground(bg_colour);
            panel.setBackground(bg_colour);
            Color fg_colour = getTextColour(infoElement);
            info.setForeground(fg_colour);

            // Vertically align the JTextPane component within its parent JPanel
            GridBagConstraints c = getStandardGridConstraints(0,0);
            c.fill = GridBagConstraints.HORIZONTAL;
            c.weighty = 1.0;
            c.weightx = 1.0;
            panel.add(info, c);

            GridBagConstraints d = getStandardGridConstraints(0, 2);
            add(panel, d);
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
            billboard_background = DEFAULT_BG_COLOUR;
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
            message_colour = DEFAULT_TEXT_COLOUR;
        }
        return Color.decode(message_colour);
    }


    /**
     * Gets a standard GridBagConstraints object to place a centered GUI component at a specified grid position.
     * @param gridx Grid x position.
     * @param gridy Grid y position.
     * @return The GridBagConstraints object.
     */
    private GridBagConstraints getStandardGridConstraints(int gridx, int gridy) {
        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.CENTER;
        c.gridx = gridx;
        c.gridy = gridy;
        return c;
    }


    /**
     * Gets the dimensions in pixels for the billboard message component, based on what other components will be on the
     * billboard.
     * @return The dimensions of the message component.
     */
    private Dimension getMessageDimensions() {
        if (billboardElements.get(PICTURE) == null && billboardElements.get(INFORMATION) == null) {
            return new Dimension(displaySize.width, displaySize.height);
        }
        if (billboardElements.get(PICTURE) == null) {   //Implies that the information component is not null
            return new Dimension(displaySize.width, displaySize.height/2);
        }
        return new Dimension(displaySize.width, displaySize.height/3);
    }


    /**
     * Gets the dimensions in pixels for the billboard picture component, based on what other components will be on the
     * billboard.
     * @return The dimensions of the image component.
     */
    private Dimension getPictureDimensions() {
        if (billboardElements.get(MESSAGE) == null && billboardElements.get(INFORMATION) == null) {
            return new Dimension(displaySize.width, displaySize.height);
        }
        if (billboardElements.get(MESSAGE) == null || billboardElements.get(INFORMATION) == null) {
            return new Dimension(displaySize.width, 2*displaySize.height/3);
        }
        return new Dimension(displaySize.width, displaySize.height/3);
    }


    /**
     * Gets the boundary dimensions in pixels for the image that goes in the billboard picture component. The image
     * should fit inside these bounds.
     * @return The bounding dimensions the image should fit within.
     */
    private Dimension getPictureImageDimensions() {
        if (billboardElements.get(MESSAGE) != null && billboardElements.get(INFORMATION) != null) {
            return new Dimension(displaySize.width/3, displaySize.height/3);
        }
        return new Dimension(displaySize.width/2, displaySize.height/2);
    }


    /**
     * Gets the dimensions in pixels for the billboard information component, based on what other components will be on
     * the billboard.
     * @return The dimensions of the information component.
     */
    private Dimension getInfoDimensions() {
        if (billboardElements.get(MESSAGE) == null && billboardElements.get(PICTURE) == null) {
            return new Dimension((int) (displaySize.width*0.75), displaySize.height/2);
        }
        if (billboardElements.get(PICTURE) == null) {   //Implies that the message component is not null
            return new Dimension((int) (displaySize.width*0.75), displaySize.height/2);
        }
        return new Dimension((int) (displaySize.width*0.75), displaySize.height/3);
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


    /**
     * Returns a font with a size such that the String 'text' will fit entirely within the Dimension 'size' on one line.
     * @param text The string of text to fit within the bounds
     * @param size The dimensions to fit the text within
     * @return The font of appropriate size.
     */
    private Font scaleSingleLineFont(String text, Dimension size) {
        // Make the text fit in one line
        double safetyFactor = size.width * 0.1;
        double fontSize = 500;
        Font tempFont = new Font(Font.SANS_SERIF, Font.PLAIN, (int) Math.floor(fontSize));
        double width = getFontMetrics(tempFont).stringWidth(text) + safetyFactor;
        fontSize = (size.width / width) * fontSize;
        Font newFont = new Font(Font.SANS_SERIF, Font.PLAIN, (int) Math.floor(fontSize));

        // Check the font size does not exceed height restriction
        double newHeight = getFontMetrics(newFont).getHeight();
        if (newHeight > size.height) {
            fontSize = (size.height * fontSize) / newHeight;
            newFont = new Font(Font.SANS_SERIF, Font.PLAIN, (int) Math.floor(fontSize));
        }

        return newFont;
    }


    /**
     * Returns a font with a size such that the String 'text' will fit entirely within the Dimension 'size.' This may
     * be over multiple lines.
     * @param text The string of text to fit within the bounds
     * @param size The dimensions to fit the text within
     * @param maxFontSize The maximum font size the text is allowed to be
     * @return The font of appropriate size.
     */
    private Font scaleMultilineFont(String text, Dimension size, int maxFontSize) {
        double safetyMultiplier = 2.0;
        int maxInfoFont = 200;
        double fontSize = maxFontSize <= 0 ? maxInfoFont : maxFontSize;    //TODO: make this function more reliable
        Font tempFont = new Font(Font.SANS_SERIF, Font.PLAIN, (int) Math.floor(fontSize));
        double width = getFontMetrics(tempFont).stringWidth(text) * safetyMultiplier;
        double height = getFontMetrics(tempFont).getHeight();

        while(width * height > size.width * size.height) {
            fontSize--;
            tempFont = new Font(Font.SANS_SERIF, Font.PLAIN, (int) Math.floor(fontSize));
            width = getFontMetrics(tempFont).stringWidth(text) * safetyMultiplier;
            height = getFontMetrics(tempFont).getHeight();
        }

        return new Font(Font.SANS_SERIF, Font.PLAIN, (int) Math.floor(fontSize));
    }
}
