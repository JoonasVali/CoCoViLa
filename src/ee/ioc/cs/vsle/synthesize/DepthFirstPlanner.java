/**
 * 
 */
package ee.ioc.cs.vsle.synthesize;

import java.awt.*;
import java.util.*;
import java.util.List;

import javax.swing.*;
import javax.swing.event.*;

import ee.ioc.cs.vsle.editor.*;
import ee.ioc.cs.vsle.util.*;


/**
 * @author pavelg
 * 
 */
public class DepthFirstPlanner implements IPlanner {

	private static DepthFirstPlanner s_planner = null;

    private DepthFirstPlanner() {
    }

    public static DepthFirstPlanner getInstance() {
        if ( s_planner == null ) {
            s_planner = new DepthFirstPlanner();
        }
        return s_planner;
    }

    public String getDescription() {
    	return "Depth First";
    }
    
    public ArrayList<Rel> invokePlaning( Problem problem, boolean computeAll ) {
    	long startTime = System.currentTimeMillis();
    	
        ArrayList<Rel> algorithm = new ArrayList<Rel>();
        
        //manage axioms
        for ( Iterator<Rel> axiomIter = problem.getAxioms().iterator(); axiomIter.hasNext(); ) {
            Rel rel = axiomIter.next();
            algorithm.add( rel );
            axiomIter.remove();
        }

        problem.getFoundVars().addAll(problem.getKnownVars());
        
        //invoke linear planning
        if ( linearForwardSearch( problem, algorithm, problem.getGoals(), 
        		// do not optimize yet if subtasks exist
        		( computeAll || !problem.getRelsWithSubtasks().isEmpty() ) ) && !computeAll ) {
        	
        	if ( RuntimeProperties.isLogInfoEnabled() )
    			db.p( "Problem solved without subtasks");
        	
        } else if ( !problem.getRelsWithSubtasks().isEmpty() ) {
        	
        	subtaskPlanning( problem, algorithm );
        	
        	linearForwardSearch( problem, algorithm, problem.getGoals(), computeAll );
        	
        }
		if ( RuntimeProperties.isLogInfoEnabled() )
			db.p( "Planning time: " + ( System.currentTimeMillis() - startTime ) + "ms.");
		
        return algorithm;
    }
    
