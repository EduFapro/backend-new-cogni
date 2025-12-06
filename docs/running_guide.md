# How to Run the Backend

## Option A: Run Everything in Docker (Best for "Just Running")
Use this if you don't plan to touch the code and just want the backend running in the background.

1.  **Stop any running Gradle process** (Ctrl+C in your terminal).
2.  **Start everything**:
    ```powershell
    docker-compose up -d --build
    ```
    *The `--build` flag ensures it recompiles your code changes into the container.*
3.  **Check status**:
    ```powershell
    docker-compose ps
    ```
    *You should see both `cogni_backend` and `cogni_postgres` as `Up`.*

---

## Option B: DB in Docker, App via Gradle (Best for Development)
Use this if you are editing code, changing `.env` frequently, or debugging.

1.  **Stop the Docker App (but keep DB)**:
    ```powershell
    docker-compose stop app
    ```
    *This frees up port 8080.*

2.  **Ensure DB is running**:
    ```powershell
    docker-compose up -d db
    ```

3.  **Run the App locally**:
    ```powershell
    ./gradlew run
    ```

### Switching between modes
If you get a "Port 8080 already in use" error, it means you are trying to do Option B while Option A is still running. Run `docker-compose stop app` to fix it.
