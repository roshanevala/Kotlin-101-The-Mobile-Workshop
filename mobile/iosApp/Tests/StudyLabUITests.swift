import XCTest
import CoreGraphics

final class StudyLabUITests: XCTestCase {
    @MainActor private func launch(
        waitingFor readyElement: (XCUIApplication) -> XCUIElement = { $0.staticTexts["progress"] }
    ) -> XCUIApplication {
        continueAfterFailure = false
        XCUIDevice.shared.orientation = .portrait
        let app = XCUIApplication()
        app.launch()
        XCTAssertTrue(readyElement(app).waitForExistence(timeout: 8))
        return app
    }

    @MainActor private func open(_ id: Int, in app: XCUIApplication) {
        app.buttons["session-\(id)"].tap()
        XCTAssertTrue(app.textFields["topic-field"].waitForExistence(timeout: 5))
    }

    @MainActor private func replace(_ text: String, in app: XCUIApplication) {
        app.buttons["clear-draft"].tap()
        let field = app.textFields["topic-field"]
        field.tap()
        field.typeText(text)
        let done = app.buttons["dismiss-keyboard"]
        XCTAssertTrue(done.waitForExistence(timeout: 5))
        done.tap()
        XCTAssertEqual(field.value as? String, text)
    }

    @MainActor private func back(in app: XCUIApplication) {
        app.navigationBars.buttons.firstMatch.tap()
        XCTAssertTrue(app.staticTexts["progress"].waitForExistence(timeout: 5))
    }

    @MainActor private func toggleLabeledControl(_ id: String, in app: XCUIApplication) {
        // Tap the visible trailing switch in the row-sized accessibility frame.
        app.switches[id].coordinate(withNormalizedOffset: CGVector(dx: 0.95, dy: 0.5)).tap()
    }

    @MainActor func testInitialListAndProgress() {
        let app = launch()
        XCTAssertEqual(app.staticTexts["progress"].label, "Completed 0 of 2")
        XCTAssertTrue(app.buttons["session-1"].label.contains("Kotlin"))
        XCTAssertTrue(app.buttons["session-2"].label.contains("Swift"))
    }

    @MainActor func testSecondRowOpensItsOwnIdentity() {
        let app = launch(); open(2, in: app)
        XCTAssertTrue(app.navigationBars["Session 2"].exists)
        XCTAssertEqual(app.staticTexts["saved-topic"].label, "Swift")
    }

    @MainActor func testRotationKeepsDetailAndDraft() {
        let app = launch(); open(2, in: app)
        replace("Rotated draft", in: app)
        XCUIDevice.shared.orientation = .landscapeLeft
        XCTAssertTrue(app.textFields["topic-field"].waitForExistence(timeout: 5))
        XCTAssertEqual(app.textFields["topic-field"].value as? String, "Rotated draft")
        XCTAssertTrue(app.navigationBars["Session 2"].exists)
        XCUIDevice.shared.orientation = .portrait
    }

    @MainActor func testSaveTrimsOnlyTheSelectedTopic() {
        let app = launch(); open(2, in: app)
        replace("  Observation  ", in: app)
        app.buttons["save-topic"].tap()
        XCTAssertEqual(app.staticTexts["saved-topic"].label, "Observation")
        XCTAssertEqual(app.textFields["topic-field"].value as? String, "Observation")
        back(in: app)
        XCTAssertTrue(app.buttons["session-1"].label.contains("Kotlin"))
        XCTAssertTrue(app.buttons["session-2"].label.contains("Observation"))
    }

    @MainActor func testCancelReloadsTheOwnersCommittedTopic() {
        let app = launch(); open(1, in: app)
        replace("Temporary", in: app)
        app.buttons["cancel-edits"].tap()
        XCTAssertEqual(app.textFields["topic-field"].value as? String, "Kotlin")
    }

