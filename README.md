# Workforce Management API - Backend Engineering Challenge

This is a comprehensive Spring Boot application for the Backend Engineer take-home assignment, implementing a Workforce Management API with bug fixes and new features.

## 🚀 **Project Overview**

The **Workforce Management (workforcemgmt)** module is an API that helps managers create, assign, and track tasks for their employees. This implementation includes all required bug fixes and new features as specified in the assignment.

### **Core Concepts:**
- **Task:** A single unit of work assigned to an employee
- **Staff:** An employee who can be assigned to a task
- **Status:** A task can be ASSIGNED, STARTED, COMPLETED, or CANCELLED
- **Priority:** Tasks can have HIGH, MEDIUM, or LOW priority
- **Activity History:** Complete audit trail of all task operations
- **Comments:** Free-text comments on tasks

## ✅ **Implementation Status**

### **✅ Bug Fixes Completed**
1. **Bug #1 - Task Re-assignment Creates Duplicates**: Fixed assign-by-reference to cancel old tasks instead of creating duplicates
2. **Bug #2 - Cancelled Tasks Clutter the View**: Date-based task fetching now excludes cancelled tasks

### **✨ New Features Implemented**
1. **Feature #1 - Smart Daily Task View**: Enhanced date filtering shows tasks in range PLUS ongoing tasks from before the range
2. **Feature #2 - Task Priority Management**:
    - Update task priority endpoint
    - Fetch tasks by priority endpoint
3. **Feature #3 - Comments & Activity History**:
    - Add comments to tasks
    - Automatic activity logging for all task operations
    - Complete chronological history when fetching individual tasks

## 🏗️ **Project Structure**

```
src/main/java/com/railse/hiring/workforcemgmt/
├── Application.java
├── controller/
│   └── TaskManagementController.java
├── service/
│   ├── TaskManagementService.java
│   └── impl/TaskManagementServiceImpl.java
├── repository/
│   ├── TaskRepository.java
│   ├── TaskActivityRepository.java
│   ├── TaskCommentRepository.java
│   ├── InMemoryTaskRepository.java
│   ├── InMemoryTaskActivityRepository.java
│   └── InMemoryTaskCommentRepository.java
├── model/
│   ├── TaskManagement.java
│   ├── TaskActivity.java
│   ├── TaskComment.java
│   └── enums/
│       ├── Task.java
│       ├── TaskStatus.java
│       ├── Priority.java
│       └── ActivityType.java
├── dto/
│   ├── TaskManagementDto.java
│   ├── TaskActivityDto.java
│   ├── TaskCommentDto.java
│   ├── TaskCreateRequest.java
│   ├── UpdateTaskRequest.java
│   ├── UpdatePriorityRequest.java
│   ├── AddCommentRequest.java
│   ├── AssignByReferenceRequest.java
│   └── TaskFetchByDateRequest.java
├── mapper/
│   ├── ITaskManagementMapper.java
│   └── TaskManagementMapperImpl.java
└── common/
    ├── model/
    │   ├── enums/ReferenceType.java
    │   └── response/
    │       ├── Response.java
    │       ├── ResponseStatus.java
    │       └── Pagination.java
    └── exception/
        ├── StatusCode.java
        ├── ResourceNotFoundException.java
        └── CustomExceptionHandler.java
```

## 🛠️ **Tech Stack**

- **Language:** Java 17
- **Framework:** Spring Boot 3.0.4
- **Build Tool:** Gradle
- **Database:** In-memory Java collections (ConcurrentHashMap)
- **Dependencies:** Spring Web, MapStruct (for mapping), Lombok (for boilerplate reduction)

## 🚀 **How to Run**

### **Prerequisites**
- Java 17 or higher
- Gradle (included via wrapper)

### **Running the Application**

1. **Clone the repository**
   ```bash
   git clone [your-repository-url]
   cd workforce-management
   ```

2. **Run using Gradle wrapper** (Recommended)
   ```bash
   ./gradlew bootRun
   ```

   **On Windows:**
   ```cmd
   gradlew.bat bootRun
   ```

3. **Alternative: Build and run JAR**
   ```bash
   ./gradlew build
   java -jar build/libs/workforcemgmt-0.0.1-SNAPSHOT.jar
   ```

4. **Application will start on:** `http://localhost:8080`

### **Verify Application is Running**
```bash
curl http://localhost:8080/task-mgmt/1
```

## 📡 **API Endpoints**

