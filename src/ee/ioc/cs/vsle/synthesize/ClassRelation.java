package ee.ioc.cs.vsle.synthesize;

import static ee.ioc.cs.vsle.synthesize.SpecParser.*;

import java.util.*;
import java.util.regex.*;

import ee.ioc.cs.vsle.vclass.*;

/**
 * <p>Title: ee.ioc.cs.editor.synthesize.ClassRelation</p>
 * <p>Description: <description></p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author  Ando Saabas
 * @version 1.0
 */
class ClassRelation  {

    /**
     * 
     */
    private static final long serialVersionUID = -6822667728220713188L;
    
    private static final Pattern PATTERN_EXCEPTION = Pattern.compile( "^\\(([\\._A-Za-z0-9]+)\\)$" );
    
    protected Collection<ClassField> inputs = new LinkedHashSet<ClassField>();
    private Collection<SubtaskClassRelation> subtasks = new LinkedHashSet<SubtaskClassRelation>();
    protected Collection<ClassField> outputs = new LinkedHashSet<ClassField>();
    private Collection<ClassField> exceptions = new LinkedHashSet<ClassField>();

	/**
	 * Type of the relation.
	 * 2 - javamethod
	 * 3 - equation(and assignment)
	 * 4 - alias
	 * 5 - subtask
	 * 6 - method with subtask
	 * 7 - unimplemented
	 */
    private RelType type;
	private String method;
	protected String specLine;

	/**
	 * Class constructor.
	 * @param type RelType - relation type.
	 */
        ClassRelation(RelType type, String specLine ) {
		this.type = type;
		this.specLine = specLine;
	} // ee.ioc.cs.editor.synthesize.ClassRelation

        RelType getType() {
            return type;
        }

        void setType( RelType value ) {
            type = value;
        }

        String getMethod() {
            return method;
        }

        /**
         * @return first input
         */
        ClassField getInput() {
            return inputs.iterator().next();
        }
        
        Collection<ClassField> getInputs() {
            return inputs;
        }

        /**
         * @return first output
         */
        ClassField getOutput() {
            return outputs.iterator().next();
        }
        
        Collection<ClassField> getOutputs() {
            return outputs;
        }

        Collection<SubtaskClassRelation> getSubtasks() {
            return subtasks;
        }

        Collection<ClassField> getExceptions() {
            return exceptions;
        }

        /**
         * Adds a new input to the list of inputs.
         * @param input String
         * @param vars ArrayList
         * @throws ee.ioc.cs.vsle.synthesize.UnknownVariableException
         */
        void addInput(String input, Collection<ClassField> vars) throws UnknownVariableException {
        	
        	if (!input.equals("#")) {
				ClassField var = getVar(input, vars);

				if (var != null) {
					inputs.add(var);
				}
				else if ( input.indexOf(".") >= 1 ) {
					ClassField cf = new ClassField( input );

					inputs.add(cf);
				}
				else {
					throw new UnknownVariableException( input );
				}
			}
        } // setInput

    	/**
    	 * @param input String[]
    	 * @param varList ArrayList
    	 * @throws ee.ioc.cs.vsle.synthesize.UnknownVariableException
    	 */
    	void addInputs(String[] input, Collection<ClassField> varList) throws UnknownVariableException {
    		for (int i = 0; i < input.length; i++) {
    			addInput( input[i], varList );
    		}
    	} // addInputs
	/**
	 * Specify output.
	 * @param output String
	 * @param vars ArrayList
	 * @throws ee.ioc.cs.vsle.synthesize.UnknownVariableException
	 */
	void addOutput(String output, Collection<ClassField> vars) throws UnknownVariableException {
		ClassField f = getVar(output, vars);

		if (f != null) {
			outputs.add(f);
		}
		else if (output.indexOf(".") >= 1) {
			ClassField cf = new ClassField( output );

			outputs.add(cf);
		} else if (output.startsWith("*.")) {
			ClassField cf = new ClassField( output );

			outputs.add(cf);
		}
		else {
			throw new UnknownVariableException(output);
		}
	} // setOutput

	/**
	 * @param _outputs String[]
	 * @param varList ArrayList
	 * @throws ee.ioc.cs.vsle.synthesize.UnknownVariableException
	 */
        void addOutputs( String[] _outputs, Collection<ClassField> varList ) throws UnknownVariableException {
            for ( int i = 0; i < _outputs.length; i++ ) {
                //we must have at least one output, others will be considered as exceptions
                String output = _outputs[ i ];
                
                Matcher matcher = PATTERN_EXCEPTION.matcher( output.trim() );
                if ( matcher.find() ) {
                    ClassField cf = new ClassField( matcher.group( 1 ), "exception" );

                    exceptions.add( cf );

                } else {
                	addOutput(output, varList);
                }
            }
        } // addOutputs

	/**
	 * @param subtaskList ArrayList
	 * @param varList ArrayList
	 * @throws ee.ioc.cs.vsle.synthesize.UnknownVariableException
	 */
	void addSubtask( SubtaskClassRelation subtask ) {
		subtasks.add( subtask );
	} // addSubtasks

	/**
	 * @param method String
	 */
	void setMethod(String method) {
		this.method = method;
	} // setMethod
	
	public String getSpecLine() {
		return specLine;
	}
	
	/**
	 * @return String
	 */ 
	@Override
    public String toString() {
		if (type == RelType.TYPE_METHOD_WITH_SUBTASK) {
			return "[Subtasks: " + subtasks + "][Inputs: " + inputs + "][Output: " + outputs + "][Method: " + method + "][Type: " + type + "]";
		}
		return "[Inputs: " + inputs + "][Output: " + outputs + "][Method: " + method + "][Type: " + type + "]";
	} // toString

}