    @MainActor func testUnfinishedDraftSurvivesBackAndReopen() {
        let app = launch(); open(1, in: app)
        replace("Unfinished", in: app)
        back(in: app); open(1, in: app)
        XCTAssertEqual(app.textFields["topic-field"].value as? String, "Unfinished")
        XCTAssertEqual(app.staticTexts["saved-topic"].label, "Kotlin")
    }

    @MainActor func testCompletionIsIndependent() {
        let app = launch()
        app.switches["complete-1"].tap()
        XCTAssertEqual(app.staticTexts["progress"].label, "Completed 1 of 2")
        XCTAssertEqual(app.switches["complete-1"].value as? String, "1")
        XCTAssertEqual(app.switches["complete-2"].value as? String, "0")
    }

    @MainActor func testFilterChangesVisibilityWithoutChangingTotal() {
        let app = launch()
        app.switches["complete-1"].tap()
        toggleLabeledControl("include-completed", in: app)
        XCTAssertEqual(app.switches["include-completed"].value as? String, "0")
        XCTAssertFalse(app.buttons["session-1"].exists)
        XCTAssertTrue(app.buttons["session-2"].exists)
        XCTAssertEqual(app.staticTexts["progress"].label, "Completed 1 of 2")
    }

    @MainActor func testParentReconstructionRetainsStore() {
        let app = launch()
        app.switches["complete-1"].tap()
        toggleLabeledControl("show-tip", in: app)
        XCTAssertEqual(app.switches["show-tip"].value as? String, "1")
        XCTAssertTrue(app.staticTexts["study-tip"].exists)
        XCTAssertEqual(app.staticTexts["progress"].label, "Completed 1 of 2")
    }

    @MainActor func testBackgroundForegroundKeepsDraftAndCommit() {
        let app = launch(); open(2, in: app)
        replace("Saved Swift", in: app); app.buttons["save-topic"].tap()
        replace("Unfinished Swift", in: app)
        XCUIDevice.shared.press(.home)
        XCTAssertTrue(app.wait(for: .runningBackground, timeout: 3) || app.state == .runningBackgroundSuspended)
        app.activate()
        XCTAssertTrue(app.textFields["topic-field"].waitForExistence(timeout: 5))
        XCTAssertEqual(app.textFields["topic-field"].value as? String, "Unfinished Swift")
        XCTAssertEqual(app.staticTexts["saved-topic"].label, "Saved Swift")
    }

    @MainActor func testNewProcessStartsWithFreshInMemoryData() {
        let app = launch(); open(1, in: app)
        replace("Temporary saved", in: app); app.buttons["save-topic"].tap()
        app.terminate(); app.launch()
        XCTAssertTrue(app.staticTexts["progress"].waitForExistence(timeout: 5))
        XCTAssertTrue(app.buttons["session-1"].label.contains("Kotlin"))
        XCTAssertEqual(app.staticTexts["progress"].label, "Completed 0 of 2")
    }

    @MainActor func testDuplicateTopicsStillHaveDistinctNavigationIds() {
        let app = launch(); open(2, in: app)
        replace("Kotlin", in: app); app.buttons["save-topic"].tap(); back(in: app)
        XCTAssertTrue(app.buttons["session-1"].exists)
        XCTAssertTrue(app.buttons["session-2"].exists)
        open(2, in: app)
        XCTAssertTrue(app.navigationBars["Session 2"].exists)
        XCTAssertEqual(app.staticTexts["saved-topic"].label, "Kotlin")
    }

    @MainActor func testBlankDraftDisablesSaveButCancelWorks() {
        let app = launch(); open(1, in: app)
        replace(" ", in: app)
        XCTAssertFalse(app.buttons["save-topic"].isEnabled)
        app.buttons["cancel-edits"].tap()
        XCTAssertEqual(app.textFields["topic-field"].value as? String, "Kotlin")
    }
}

extension StudyLabUITests {
    @MainActor func testNativeDraftSurvivesClosingPlanner() {
        checkPlannerReopening(design: "Native")
    }

    @MainActor func testSharedDraftSurvivesClosingPlanner() {
        checkPlannerReopening(design: "Shared")
    }

