# Backend New Cogni

This is the backend service for the Cogni application, built with Kotlin and Ktor.

## Prerequisites

*   **Docker Desktop**: Required for running the application and database.
    *   [Download Docker Desktop](https://www.docker.com/products/docker-desktop/)
*   **Postman**: For testing API endpoints.
    *   [Download Postman](https://www.postman.com/downloads/)
*   **Flutter SDK**: For running the mobile application.
    *   [Download Flutter](https://docs.flutter.dev/get-started/install)

## Quick Start (Docker)

The easiest way to run the application is using Docker. This will start both the PostgreSQL database and the Backend service with a single command.

1.  Open a terminal in the project root (`c:/DevProjects/backend-new-cogni`).
2.  Run the following command:

    ```bash
    docker-compose up --build
    ```

    *   The **Database** will start on port `5432`.
    *   The **Backend** will start on port `8080`.

3.  Wait for the logs to show that the application has started.

## Testing with Postman

1.  Open Postman.
2.  Create a new **POST** request.
3.  **URL**: `http://localhost:8080/admin/register`
4.  **Headers**:
    *   Key: `Content-Type`
    *   Value: `application/json`
5.  **Body** (select `raw` -> `JSON`):

    ```json
    {
      "name": "João",
      "surname": "Silva",
      "birthDate": "1990-05-20",
      "specialty": "Neuropsicologia",
      "cpfOrNif": "12345678900",
      "username": "joao_silva",
      "password": "123456",
      "firstLogin": true,
      "isAdmin": true
    }
    ```
6.  Click **Send**. You should receive a success response.

## Testing with Bruno

This project includes a **Bruno** collection for easier API testing.

1.  **Install Bruno**: [Download Bruno](https://www.usebruno.com/downloads)
2.  **Open Collection**:
    *   Open Bruno.
    *   Click **Open Collection**.
    *   Select the folder `backend-new-cogni/new_cogni_collection`.
3.  **Run Requests**:
    *   You will see the available requests (e.g., `Post Admin`).
    *   Select a request and click the **Run** button (arrow icon).
    *   *Note: Ensure the environment URL matches your local setup (default is `http://127.0.0.1:8080`).*

## Connecting with Flutter App

To test the backend with the Flutter app (`segundo_cogni`):

1.  **Ensure Backend is Running**: Make sure `docker-compose` is running.
2.  **Configure API URL in Flutter**:
    *   **For Android Emulator**: Use `http://10.0.2.2:8080`.
    *   **For iOS Simulator / Web / Desktop**: Use `http://localhost:8080`.
    *   **For Physical Device**: Use your computer's local IP address (e.g., `http://192.168.1.x:8080`).

3.  **Run Flutter App**:
    ```bash
    cd c:/DevProjects/StudioProjects/segundo_cogni
    flutter run
    ```

## Development (Optional: Running Locally without Docker)

If you want to run the backend locally (e.g., for debugging in IntelliJ):

1.  **Start Database**:
    ```bash
    docker-compose up db -d
    ```
2.  **Run Backend**:
    ```bash
    ./gradlew run
    ```
    *Note: Ensure you have JDK 21 installed.*

## Troubleshooting

*   **IntelliJ Error: "Unsupported class file major version 68"**:
    *   This means IntelliJ is trying to use a newer Java version (like Java 24) that is incompatible with the project's Gradle version.
    *   **Fix**: Go to `Settings > Build, Execution, Deployment > Build Tools > Gradle` and set **Gradle JVM** to **Project SDK 21** (or your installed JDK 21).

*   **Database Connection Refused**:
    *   Ensure the Docker container is running (`docker ps`).
    *   Check if port 5432 is already in use.

*   **"No main manifest attribute"**:
    *   Ensure you are using the `shadowJar` task or the Docker image which is built correctly.