	private boolean linearForwardSearch(Problem p, List<Rel> algorithm, 
			Set<Var> targetVars,
			boolean computeAll) {
		
		/*
		 * while iterating through hashset, items cant be removed from/added to
		 * that set. Theyre collected into these sets and added/removedall
		 * together after iteration is finished
		 */
		HashSet<Var> newVars = new HashSet<Var>();
		HashSet<Var> relOutputs = new HashSet<Var>();
		HashSet<Var> removableVars = new HashSet<Var>();
		// backup goals for later optimization
		HashSet<Var> allTargetVars = new HashSet<Var>( targetVars );
		
		boolean changed = true;

		if (RuntimeProperties.isLogDebugEnabled())
			db.p("------Starting linear planning with (sub)goals: "
					+ targetVars + "--------");

		if (RuntimeProperties.isLogDebugEnabled())
			db.p("Algorithm " + algorithm);
		
		int counter = 1;

		while ((!computeAll && changed && !targetVars.isEmpty())
				|| (changed && computeAll)) {

			if (RuntimeProperties.isLogDebugEnabled())
				db.p("----Iteration " + counter + " ----");

			counter++;
			changed = false;
			// remove targets if they're already known
			for (Iterator<Var> targetIter = targetVars.iterator(); targetIter.hasNext();) {
				Var targetVar = targetIter.next();
				if (p.getFoundVars().contains(targetVar)) {
					targetIter.remove();
				}
			}
			// iterate through all knownvars
			if (RuntimeProperties.isLogDebugEnabled())
				db.p("Known:" + p.getKnownVars());
			
			for (Var var : p.getKnownVars() ) {
				
				if (RuntimeProperties.isLogDebugEnabled())
					db.p("Current Known: " + var);
				// Check the relations of all components
				for (Rel rel : var.getRels() ) {
					if (RuntimeProperties.isLogDebugEnabled())
						db.p("And its rel: " + rel);
					if (p.getAllRels().contains(rel)) {
						if( !var.getField().isConstant() ) {
							rel.setUnknownInputs(rel.getUnknownInputs() - 1);
						}

						if (RuntimeProperties.isLogDebugEnabled())
							db.p("problem contains it " + rel
									+ " unknownInputs: "
									+ rel.getUnknownInputs());

						removableVars.add(var);

						if (rel.getUnknownInputs() == 0
								&& rel.getType() != RelType.TYPE_METHOD_WITH_SUBTASK) {

							if (RuntimeProperties.isLogDebugEnabled())
								db.p("rel is ready to be used " + rel);

							boolean relIsNeeded = false;

							if (RuntimeProperties.isLogDebugEnabled())
								db.p("tema outputsid " + rel.getOutputs());

							for (Var relVar : rel.getOutputs() ) {

								if (!p.getFoundVars().contains(relVar)) {
									relIsNeeded = true;
								}
							}

							if (rel.getOutputs().isEmpty()) {
								relIsNeeded = true;
							}
							if (RuntimeProperties.isLogDebugEnabled())
								db.p("relIsNeeded " + relIsNeeded);

							if (relIsNeeded) {

								if (RuntimeProperties.isLogDebugEnabled())
									db.p("ja vajati " + rel);

								if (!rel.getOutputs().isEmpty()) {
									relOutputs.clear();
									addVarsToSet( rel.getOutputs(), relOutputs );
									newVars.addAll( relOutputs );
									p.getFoundVars().addAll( relOutputs );
								}
								algorithm.add(rel);
								if (RuntimeProperties.isLogDebugEnabled())
									db.p("algorithm " + algorithm);
							}

							p.getAllRels().remove(rel);
							changed = true;
						}
					}
				}
			}

			if (RuntimeProperties.isLogDebugEnabled())
				db.p("foundvars " + p.getFoundVars());

			p.getKnownVars().addAll(newVars);
			p.getKnownVars().removeAll(removableVars);
			newVars.clear();
		}
		if (RuntimeProperties.isLogDebugEnabled())
			db.p("algorithm " + algorithm);

		if (!computeAll) {
			Optimizer.optimize( algorithm, new HashSet<Var>( allTargetVars ) );
		}

		return targetVars.isEmpty() || p.getFoundVars().containsAll( allTargetVars );
	}

	private static int maxDepth = 2;//0..2, i.e. 3
	private static boolean m_isSubtaskRepetitionAllowed = false;
	
	private void subtaskPlanning(Problem problem, ArrayList<Rel> algorithm ) {
		
		if (RuntimeProperties.isLogDebugEnabled()) {
			db.p( "!!!--------- Starting Planning With Subtasks ---------!!!" );
		}
		
		int maxDepthBackup = maxDepth;
		
		if( !m_isSubtaskRepetitionAllowed ) {
			maxDepth = problem.getRelsWithSubtasks().size() - 1;
		}
		
		if (RuntimeProperties.isLogDebugEnabled()) 
			db.p( "maxDepth: " + ( maxDepth + 1 ) );
		
		subtaskPlanningImpl( problem, algorithm, new HashSet<Rel>(), null, 0 );
		
		maxDepth = maxDepthBackup;
	}
	
