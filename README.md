# Ogya

Ogya is a set of tools for quick android development. It gives you a consistent way to display dialogs and load lists with only one recycler adapter. The adapter can handle multiple view types. Do more with less.

*   Quick Dialog for dialogs
*   Quick Lists for recycler views
*   Quick Permissions for permissions
 
## Usage
1. Enabled __android data binding__
    ```gradle
    android {
        ...
    
        dataBinding {
            enabled = true
        }
    }
    ```

2. __Add it in your root build.gradle at the end of repositories:__
    ```gradle
    	allprojects {
    		repositories {
    			...
    			maven { url 'https://jitpack.io' }
    		}
    	}
    ```
3. __Add the dependency__    
    ```gradle
    dependencies {
    	        implementation 'com.github.billkainkoom:ogya:0.55'
    	}
    ```



## Quick List (Listable Adapter)
![](https://github.com/billkainkoom/ogya/blob/master/images/listableadapter.jpg)

Quick list simply gives you one method to use for all types of list

## Idealistic way to use Quick List
First create an  Object eg(ListableTypes) in Kotlin like

```kotlin
object ListableTypes {
    val Person = ListableType(R.layout.person)
    val Animal = ListableType(R.layout.animal)
    val Furniture = ListableType(R.layout.furniture)
}
```

__Please note that all layouts used should be data-binding compatible, that is it must be of this form__
```xml
<?xml version="1.0" encoding="utf-8"?>
<layout>
...
</layout>
```

Now let your classes that you wish to display in a list implement __Listable__
eg

```kotlin
data class MyPerson(val name: String = "", val email: String = "", val type: ListableType = ListableTypes.Person) : Listable {
    override fun getListableType(): ListableType? {

        return type
    }
}

data class Animal(val name: String = "", val specie: String = "") : Listable {
    override fun getListableType(): ListableType? {
        return ListableTypes.Animal
    }
}

data class Furniture(val name: String = "", val specie: String = "") : Listable {
    override fun getListableType(): ListableType? {
        return ListableType(R.layout.furniture)
    }
}
```


Well thats it you are almost there...

Assuming this was your data source

```kotlin
val peopleAndThings  = mutableListOf(
                MyPerson(name = "Kwasi Malopo", email = "kwasimalopo@outlook.com"),
                MyPerson(name = "Adwoa Lee", email = "adwoalee@gmail.com", type = ListableTypes.Furniture),
                Animal(name = "Cassava", specie = "Plantae"),
                Animal(name = "Cat", specie = "Felidae"),
                Furniture(name = "Cat", specie = "Felidae")
        )       
```

Go ahead and show your list by calling __loadList__ from __ListableHelper__

```kotlin
ListableHelper.loadList(
                context = context,
                recyclerView = recyclerView,
                listableType = ListableTypes.Person,
                listables = peopleAndThings,
                listableBindingListener = { listable, listableBinding, position ->
                    when (listable) {
                        is MyPerson -> {
                            if (listableBinding is PersonBinding) {
                                listableBinding.name.text = listable.name
                                listableBinding.email.text = listable.email
                            } else if (listableBinding is FurnitureBinding) {
                                listableBinding.image.setImageResource(R.drawable.ic_info_outline_black_24dp)
                                listableBinding.name.text = listable.name
                                listableBinding.specie.text = listable.email
                            }
                        }
                        is Animal -> {
                            if (listableBinding is AnimalBinding) {
                                listableBinding.name.text = listable.name
                                listableBinding.specie.text = listable.specie
                            }
                        }
                        is Furniture -> {
                            if (listableBinding is FurnitureBinding) {
                                listableBinding.image.setImageResource(R.drawable.ic_info_outline_black_24dp)
                                listableBinding.name.text = listable.name
                                listableBinding.specie.text = listable.specie
                            }
                        }
                    }

                },
                listableClickedListener = { listable, listableBinding, position ->
                    when (listable) {
                        is MyPerson -> {
                            Toast.makeText(context, listable.name, Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                layoutManagerType = LayoutManager.Vertical
        )
```

__PersonBinding__ is the DataBinding Class that was generated by Android's Databinding Library for the layout __R.layout.person__
 

### Listable
Listable is an abstract class that all classes that you wish to display in a list should implement.

```kotlin
abstract class Listable(@Transient val identifier: String = "", @Transient val span: Int = 1) {
    abstract fun getListableType(): ListableType?
}
```

The ```identifier``` is used by the diffUtils to find out which objects are the same and those that have changed in the case of an update (When you submit a new List using ```listableAdapter.submitList(list)```).
The ```span``` is used when you want to have dynamic span lengths when using  ```LayoutManager.Grid``` to display your list. This helps you to create a list with items having different spans on different rows.

```@Transient``` : Marks the JVM backing field of the annotated property as transient, meaning that it is not part of the default serialized form of the object.
https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.jvm/-transient/index.html

### ListableType
The listable type is a simple class that tells listable adapter what type of layout to use.
Its defined as

```kotlin
class ListableType(val layout: Int = 0)
```

### Listable Helper
The listableHelper is a set of functions and variables that make the usage of QuickList easier.
The constructor is internal to the module so it cannot be instantiated from your code. 
To display items in a list just call the public function __loadList(...)__ in __ListableHelper__

```kotlin
fun <T : Listable> loadList(context: Context, 
                                recyclerView: RecyclerView, 
                                listables: MutableList<T>, 
                                listableType: ListableType,
                                listableBindingListener: (T, ViewDataBinding, Int) -> Unit = { x, y, z -> },
                                listableClickedListener: (T, ViewDataBinding,Int) -> Unit = { x, y,z -> },
                                layoutManagerType: LayoutManager = LayoutManager.Vertical,
                                stackFromEnd: Boolean = false
    ): ListableAdapter<T>
```
 

 ### Arguments in LoadList  
 | Variable                | Purpose                                                                                                                                                               |
 |-------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------|
 | context                 | context                                                                                                                                                               |
 | recyclerView            | is a reference to your recycler view                                                                                                                                  |
 | listables               | is a reference to your recycler viewis a mutable list of objects that you want to render in a list. All objects in this list should implement the Listable interface. |
 | listableType            | Default type to use when no listableType is not specified on an object                                                                                                |
 | listableBindingListener | An anonymous function that supplies you with a listable its position and a viewDataBinder to display information                                                      |
 | listableClickedListener | An anonymous function that supplies you with a listable its position and a viewDataBinder. It is called when an item on the list is clicked                           |
 | layoutManagerType       | Type of layout manager that ListableAdapter would set to the recyclerView supplied                                                                                    |
 | stackFromEnd            | A boolean which determines whether a recycler view should be stacked from end or not                                                                                  |


__loadList__ returns a __ListableAdapter<T>__ that you can use to add or remove elements from your list

### Methods on  ListableAdapter<T>

| Method                                   | Purpose                                                                            |
|------------------------------------------|------------------------------------------------------------------------------------|
| ```submitList(list: MutableList<T>)```         | Submits list to ListableAdapter -> Which would use diffutils to calculate changes and render results. This method is called in ListableHelper.loadList, so only use it when you want to update your list. It supersedes all other change (eg . Add , Remove , Replace etc ...) calls. |
| ```removeAt(position: Int)```                 | Removes listable at a position                                                     |
| ```addAt(position: Int, listable: T)```        | Add a listable at position                                                         |
| ```replaceAt(position: Int, listable: T)```    | Replace listable at position                                                       |
| ```addAt(position: Int, vararg listable: T)``` | Add listable(s) at position eg listableAdapter<T>.addAt(2,listable1,listable2,...) |
| ```addAt(position: Int, newListables: List)``` | Add listable(s) at position                                                        |
| ```add(newListables: List)```                  | Add listable(s). This would add to bottom of the list                              |
| ```add(listable: T)```                         | Add listable. This would add to bottom of the list                                 |


## Quick Dialog

![](https://github.com/billkainkoom/ogya/blob/master/images/quickdialogs.jpg)

Quick dialog simply gives you multiple consistent variants of dialogs you need in your  Android App.

  - Message Dialog
  - Progress Dialog
  - Alert Dialog
  - Input Dialog
  
### Message Dialog
 A message dialog simply displays an image with one button.

 ```kotlin
  QuickDialog(
                context = this,
                style = QuickDialogType.Message,
                title = "Hello World",
                message = "The quick dialog jumped over the old dialog",
                image = R.drawable.ic_info_outline_black_24dp)
                .overrideButtonNames("OK" ).overrideClicks({ ->
                    Toast.makeText(context, "Clicked on OK", Toast.LENGTH_SHORT).show()
                }).show()
 ```

 ### Progress Dialog
 A progress dialog shows a circular progress in an indeterminate state with or without a button

 ```kotlin
 QuickDialog(
                context = context,
                style = QuickDialogType.Progress,
                title = "Please wait",
                message = "Walking round the world")
                .show()
 ```

 This variant however shows a button so that a user can dismiss the dialog

 ```kotlin
QuickDialog(
                context = context,
                style = QuickDialogType.Progress,
                title = "Please wait",
                message = "Walking round the world")
                .overrideButtonNames("Hide Progress")
                .overrideClicks({ ->
                    Toast.makeText(context, "Clicked on Hide Progress", Toast.LENGTH_SHORT).show()
                }).showPositiveButton()
                .show()
 ```

### Alert Dialog
An alert dialog is used in situations when a user has to make a decision

```kotlin
QuickDialog(
                context = context,
                style = QuickDialogType.Alert,
                title = "Proceed",
                message = "Do you want to take this action")
                .overrideButtonNames("Yes", "No")
                .overrideClicks(positiveClick = { ->
                    Toast.makeText(context, "Yes", Toast.LENGTH_SHORT).show()
                }, negativeClick = { ->
                    Toast.makeText(context, "No", Toast.LENGTH_SHORT).show()
                })
                .show()
 ```

 The overrideClicks appears in three forms

 #### OverrideClicks #1
 ```kotlin
 fun overrideClicks(
            positiveClick: () -> Unit = {},
            negativeClick: () -> Unit = {},
            neutralClick: () -> Unit = {}
    )
 ```

 #### OverrideClicks #2
 ```kotlin
  fun overrideClicks(
            positiveClick: (dismiss: () -> Unit) -> Unit = { d -> },
            negativeClick: (dismiss: () -> Unit) -> Unit = { d -> },
            neutralClick: (dismiss: () -> Unit) -> Unit = { d -> }
    )
 ```
 The variable __d__ is an anonymos function that is passed from the implemetation
 of the overideClicks function. It is the __dismiss__ function in QuickDialog and it helps you dismiss the dialog in the click closure. All overloaded methods with __d__ supplied do not dismiss automatically.

 Lets see an example
 ```kotlin
 QuickDialog(
                context = context,
                style = QuickDialogType.Alert,
                title = "Proceed",
                message = "Do you want to take this action")
                .overrideButtonNames("Yes", "No")
                .overrideClicks(positiveClick = { dismiss ->
                    if (true) {
                        Toast.makeText(context, "Yes", Toast.LENGTH_SHORT).show()
                        dismiss()
                    }
                }, negativeClick = { dismiss ->
                    if (true) {
                        Toast.makeText(context, "No", Toast.LENGTH_SHORT).show()
                        dismiss()
                    }
                })
                .show()
 ```

 If we dont invoke __dismiss__ the Quick dialog wont disappear.

 #### OverrideClicks #3
 ```kotlin
 fun overrideClicks(
            positiveClick: (dismiss: () -> Unit, inputText: String) -> Unit = { d, s -> },
            negativeClick: (dismiss: () -> Unit, inputText: String) -> Unit = { d, s -> },
            neutralClick: (dismiss: () -> Unit, inputText: String) -> Unit = { d, s -> }
    )
 ```

 The __d__ variable is same as the one described above. However the __s__ is text that a user entered in the __WithInput__ variation of the Quick dialog

 lets see an example
 ```kotlin
 QuickDialog(
                context = context,
                style = QuickDialogType.WithInput,
                title = "Verify Code",
                message = "Please verify the SMS code that was sent to you")
                .overrideButtonNames("Verify", "Cancel", "Re-send")
                .overrideClicks(positiveClick = { dismiss, inputText ->
                    if (inputText.length < 3) {
                        Toast.makeText(context, "Please enter a 4 digit code", Toast.LENGTH_SHORT).show()
                    } else if (inputText == "4000") {
                        Toast.makeText(context, "Verified", Toast.LENGTH_SHORT).show()
                        dismiss()
                    } else {
                        Toast.makeText(context, "You entered the wrong code", Toast.LENGTH_SHORT).show()
                    }
                }, negativeClick = { dismiss, inputText ->
                    dismiss()
                }, neutralClick = { dismiss, inputText ->
                    //Your action
                    dismiss()
                })
                .withInputHint("Code")
                .withInputLength(4)
                .withInputType(InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL)
                .showNeutralButton()
                .show()
 ```

## Quick Permissions
In pre-lollipop permission checking was not really an issue. Just add it to your manifest 
and you are good to go. However in the post lollipop era we have to deal with runtime permissions. Quick Permissions makes it easier for you to request permissions at runtime.

![](https://github.com/billkainkoom/ogya/blob/master/images/permissionhelper.jpg)

```kotlin
    val REQUEST_CODE = 100
    var permissionHelper: PermissionHelper? = null
    
    fun d8(context: Context) {
        permissionHelper = PermissionHelper(this, context)
        if (permissionHelper!!.requestPermissions(REQUEST_CODE, Manifest.permission.READ_CONTACTS)) {
            //permissions are granted , if not a call to ask for permission would be triggered
        }
    }
    
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
    
            when (requestCode) {
                REQUEST_CODE -> {
                    val quickObject = QuickObject(0, "Calling is great", "MainActivity wants to read your contacts and send to Google", R.drawable.ic_info_outline_black_24dp, "")
                    permissionHelper!!.handlePermissionRequestResponse(quickObject, requestCode, permissions, grantResults, object : PermissionHelper.PermissionRequestListener {
                        override fun onPermissionRequestResponse(granted: Boolean) {
                            if (granted) {
                                Toast.makeText(context, "Permission Granted", Toast.LENGTH_SHORT).show()
                            }
                        }
                    })
                }
            }
    }
```

# Componentization

In previous versions of Ogya, the **listableBindingListener** looked something like this

```kotlin

listableBindingListener = { listable, listableBinding, position ->
		when (listable) {
			is MyPerson -> {
				if (listableBinding is PersonBinding) {
					listableBinding.name.text = listable.name
					listableBinding.email.text = listable.email
					
				} else  if (listableBinding is FurnitureBinding) {
					listableBinding.image.setImageResource(R.drawable.ic_info_outline_black_24dp)
					listableBinding.name.text = listable.name
					listableBinding.specie.text = listable.email
			}
		}
		is Animal -> {
			if (listableBinding is AnimalBinding) {
				listableBinding.name.text = listable.name
				listableBinding.specie.text = listable.specie
			}
		}
		is Furniture -> {
			if (listableBinding is FurnitureBinding) {
				listableBinding.image.setImageResource(R.drawable.ic_info_outline_black_24dp)
				listableBinding.name.text = listable.name
				listableBinding.specie.text = listable.specie
			}
		}
	}
}
```



This is *quite* ok. But Imagine you had a project where you had to display persons at multiple places. You would need to set the properties over and over again in all places. But with **Componentization**. Its done at one place and the change ripples across your entire project.


## **Componentization Axioms**

 - A component should have its dependencies injected into it
 - A components state is determined by the listable object that’s passed to it.
 
 

## **Origins**

All components are derived from the abstract class **BaseComponent.**

```kotlin
abstract class BaseComponent<V : ViewDataBinding, L : Listable> { 

	abstract fun render(binding: V, listable: L) 
	
	abstract fun listableType(): ListableType  
}
```


It’s a generic abstract class that expects a **ViewDataBinding** Type and a **Listable (any class that extends Listable)** Type. The **ViewDataBinding** files are generated classes created by any layout file which has a root parent of  ```<layout>```.


***Function: Render***

The render method is used to display the component.

***Function: ListableType***

The listableType function returns the listableType of the component.


## **Lets Dive In**

The **Kotlin** language provides as with beautiful structures. One such structure is **object**.

An **object** is a thread-safe singleton class. Our derived components are all **objects.** They have no constructors and they do not keep state. The only state they know of is the state of the **listable.**

```kotlin
object AnimalComponent : BaseComponent<AnimalBinding, Animal>() { 

	override fun render(binding: AnimalBinding, listable: Animal) { 
		binding.name.text = listable.name binding.specie.text = listable.specie 
	} 

	override fun listableType(): ListableType { 
		return ListableTypes.Animal 
	} 
}
```

**Then …**

```kotlin
ListableHelper.loadList( 
	context = context, 
	recyclerView = recyclerView, 
	listableType = ListableTypes.Person, 
	listables = people, 
	listableBindingListener = { listable, listableBinding, position -> 
		when (listable) { 
			is MyPerson -> { 
				MyPersonComponent.render(listableBinding as PersonBinding, listable) 
			} is Animal -> { 
				AnimalComponent.render(listableBinding as AnimalBinding, listable) 
			} is Furniture -> { 
				//old way 
				if (listableBinding is FurnitureBinding) { 						     
					listableBinding.image.setImageResource(R.drawable.ic_info_outline_black_24dp) 
					listableBinding.name.text = listable.name listableBinding.specie.text = listable.specie 
				} 
			}
		}
	},
	listableClickedListener = { listable, listableBinding, position -> 
			when (listable) { 
				is MyPerson -> { 
					Toast.makeText(context, listable.name, Toast.LENGTH_SHORT).show() 
					} 
				} 
			}, 
	layoutManagerType = LayoutManager.Vertical 
)
```


This makes it easy to reuse code. As long as dependencies are provided for render, the components can be recreated with its expected behavior.



**BaseComponents** are found in Ogya : 0.54

```groovy
	dependencies { implementation 'com.github.billkainkoom:ogya:0.54' }
```
