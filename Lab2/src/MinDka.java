
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;

/**
 * UTR_Lab02: Deterministic finite automaton minimization
 * 
 * @author Viran
 *
 */
public class MinDka {

	public static void main(String[] args) {

		DKA dka = new DKA();

		try (Scanner input = new Scanner(System.in)) {

			// row 1: existing states
			String ln = input.nextLine().trim();
			String[] states = ln.split(",");

			// row 2: supported alphabet
			ln = input.nextLine().trim();
			String[] alphabet = ln.split(",");

			// row 3: accepted states
			ln = input.nextLine().trim();
			String[] acceptedStates = ln.split(",");

			// row 4: starting state
			ln = input.nextLine().trim();
			String startingState = ln.trim();

			// Initialize the automata
			dka.addStartingStateLabel(startingState);
			dka.addStatesLabels(states);
			dka.addAcceptedStatesLabels(acceptedStates);
			dka.addAlphabetLabels(alphabet);

			// start creating automata relations

			while (true) {
				ln = input.nextLine().trim();
				if (ln.trim().equals(""))
					break;

				String[] trans = ln.split("->");
				String[] arguments = trans[0].split(",");
				dka.addRelations(arguments[0].trim(), arguments[1].trim(), trans[1].trim());
			}

		} catch (Exception e) {
		}

		//minimize the DKA
		//dka.printDka();
		dka.minimize();
		dka.printDka();
	}

	/**
	 * Nondeterministic finite automaton with e-moves
	 * 
	 * @author Viran
	 *
	 */
	private static class DKA {
		private HashMap<String, State> statesMap = new HashMap<>();
		private TreeSet<String> alphabet = new TreeSet<>(); 
		private TreeSet<String> acceptedStates = new TreeSet<>(); 
		private TreeSet<String> stateSet = new TreeSet<>(); 
		private String startingState;
		private Set<String> currentStates = new HashSet<>(); 

		public DKA() {
			statesMap.put("#", new State("#",false));
		}

		

		/**
		 * State representation for a finite automata
		 * 
		 * @author Viran
		 *
		 */
		private static class State {
			private String name;
			private Map<String, String> transitions = new HashMap<String, String>();
			private boolean accepted;
			/**
			 * State constructor.
			 * 
			 * @param name
			 *            State label
			 */
			public State(String name,boolean accepted) {
				this.name = name;
				this.accepted=accepted;
			}

			/**
			 * Return all transitions of this state for the given letter.
			 * 
			 * @param a
			 *            Letter of alphabet which this state recognizes
			 * @return Array of strings containing transition state labels
			 */
			public String getTransitions(String a) {
				return transitions.get(a);
			}

			/**
			 * Assign the given transition to a symbol which leads to it.
			 * 
			 * @param symbols
			 * @param trans
			 */
			public void addTransitions(String symbol, String trans) {
					transitions.put(symbol, trans);
			}

			/**
			 * Return all states this particular state can reach in a new set.
			 * @return Reachable states set
			 */
			public Collection<String> getAllTransitions(){
				return new HashSet<>(transitions.values());
			}
			
			/**
			 * Tell if this state is in list of accepted states.
			 * @return True if this state is acceptable, false otherwise
			 */
			private boolean isAccepterd(){
				return accepted;
			}
			
			@Override
			public int hashCode() {
				final int prime = 31;
				int result = 1;
				result = prime * result
						+ ((name == null) ? 0 : name.hashCode());
				return result;
			}

			@Override
			public boolean equals(Object obj) {
				if (this == obj)
					return true;
				if (obj == null)
					return false;
				if (getClass() != obj.getClass())
					return false;
				State other = (State) obj;
				if (name == null) {
					if (other.name != null)
						return false;
				} else if (!name.equals(other.name))
					return false;
				return true;
			}

		}

		/**
		 * Add beginning state for this e-NKA.
		 * 
		 * @param startingState
		 *            First state from which e-NKA test the given input
		 *            sequence.
		 */
		public void addStartingStateLabel(String startingState) {
			this.startingState = startingState;
			currentStates.add(startingState.trim());

		}