	private void subtaskPlanningImpl(Problem problem, List<Rel> algorithm,
			HashSet<Rel> subtaskRelsInPath, SubtaskRel parentSubtask, int depth) {

		Set<Var> newVars = new HashSet<Var>();
		
		// or
		OR: for (Rel subtaskRel : problem.getRelsWithSubtasks()) {
			if (RuntimeProperties.isLogDebugEnabled())
				db.p( "OR: rel with subtasks - " + subtaskRel + " depth: " + ( depth + 1 ) );
			
			if( ( subtaskRel.getUnknownInputs() > 0 ) 
					|| newVars.containsAll( subtaskRel.getOutputs() ) 
					|| problem.getFoundVars().containsAll( subtaskRel.getOutputs() ) ) {
				if (RuntimeProperties.isLogDebugEnabled())
					db.p( "skipped" );
				continue OR;
			}
			
			HashSet<Rel> newPath = new HashSet<Rel>();
			
			if( !m_isSubtaskRepetitionAllowed && subtaskRelsInPath.contains(subtaskRel) ) {
				if (RuntimeProperties.isLogDebugEnabled())
					db.p( "This rel with subtasks is already in use, path: " + newPath );
				continue;
			} else if( !m_isSubtaskRepetitionAllowed ) {
				if (RuntimeProperties.isLogDebugEnabled())
					db.p( "This rel with subtasks can be used, path: " + newPath );
				newPath.addAll( subtaskRelsInPath );
				newPath.add( subtaskRel );
			}
			
			// this is true if all subtasks are solvable
			boolean allSolved = true;
			// and
			AND: for (SubtaskRel subtask : subtaskRel.getSubtasks()) {
				if (RuntimeProperties.isLogDebugEnabled())
					db.p( "AND: subtask - " + subtask );
				// lets clone the environment
				Problem problemNew = problem.getCopy();
				// we need fresh cloned subtask instance
				SubtaskRel subtaskNew = problemNew.getSubtask(subtask);

				prepareSubtask( problemNew, subtaskNew );
				
				Set<Var> goals = new HashSet<Var>();
				addVarsToSet( subtaskNew.getOutputs(), goals );
				
				boolean solved = 
					linearForwardSearch( problemNew, subtaskNew.getAlgorithm(), 
							// never optimize here
							goals, true );
				
				if( solved ) {
					if (RuntimeProperties.isLogDebugEnabled())
						db.p( "Subtask: " + subtaskNew + " solved" );
					subtask.getAlgorithm().addAll( subtaskNew.getAlgorithm() );
					continue AND;
				} else if( !solved && ( depth == maxDepth ) ) {
					if (RuntimeProperties.isLogDebugEnabled())
						db.p( "Subtask: " + subtaskNew + " not solved and cannot go any deeper" );
					continue OR;
				}
				
				if (RuntimeProperties.isLogDebugEnabled())
					db.p( "Recursing deeper" );
				
				subtaskPlanningImpl( problemNew, subtaskNew.getAlgorithm(), newPath, subtaskNew, depth + 1 );
				
				if (RuntimeProperties.isLogDebugEnabled())
					db.p( "Back to depth " + ( depth + 1 ) );
				
				solved = linearForwardSearch( problemNew, subtaskNew.getAlgorithm(), 
						// always optimize here in order to get rid of unnecessary subtask instances
						// temporary true while optimization does not work correctly
						goals, true );
				
				if (RuntimeProperties.isLogDebugEnabled())
					db.p( "Subtask: " + subtaskNew + " solved: " + solved );
				
				allSolved &= solved;
				//if at least one subtask is not solvable, try another branch
				if( !allSolved ) {
					continue OR;
				}
				// copy algorithm from cloned subtask to old
				subtask.getAlgorithm().addAll( subtaskNew.getAlgorithm() );
			}
			
			if( allSolved ) {
				algorithm.add( subtaskRel );
				
				addVarsToSet( subtaskRel.getOutputs(), newVars );
				
//				if( depth == 0 ) {
//					addKnownVarsToSet( subtaskRel.getOutputs(), problem.getFoundVars() );
//				}
				
			} else if( !m_isSubtaskRepetitionAllowed ) {
				if (RuntimeProperties.isLogDebugEnabled())
					db.p( "Subtasks not solved, removing rel from path " + subtaskRel );
				newPath.remove( subtaskRel );
			}
		}
		
		problem.getKnownVars().addAll( newVars );
		problem.getFoundVars().addAll( newVars );
		return;// false;
	}

	
	/**
	 * This method helps to copy Vars from one collection to another taking into account Aliases, 
	 * i.e. it flattens the hierarchical structure of aliases.
	 * 
	 * @param from
	 * @param to
	 */
	private void addVarsToSet( Collection<Var> from, Collection<Var> to ) {
		for (Var topvar : from ) {

			to.add(topvar);
			// if output var is alias then all its vars should be copied as well
			if ( topvar.getField().isAlias() ) {
				for ( Var var : topvar.getChildVars() ) {

					to.add(var);
					if( var.getField().isAlias() ) {
						//this is used if we have alias in alias structure
						if (RuntimeProperties.isLogDebugEnabled())
							db.p( "addVarsToList: alias " + var + " in alias " + topvar );
						addVarsToSet( var.getChildVars(), to );//recursion
					}
				}
			}
		}
	}
	
