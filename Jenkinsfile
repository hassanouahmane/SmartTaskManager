pipeline {
    agent any

    tools {
        maven 'Maven'
        jdk 'JDK17'
    }

    environment {
        // You can remove this if not using elsewhere
        DOCKER_IMAGE = "smarttaskmanager:latest"
    }

    stages {
        stage('Clean Workspace') {
            steps {
                cleanWs()  // Clean workspace before starting
            }
        }

        stage('Checkout') {
            steps {
                withCredentials([string(credentialsId: 'github-token', variable: 'GITHUB_TOKEN')]) {
                    sh '''
                        echo "=== Starting checkout ==="
                        git config --global user.email "jenkins@example.com"
                        git config --global user.name "Jenkins"

                        # Clone the repository
                        git clone https://x-access-token:${GITHUB_TOKEN}@github.com/hassanmouhamane/SmartTaskManager.git .

                        # Checkout main branch
                        git checkout main

                        echo "=== Repository cloned successfully ==="
                        echo "=== Checking pom.xml ==="
                        if [ -f "taskmanager/pom.xml" ]; then
                            echo "✅ pom.xml found in taskmanager/"
                            echo "=== pom.xml first few lines ==="
                            head -20 taskmanager/pom.xml
                        else
                            echo "❌ pom.xml not found!"
                            exit 1
                        fi
                    '''
                }
            }
        }

        stage('Build with Maven') {
            steps {
                dir('taskmanager') {
                    sh '''
                        echo "=== Current directory: ==="
                        pwd
                        echo "=== Running Maven clean compile ==="
                        mvn clean compile -DskipTests
                    '''
                }
            }
        }

        stage('Run Tests') {
            steps {
                dir('taskmanager') {
                    sh 'mvn test'
                }
            }
            post {
                always {
                    junit 'taskmanager/target/surefire-reports/*.xml'
                }
            }
        }

        stage('Package JAR') {
            steps {
                dir('taskmanager') {
                    sh '''
                        echo "=== Packaging JAR ==="
                        mvn package -DskipTests
                        echo "=== JAR file created: ==="
                        ls -la target/*.jar
                    '''
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                dir('taskmanager') {
                    sh '''
                        echo "=== Building Docker image ==="
                        docker build -t smarttaskmanager:latest .
                        echo "=== Docker images: ==="
                        docker images | grep smarttaskmanager
                    '''
                }
            }
        }

        stage('Run with Docker Compose') {
            steps {
                dir('taskmanager') {
                    sh '''
                        echo "=== Starting Docker Compose ==="
                        docker-compose up -d --build
                        echo "=== Running containers: ==="
                        docker-compose ps
                    '''
                }
            }
        }
    }

    post {
        success {
            echo '✅ Pipeline executed successfully!'

            // Archive artifacts
            archiveArtifacts 'taskmanager/target/*.jar'

            // Save Docker image (optional)
            sh '''
                docker save smarttaskmanager:latest -o smarttaskmanager.tar 2>/dev/null || true
            '''
            archiveArtifacts 'smarttaskmanager.tar'
        }
        failure {
            echo '❌ Pipeline failed!'

            // Additional debugging
            sh '''
                echo "=== Maven version ==="
                mvn --version
                echo "=== Java version ==="
                java --version
                echo "=== Checking pom.xml syntax ==="
                cd taskmanager && mvn help:effective-pom 2>/dev/null | head -50 || true
            '''
        }
        always {
            echo '=== Cleaning up ==='

            // Stop Docker Compose
            sh '''
                cd taskmanager && docker-compose down 2>/dev/null || true
            '''

            // Remove Docker images to save space
            sh '''
                docker rmi smarttaskmanager:latest 2>/dev/null || true
                docker system prune -f 2>/dev/null || true
            '''

            // Optional: Clean workspace
            // cleanWs()
        }
    }
}