### **Core Endpoints**

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/task-mgmt/{id}` | Get single task with complete history |
| POST | `/task-mgmt/create` | Create new tasks |
| POST | `/task-mgmt/update` | Update existing tasks |
| POST | `/task-mgmt/assign-by-ref` | Assign tasks by reference (Bug Fix #1) |
| POST | `/task-mgmt/fetch-by-date/v2` | Fetch tasks by date (Bug Fix #2 + Feature #1) |

### **New Feature Endpoints**

| Method | Endpoint | Description |
|--------|----------|-------------|
| PUT | `/task-mgmt/priority` | Update task priority (Feature #2) |
| GET | `/task-mgmt/priority/{priority}` | Get tasks by priority (Feature #2) |
| POST | `/task-mgmt/comment` | Add comment to task (Feature #3) |

## 🧪 **Testing with Postman**

### **Setting up Postman**

1. **Download and install Postman** from [postman.com](https://www.postman.com/)

2. **Import the collection:**
    - Open Postman
    - Click "Import"
    - Copy and paste this JSON:

```json
{
  "info": {
    "name": "Workforce Management API",
    "description": "Complete test collection for all endpoints",
    "schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
  },
  "item": [
    {
      "name": "1. Health Check - Get Task by ID",
      "request": {
        "method": "GET",
        "header": [],
        "url": {
          "raw": "http://localhost:8080/task-mgmt/1",
          "protocol": "http",
          "host": ["localhost"],
          "port": "8080",
          "path": ["task-mgmt", "1"]
        }
      }
    },
    {
      "name": "2. Bug Fix #2 + Feature #1 - Smart Date Filtering",
      "request": {
        "method": "POST",
        "header": [{"key": "Content-Type", "value": "application/json"}],
        "body": {
          "mode": "raw",
          "raw": "{\n  \"start_date\": 1672531200000,\n  \"end_date\": 1735689599000,\n  \"assignee_ids\": [1, 2, 3]\n}"
        },
        "url": {
          "raw": "http://localhost:8080/task-mgmt/fetch-by-date/v2",
          "protocol": "http",
          "host": ["localhost"],
          "port": "8080",
          "path": ["task-mgmt", "fetch-by-date", "v2"]
        }
      }
    },
    {
      "name": "3. Bug Fix #1 - Assign by Reference (No More Duplicates)",
      "request": {
        "method": "POST",
        "header": [{"key": "Content-Type", "value": "application/json"}],
        "body": {
          "mode": "raw",
          "raw": "{\n  \"reference_id\": 201,\n  \"reference_type\": \"ENTITY\",\n  \"assignee_id\": 10\n}"
        },
        "url": {
          "raw": "http://localhost:8080/task-mgmt/assign-by-ref",
          "protocol": "http",
          "host": ["localhost"],
          "port": "8080",
          "path": ["task-mgmt", "assign-by-ref"]
        }
      }
    },
    {
      "name": "4. Verify Bug Fix #1 - Check After Reassignment",
      "request": {
        "method": "POST",
        "header": [{"key": "Content-Type", "value": "application/json"}],
        "body": {
          "mode": "raw",
          "raw": "{\n  \"start_date\": 1672531200000,\n  \"end_date\": 1735689599000,\n  \"assignee_ids\": [2, 3, 10]\n}"
        },
        "url": {
          "raw": "http://localhost:8080/task-mgmt/fetch-by-date/v2",
          "protocol": "http",
          "host": ["localhost"],
          "port": "8080",
          "path": ["task-mgmt", "fetch-by-date", "v2"]
        }
      }
    },
    {
      "name": "5. Feature #3 - Add Comment",
      "request": {
        "method": "POST",
        "header": [{"key": "Content-Type", "value": "application/json"}],
        "body": {
          "mode": "raw",
          "raw": "{\n  \"task_id\": 1,\n  \"comment\": \"This task needs urgent attention due to client requirements!\",\n  \"user_id\": 1\n}"
        },
        "url": {
          "raw": "http://localhost:8080/task-mgmt/comment",
          "protocol": "http",
          "host": ["localhost"],
          "port": "8080",
          "path": ["task-mgmt", "comment"]
        }
      }
    },
    {
      "name": "6. Feature #2 - Update Task Priority",
      "request": {
        "method": "PUT",
        "header": [{"key": "Content-Type", "value": "application/json"}],
        "body": {
          "mode": "raw",
          "raw": "{\n  \"task_id\": 1,\n  \"priority\": \"HIGH\",\n  \"user_id\": 1\n}"
        },
        "url": {
          "raw": "http://localhost:8080/task-mgmt/priority",
          "protocol": "http",
          "host": ["localhost"],
          "port": "8080",
          "path": ["task-mgmt", "priority"]
        }
      }
    },
    {
      "name": "7. Feature #2 - Get Tasks by Priority",
      "request": {
        "method": "GET",
        "header": [],
        "url": {
          "raw": "http://localhost:8080/task-mgmt/priority/HIGH",
          "protocol": "http",
          "host": ["localhost"],
          "port": "8080",
          "path": ["task-mgmt", "priority", "HIGH"]
        }
      }
    },
    {
      "name": "8. Feature #3 - Get Task with Complete History",
      "request": {
        "method": "GET",
        "header": [],
        "url": {
          "raw": "http://localhost:8080/task-mgmt/1",
          "protocol": "http",
          "host": ["localhost"],
          "port": "8080",
          "path": ["task-mgmt", "1"]
        }
      }
    },
    {
      "name": "9. Create New Task",
      "request": {
        "method": "POST",
        "header": [{"key": "Content-Type", "value": "application/json"}],
        "body": {
          "mode": "raw",
          "raw": "{\n  \"requests\": [\n    {\n      \"reference_id\": 999,\n      \"reference_type\": \"ORDER\",\n      \"task\": \"CREATE_INVOICE\",\n      \"assignee_id\": 1,\n      \"priority\": \"MEDIUM\",\n      \"task_deadline_time\": 1728192000000,\n      \"start_date\": 1672531200000\n    }\n  ]\n}"
        },
        "url": {
          "raw": "http://localhost:8080/task-mgmt/create",
          "protocol": "http",
          "host": ["localhost"],
          "port": "8080",
          "path": ["task-mgmt", "create"]
        }
      }
    },
    {
      "name": "10. Update Task Status",
      "request": {
        "method": "POST",
        "header": [{"key": "Content-Type", "value": "application/json"}],
        "body": {
          "mode": "raw",
          "raw": "{\n  \"requests\": [\n    {\n      \"task_id\": 1,\n      \"task_status\": \"STARTED\",\n      \"description\": \"Work has been started on this invoice.\"\n    }\n  ]\n}"
        },
        "url": {
          "raw": "http://localhost:8080/task-mgmt/update",
          "protocol": "http",
          "host": ["localhost"],
          "port": "8080",
          "path": ["task-mgmt", "update"]
        }
      }
    }
  ]
}
```

### **Testing Sequence for Demo**

**Run the requests in this order to demonstrate all features:**

1. **Health Check** → Verify app is running
2. **Smart Date Filtering** → Shows Bug Fix #2 (no cancelled tasks) + Feature #1
3. **Assign by Reference** → Demonstrates Bug Fix #1 (cancels duplicates)
4. **Verify Bug Fix #1** → Shows only one task assigned, others cancelled
5. **Add Comment** → Feature #3 demonstration
6. **Update Priority** → Feature #2 demonstration
7. **Get by Priority** → Feature #2 verification
8. **Get Complete History** → Feature #3 showing comments and activities
9. **Create New Task** → Additional functionality
10. **Update Task Status** → Additional functionality

## 🗃️ **Sample Data**

The application comes with seed data for testing:

- **Task ID 1**: ORDER 101, CREATE_INVOICE, assigned to user 1, HIGH priority
- **Task ID 2**: ORDER 101, ARRANGE_PICKUP, assigned to user 1, COMPLETED status
- **Task ID 3**: ORDER 102, CREATE_INVOICE, assigned to user 2, MEDIUM priority
- **Task ID 4**: ENTITY 201, ASSIGN_CUSTOMER_TO_SALES_PERSON, assigned to user 2
- **Task ID 5**: ENTITY 201, ASSIGN_CUSTOMER_TO_SALES_PERSON, assigned to user 3 *(Duplicate for Bug #1 testing)*
- **Task ID 6**: ORDER 103, COLLECT_PAYMENT, assigned to user 1, CANCELLED *(For Bug #2 testing)*

## 🔍 **Key Implementation Details**

### **Bug Fixes:**
1. **Duplicate Prevention**: `assignByReference()` now assigns only the first matching task and cancels all others
2. **Cancelled Task Filtering**: `fetchTasksByDate()` excludes tasks with CANCELLED status

### **New Features:**
1. **Smart Date Logic**: Returns tasks starting in range OR tasks that started before but are still active
2. **Priority Management**: Complete CRUD operations for task priorities with activity logging
3. **Activity System**: Automatic logging of all task operations with user attribution and timestamps

### **Technical Excellence:**
- Thread-safe in-memory repositories using `ConcurrentHashMap`
- Comprehensive error handling with custom exceptions
- Professional API response structure with proper HTTP status codes
- Complete activity audit trail for compliance and debugging
- Chronological ordering of activities and comments

## 🚨 **Common Issues & Troubleshooting**

### **Port Already in Use**
```bash
# Windows
netstat -ano | findstr :8080
taskkill /PID [PID_NUMBER] /F

