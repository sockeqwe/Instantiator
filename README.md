# Instantiator

Tired of manually setup test data of Kotlin data classes or POJOs? Instantiator creates Instances of any class for you
so that you can focus on writing tests instead of spending time and effort to set up test data.

This is not a mocking library. When saying that Instantiator can create an instance of any class for you, I'm
referring to data classes or POJOs, not mock functionality.

It doesn't use any black magic. It uses reflection and invokes the public primary constructors. If there is no public
primary constructor available, then Instantiator cannot instantiate it.

## Dependencies

```
testImplementation 'com.hannesdorfmann.instantiator:instantiator:0.4.0'
```

or `SNAPSHOT` (directly built from `main` branch):

```
testImplementation 'com.hannesdorfmann.instantiator:instantiator:0.4.1-SNAPSHOT'
```

## Usage

Assuming you have some data structures like this

```kotlin
data class Person(
    val id: String,
    val firstname: String,
    val lastname: String,
    val age: Int,
    val gender: Gender
)

enum class Gender {
    MALE,
    FEMALE,
    UNDEFINED
}

fun computeFullname(p: Person): String {
    return "${p.firstname} ${p.lastname}"
}
```

And you want to write some (unit) tests to test some functionality you often have to manually set up some object
instances just for testing. This is where Instantiator comes in: it creates such instances for you.

```kotlin
@Test
fun someTest() {
    val person: Person =
        instance() // instance() is Instantiator's API. It creates a new instance of Person with random values. 

    // Now that you have some random Person object 
    // you can do with it whatever you want
    val expected = "${person.firstname} ${person.lastname}"
    val fullname = computeFullname(person)
    assertEquals(expected, fullname)
}
```

Bonus:
if you want to get an instance for each subclass of a sealed class hierarchy use `instantiateSealedSubclasses()` like
this:

```kotlin
sealed class Root
data class A(i: Int) : Root()
data class B(i: Int) : Root()


sealed class NestedRoot : Root()
data class N1(i: Int) : NestedRoot()


val subclassesInstances: List<Root> = instantiateSealedSubclasses()
println(subclasses) // contains 3 instances and prints  "A, B, N1"
```

## Supported use cases

Type | Support | Note and default behavior description
--- | --- | ---
`data class` | ✅️ | invokes primary constructor and fills parameter with random values. This works incl. other types: i.e. `data class Foo( id : Int, bar : Bar)`. Instantiator will also instantiate a `Bar` instance to eventually instantiate `Foo`
`class` | ✅️ | works the same as `data class`.
`sealed class` | ✅ | randomly creates an instance by randomly picking a subclass of the sealed class hierarchy and then instantiates this one (meaning what is written in the rows above about support for `data class` or `class` still holds). Additionally, if you want to have a full list of instances of all subclasses of a sealed class hierarchy use `val subclassesInstances : List<SomeSealedClass> = instantiateSealedSubclasses()`.
`sealed interface` | ✅ | works exactly the same way as `sealed class` (see above).
`object` | ✅ | Objects / Singleton are supported and it will return exactly that one object instance that already exists (not instantiate via generics another instance of the same object so having 2 with different memory address).
`interface` | ❌️ | Not supported out of the box because by using reflections there is no straight forward way (apart from class path scanning which is not implemented at the moment) to find out which class implements an interface.
`abstract class`| ❌️ | same reason as for interface (see above).
`enum` | ✅️ | fully supported. It randomly picks one case and returns it.
`List` | ✅️ | `List` and `MutableList` are supported in class constructors. i.e. in instance of AdressBook can be instantiated: `data class AdressBook(val persons : List<Person>)`  but you can also directly request an instance with `val persons : List<Person> = instance()`.
`Set` | ✅️ | `Set` and `MutableSet` are supported in class constructors. i.e. in instance of AdressBook can be instantiated: `data class AdressBook(val persons : Set<Person>)` but you can also directly request an instance with `val persons : Set<Person> = instance()`
`Map` | ✅️ | `Map` and `MutableMap` are supported in class constructors. i.e. in instance of PhoneBook can be instantiated: `data class PhoneBook(val phoneNumbers : Map<Person, PhoneNumber>)` but you can also directly request an instance with `val phoneBook : Map<Person, PhoneNumber>> = instance()`
`Collection` | ✅️ | `Collection` and `MutableCollection` are supported in class constructors. i.e. in instance of AdressBook can be instantiated: `data class AdressBook(val persons : Collection<Person>)`  but you can also directly request an instance with `val persons : Collection<Person>> = instance()`
`Pair` | ✅️ | supported in class constructors and directly in request
`Triple` | ✅️ | supported in class constructors and directly in request
`Int`  | ✅️ | random number is returned
`Long`  | ✅️ | random number is returned
`Float`  | ✅️ | random number is returned
`Double`  | ✅️ | random number is returned
`Short`  | ✅️ | random number is returned
`String`  | ✅️ | random string with default length of 10 characters is returned. Pool of chars that is used to compute random string is `a..z` + `A..Z` + `0..9`.
`Char`  | ✅️ | random char is returned from the following pool of chars: `a..z` + `A..Z` + `0..9`.
`Boolean`  | ✅️ | randomly returns true or false
`Byte` | ✅️ | randomly creates one byte and returns it
`java.util.Date` | ✅️ | randomly creates a `Date`
`java.time.Instant` | ✅️ | randomly creates a `Instant`
`java.time.LocalDateTime` | ✅️ | randomly creates a `LocalDateTime` in a random `ZoneId`
`java.time.LocalDate` | ✅️ | same as `LocalDateTime`
`java.time.LocalTime` | ✅️ | same as `LocalDateTime`
`java.time.ZonedDateTime` | ✅️ | same as `LocalDateTime`
`java.time.OffsetDateTime` | ✅️ | same as `LocalDateTime`
`java.time.OffsetTime` | ✅️ | same as `LocalDateTime`

