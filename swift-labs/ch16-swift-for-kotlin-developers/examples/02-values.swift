struct Session { var minutes: Int }
struct Plan { var sessions: [Session] }

let original = Plan(sessions: [Session(minutes: 25)])
var copy = original
copy.sessions[0].minutes = 40
print("\(original.sessions[0].minutes) \(copy.sessions[0].minutes)")

final class MinuteBox {
    var minutes: Int
    init(minutes: Int) { self.minutes = minutes }
}
struct SharedPlan { let box: MinuteBox }
let shared = SharedPlan(box: MinuteBox(minutes: 25))
let alias = shared
alias.box.minutes = 40
print("\(shared.box.minutes) \(alias.box.minutes)")

struct Counter {
    var value = 0
    mutating func increment() { value += 1 }
}
var counter = Counter()
counter.increment()
print(counter.value)