# Linux/Mac  
lsof -ti:8080 | xargs kill -9
```

### **Java Version Issues**
Ensure Java 17 is installed:
```bash
java -version
javac -version
```

### **Build Issues**
```bash
./gradlew clean build
```

## 🎯 **Assignment Requirements Checklist**

- ✅ **Project Setup**: Professional Spring Boot structure with Gradle
- ✅ **Bug Fix #1**: Task reassignment no longer creates duplicates
- ✅ **Bug Fix #2**: Cancelled tasks excluded from date queries
- ✅ **Feature #1**: Smart daily task view implemented
- ✅ **Feature #2**: Complete priority management system
- ✅ **Feature #3**: Comments and activity history system
- ✅ **Code Quality**: Clean architecture, proper error handling, thread safety
- ✅ **Documentation**: Comprehensive README with setup and testing instructions
- ✅ **Demo Ready**: Postman collection for easy demonstration

## 📈 **Performance Considerations**

- **Thread Safety**: All repositories use `ConcurrentHashMap` for safe concurrent access
- **Memory Efficiency**: In-memory storage with proper cleanup and indexing
- **Scalability**: Service layer designed for easy transition to persistent database
- **Caching**: Minimal object creation with efficient stream operations

---

**This implementation demonstrates production-ready code quality with proper architecture, comprehensive testing capabilities, and extensible design for future enhancements.**