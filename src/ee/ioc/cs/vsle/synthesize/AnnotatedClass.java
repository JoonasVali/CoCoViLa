package ee.ioc.cs.vsle.synthesize;

import ee.ioc.cs.vsle.vclass.ClassField;

import java.util.*;

/**
 * <p>Title: ee.ioc.cs.editor.synthesize.AnnotatedClass</p>
 * <p>Description:  A representation of a specifcation of a class. Includes information
 * about fields and relations declared.</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Ando Saabas
 * @version 1.0
 */
public class AnnotatedClass {

	private String name;
	private ClassList superClasses = new ClassList();
	//relations declared in the specification of this class and in specifications of superclasses
	private Collection<ClassRelation> classRelations = new LinkedHashSet<ClassRelation>();
	//fields declared in this annotated class
	private Collection<ClassField> classFields = new LinkedHashSet<ClassField>();
	//all fields declared here and in superclasses
	private Collection<ClassField> allFields = new LinkedHashSet<ClassField>();
	
	/**
	 * Class constructor.
	 * @param name String
	 */ 
	AnnotatedClass(String name) {
		this.name = name;
	} // ee.ioc.cs.editor.synthesize.AnnotatedClass

	/**
	 * Adds a new field to the ArrayList of fields.
	 * @param field ClassField - a field to be appended to the list of fields.
	 */ 
	 void addField(ClassField field) {
		classFields.add(field);
		allFields.add(field);
	} // addField

	/**
	 * Adds a list of variables to the ArrayList of fields.
	 * @param v ArrayList - list of variables to be appended to the list of fields.
	 */
	void addFields(Collection<ClassField> v) {
		classFields.addAll(v);
		allFields.addAll(v);
	} // addVars

	/**
	 * Adds a new class relation to the list of class relations.
	 * @param classRelation ClassRelation - a class relation to be added to the list of class relations.
	 */
	void addClassRelation(ClassRelation classRelation) {
		classRelations.add(classRelation);
	} // addClassRelation

	@Override
	public boolean equals( Object o ) {
		if( o != null && o instanceof AnnotatedClass )
		{
			return name.equals( ((AnnotatedClass)o).name );
		}
		return false;
	}
	
	@Override
    public int hashCode() {
        return name.hashCode();
    }
	
	/**
	 * Converts the ee.ioc.cs.editor.synthesize.AnnotatedClass to a string, returning the name of the class.
	 * @return String - a name of the class.
	 */
	public String toString() {
		return name;
	} // toString

	boolean hasField(String fieldName) {
		return getFieldByName(fieldName) != null;
	}

	public ClassField getFieldByName(String fieldName) {
		for ( ClassField f : allFields ){
			if (f.getName().equals(fieldName))
				return f;
		}
		return null;
	}

	String getName() {
		return name;
	}

	/*
	 * need to keep relations up-to-date (i.e. not cache rels from superclasses because some relations
	 * may be added into superclasses later
	 */
	Collection<ClassRelation> getClassRelations() {
	    Collection<ClassRelation> relations = new LinkedHashSet<ClassRelation>();
	    for ( AnnotatedClass superclass : superClasses ) {
	        relations.addAll( superclass.getClassRelations() );
        }
	    //the order does matter! rels from superclasses have to go first
	    relations.addAll( classRelations );
		return relations;
	}

	Collection<ClassField> getFields() {
		return allFields;
	}

	Collection<ClassField> getClassFields() {
		return classFields;
	}
	
	void addSuperClass( AnnotatedClass clas ) {
		
		for( ClassField cf : clas.getFields() ) {
			if( !CodeGenerator.SPEC_OBJECT_NAME.equals( cf.getName() ) ) {
				allFields.add( cf );
			}
		}
		superClasses.add( clas );
	}

	public ClassList getSuperClasses() {
		return superClasses;
	}

	public ClassList getAllSuperClasses() {
	    ClassList allSuperClasses = new ClassList();
	    allSuperClasses.addAll( superClasses );
	    for ( AnnotatedClass superclass : superClasses ) {
	        allSuperClasses.addAll( superclass.getAllSuperClasses() );
        }
        return allSuperClasses;
    }
}

