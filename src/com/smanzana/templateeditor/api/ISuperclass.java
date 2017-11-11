package com.smanzana.templateeditor.api;

import java.util.List;

/**
 * Marks this class as a DataLoader superclass.
 * That means that references to this type actually mean references to
 * a child type. In order to make the proper editor, this interface
 * provides all subclasses.
 * @author Skyler
 *
 */
public interface ISuperclass {
	
	/**
	 * Return a list of <b>all</b> children types that can be built by the
	 * editor and stuffed into a reference to this class.<br />
	 * It goes without saying that all objects in the returned list are
	 * all subclasses of the defining class.
	 * <p>
	 * As an example, consider a class Animal which has only three
	 * child classes: Dog, Cat, and Bird. This method should return a list
	 * of one object of each type. The editor uses information from the objects
	 * to say that any time you want to edit an object with a
	 * field like "Animal myAnimal", you actually want an editor for either
	 * a dog, cat, or bird object.
	 * </p>
	 * For more information, see
	 * {@link com.smanzana.templateeditor.editor.fields.ChildEditorField} 
	 * </p>
	 * @return A list with exactly one object of each acceptable subtype an
	 * editor is allowed to produce to fill a reference to this type.
	 */
	public List<ISuperclass> getChildTypes();
	
	/**
	 * For an element (or clone) of one of the objects passed with getChildrenTypes(),
	 * return a display name. This name is used by the editor in the selection
	 * window to specify which subtype to create.
	 * @param child
	 * @return
	 */
	public String getChildName(ISuperclass child);
	
	/**
	 * Construct a clone of the current child object.
	 * @return
	 */
	public ISuperclass cloneObject();
	
}
