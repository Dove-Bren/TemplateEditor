# TemplateEditor
Java GUI based editor library

Table Of Contents
=================
1. [Overview](#overview)
2. [What It Looks Like](#what-it-looks-like)
3. [Basics Of Use](#basics-of-use)
    - [The Color Picker](#the-color-picker)
    - [The Icon Registry](#the-icon-registry)
4. [Integration With ObjectDataLoader (Simple)](#integration-with-objectdataloader-simple)
    1. [Annotations](#annotations)
        - [DataLoaderData](#dataloaderdata)
        - [DataLoaderName](#dataloadername)
        - [DataLoaderDescription](#dataloaderdescription)
        - [DataLoaderList](#dataloaderlist)
        - [DataLoaderFactory](#dataloaderfactory)
        - [DataLoaderRuntimeEnum](#dataloaderruntimeenum)
    2. [Interfaces](#interfaces)
        - [Superclass](#isuperclass)
        - [CustomData](#icustomdata)
5. [Integration With Maps (Advanced)](#integration-with-maps-advanced)
    - [FieldData Overview](#fielddata-overview)
    - [SimpleFieldData](#simplefielddata)
    - [EnumFieldData](#enumfielddata)
    - [ReferenceFieldData](#referencefielddata)
    - [SubsetFieldData](#subsetfielddata)
    - [ComplexFieldData](#complexfielddata)
    - [SubclassFieldData](#subclassfielddata)
    - [MapFieldData](#mapfielddata)
    - [CustomFieldData](#customfielddata)

## Overview

TemplateEditor is a library that creates dynamic runtime editors and manages the data. The idea is simple: your software supports mid-to-large amounts of structured data be entered by the user. With TemplateEditor, you supply the structure, spawn an editor and display it, and ask for the edited data when the user is done.

TemplateEditor works by mapping fields to types of data (termed FieldData throughout the library). These maps support many different structures including deeply-nested data and recursive structures. For each type of data, TemplateEditor knows how to display that to the user, get input, and convert that back into that sort of data. For primitive data, this is simple; display a text field and do some basic formatting to get back to the primitive type. For more complex types of data -- lists of primitives, maps, enums, subsets, even Java classes -- it gets more complicated.

I am by no means an expert (or even _good_) at UX design. TemplateEditor comes with many different customizable options including a runtime color customization option (and saving/loading color 'schemes') and runtime image/icon swapping. That means despite my lack of ability to create good-looking of behaving UI, you can create something that both looks good and fits your application!

## What It Looks Like

## Basics Of Use
To use TemplateEditor, you first need to obtain a build (.jar) and include it in your project. The built jar should contain sources, so you should be able to reference the actual source when needed. There is plenty of header comments that should help.

After you've got your project set up, you use TemplateEditor by creating an editor and displaying it. Editors return Java Swing components (JPanels in all current implementations). As described below, editors are created either using ObjectDataLoaders (where you simply provide a marked-up Java object to edit) or FieldData maps. After constructing the editor and fetching it's component, it should be added to a visible Java swing frame and made visible.

The editor does not come with a 'done' button. That's entirely up to your side of the UI. Instead the editor is dumb to changes. Besides notifying the registered owner that data has changed, it does no collection or formation of data until queried. This means querying is a somewhat heavy operation, so use it wisely!

Besides creating and displaying editors, TemplateEditor interacts in two other ways: the color picker and the icon registery. Both are singleton classes.

### The Color Picker
All TemplateEditor panels and components are hooked up to a color callback agent called UIColor. This automatically updates components with colors as they are changed. UIColor *also* has the ability to generate a JMenuItem that can be added to a JMenu. When the item is clicked, a modal color-picker dialog appears and allows users to change colors on the fly. The modal window also allows users to save and load schemes at will. It is entirely optional that this be provided to the user.

### The Icon Registry
A small handful of icons are used by the TemplateEditor in various editor components. These are static and do not change color. They don't change size, border, etc., and might not work with your theme. This story has a happy ending, however, as all icons used by TemplateEditor can be overriden easily using the EditorIconRegistry class. The *register* method takes an icon key and sets the corresponding icon.

## Integration With ObjectDataLoader (Simple)
As mentioned above, TemplateEditor works by defining a map between fields (actually, arbitrary keys) to types of data. Creating these maps can be tiresome and error-prone. TemplateEditor goes above and beyond for you and makes this process simple using Java Reflection contained in a class called the ObjectDataLoader. This class allows you to provide a generic Java Object (with special annotations tagging fields) and get out an editor with no extra work!
Creating editors using ObjectDataLoaders makes the process extremely easy. The only work that needs to be done on your part is learning how to use annotations and supply any extra data the ObjectDataLoader needs to create the mappings for you. The ObjectDataLoader even takes care of converting the raw return from TemplateEditor **back into a Java object**!

### Annotations

Annotations are used to mark fields in your Java classes so that ObjectDataLoaders can properly pull out and use data. **If a field is not marked with an appropriate annotation, it is ignored by the ObjectDataLoader.** If any loader annotation is present, the field will be included. It is perfectly acceptable and sometimes neccessary to include multiple tags together (like DataLoaderData(name="") + DataLoaderName)

An enumerated list of annotations follows.

- [DataLoaderData](#dataloaderdata)
- [DataLoaderName](#dataloadername)
- [DataLoaderDescription](#dataloaderdescription)
- [DataLoaderList](#dataloaderlist)
- [DataLoaderFactory](#dataloaderfactory)
- [DataLoaderRuntimeEnum](#dataloaderruntimeenum)

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

- [Superclass](#isuperclass)
- [CustomData](#icustomdata)

#### ISuperclass
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

#### ICustomData
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

## Integration With Maps (Advanced)
TemplateEditor was originally built to accept lists of data types. It would take these lists, produce fields for each, and pass that information back when asked for it. To make it work a little better, this evolved into maps of data types, where keys were some sort of element the map provider could use to pull out specific pieces of data. A simple implementation could be a map where the key was the name of the member field.

Under the hood, this is what the ObjectDataLoader does except with integers: it constructs a map between fields and data types and then provides it to TemplateEditor. When it comes time to reconstruct an object, it recieves the data in a map and uses an internal mapping between integer keys to fields in an object.

If ObjectDataLoader does not perform as expected or preferred, it's entirely possible to construct and deconstruct these maps yourself. Doing so does not require learning the DataLoader annotations or interfaces and instead requires knowledge of the different data types -- called FieldData. The flow would then look like:
1. Construct a map of data, with some series of meaningful keys
2. Construct an editor based on this map of data
3. After editting is finished, query the editor for an up-to-date map of data
4. Use the map from 3 to set fields as appropriate

This process is very straight-forward for pieces of data without nested data. When nested references are allowed, this becomes much harder and probably includes some manner of recursion. Optimal map construction also means defining names and descriptions for each field which may in itself become cumbersome.

Like mentioned above, the only thing you really need to learn to construct a data map is all the different types of FieldData. Each type (including an overview of the FieldData abstract class) is explained below:

- [FieldData Overview](#fielddata-overview)
- [SimpleFieldData](#simplefielddata)
- [EnumFieldData](#enumfielddata)
- [ReferenceFieldData](#referencefielddata)
- [SubsetFieldData](#subsetfielddata)
- [ComplexFieldData](#complexfielddata)
- [SubclassFieldData](#subclassfielddata)
- [MapFieldData](#mapfielddata)
- [CustomFieldData](#customfielddata)

### FieldData Overview
FieldData acts both to specify what type of data is being used (e.g. for figuring out what sort of editor element to use) as well as a container for the data. Many of the types of FieldData have multiple modes they can operate in (such as Complex which can also be a list of Complex elements). Do help, the FieldData abstract class comes pre-baked with lots of static helper functions that make it easy to find which FieldData is right for the right situation.

FieldData supports some small amount of metadata including a name and description of the data. This is used when displaying the field in the editor, where it gets a label and optionally a tooltip description. Descriptions support single string descriptions as well as lists of strings (one displayed per line).

FieldData are one-per-editor-field. Each element that takes input is exactly one field data.

### SimpleFieldData
SimpleFieldData designates some form of primitive (or list of primitive). These include ints (not longs), doubles (not floats), booleans, and Strings. It also supports lists of ints, doubles, and Strings (not booleans). SimpleFieldData translates into text fields that use a filter to match valid strings.

SimpleFieldData behaves extremely simply; it gets a primitive value, creates an editor with that value to begin with, and polls the text field for it's converted value when it's queried. Not all FieldData types are this easy!

### EnumFieldData
EnumFieldData give a drop-down menu for selecting an option -- where each option that can be selected is a member of an enum. It is incredibly straight-forward to use. EnumFieldData support selecting 1 element. To select a subset, see the [SubsetFieldData](#subsetfielddata).

EnumFieldDatas are also very straight-foward; you supply an enum and the current value. It ends up returning one value of that enum.

### ReferenceFieldData
This type is exactly like EnumFieldData except it is not bound to an enum. In other words, this lets you select one of a range of options. Unlike EnumFieldData, this set of options is determined at runtime instead of compile-time.

This is exactly what the [RuntimeEnumerableData](#runtimeenumerabledata) tag translates to.

While not as type-safe as the EnumFieldData, the logic here is similarly simple: you provide a list of possible elements and a current value. It ends up returning one of the elements of the list or the starting value.

### SubsetFieldData
Like EnumFieldData or ReferenceFieldData, this FieldData type allows selection from a limited set of items. Unlike the former, this FieldData supports selection of 0+ elements rather than exactly 1. In other words, it allows the user to select a subset of the options.

The data here is like ReferenceFieldData, except you get a collection of elements instead of a single one. This is the last super-simple FieldData type!

### ComplexFieldData
ComplexFieldData is basically a nested type. To illustrate, note that ComplexFieldData's data is actaully just a map between some key and FieldDatas. It's just a nested map. ComplexFieldData is used by the ObjectDataLoader when dealing with nested classes.

Using ComplexFieldData is as easy (or hard!) as making another nested map. When displayed in the editor, ComplexFieldData is displayed as a preview window with an 'inspect' button. Upon 'inpecting' the data, a modal editor is launched to edit the nested object.

ComplexFieldData also take a special formatter to take the current version of the map and produce a meaningful name and description -- where the name is displayed in the collapsed preview window.

ComplexFieldData can also operate as a list. In this mode, you supply a template map which is used to construct and operate the modal editor while also supplying (and getting back) a list of maps, where each map represents one object in the list.

### SubclassFieldData
This FieldData is used when an piece of data can be constructed in multiple ways. The easiest example of use (and also the one it's named for) is when referring to abstract parent classes. Instead of making an editor for the parent class, you'd probably rather choose one of the child concrete classes to edit/construct. 

To use this FieldData type, you supply a list of all subtypes, and a map between each one of those subtypes and *another map* which is used to construct an editor for that type. As an example, consider the following:

```
SubTypes = [Dog, Bird, Fish]
Map = {
  Dog => {
    "name" => SimpleFieldData(String type),
    "bark volume" => SimpleFieldData(int type),
    "collar color" => EnumFieldData(Color enum)
  },
  Bird => {
    "name" => SimpleFieldData(String type),
    "can speak" => SimpleFieldData(boolean type),
    "phrases" => SimpleFieldData(String list type)
  },
  Fish => {
    "name" => SimpleFieldData(String type),
    "number of fins" => SimpleFieldData(int type),
    "length" => SimpleFieldData(double type)
  }
}
```
  
  
In addition to the list and map of types and submaps, SubclassFieldDatas also take a factory which they use to construct elements of each type to build. Optionally (and recommended), SubclassFieldData also takes a 'resolver' which simply takes an element of the list and returns its type.

### MapFieldData
Creating a MapFieldData boils down to providing a map. That's it. MapFieldData support editting maps from arbitrary keys to FieldData. That means creating a MapFieldData to edit a field likely means doing a little work on your end to convert the elements into FieldData first. 

Maps editting using MapFieldData support null values in two ways: existing mappings can be nulled out (e.g. the key maps to null) AND existing mappings to null can be given actual data to map to. In order for the second part to work, a template object must be given. The template is cloned to produce a FieldData for the new mapping.

It is not required that all FieldData be the same type. Know, however, that users can only add/create data types of the template FieldData. Even if your map maps to all sorts of different FieldData, new elements will only be of the same type as the template.

### CustomFieldData
The most versitile and the simplest (besides SimpleFieldData, of course!) is CustomFieldData, which is a single or list of [ICustomData](#icustomdata). These are objects which specify their own custom editor fields.

Creation is simple; provide either
1. a ICustomData to edit OR
2. a list of ICustomData as your data and a single ICustomData as a template
