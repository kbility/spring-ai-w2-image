# W-2 Vision Extractor - React Frontend

## Installation Steps

### 1. Prerequisites
- Node.js 18+ installed
- Spring Boot backend running on port 8080

### 2. Install Dependencies
```bash
cd frontend
npm install
```

### 3. Run Development Server
```bash
npm run dev
```

The app will run on `http://localhost:3000`

### 4. Build for Production
```bash
npm run build
```

The build output will be in the `dist` folder.

## Backend Integration

The frontend expects these endpoints from Spring Boot:

- `POST /upload` - Single file upload
- `POST /upload-multi` - Multiple files upload
- `GET /download` - Download Excel file

### Update Spring Boot Controller

Your controller should return JSON in this format:

```java
@RestController
public class W2Controller {
    
    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> uploadSingle(@RequestParam("file") MultipartFile file) {
        // Process file
        Map<String, Object> response = new HashMap<>();
        response.put("table", List.of(extractedData)); // List of Map<String, Object>
        response.put("previews", List.of(base64Images)); // List of base64 image strings
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/upload-multi")
    public ResponseEntity<Map<String, Object>> uploadMulti(@RequestParam("files") MultipartFile[] files) {
        // Process files
        Map<String, Object> response = new HashMap<>();
        response.put("table", extractedDataList);
        response.put("previews", base64ImagesList);
        return ResponseEntity.ok(response);
    }
}
```

### Add CORS Configuration (if needed)

```java
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:3000")
                .allowedMethods("GET", "POST", "PUT", "DELETE");
    }
}
```

## Project Structure

```
frontend/
├── src/
│   ├── components/
│   │   ├── Header.jsx
│   │   ├── UploadForms.jsx
│   │   ├── ResultsSection.jsx
│   │   ├── ResultCard.jsx
│   │   ├── ProgressOverlay.jsx
│   │   └── ImageModal.jsx
│   ├── App.jsx
│   ├── main.jsx
│   └── index.css
├── package.json
└── vite.config.js
```

## Features

✅ Material-UI design system
✅ Gradient theme matching original design
✅ Single & multiple file upload
✅ Progress overlay during processing
✅ Image magnification modal
✅ Responsive grid layout
✅ Excel download
✅ Employee name in card headers
✅ Hover effects and animations
