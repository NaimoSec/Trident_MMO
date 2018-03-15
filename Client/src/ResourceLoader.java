import java.awt.Image;
import java.awt.Toolkit;
import java.util.HashMap;

/**
 * This class grabs resources from the 'images' folder in the res package.
 * @author Gabbe
 */
public class ResourceLoader {

	private static final HashMap<String, Image> loadedImages = new HashMap<String, Image>();

	public Image getImage(String imageName) {
		if(loadedImages.containsKey(imageName))
			return loadedImages.get(imageName);
		Image img = null;
		try {
			img = Toolkit.getDefaultToolkit().getImage(signlink.findcachedir() + "res/ig/"+imageName+".png");
		} catch(Exception e) {
			e.printStackTrace();
			img = null;
		}
		if(img != null)
			loadedImages.put(imageName, img);
		return img;
	}


}
