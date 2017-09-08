package controller;

import model.Board;
import model.Board.State;
import model.Game;
import model.Location;
import model.NotImplementedException;
import model.Player;

/**
 * A DumbAI is a Controller that always chooses the blank space with the
 * smallest column number from the row with the smallest row number.
 */
public class DumbAI extends Controller {

	public DumbAI(Player me) {
		super(me);
	}

	protected @Override Location nextMove(Game g) {
		// Note: Calling delay here will make the CLUI work a little more
		// nicely when competing different AIs against each other.

		//imported Board. is this ok?

		// TODO Auto-generated method stub
		//throw new NotImplementedException();
		delay();
		Board b=g.getBoard();
		for (Location loc : b.LOCATIONS) {
			//taking advantage of fact that ordinary traversal is
			//in the order specified
			if (b.get(loc)==null)	{
				return loc;
			}
		}
		return null;	
	}
}