# TemplateEditor
Java GUI based editor library

## Overview

TemplateEditor is a library that creates dynamic runtime editors and manages the data. The idea is simple: your software supports mid-to-large amounts of structured data be entered by the user. With TemplateEditor, you supply the structure, spawn an editor and display it, and ask for the edited data when the user is done.

TemplateEditor works by mapping fields to types of data (termed FieldData throughout the library). These maps support many different structures including deeply-nested data and recursive structures. For each type of data, TemplateEditor knows how to display that to the user, get input, and convert that back into that sort of data. For primitive data, this is simple; display a text field and do some basic formatting to get back to the primitive type. For more complex types of data -- lists of primitives, maps, enums, subsets, even Java classes -- it gets more complicated.

I am by no means an expert (or even _good_) at UX design. TemplateEditor comes with many different customizable options including a runtime color customization option (and saving/loading color 'schemes') and runtime image/icon swapping. That means despite my lack of ability to create good-looking of behaving UI, you can create something that both looks good and fits your application!

## What It Looks Like



## How To Use It (Simple)

As mentioned above, TemplateEditor works by defining a map between fields (actually, arbitrary keys) to types of data. Creating these maps can be tiresome and error-prone. TemplateEditor goes above and beyond for you and makes this process simple using Java Reflection contained in a class called the ObjectDataLoader. This class allows you to provide a generic Java Object (with special annotations tagging fields) and get out an editor with no extra work!
Creating editors using ObjectDataLoaders makes the process extremely easy. The only work that needs to be done on your part is learning how to use annotations and supply any extra data the ObjectDataLoader needs to create the mappings for you. The ObjectDataLoader even takes care of converting the raw return from TemplateEditor **back into a Java object**!

### Annotations

Annotations are used to mark fields in your Java classes so that ObjectDataLoaders can properly pull out and use data. **If a field is not marked with an appropriate annotation, it is ignored by the ObjectDataLoader.** If any loader annotation is present, the field will be included. It is perfectly acceptable and sometimes neccessary to include multiple tags together (like DataLoaderData(name="") + DataLoaderName)

An enumerated list of annotations follows.

