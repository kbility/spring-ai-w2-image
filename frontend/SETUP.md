# Setup Instructions

## 1. Update Spring Boot Controller

Replace your existing controller with the new `W2Controller.java` file that:
- Uses `@RestController` instead of `@Controller`
- Returns JSON via `ResponseEntity<Map<String, Object>>`
- Adds `@CrossOrigin` for React frontend

## 2. Start Spring Boot Backend

```bash
# Make sure your Spring Boot app runs on port 8080
./mvnw spring-boot:run
```

## 3. Start React Frontend

```bash
cd frontend
npm run dev
```

Access the app at: **http://localhost:3000**

## Changes Made

### Controller Changes:
- `@Controller` → `@RestController`
- `Model model` → `ResponseEntity<Map<String, Object>>`
- `return "index"` → `return ResponseEntity.ok(response)`
- Added `@CrossOrigin(origins = "http://localhost:3000")`

### Response Format:
```json
{
  "table": [
    {
      "Employer Name": "ABC Corp",
      "Employee Name": "John Doe",
      ...
    }
  ],
  "previews": [
    "data:image/png;base64,iVBORw0KG..."
  ]
}
```

## Troubleshooting

**CORS errors?**
- Ensure `@CrossOrigin` is on the controller
- Check Spring Boot is running on port 8080

**API not found?**
- Verify Spring Boot is running
- Check console for errors

**Upload not working?**
- Check browser console for errors
- Verify file types are accepted (PDF, images)
