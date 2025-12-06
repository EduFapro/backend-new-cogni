# PROD Mode Testing Guide

In **PROD Mode**, authentication is **required**. You must login to get a JWT token and include it in the header of subsequent requests.

## 1. Apply Changes & Restart
Since we changed the `.env` file, you must rebuild the container.

```powershell
docker-compose up -d --build --force-recreate
```

## 2. Verify PROD Mode
Check if the backend is now in PROD mode.
```bash
curl http://localhost:8080/api/status
```
**Expected Response**: `{"status":"ok","mode":"PROD"}`

---

## 3. Authentication Flow

### Step 1: Login
Use the default admin credentials (or the user you created).
```bash
curl -X POST http://localhost:8080/api/evaluators/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "admin_password"
  }'
```
**Response**:
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```
**Copy this token.** You will need it for all other requests.

---

## 4. Protected Endpoints
Add the header `-H "Authorization: Bearer <YOUR_TOKEN>"` to your requests.

### Get Your Profile
```bash
curl http://localhost:8080/api/evaluators/1 \
  -H "Authorization: Bearer <YOUR_TOKEN>"
```

### Create a Participant (Protected)
```bash
curl -X POST http://localhost:8080/api/participants \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <YOUR_TOKEN>" \
  -d '{
    "name": "Bob",
    "surname": "Jones",
    "birthDate": "2016-01-01",
    "sex": 1,
    "educationLevel": 1,
    "laterality": 1,
    "evaluatorId": 1
  }'
```

### List Participants
```bash
curl "http://localhost:8080/api/participants?evaluatorId=1" \
  -H "Authorization: Bearer <YOUR_TOKEN>"
```

---

## 5. Flutter App Verification
1.  **Restart the Flutter App**.
2.  It should log: `🌍 Backend Connected | Mode: PROD`.
3.  The app will now **require login**.
4.  Try logging in with `admin` / `admin_password`.
5.  If successful, the token will be stored locally, and you can proceed to use the app.

---

## 6. Local Encryption Verification (Flutter)
Since Phase 3, the Flutter app encrypts local data using a hybrid approach (AES-GCM + AES-CBC).

1.  **Inspect Database**:
    - Open the SQLite database file (usually in `Documents` or AppData).
    - Check the `evaluators` table.
2.  **Verify Encrypted Fields**:
    - **Searchable Fields** (`email`, `username`): Should look like Base64 strings (AES-CBC).
    - **Non-Searchable Fields** (`name`, `surname`, etc.): Should look like JSON objects (AES-GCM) containing `nonce`, `ciphertext`, and `mac`.
3.  **Verify Decryption**:
    - Log in to the app.
    - Go to the Profile or Home screen.
    - Verify that your name and details are displayed correctly (decrypted).