		/**
		 * Add specific alphabet for this e-NKA.
		 * 
		 * @param alphabet
		 *            Specific alphabet
		 */
		public void addAlphabetLabels(String[] alphabet) {
			for (String s : alphabet) {
				this.alphabet.add(s.trim());
			}

		}

		/**
		 * Add accepted states for this e-NKA.
		 * 
		 * @param acceptedStates
		 *            States for which the e-NKA returns the value of 1 (true).
		 */
		public void addAcceptedStatesLabels(String[] acceptedStates) {
			for (String s : acceptedStates) {
				this.acceptedStates.add(s.trim());
			}

		}

		/**
		 * Add states contained for this e-NKA.
		 * 
		 * @param states
		 *            States in which e-NKA can operate.
		 */
		public void addStatesLabels(String[] states) {
			for (String s : states) {
				this.stateSet.add(s.trim());
			}
		}

		/**
		 * Add a new relation function to this e-NKA.
		 * 
		 * @param stateName
		 *            State label which extends its transition functions.
		 * @param symbol
		 *            Element of the e-NKA alphabet which triggers the change.
		 * @param transitions
		 *            List of states to which one state changes given a specific
		 *            element of the alphabet.
		 */
		public void addRelations(String stateName, String symbol,
				String transitions) {
			State state;
			if (statesMap.containsKey(stateName)) {
				state = statesMap.get(stateName);
			} else {
				state = new State(stateName, acceptedStates.contains(stateName));
			}
			state.addTransitions(symbol, transitions);
			statesMap.put(stateName, state);
		}

		/**
		 * Set the current state to the beginning state of the machine.
		 */
		@SuppressWarnings("unused")
		public void reset() {
			currentStates = new HashSet<>();
			currentStates.add(startingState);
		}

		/**
		 * Minimize this DKA.
		 */
		public void minimize() {
			//eliminate unreachable states
			Set<String> reachableStates=reachableStates(startingState,new HashSet<String>());
			Set<String> unreachableStates= new TreeSet<String>(stateSet);
			unreachableStates.removeAll(reachableStates);
			for(String p:unreachableStates){
				stateSet.remove(p);
				statesMap.remove(p);
				acceptedStates.remove(p);
				
			}
			//this.printDka();
			
			//minimize this dka using the 2nd algorithm presented in Sinisa Srbljic book: "Uvod u teoriju racunarstva"
			minimizeStates();
		}
		

		/**
		 * Filter all reachable states from this DKA.
		 * @param q State which is tested for reachable
		 * @param set Set of known reachable states
		 * @return Resulting set of reachable states
		 */
		private Set<String> reachableStates(String q, Set<String> set) {
			set.add(q);
			Collection<String> trans=statesMap.get(q).getAllTransitions();
			for(String p:trans){
				if(!set.contains(p)){
					set.addAll(reachableStates(p, set));
				}
			}
			return set;
		}


