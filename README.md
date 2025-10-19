# Kaiburr Task 2 - Kubernetes Deployment

## 📋 Overview

This task extends the Task Runner application (from Task 1) by containerizing it with Docker and deploying it to a Kubernetes cluster. The setup uses MongoDB as a backend database and implements a cloud-native architecture with persistent storage.

## ✨ Key Features

- ✅ Application and MongoDB run in **separate pods**
- ✅ MongoDB data persisted via **Persistent Volume Claim (PVC)**
- ✅ Environment variables for MongoDB connection
- ✅ Application accessible via **NodePort** service
- ✅ Dynamic **BusyBox pod creation** for command execution
- ✅ Scalable and production-ready setup

## 🛠️ Technologies

- **Java 17**
- **Spring Boot 3.5.6**
- **MongoDB**
- **Docker**
- **Kubernetes**
- **kubectl CLI**

## 📁 Project Structure

```
TASKRUNNER/
├── .mvn/
├── Screenshots/
├── src/
│   ├── main/
│   │   ├── java/com/kaiburi/assessment/taskrunner/
│   │   │   ├── config/
│   │   │   │   └── CorsConfig.java
│   │   │   ├── controller/
│   │   │   │   └── TaskController.java
│   │   │   ├── model/
│   │   │   │   ├── Task.java
│   │   │   │   └── TaskExecution.java
│   │   │   ├── repository/
│   │   │   │   └── TaskRepository.java
│   │   │   └── service/
│   │   │       ├── CommandValidator.java
│   │   │       ├── TaskService.java
│   │   │       └── TaskRunnerApplication.java
│   │   ├── resources/
│   │   │   ├── static/
│   │   │   ├── templates/
│   │   │   └── application.properties
│   │   └── test/
│   └── target/
├── docker-compose.yaml
├── Dockerfile
├── mongo-deployment.yaml
├── taskrunner-deployment.yaml
├── mvnw
├── mvnw.cmd
├── pom.xml
└── README.md
```

## 🚀 Deployment Setup

### 1. Build the Application

```bash
# Clean and package the application
mvn clean package -DskipTests

# Build Docker image
docker build -t taskrunner-app:latest .
```

### 2. Deploy to Kubernetes

```bash
# Deploy MongoDB with persistent storage
kubectl apply -f mongo-deployment.yaml

# Deploy TaskRunner application
kubectl apply -f taskrunner-deployment.yaml

# Verify deployment
kubectl get pods,svc,pvc
```

### 3. Access the Application

The application is exposed via **NodePort** service:

```
http://localhost:30080/api/tasks
```

## 🔌 API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/tasks` | Get all tasks |
| `GET` | `/api/tasks/{id}` | Get a specific task |
| `PUT` | `/api/tasks` | Create or update a task |
| `DELETE` | `/api/tasks/{id}` | Delete a task |
| `GET` | `/api/tasks/search?name=...` | Search by name |
| `PUT` | `/api/tasks/{id}/executions` | Execute task (creates BusyBox pod) |

## 🔍 Verify Deployment

```bash
# Check pod status
kubectl get pods

# Check services
kubectl get svc

# Check persistent volumes
kubectl get pvc

# View logs
kubectl logs <pod-name>

# Describe pod for details
kubectl describe pod <pod-name>
```

## 📦 Kubernetes Resources

### MongoDB Deployment
- **Persistent Volume Claim**: 1Gi storage
- **Service**: ClusterIP on port 27017
- **Replicas**: 1

### TaskRunner Deployment
- **Image**: taskrunner-app:latest
- **Service**: NodePort (30080)
- **Environment Variables**:
  - `MONGO_HOST`
  - `MONGO_PORT`
  - `MONGO_DATABASE`
- **Replicas**: 1

## 🧪 Testing

```bash
# Create a task
curl -X PUT http://localhost:30080/api/tasks \
  -H "Content-Type: application/json" \
  -d '{
    "id": "1",
    "name": "Test Task",
    "owner": "admin",
    "command": "echo Hello Kubernetes"
  }'

# Execute the task
curl -X PUT http://localhost:30080/api/tasks/1/executions

# Get all tasks
curl http://localhost:30080/api/tasks
```

## 🛑 Cleanup

```bash
# Delete all resources
kubectl delete -f taskrunner-deployment.yaml
kubectl delete -f mongo-deployment.yaml

# Verify deletion
kubectl get all
```