    @MainActor private func checkPlannerReopening(design: String) {
        let app = launch { $0.buttons["open-plan"] }
        app.buttons["open-plan"].tap()
        let option = app.segmentedControls["draft-design"].buttons[design]
        XCTAssertTrue(option.waitForExistence(timeout: 5))
        option.tap()
        let topic = app.textFields["plan-topic"]
        let minutes = app.textFields["plan-minutes"]
        topic.tap()
        topic.typeText("Loops")
        minutes.tap()
        minutes.typeText("025")
        app.buttons["plan-submit"].tap()
        XCTAssertTrue(app.staticTexts["plan-summary"].waitForExistence(timeout: 5))
        XCTAssertEqual(app.staticTexts["plan-summary"].label, "Loops · 25 min")
        app.buttons["close-plan"].tap()

        app.buttons["open-plan"].tap()
        XCTAssertTrue(topic.waitForExistence(timeout: 5))
        XCTAssertTrue(option.isSelected)
        XCTAssertEqual(topic.value as? String, "Loops")
        XCTAssertEqual(minutes.value as? String, "025")
        XCTAssertFalse(app.staticTexts["plan-summary"].exists)
        XCTAssertFalse(app.buttons["confirm-plan"].exists)
        let clear = app.buttons["plan-clear"]
        app.buttons["plan-submit"].tap()
        XCTAssertTrue(app.staticTexts["plan-summary"].waitForExistence(timeout: 5))
        let otherDesign = design == "Native" ? "Shared" : "Native"
        app.segmentedControls["draft-design"].buttons[otherDesign].tap()
        XCTAssertFalse(clear.isEnabled)
        XCTAssertFalse(app.staticTexts["plan-summary"].exists)
        option.tap()
        XCTAssertEqual(topic.value as? String, "Loops")
        XCTAssertEqual(minutes.value as? String, "025")
        XCTAssertFalse(app.staticTexts["plan-summary"].exists)
        clear.tap()
        XCTAssertFalse(clear.isEnabled)
    }

    @MainActor func testSharedPlanRejectsDurationThenDisplaysKotlinSession() {
        let app = launch()
        app.buttons["open-plan"].tap()
        let minutes = app.textFields["plan-minutes"]
        XCTAssertTrue(minutes.waitForExistence(timeout: 5))
        minutes.tap()
        minutes.typeText("121")
        app.buttons["plan-submit"].tap()
        XCTAssertTrue(app.staticTexts["plan-duration-error"].waitForExistence(timeout: 5))

        minutes.tap()
        minutes.typeText(XCUIKeyboardKey.delete.rawValue)
        app.buttons["plan-submit"].tap()
        let summary = app.staticTexts["plan-summary"]
        XCTAssertTrue(summary.waitForExistence(timeout: 5))
        XCTAssertEqual(summary.label, "Untitled · 12 min")
        app.buttons["confirm-plan"].tap()
        let receipt = app.staticTexts["plan-receipt"]
        XCTAssertTrue(receipt.waitForExistence(timeout: 5))
        XCTAssertNotNil(UUID(uuidString: String(receipt.label.dropFirst("Confirmed plan ".count))))
        app.buttons["close-plan"].tap()
        XCTAssertTrue(app.staticTexts["progress"].waitForExistence(timeout: 5))
    }

    @MainActor func testReviewFailureRetryAndReturnToSessions() {
        let app = launch()
        app.buttons["open-review"].tap()
        XCTAssertTrue(app.staticTexts["review-idle"].waitForExistence(timeout: 5))
        app.buttons["review-swift"].tap()
        XCTAssertTrue(app.staticTexts["review-failed"].waitForExistence(timeout: 5))
        app.buttons["review-retry"].tap()
        let prompt = app.staticTexts["review-ready"]
        XCTAssertTrue(prompt.waitForExistence(timeout: 5))
        XCTAssertEqual(prompt.label, "How does a Swift actor protect its state?")
        app.buttons["close-review"].tap()
        XCTAssertTrue(app.staticTexts["progress"].waitForExistence(timeout: 5))
    }
}
