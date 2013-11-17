package game;

/**
 * constants used by the game
 *
 */
public interface Constants {
   final int CELL = 20; //cell dimension in pixels
   final String soundDirectory = "resources/sounds/";
   final String iconDirectory = "resources/icons/";
   final String mapDirectory = "resources/maps/";
   final String fontDirectory = "resources/fonts/";
   final String scoreDirectory = "resources/scores/";
   final char scoreSeparator = '\u0002';
   
   
   //location of images
   final String[][] blinkyImageNames = {
         { "blinky3up", "blinky4up" },
         { "blinky3down", "blinky4down" },
         { "blinky3right", "blinky4right" },
         { "blinky3left", "blinky4left" },
         { "blinky3left", "blinky4left" }         
   };
   final String[][] pinkyImageNames = {
         { "pinky3up", "pinky4up" },
         { "pinky3down", "pinky4down" },
         { "pinky3right", "pinky4right" },
         { "pinky3left", "pinky4left" },
         { "pinky3left", "pinky4left" }         
   };
   final String[][] inkyImageNames = {
         { "inky3up", "inky4up" },
         { "inky3down", "inky4down" },
         { "inky3right", "inky4right" },
         { "inky3left", "inky4left" },
         { "inky3left", "inky4left" }         
   };
   final String[][] clydeImageNames = {
         { "clyde3up", "clyde4up" },
         { "clyde3down", "clyde4down" },
         { "clyde3right", "clyde4right" },
         { "clyde3left", "clyde4left" },
         { "clyde3left", "clyde4left" }         
   };
   final String[][] pacmanImageNames = {
         { "pacmanup", "pacmanopenup", "pacmanup", "pacmanclosed" },
         { "pacmandown", "pacmanopendown", "pacmandown", "pacmanclosed" },
         { "pacmanright", "pacmanopenright", "pacmanright", "pacmanclosed" },   
         { "pacmanleft", "pacmanopenleft", "pacmanleft", "pacmanclosed" },
         { "pacmanleft", "pacmanopenleft", "pacmanleft", "pacmanclosed" }
   };
   final String[][] missPacmanImageNames ={
	         { "misspacmanup", "misspacmanopenup", "misspacmanup", "misspacmanclosedup" },
	         { "misspacmandown", "misspacmanopendown", "misspacmandown", "misspacmanclosed" },
	         { "misspacmanright", "misspacmanopenright", "misspacmanright", "misspacmanclosed" },   
	         { "misspacmanleft", "misspacmanopenleft", "misspacmanleft", "misspacmanclosedleft" },
	         { "misspacmanleft", "misspacmanopenleft", "misspacmanleft", "misspacmanclosedleft" }
	   };
   
   final String[] itemNames= 
	         {"cherry",
	         "peach",
	         "strawberry",
	         "mellon",
	         "apple",
	         "ship",
	         "bell",
	         "key",
	         "icecube",
	         "speedup",
	         "speeddown",
	         "gspeedup",
	         "gspeeddown",
	         "bomb",
   };
   
   final String[] explosionNames={
	"boom1","boom2","boom3"
   };
 
   final String[] bombNames={
		   "bomb","bomb2"
   };
   
   //chances of apearing per 10000 frames if itemRate in gui=1
   final int[] itemProbs= {
		    60,
			40,
			25,
			20,
			15,
			10,
			5,
			1,
			50,
			50,
			50,
			50,
			50,
			200,
 };
   
   final String[][] vulnerableGhostImageNames = {
         { "ghost3", "ghost4" },
         { "ghost3", "ghost4" },
         { "ghost3", "ghost4" },
         { "ghost3", "ghost4" },
         { "ghost3", "ghost4" }
   };
   final String[][] recoveringGhostImageNames = {
         { "ghost3", "ghost4", "white3", "white4" },
         { "ghost3", "ghost4", "white3", "white4" },
         { "ghost3", "ghost4", "white3", "white4" },
         { "ghost3", "ghost4", "white3", "white4" },
         { "ghost3", "ghost4", "white3", "white4" }
   };   
   final String[][] tokenImageNames = {
         { }   
   };
}
