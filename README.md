### **Project Justification**

**Core Logic (Original Work)**
* **CryptoManager:** Entirely my own implementation of RSA encryption and decryption.
* **ViewModel:** Developed the state management and business logic flow from scratch.
* **Architecture:** Designed the repository pattern and dependency injection (ViewModel Factory) to ensure a clean separation of concerns.


**Cryptography (RSA)**
* **Private Key:** Stored in the **AndroidKeyStore**, ensuring the key is hardware-secured and never leaves the device.
* **Public Keys:** Managed via a `FriendListRepository` and persisted in `SharedPreferences`.

**Bonus: 1-to-1 Relationships**
* The foundation is fully implemented: the system maps `nicknames` and `UUIDs` to specific public keys, enabling a future-proof path for contact-specific encryption.

**AI Implementation (Gemini/Cursor)**

To come to a actual working ui i used AI, building these things would cost more time than 6 hours.
* **QR Scanning:** Integration of CameraX and ML Kit to allow scanning of ciphertexts and keys.
* **QR Generation:** Implementation of ZXing for converting public keys and ciphertexts into scanable bitmaps.
* **UI/UX:** The Jetpack Compose layouts, dialogs, and Material 3 styling were generated and refined using AI to accelerate development.
* **Permissions:** Handling of Android Manifest requirements and runtime permission logic for camera access.