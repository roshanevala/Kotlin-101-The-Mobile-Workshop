enum Lookup {
    case found(String)
    case missing(Int)
}

func describe(_ result: Lookup) -> String {
    switch result {
    case .found(let topic): return "Topic: \(topic)"
    case .missing(let id): return "Missing: \(id)"
    }
}

print(describe(.found("Swift")))
print(describe(.missing(9)))
