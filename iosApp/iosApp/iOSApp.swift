import SwiftUI
import ComposeApp

@main
struct iOSApp: App {
    init(){
        KoinInitializer(signInProvider: GoogleSignInIOS.shared).doInit()
    }
	var body: some Scene {
		WindowGroup {
			ContentView()
		}
	}
}
