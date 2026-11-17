struct StudySession: Identifiable, Equatable {
    let id: Int
    var topic: String
    let minutes: Int
    var isCompleted: Bool
}
