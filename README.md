# Instantiator
Tired of manually setup test data of Kotlin data classes or POJOs? Instantiator creates Instances of any class for you so that you can focus on writing tests instead of spending time and effort to set up test data.


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
    val actual = computeFullname(person)
    assertEquals(expected, actual)
}
```
