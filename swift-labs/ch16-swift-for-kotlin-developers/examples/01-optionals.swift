import Foundation

func readMinutes(_ text: String?) -> Int? {
    guard let text else { return nil }
    return Int(text.trimmingCharacters(in: .whitespacesAndNewlines))
}

let inputs: [String?] = [nil, " 25 ", "later"]
for input in inputs {
    if let minutes = readMinutes(input) { print(minutes) }
    else { print("No integer") }
}
