package game.player;

import game.Game;

/**
 * Interface for a player of the game.
 */
public abstract class Player {
   
   Game game;
   /**A player to keep track of actors*/
   Player(Game game) {
      this.game = game;
   }
   
   abstract public void move(double dt);
}
