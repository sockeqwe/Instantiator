# Instantiator
Tired of manually setup test data of Kotlin data classes or POJOs? Instantiator creates Instances of any class for you so that you can focus on writing tests instead of spending time and effort to set up test data.

This is not a mocking library. 
When talking about Instantiator can create an instance of any class for you, I'm referring on data class or POJOs, not mock functionality.

It doesn't use any black magic. 
It uses reflection and invokes the public primary constructors. 
If there is no public primary constructor available, then Instantiator cannot instantiate it.

## Usage
Assuming you have some data structures like this

```kotlin
data class Person(
    val id : String,
    val firstname : String, 
    val lastname : String, 
    val age : Int,
    val gender: Gender
)

enum class Gender {
    MALE, 
    FEMALE, 
    UNDEFINED
}

fun computeFullname(p : Person) : String {
    return "${p.firstname} ${p.lastname}"
}
```

And you want to write some (unit) tests to test some functionality you often have to manually set up some object instances just for testing. 
This is where Instantiator comes in: it creates such instances for you.

```kotlin
@Test
fun someTest(){
    val person : Person = instance() // instance() is Instantiator's API. It creates a new instance of Person with random values. 

    // Now that you have some random Person object 
    // you can do with it whatever you want
    val expected = "${person.firstname} ${person.lastname}"
    val fullname = computeFullname(person)
    assertEquals(expected, fullname)
}
```


## Supported use cases
Type | Support | Note and default behavior description
--- | --- | ---
`data class` | ✅️ | invokes primary constructor and fills parameter with random values. This works incl. other types: i.e. `data class Foo( id : Int, bar : Bar)`. Instantiator will also instantiate a `Bar` instance to eventually instantiate `Foo`
`class` | ✅️ | works the same as `data class`.
`sealed class` | ✅ | randomly creates an instance by randomly picking a subclass of the sealed class hierarchy and then instantiates this one (meaning what is written in the rows above about support for `data class` or `class` still holds).
`object` | ✅ | Objects / Singleton are supported and it will return exactly that one object instance that already exists (not instantiate via generics another instance of the same object so having 2 with different memory address).
`interface` | ❌️ | Not supported out of the box because by using reflections there is no straight forward way (apart from class path scanning which is not implemented at the moment) to find out which class implements an interface.
`abstract class`| ❌️ | same reason as for interface (see above).
`enum` | ✅️ | fully supported. It randomly picks one case and returns it.





