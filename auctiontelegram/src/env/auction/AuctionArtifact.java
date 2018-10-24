package auction;

import cartago.*;
import java.util.ArrayList;
import java.util.List;
import jason.asSyntax.Atom;

public class AuctionArtifact extends Artifact {

	String currentWinner = "no_winner";
	private List<String> participants;
	
	public void init() {
		participants = new ArrayList<String>();
		
		defineObsProperty("minOffer", 1980);
		defineObsProperty("participants", 0);
		defineObsProperty("winner", new Atom(currentWinner)); // Atom is a Jason type
	}

	@OPERATION
	public void getIn() {
		ObsProperty opParticipants  = getObsProperty("participants");
		opParticipants.updateValue(opParticipants.intValue()+1);
		participants.add(getCurrentOpAgentId().getAgentName());
	}

	@OPERATION
	public void getOut() {
		ObsProperty opParticipants  = getObsProperty("participants");
		if (opParticipants.intValue() > 1) {
			opParticipants.updateValue(opParticipants.intValue()-1);
			participants.remove(getCurrentOpAgentId().getAgentName());
		}
		
		if (opParticipants.intValue() == 1) {
			 currentWinner = participants.get(0);
			 getObsProperty("winner").updateValue(new Atom(currentWinner));
		}
	}

	@OPERATION
	public void setOffer(double minValue) {
		ObsProperty opMinOffer  = getObsProperty("minOffer");
		opMinOffer.updateValue(minValue);			
		//System.out.println("Minimum price set as " + minValue + " from " + getCurrentOpAgentId().getAgentName());
	}
}