- [DataLoaderData](#DataLoaderData)
- [DataLoaderName](#DataLoaderName)
- [DataLoaderDescription](#DataLoaderDescription)
- [DataLoaderList](#DataLoaderList)
- [DataLoaderFactory](#DataLoaderFactory)
- [DataLoaderRuntimeEnum](#DataLoaderRuntimeEnum)

#### DataLoaderData
The most generic tag. Use on single references or primitive data (e.g. String, MyClassB, int). This tag allows you to specify an alternate name and description of the field (displayed in the editor; defaults to a cleaned-up version of the field name) as well as whether the field should be expanded (Instead of displaying a link to the field, pull out all fields and show them inline. Only makes sense when the field is a reference to a class).

```java
@DataLoaderData(name="Gold", description="Amount of gold")
private int secretObsureGoldName;
```

#### DataLoaderName
Marks this field as the field to display when summarizing the object. This is relevant when the object is embedded in another and a summary is displayed. Only one field should be marked with this tag.

```java
@DataLoaderName
private String displayName;
```

#### DataLoaderDescription
Like DataLoaderName, marks a field to use when a description is requested. This is used as a tooltip in the same place the name is used.

```java
@DataLoaderDescription
private String displayDesc;
```

#### DataLoaderList
Lists may require extra information to be used. In order to use lists, they must be marked with this tag. The tag requires specification of a template field, which is used to stamp values to. This should be the name of a field of the generic type of the list (so MyClass for List<MyClass>). For lists of primitives and Strings, this value is still required but ignored (whoops!).
In addition to a template, a factory is required so that new elements can be created. This is done in one of two ways:
1. By providing the name of a template function in the class with the list OR
2. Marking the generic class with the @DataLoaderFactory annotation (described below)

```java
/********* 1 ************/
@DataLoaderList(templateName="mySecondTemplate", factoryName="templateFactory")
private List<Class2> list2;
protected static Class2 templateFactory() { return new Class2(); }
protected static Class2 mySecondTemplate = templateFactory();

/********* 2 ************/
@DataLoaderList(templateName="myTemplateObject")
private List<MyClass> list1;
private MyClass myTemplateObject = new MyClass();
// MyClass is tagged with @DataLoaderFactory
```

#### DataLoaderFactory
This tag is applied to a class rather than a field. It marks the class as easily included in a list, as it defines a factory
for itself. This allows you to define the factory once for a type and use it in lists all over.
This tag works in one of two ways:
1. The marked class is tagged with the DataLoaderFactory annotation and a factory method is specified OR
2. The marked class has a method called "construct" which takes no arguments and returns its own type

```java
/********* 1 ************/
@DataLoaderFactory("factoryMethod")
public class MyClass {
  protected static MyClass factoryMethod() { return new MyClass(); }
}

/********* 2 ************/
@DataLoaderFactory
public class MyClass {
  protected static MyClass construct() { return new MyClass(); }
}
```

#### DataLoaderRuntimeEnum
This tag marks a field as one which should have one of a set of values. It's like an enum, except the possible values aren't known until runtime. This works well when the user must pick one of some other set of data that isn't known until runtime -- like a list of user-created stuff they create elsewhere in the editor!
In order to use this field, there must be some method to call to get all possible values. This method is specified by either:
1. providing the name of a method to call to get a list* of all the values OR
2. implement the IRuntimeEnumerable interface, which takes a String and returns a list*

> *This is actually a map between a String (display version of the selection) and the actual selection

When using option 1, this is straightforward: Name a different method for each runtime-enumerable field. Option 2, however, *also* allows for multiple runtime-enumerable fields to be present. It does this by taking any key you provide in the tag (e.g. `@DataLoaderRuntimeEnum("key1")`) and giving it as input to the interface function. That way, you can provide different maps based on the key given.

```java
/********* 1 ************/
public class Class1 {
  @DataLoaderRuntimeEnum("method1")
  private Spell primary;
  @DataLoaderRuntimeEnum("method2")
  private Spell secondary;
  
  private Map<String, Spell> method1() {
    /* Returns map:
     * "Fireball" => Fireball spell
     * "Ice Rain" => IceRain spell
     */
    return getPrimarySpells();
  }
  
  private Map<String, Spell> method2() {
    // ...
    return getSecondarySpells();
  }
}

/********* 2 ************/
public class Class2 implements IRuntimeEnumerable<Spell> {
  @DataLoaderRuntimeEnum("primary")
  private Spell primary;
  @DataLoaderRuntimeEnum("secondary")
  private Spell secondary;
  
  public Map<String, Spell> fetchValidValues(String key) {
    if (key.equals("primary")
      return getPrimarySpells();
    else if (key.equals("secondary")
      return getSecondarySpells();
    else
      DoErrorStuff();
  }
}
``` 

### Interfaces
In addition to the tags listed above, ObjectDataLoaders use a handful of interfaces to decide exactly how a type should be editted. Even if marked with one of these interfaces, fields **must be marked with one of the above tags** to even be considered.

- [Superclass](#ISuperclass)
- [CustomData](#ICustomData)

#### Superclass
This interface is used to specify that references to this type should construct elements of one of a list of known subtypes. This works exceptionally well for abstract classes: a reference to the base class doesn't care which subtype it is AND the editor cannot instantiate the abstract base class!

Filling this interface means providing three pieces of data: a list of objects where each object represents one of the child types; A means to associate each child class with a displayable name; the ability to clone arbitrary child elements. All of this comes automatically with the Java interface.

```java
public abstract class Item implements ISuperclass {
  @DataLoaderName
  protected String name;
  @DataLoaderData
  protected int value;
  
  public Item(String name, int value) {
    this.name = name;
    this.value = value;
  }
  
  @Override
  public List<ISuperclass> getChildTypes() {
    List<ISuperclass> list = new LinkedList<>();
    list.add(new Weapon());
    list.add(new Armor());
    list.add(new Potion());
    list.add(new Junk());
    return list;
  }
  
  @Override
  public String getChildName(ISuperclass child) {
    return ((Item) child).name;
  }
  
  // Child elements must implement clone method
}

public class Weapon extends Item {
  @DataLoaderData
  private int damage;
  
  public Weapon() {
    this("sword", 10, 5);
  }
  
  public Weapon(String name, int value, int damage) {
    super(name, value);
    this.damage = damage;
  }
  
  @Override
  public ISuperclass cloneObject() {
    return new Weapon(name, value, damage);
  }
}

// ...
```

#### CustomData
CustomData tells the ObjectDataLoader that this type specifies its own editor to use. That means the ObjectDataLoader will not iterate over its fields and decide what editors to use. This is ideal when you want to provide an editor with functionality not available in TemplateEditor or that just doesn't work in the way you want.

Filling this interface means two things: providing an EditorField to use when references to this type are used and providing a means to convert between the EditorField previously supplied back into an object.

In my example, I will not explain how to create a custom EditorField.

```java
public class Player implements ICustomData {
  private String name;
  private int maxhp;
  private int maxmp;
  private int level;
  
  @Override
  public EditorField<Player> getField() {
    return new PlayerEditorField(this);
  }
  
  @Override
  public ICustomData fillFromField(EditorField<?> field) {
    // Assume PlayerEditorField has a method 'apply(Player p)' which applies
    // it's data to an exiting player field
    ((PlayerEditorField) field).apply(this);
    return this;
  }
}
```

## How To Use It (Advanced)
