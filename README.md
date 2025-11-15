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
  - `MainActivity.kt`: The main entry point of the application. It sets up the Compose content.
  - `QuizViewModel.kt`: The ViewModel that holds the application's state and business logic.
  - `Question.kt`: The data class for the quiz questions.
  - `ui/`: This package contains the Composables for the different screens of the app.
    - `QuizScreen.kt`: The Composable for the main quiz screen.
    - `ResultsScreen.kt`: The Composable for the results screen.
    - `LoadingScreen.kt`: The Composable for the loading screen.
    - `Components.kt`: Contains reusable UI components.
    - `theme/`: The theme for the application.
- `app/src/main/assets/`: This directory contains the `questions.json` file.

## Dependencies

The project uses the following key dependencies:

- **Jetpack Compose:** For building the UI.
- **ViewModel:** For managing the UI-related data in a lifecycle-conscious way.
- **Navigation-Compose:** For navigating between the different screens of the app.
- **Material Design:** For the UI components.

## Notes

- This is a minimal functional example for the assignment. You can extend it with more features like animations, fetching questions from a network source, and more polished UI elements.
