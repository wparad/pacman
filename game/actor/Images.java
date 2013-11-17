package game.actor;

import game.Constants;

import java.awt.Image;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;

/**
 * This class describes the sequence of images that the actor goes through
 * depending on the direction of movement.
 */
public class Images implements Constants {
   
   private static Map<String,Image> images = new HashMap<String,Image>();
   private Map<MovementDirection, Image[]> imageMap = new HashMap<MovementDirection, Image[]>();
   private int currentIndex = 0;
   private final int N_IMAGES;
   private MovementDirection[] directions = MovementDirection.values();
   /**Constructor to load new images*/
   Images(String[][] names) {
      // north, south, east, west, none
      N_IMAGES = names[0].length;
      Image[] icons;
      for (int i = 0; i < directions.length; i++) {
         icons = new Image[N_IMAGES];
         for (int j = 0; j < N_IMAGES; j++) {
            icons[j] = getImage(names[i][j]);
         }
         imageMap.put(directions[i], icons);
      }
   }
   
   private Image getImage(String name) {
      Image icon = images.get(name);
      if (icon == null) { //haven't read it in yet         
         icon = new ImageIcon(ClassLoader.getSystemResource(iconDirectory + name + ".png")).getImage();
         images.put(name, icon);
      }
      return icon;
   }
   /**get the image for a particular direction and type*/
   public Image getImage(MovementDirection dir, int index) {
      Image[] icons = imageMap.get(dir);
      return icons[index % icons.length];
   }
   /**returns the next image in the sequence of moving*/
   public Image getNextImage(MovementDirection dir) {
      currentIndex = (currentIndex + 1) % N_IMAGES;
      return getImage(dir, currentIndex);
   }
}
