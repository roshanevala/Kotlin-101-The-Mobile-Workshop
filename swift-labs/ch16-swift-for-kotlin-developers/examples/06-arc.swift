final class Messenger {
    let text: String
    var read: (() -> String)?
    init(text: String) { self.text = text }
    func connect() {
        read = { [weak self] in self?.text ?? "No messenger" }
    }
}

var messenger: Messenger? = Messenger(text: "Hello")
weak var observer = messenger
messenger?.connect()
let action = messenger?.read
withExtendedLifetime(messenger) { print(action?() ?? "No action") }
messenger = nil
print(observer == nil)
print(action?() ?? "No action")