	private void prepareSubtask(Problem problem, Rel subtask) {
		// [x->y] assume x is known
		Set<Var> flatVars = new HashSet<Var>();
		addVarsToSet( subtask.getInputs(), flatVars );
		problem.getKnownVars().addAll( flatVars );
		problem.getFoundVars().addAll( flatVars );
		
//		HashSet<Rel> removableRels = new HashSet<Rel>();
//		// remove all rels with otputs same as subtask inputs
//		for (Var subtaskInput : subtask.getInputs()) {
//
//			for (Rel rel : problem.getAllRels()) {
//				if ( ( rel.getOutputs().size() > 0 ) 
//						&& ( rel.getOutputs().get(0) == subtaskInput ) ) {
//					removableRels.add( rel );
//				}
//			}
//		}
		
		/* Pavel[27Mar06]
		 * probably we do not need the functionality commented below 
		 * because sometimes we want to compute something else 
		 * when subtask's output is known, e.g. -
		 * 
		 * double a,b,c,d,x,y;
		 * y=sin(x);
		 * a = x;
		 * b = y;
		 * c=a+b;
		 * [x->y]->d{f};
		 * 
		// remove all rels with inputs same as subtask outputs
		for (Var subtaskOutput : subtask.getOutputs()) {
			for (Rel rel : problem.getAllRels()) {
				if (rel.getType() == RelType.TYPE_EQUATION
						&& rel.getInputs().get(0) == subtaskOutput) {
					removableRels.add( rel );
				}
			}
		}
		*/
		
//		problem.getAllRels().removeAll(removableRels);
//		removableRels.clear();
	}
	
	public Component getCustomOptionComponent() {
		final JSpinner spinner = new JSpinner( new SpinnerNumberModel( maxDepth + 1, 1, 10, 1 ) );
		spinner.addChangeListener(new ChangeListener(){

			public void stateChanged(ChangeEvent e) {
				Integer value = (Integer)((JSpinner)e.getSource()).getValue();
				maxDepth = value - 1;
				
				if (RuntimeProperties.isLogDebugEnabled())
					db.p( "maxDepth " + ( maxDepth + 1 ) );
			}});
		final JPanel panel = new JPanel()
		{
			public void setEnabled( boolean b ) {
				super.setEnabled( b );
				spinner.setEnabled( b );
			}
		};
		panel.add( new JLabel("Max Depth: ") );
		panel.add( spinner );
		
		panel.setEnabled( m_isSubtaskRepetitionAllowed );
		
		final JCheckBox chbox = new JCheckBox( "Allow subtask recursive repetition", m_isSubtaskRepetitionAllowed );
		
		chbox.addChangeListener( new ChangeListener() {

			public void stateChanged(ChangeEvent e) {
				m_isSubtaskRepetitionAllowed = chbox.isSelected();
				panel.setEnabled( m_isSubtaskRepetitionAllowed );
				
				if (RuntimeProperties.isLogDebugEnabled())
					db.p( "m_isSubtaskRepetitionAllowed " + m_isSubtaskRepetitionAllowed );
			}} );
		
		JPanel container = new JPanel( new GridLayout( 2, 0 ) );
		
		container.add( chbox );
		container.add( panel );
		
    	return container;
    }  
}
