package ee.ioc.cs.vsle.equations;/* <expression>  ::=  [ "-" ] <term> [ [ "+" | "-" ] <term> ]... <term>  ::=  <factor> [ [ "*" | "/" ] <factor> ]... <factor>  ::=  <number>  |  "(" <expression> ")" */// ------------------------------------------------------------------------import ee.ioc.cs.vsle.synthesize.EquationException;import ee.ioc.cs.vsle.util.db;import java.util.HashMap;public class EquationSolver {	static java.util.ArrayList vars = new java.util.ArrayList();	public static java.util.ArrayList relations = new java.util.ArrayList();	static StringStack expString;	static String equation;	public static void solve(String eq) throws EquationException {		equation = eq;		expString = new StringStack(eq);		vars.clear();		relations.clear();		ExpNode exp = upperTree();		skipBlanks();		exp.getVars();		exp.decorate();		exp.getExpressions();	}	public static double calcValue(String equation, HashMap table) throws EquationException {		expString = new StringStack(equation);		ExpNode exp = upperTree();		skipBlanks();		return exp.calcValue(table);	}	public static void main(String[] args) throws EquationException {		expString = new StringStack(args[0]);		skipBlanks();		ExpNode exp = upperTree();		System.out.println(exp);		skipBlanks();		exp.postFix();		System.out.println(exp.inFix());		exp.getVars();		exp.decorate();		exp.getExpressions();		HashMap m = new HashMap();		m.put("x", new Double(4));		System.out.println(exp.calcValue(m));		for (int i = 0; i < EquationSolver.relations.size(); i++) {			System.out.println(EquationSolver.relations.get(i));		}	}	static void skipBlanks() {		// Skip past any spaces and tabs on the current line of input.		// Stop at a non-blank character or end-of-line.		while (expString.peek() == ' ' || expString.peek() == '\t') {			expString.getAnyChar();		}	}	static ExpNode upperTree() throws EquationException {		skipBlanks();		ExpNode exp = expressionTree();		skipBlanks();		if (expString.peek() == '=') {			expString.getAnyChar();			ExpNode nextExp = expressionTree();			exp = new BinOpNode('=', exp, nextExp);			skipBlanks();		}		return exp;	}	static ExpNode expressionTree() throws EquationException {		// Read an expression from the current line of input and		// return an expression tree representing the expression.		skipBlanks();		boolean negative; // True if there is a leading minus sign.		negative = false;		if (expString.peek() == '-') {			expString.getAnyChar();			negative = true;		}		ExpNode exp; // The expression tree for the expression.		exp = termTree(); // Start with the first term.		if (negative) {			exp = new UnaryOpNode(exp, "-");		}		skipBlanks();		while (expString.peek() == '+' || expString.peek() == '-') {			// Read the next term and combine it with the			// previous terms into a bigger expression tree.			char op = expString.getAnyChar();			ExpNode nextTerm = termTree();			exp = new BinOpNode(op, exp, nextTerm);			skipBlanks();		}		return exp;	}	static ExpNode termTree() throws EquationException {		// Read a term from the current line of input and		// return an expression tree representing the term.		skipBlanks();		ExpNode term; // The expression tree representing the term.		term = factorTree();		skipBlanks();		while (expString.peek() == '*' || expString.peek() == '/') {			// Read the next factor, and combine it with the			// previous factors into a bigger expression tree.			char op = expString.getAnyChar();			ExpNode nextFactor = factorTree();			term = new BinOpNode(op, term, nextFactor);			skipBlanks();		}		return term;	}	static ExpNode factorTree() throws EquationException {		skipBlanks();		ExpNode factor;		factor = primaryTree();		while (expString.peek() == '^') {			// Read the next primary, and exponentiate			// the postFix-so-far by the postFix of this primary.			char op = expString.getAnyChar();			ExpNode nextPrimary = primaryTree();			factor = new BinOpNode(op, factor, nextPrimary);			skipBlanks();		}		return factor;	}	static ExpNode primaryTree() throws EquationException {		skipBlanks();		char ch = expString.peek();		if (Character.isDigit(ch) || Character.isLetter(ch)) {			String val = readWord();			if (isFunction(val)) {				skipBlanks();				if (expString.peek() != '(') {					// The function name should have been followed					// by the argument of the function, in parentheses.					throw new EquationException(						"Error in equation '" + equation						+ "'. Missing left parenthesis after function name.");				}				expString.getAnyChar(); // Read the '('				ExpNode exp = expressionTree();				skipBlanks();				if (expString.peek() != ')') {					// There must be a right parenthesis after the argument.					throw new EquationException(						"Error in equation '" + equation						+ "'. Missing right parenthesis after function argument.");				}				expString.getAnyChar(); // Read the ')'				return new UnaryOpNode(exp, val);			} else {				return new ConstNode(val);			}		} else if (ch == '(') {			expString.getAnyChar(); // Read the "("			ExpNode exp = expressionTree();			skipBlanks();			if (expString.peek() != ')') {				throw new EquationException(					"Error in equation '" + equation					+ "'. Missing right parenthesis.");			}			expString.getAnyChar(); // Read the ")"			return exp;		} else if (ch == '\n') {			throw new EquationException(				"Error in equation '" + equation				+ "'. End-of-line encountered in the middle of an expression.");		} else if (ch == ')') {			throw new EquationException(				"Error in equation '" + equation + "'. Extra right parenthesis.");		} else if (ch == '+' || ch == '-' || ch == '*' || ch == '/') {			throw new EquationException(				"Error in equation '" + equation + "'. Misplaced operator.");		} else {			throw new EquationException(				"Error in equation '" + equation + "'. Unexpected character \""				+ ch + "\" encountered.");		}	}	static boolean isFunction(String s) {		if (s.equals("sin") || s.equals("cos") || s.equals("tan")			|| s.equals("log") || s.equals("abs")) {			return true;		}		return false;	}	static String readWord() throws EquationException {		// Reads a word from input.  A word is any sequence of		// letters and digits, starting with a letter.  When		// this subroutine is called, it should already be		// known that the next character in the input is		// a letter.		String word = ""; // The word.		char ch = expString.peek();		boolean digit = false;		if (Character.isDigit(ch)) {			digit = true;		}		while (Character.isLetter(ch) || Character.isDigit(ch) || ch == '.'			|| ch == '_') {			word += expString.getChar(); // Add the character to the word.			ch = expString.peek();			if (Character.isLetter(ch) && digit) {				throw new EquationException(					"Error in equation '" + equation					+ "'. Not a number or variable, beginning with: " + word);			}		}		return word;	}}