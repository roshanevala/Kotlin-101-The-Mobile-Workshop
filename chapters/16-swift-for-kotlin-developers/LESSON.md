# Lesson 16 Swift for Kotlin developers

The study plan looks familiar in Swift. The important question is what happens when you assign it to another variable. Does a later edit change the original? Then ask a different question: if a class stores a callback that refers to itself, which reference permits that instance to be released?

Start after [Chapter 15](../15-android-app-architecture/LESSON.md). This chapter introduces Swift syntax before its [repair mission](WORKSHOP.md). It runs on the Mac command line, without creating an iOS application. Follow [Swift setup](../../swift-labs/README.md) when ready to run the examples.

## Translate small expressions first

| Familiar Kotlin idea | Swift spelling | Read it as |
| --- | --- | --- |
| `val topic = "Swift"` | `let topic = "Swift"` | A binding you cannot reassign |
| `var minutes: Int = 25` | `var minutes: Int = 25` | A variable with an explicit type |
| `fun double(n: Int): Int` | `func double(_ n: Int) -> Int` | A function with a result type |
| `"$minutes min"` | `"\(minutes) min"` | String interpolation |
| `String?` / `null` | `String?` / `nil` | A possibly absent string |
| `List<Int>` | `[Int]` | An array of integers here |
| `when` over a sealed result | `switch` over an enum | Handle each possible case |
| An interface requirement | A protocol requirement | A capability a type provides |

Swift infers types from initial values. A function's `_` omits the external argument label, so `double(25)` calls the signature above. A parameter such as `for values: [T]` has an external label and an internal name: `labels(for: values)` uses the label at the call site. Swift uses `->` before a return type and `return` in a multi-statement body. [Swift basics](https://docs.swift.org/swift-book/documentation/the-swift-programming-language/thebasics/).

The six complete programs linked below are separate executables. Run all with `python3 lab.py examples` from the chapter lab directory. Their small local types are independent; they are not extra app modules. Later code excerpts in the lesson illustrate declarations rather than separate entry points.

## Unwrap an optional before using its value

`Int(text)` can fail and returns `Int?`. A missing value stays explicit rather than becoming an invented zero. In [Example 1](../../swift-labs/ch16-swift-for-kotlin-developers/examples/01-optionals.swift), `guard let text else { return nil }` exits when its optional argument is absent. Inside the remaining body, `text` is a usable String. `if let minutes = ...` similarly unwraps a value inside a branch.

```text
No integer
25
No integer
```

`??` supplies a fallback; optional chaining such as `topic?.trimmingCharacters(...)` invokes a member only when a value exists. The Foundation import supplies the string-trimming API used here. A force unwrap with `!` assumes presence and can trap if the value is nil; this mission needs no force unwrap.

Parsing and business validation remain separate. `"0"` and `"-1"` describe representable integers, while the mission's duration rule accepts only 1...180. Text outside the representable Int range fails conversion. These distinctions reuse the input-boundary reasoning from Kotlin Chapter 03.

## Give models value semantics deliberately

Swift structs and enums are value types. Assigning a plan built from structs, arrays, integers, and strings gives the second plan its own value behavior. In [Example 2](../../swift-labs/ch16-swift-for-kotlin-developers/examples/02-values.swift), changing a copied session's minutes prints:

```text
25 40
40 40
1
```

