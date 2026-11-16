protocol Named { var name: String { get } }
struct Topic: Named { let name: String }
struct Reminder: Named { let name: String }

func labels<T: Named>(for values: [T]) -> [String] {
    values.map { $0.name }
}

print(labels(for: [Topic(name: "Swift"), Topic(name: "ARC")]).joined(separator: ", "))
print(labels(for: [Reminder(name: "Recall")]).joined(separator: ", "))