## Configuration

You can configure Instantiator by passing a `InstantiatorConfig` instance as parameter
to `instance(config : InstantiatorConfig)` or `instantiateSealedSubclasses(config : InstantiatorConfig)`.

Some settings that you can set:

- `InstantiatorConfig.useDefaultArguments`: Set it to `true` if you want that the default arguments of constructor
  parameters are used if provided. For example, given `data class MyClass(val id : Int, val name : String = "Barbra")`,
  if `config.useDefaultArguments = true`
  then the parameter`name` of any instance will be `name="Barbra"` and only `id` which has no default argument set will
  be filled with random value. Default value of the default config is
  that `InstantiatorConfig.useDefaultArguments = true`.
- `InstantiatorConfig.useNull`: Set it to `true` if for constructor parameters that can be `null`,
  `null` is actually the value without even asking an `InstanceFactory` to create an instance. 
  This is a shortcut that ensures always `null`, for example: given `data class MyClass(val id : Int?)`, if `config.useNull = true`
  then instance will look like `MyClass( id = null)`. If `config.useNull = false` then `InstanceFactor` will be called 
  to decide if a null or non-null value is returned. Default value of default config is `InstantiatorConfig.useNull = true`
- `InstantiatorConfig.random`: This random is passed to all `InstanceFactories` and is used by them to randomly create
  values. Pass a seeded `Random`, i.e. `Random(0)`, to ensure always the same random values are generated. 

As you see there are multiple configuration that impact value creation: `config.useDefaultArguments`, `config.useNull` and `InstanceFactory`.
The order and priority internally in Instantiator is the following: 
1. `config.useDefaultArguments = true` is used first. So if set to true, then default values are used.
2. `config.useNull = true` is used as second. So if `useDefaultArguments = false` and `useNull = true` then all optional parameters are filled with `null`.
3. `InstanceFactory` is used as last. So if `useDefaultArguments = false` and `useNull = false` then `InstanceFactory` for the given type then is asked to instantiate a value.

```kotlin
class Foo(val i: Int? = 42)

val foo = instance<Foo>()

println(foo.i) // prints 42
```

## Custom `InstanceFactory`

`InstantiatorConfig` takes as a constructor parameter `vararg factories: InstanceFactory`. 
An `InstanceFactory` is used to create an instance in case an unsupported build-in type needs to be instantiated 
(see supported types table above) or if you want to override how primitive types are instantiated.

The way how Kotlin's reflection and type system work is that there is a difference between i.e `Int` and `Int?` (so null and non-null types).
That is the reason why two different `InstanceFactory` exist:

1. `interface NonNullableInstanceFactory<T> : InstanceFactory`: This factory returns a `non-null` value.
2. `interface NullableInstanceFactory<T> : InstanceFactory`: This factory can return a `non-null` or `null` value. It's up to the instance factory to decide (i.e. by using the Random).

```kotlin
class MyIntInstanceFactory : InstantiatorConfig.NonNullableInstanceFactory<Int> {
    override val type: KType = Int::class.createType()
    override fun createInstance(random: Random): Int = 42
}

class MyDateInstanceFactory : InstantiatorConfig.NullableInstanceFactory<Calendar> {
  override val type: KType = Calendar::class.createType(nullable = true)
  override fun createInstance(random: Random): Date? = if (random.nextBoolean()) null else Date()
}

data class Foo(val i : Int, val date : Date?)

val config = InstantiatorConfig(useNull = false, MyIntInstanceFactory(), MyCalendarInstanceFactory())
val foo = instance<Foo>(config)

println(foo.i) // i == 42
println(foo.date) // can be null or current Date 
```

**Please note that unless you explicitly need a very custom behavior for `null` values of a specific type 
there is no need to create a subclass of `NullableInstanceFactory`.**
Just create a `NonNullableInstanceFactory`, add it to your `InstantiatorConfig` and Instantiator does 
automatically create a `NullableInstanceFactory` out of it under the hood 
(uses `NonNullableInstanceFactory.toNullableInstanceFactory()`).

`InstantiatorConfig` is immutable. You can add an `InstanceFactory` with
the `val newConfig : InstantiatorConfig = existingInstantiatorConfig.add(myCustomFactoy)`