The first line compares original/copy values. The second uses a struct containing a class reference: both copies still refer to the same `MinuteBox` instance. Copying a struct does not clone the objects referenced by its properties. Array storage can be shared internally until mutation; the observable value contract matters more than assuming every value lives on the stack. [Structures and classes](https://docs.swift.org/swift-book/documentation/the-swift-programming-language/classesandstructures/).

The last line comes from a small Counter struct. A method that changes its struct value is marked `mutating`; its caller needs a `var` binding. A `let` struct cannot have its stored properties changed. A `let` class binding keeps the reference fixed while writable properties of that instance can still change, much like a Kotlin `val` referring to a mutable object. [Mutating methods](https://docs.swift.org/swift-book/documentation/the-swift-programming-language/methods/).

A struct can receive a memberwise initializer automatically. `StudySession(id: ..., topic: ..., minutes: ..., isCompleted: false)` supplies its stored values. A class with non-default stored properties needs an initializer that assigns them. `final class` prevents subclassing; it still has reference semantics.

`Equatable` lets values be compared with `==` and `!=`. For the mission's structs/enums, Swift can synthesize that conformance from Equatable stored/associated values. Class identity is a separate question expressed with `===`; the plan contract asks for independent values rather than a shared class instance.

## Put alternatives and their data in an enum

[Example 3](../../swift-labs/ch16-swift-for-kotlin-developers/examples/03-enums.swift) declares a `Lookup` enum whose found case carries a string and missing case carries an integer. These are **associated values**, stored with the chosen case. They are different from fixed raw values assigned to cases.

```text
Topic: Swift
Missing: 9
```

`case .found(let topic)` binds the associated value in a switch branch. Switching over the local enum must cover every case; a default branch is unnecessary when all cases are named. Context permits the shorter `.found(...)` spelling instead of repeating `Lookup.found(...)`. [Enumerations](https://docs.swift.org/swift-book/documentation/the-swift-programming-language/enumerations/).

The mission uses this pattern for `SessionResult.valid(StudySession)` and `.invalid(ValidationIssue)`. A caller must inspect the result before using its session. The validation factory is the input boundary; directly constructing a stored-value struct does not automatically execute that factory.

## State a capability, then use it generically

A protocol such as `TopicNamed` declares `var topic: String { get }`. A conforming type must provide readable topic text; a `let` stored property can satisfy that requirement. Structs, classes, and enums can conform to protocols. [Protocols](https://docs.swift.org/swift-book/documentation/the-swift-programming-language/protocols/).

In [Example 4](../../swift-labs/ch16-swift-for-kotlin-developers/examples/04-protocols-and-generics.swift), `labels<T: Named>(for values: [T])` accepts an array whose element type provides `name`. It works for both Topic and Reminder arrays and prints:

```text
Swift, ARC
Recall
```

The constraint lets the function read the capability without knowing either concrete model. The mission's `topicNames<T: TopicNamed>` uses the same idea. It preserves input order and duplicates; it does not sort or deduplicate. This bounded generic is enough here; associated types and existential-type design can wait. [Generics](https://docs.swift.org/swift-book/documentation/the-swift-programming-language/generics/).

## Read collection closures and mutation explicitly

[Example 5](../../swift-labs/ch16-swift-for-kotlin-developers/examples/05-closures.swift) passes a closure to map, then filters/reduces minutes:

```text
[20, 40, 60]
50
```

`{ value in value * 2 }` names a closure parameter; `$0` is shorthand for the first argument, and `$1` the second. A trailing closure can appear after the call's parentheses. `filter` and `map` produce values; `reduce` accumulates a result. A computed property such as `completedMinutes` uses a body to calculate its value when read. [Closures](https://docs.swift.org/swift-book/documentation/the-swift-programming-language/closures/).

For a specific-record edit, `firstIndex(where:)` returns an optional array index. Unwrap it before subscripting. Update the first matching record and preserve the others. The exercise defines a repeat/absent match as a no-op returning false; success means one incomplete record became complete. That Boolean is a product contract, not a Swift rule.

## Trace ARC ownership around a stored closure

Swift uses ARC to manage class-instance lifetimes. A strong reference keeps an instance alive. A stored closure can strongly capture the instance that stores it, leaving a cycle even after an outside owner releases its reference. Reference counting requires a useful ownership design for that relationship. [Automatic reference counting](https://docs.swift.org/swift-book/documentation/the-swift-programming-language/automaticreferencecounting/).

```mermaid
flowchart LR
    O[Outside owner] -->|strong reference| C[Coach instance]
    C -->|owns callback| F[Stored closure]
    F -.->|weak capture after repair| C
```

Before repair, the callback's return edge is strong. In [Example 6](../../swift-labs/ch16-swift-for-kotlin-developers/examples/06-arc.swift), a capture list `[weak self]` makes that edge non-owning. A weak reference is an optional variable that becomes nil when the instance is released. The closure handles absence with optional chaining and a fallback, so a caller may keep and invoke the callback afterward.

```text
Hello
true
No messenger
```

The example uses `withExtendedLifetime` to keep the owner alive during the first callback check, then explicitly sets its optional owner to nil. These are controlled ownership observations, rather than guesses about the end of a lexical scope. The mission's checks also run under optimization. `unowned` requires a valid lifetime assumption; a callback explicitly allowed to outlive its coach cannot rely on that assumption. Capturing weakly is an ownership choice, not a rule for every closure.

## Separate language checks from the future UI client

The fixed runner compiles the selected model source and a `main.swift` entry point with `xcrun swiftc -swift-version 6`. It uses no SwiftPM dependencies or test framework. Its acceptance checks evaluate conditions, report failures, and return a failing status even in optimized builds. Swift Testing, XCTest, tasks/actors, and UI lifecycle ownership arrive in later chapters.

Before the mission, explain: how can a struct still contain shared reference state? Why does changing a plan from class to struct require a mutating method and a variable caller? What does a weak callback return after its owner is gone?

**[Start the workshop](WORKSHOP.md)**