		/**
		 * Minimize this DKA using the 2nd algorithm presented in Sinisa Srbljic book: "Uvod u teoriju racunarstva"
		 */
		private void minimizeStates() {

			Map<String,String> groupMain=new HashMap<>();

			for(String p:stateSet){
				if(statesMap.get(p).isAccepterd()){
					groupMain.put(p, "1");
				}else{
					groupMain.put(p, "0");
				}
			}


			while(true){
				Map<String,Set<String>> groupsBefore=statesByGroup(groupMain);

				int groupsBeforeSize=groupsBefore.keySet().size();
				
				Map<String,String> groupHelp=new HashMap<>();
				
				for(String p:stateSet){					
					State state=statesMap.get(p);
					String newGroup="";
					for(String symbol:alphabet){
						//for every state p in stateSet grab every alphabet symbol, get a transition symbol and determinate to which group it leads  
						String transToGroup=groupMain.get(state.getTransitions(symbol));
						///new group name is generated by the resulting groups of the previous state 
						newGroup=newGroup.concat(transToGroup);
					}
					groupHelp.put(p, newGroup);
				}
				
				Map<String,String> groupResult=new HashMap<>();
				
				for(String state:stateSet){
					groupResult.put(state, groupMain.get(state).concat(groupHelp.get(state)));
				}
				
				

				Map<String,Set<String>> groupsAfter=statesByGroup(groupResult);
				int groupsAfterSize=groupsAfter.keySet().size();
				
				if(groupsBeforeSize==groupsAfterSize){
					finalise(groupsAfter);
					return;
				}else{
					groupMain=groupResult;
				}
			}
		}
		

		
		/**
		 * Finalize the process of minimization by changing the states in this dka.
		 * @param groupsAfter Groups of same states determinated by the process of minimization
		 */
		private void finalise(Map<String, Set<String>> groupsAfter) {
			//make all necessary adjustments with:
			Set<String> groupsSet=groupsAfter.keySet();
			
			//determinate representing state for minimization of a group
			for(String group:groupsSet){
				TreeSet<String> sameStates=new TreeSet<>(groupsAfter.get(group));
				String groupRepresentingState=sameStates.first();
				//...and generate a separate list of elements we substitute 
				sameStates.remove(groupRepresentingState);
				//for every state in map
				//get all of its transitions 
				for(String duplicateState:sameStates){
					//check in map for all occurrences of this state and replace it with representing state,
					//but removing the state with the same name
					for(String mapState:statesMap.keySet()){
						if(!mapState.equals(duplicateState)){
							//check if this state has any states to replace
							State mapStateS=statesMap.get(mapState);
							for(String symbol:alphabet){
								String transState=mapStateS.getTransitions(symbol);
								if(transState!=null){
									if(transState.equals(duplicateState)){
										//replace all duplicating states with the representing state of the group
										mapStateS.addTransitions(symbol, groupRepresentingState);
									}
								}
							}
						}
						//else remove this state (done after iteration)
					}
					statesMap.remove(duplicateState);
					acceptedStates.remove(duplicateState);
					stateSet.remove(duplicateState);
				}
				
				if(sameStates.contains(startingState))
					startingState=groupRepresentingState;
				
			} 
		}


		
		/**
		 * For a given map of states and the groups they belong create a second map holding 
		 * states sorted by their specific group.
		 * @param srcMap Map~ Key: State - Value: Group 
		 * @return Map~ Key: Group - Value: State
		 */
		private Map<String,Set<String>> statesByGroup(Map<String,String> srcMap){
			//Result
			Map<String,Set<String>> resultMap=new HashMap<>();
			
			Set<String> groupSet=new HashSet<>(srcMap.values());
			Set<String> stateSet=srcMap.keySet();
			
			//for each group in existing groups groupSet generate a new list containing the corresponding elements of a group
			for(String subgroup:groupSet){
				Set<String> statesInSubgroup=new TreeSet<>();
				for(String state:stateSet){
					if(srcMap.get(state).equals(subgroup)){
						statesInSubgroup.add(state);
					}
				}
				resultMap.put(subgroup, statesInSubgroup);
			}
			return resultMap;
		}
		
		/**
		 * Print the specifications of this dka to standard output. 
		 */
		public void printDka(){
			boolean formatHelper;
			//Print states
			formatHelper=true;
			for(String state:stateSet){
				if(formatHelper){
					formatHelper=false;
					System.out.print(state);
				}else{
					System.out.print(","+state);
				}
			}
			System.out.println();
			
			//print alphabet
			formatHelper=true;
			for(String letter:alphabet){
				if(formatHelper){
					formatHelper=false;
					System.out.print(letter);
				}else{
					System.out.print(","+letter);
				}
			}
			System.out.println();
			
			//print accepted states
			formatHelper=true;
			for(String aState:acceptedStates){
				if(formatHelper){
					formatHelper=false;
					System.out.print(aState);
				}else{
					System.out.print(","+aState);
				}
			}
			System.out.println();
			
			//print starting state
			System.out.println(startingState);
			
			//print all transitions
			for(String state:stateSet){
				for(String letter:alphabet){
					State stateS=statesMap.get(state);
					String transition=stateS.getTransitions(letter);
					System.out.println(state.trim()+","+letter.trim()+"->"+transition.trim());
				}
			}
		}
	}

}