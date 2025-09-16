//
//  GoogleSignInBridge.swift
//  iosApp
//
//  Created by Paulo Inocencio on 10/09/25.
//  Copyright © 2025 orgName. All rights reserved.
//
import SwiftUI
import AppAuth
import CryptoKit
import ComposeApp

enum SignInError: Error {
    case noPresenterAvailable
    case noIdTokenReturned
    case authorizationError(String)
}

public class GoogleSignInIOS: GoogleSignInProvider {
    
    public static let shared = GoogleSignInIOS()
    
    private var currentAuthorizationFlow: OIDExternalUserAgentSession?
    
    public func resume(url: String) -> Bool {
        if let flow = self.currentAuthorizationFlow, flow.resumeExternalUserAgentFlow(with:URL(string: url)!) {
            self.currentAuthorizationFlow = nil
            return true
        }
        return false
    }
    
    
    public func __signIn(clientID: String, redirectURI: String) async throws -> KotlinPair<NSString, NSString> {
        
        let issuer = URL(string: "https://accounts.google.com")!
        let config = try await OIDAuthorizationService.discoverConfiguration(forIssuer: issuer)
        
        let rawNonce = UUID().uuidString
        let hashedNonce = self.sha256Hex(rawNonce)
        
        let request = OIDAuthorizationRequest(
            configuration: config,
            clientId: clientID,
            scopes: [OIDScopeOpenID, OIDScopeEmail, OIDScopeProfile],
            redirectURL: URL(string: redirectURI)!,
            responseType: OIDResponseTypeCode,
            nonce: hashedNonce,
            additionalParameters: nil
        )

        guard let presenter = await Self.topPresenter() else {
            throw SignInError.noPresenterAvailable
        }
        
        return try await withCheckedThrowingContinuation { continuation in
            Task { @MainActor in
                self.currentAuthorizationFlow = OIDAuthState.authState(
                    byPresenting: request,
                    presenting: presenter
                ) { authState, error in
                        
                    if let error {
                        continuation.resume(throwing: SignInError.authorizationError("Authorization error: \(error.localizedDescription)"))
                        return
                    }
                    
                    if let idToken = authState?.lastTokenResponse?.idToken {
                        continuation.resume(returning: KotlinPair(first: idToken as NSString, second: rawNonce as NSString))
                    } else {
                        continuation.resume(throwing: SignInError.noIdTokenReturned)
                    }
                }
            }
        }
    }

    private func sha256Hex(_ input: String) -> String {
        let data = Data(input.utf8)
        let digest = SHA256.hash(data: data)
        return digest.map { String(format: "%02x", $0) }.joined()
    }
    
    // Find a presenter that works in SwiftUI apps (ASWebAuthenticationSession needs a presenting VC)
    private static func topPresenter() async -> UIViewController? {
        await MainActor.run {
            let scenes = UIApplication.shared.connectedScenes.compactMap { $0 as? UIWindowScene }
            let keyWindow = scenes.flatMap { $0.windows }.first { $0.isKeyWindow }
            var top = keyWindow?.rootViewController
            while let presented = top?.presentedViewController { top = presented }
            return top
        }
    }
}
