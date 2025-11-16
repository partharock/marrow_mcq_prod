# Mcq Quiz - Android (Jetpack Compose)

This is a minimal Jetpack Compose Android app that implements a quiz with multiple-choice questions.

## What this delivers

- A minimal Jetpack Compose Android app implementing the assignment:
  - Parses questions from `app/src/main/assets/questions.json`
  - A question screen with 4 options, which reveals the correct/selected answer
  - The app auto-advances to the next question after 2 seconds on answer reveal
  - A skip button to immediately go to the next question
  - Streak logic: consecutive correct answers are tracked, and a streak badge lights up at 3 consecutive correct answers
  - A results screen with the number of correct answers, total questions, skipped questions, the longest streak, and a "Restart" button
- A project skeleton that you can open in Android Studio (Electric/Electric variants).
- The implementation focuses on clarity and separation of concerns (using a ViewModel for state management).

## How to run

1. Open the project in Android Studio.
2. If needed, update the Gradle wrapper in Android Studio (Open the Gradle tool window -> Use the default wrapper).
3. Run the app on an emulator or a physical device.

## Project Structure

The project follows a standard Android app structure:

- `app/src/main/java/com/example/mcqquiz/`: This is the main package for the application.
    - `di/`: This package contains the dependency injection container.
        - `AppContainer.kt`: The manual dependency injection container.
    - `ui/`: This package contains the Composables for the different screens of the app.
        - `theme/`: The theme for the application.
        - `Components.kt`: Contains reusable UI components.
        - `QuizScreen.kt`: The Composable for the main quiz screen.
        - `StartScreen.kt`: The Composable for the start screen.
        - `LoadingPopup.kt`: The Composable for the loading popup.
        - `LoadingScreen.kt`: The Composable for the loading screen.
        - `ResultsScreen.kt`: The Composable for the results screen.
        - `ClosingOverlay.kt`: The Composable for the closing overlay.
    - `network/`: This package contains the network-related classes.
    - `database/`: This package contains the database-related classes.
    - `repository/`: This package contains the repository classes.
    - `Question.kt`: The data class for the quiz questions.
    - `MainActivity.kt`: The main entry point of the application. It sets up the Compose content.
    - `SoundManager.kt`: Manages the sounds of the application.
    - `QuizViewModel.kt`: The ViewModel that holds the application's state and business logic.
    - `QuizApplication.kt`: The application class.
- `app/src/main/assets/`: This directory contains the `questions.json` file.

## Functionality

The app consists of a single quiz with multiple-choice questions. The user can answer the questions and see the results at the end of the quiz. The app also has the following features:
- A streak counter that tracks the number of consecutive correct answers.
- A skip button to immediately go to the next question.
- A results screen with the number of correct answers, total questions, skipped questions, the longest streak, and a "Restart" button.
- Sound effects for correct and incorrect answers.

## Dependencies

The project uses the following key dependencies:

- **Jetpack Compose:** For building the UI.
- **ViewModel:** For managing the UI-related data in a lifecycle-conscious way.
- **Navigation-Compose:** For navigating between the different screens of the app.
- **Material Design:** For the UI components.
- **Retrofit:** For networking.
- **Room:** For database caching.
- **JUnit, Mockito, and Mockito-Kotlin:** For unit testing.
- **Espresso and Compose UI Test:** For UI testing.

## Continuous Integration & Deployment (CI/CD)

This project is configured with a GitHub Actions workflow located in `.github/workflows/apk-publisher.yml`.

This workflow automatically builds a debug version of the Android application and publishes it as a GitHub Release. This process is triggered on every push to the `main` branch, ensuring that a downloadable debug APK is always available from the latest version of the code.

### How it Works

1.  **Trigger:** The workflow runs automatically on every `push` to the `main` branch. It can also be triggered manually from the Actions tab in the GitHub repository.
2.  **Build:** It sets up a standard Ubuntu environment with Java 17 and the Android SDK, then builds the debug APK using the `./gradlew assembleDebug` command.
3.  **Release:** After a successful build, the workflow creates a new GitHub Release. Each release is tagged with a unique identifier based on the workflow run ID (e.g., `apk-12345`).
4.  **Upload:** The generated `app-debug.apk` is then uploaded as an asset to this new release, making it easy to download and install.

### Where to Find the APKs

You can find all automatically generated debug APKs on the **Releases** page of this GitHub repository.

## Notes

- You can extend it with more features like animations, fetching questions from a network source, and more polished UI elements.

## License

This project is released under a dual-license model.

### Open-Source License

This project is licensed under the **GNU General Public License v3.0 (GPLv3)**. You can use, distribute, and modify this software under the terms of the GPLv3. A copy of the license is included in the `LICENSE` file.

Under the GPLv3, any derivative work you create must also be licensed under the GPLv3.

### Commercial License

If you wish to use this software in a proprietary, closed-source application, you must obtain a commercial license. Please contact us for more information on obtaining a commercial license.
