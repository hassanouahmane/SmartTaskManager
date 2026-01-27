pipeline {
    agent any

    tools {
        maven 'Maven'
        jdk 'JDK17'
    }

    stages {
        stage('Checkout') {
            steps {
                withCredentials([string(credentialsId: 'github-token', variable: 'GITHUB_TOKEN')]) {
                    sh '''
                        git config --global user.email "jenkins@example.com"
                        git config --global user.name "Jenkins"
                        git clone https://x-access-token:${GITHUB_TOKEN}@github.com/hassanmouhamane/SmartTaskManager.git .
                        git checkout main

                        # Debug: Show directory structure
                        echo "=== Workspace structure ==="
                        ls -la
                        echo "=== Looking for pom.xml ==="
                        find . -name "pom.xml" -type f
                    '''
                }
            }
        }

        stage('Build & Package') {
            steps {
                dir('taskmanager') {  // ← KEY CHANGE: Enter the subdirectory
                    sh 'mvn clean compile -DskipTests'
                }
            }
        }

        stage('Run Tests') {
            steps {
                dir('taskmanager') {  // ← KEY CHANGE: Enter the subdirectory
                    sh 'mvn test'
                }
            }
            post {
                failure {
                    echo '❌ Tests failed! Check test reports.'
                    junit 'taskmanager/target/surefire-reports/**/*.xml'
                }
            }
        }

        stage('Package JAR') {
            steps {
                dir('taskmanager') {  // ← KEY CHANGE: Enter the subdirectory
                    sh 'mvn package -DskipTests'
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    // Check if Dockerfile exists in taskmanager directory
                    if (fileExists('taskmanager/Dockerfile')) {
                        dir('taskmanager') {
                            sh 'docker build -t smarttaskmanager:latest .'
                        }
                    } else if (fileExists('Dockerfile')) {
                        // Or in root directory
                        sh 'docker build -t smarttaskmanager:latest .'
                    } else {
                        echo '⚠️  No Dockerfile found. Skipping Docker build.'
                    }
                }
            }
        }

        stage('Run with Docker Compose') {
            steps {
                script {
                    // Check for docker-compose.yml in taskmanager or root
                    if (fileExists('taskmanager/docker-compose.yml')) {
                        dir('taskmanager') {
                            sh 'docker-compose up -d --build'
                        }
                    } else if (fileExists('docker-compose.yml')) {
                        sh 'docker-compose up -d --build'
                    } else {
                        echo '⚠️  No docker-compose.yml found. Skipping Docker Compose.'
                    }
                }
            }
        }
    }

    post {
        success {
            echo '✅ Pipeline executed successfully!'

            // Archive the built JAR from taskmanager directory
            archiveArtifacts 'taskmanager/target/*.jar'
        }
        failure {
            echo '❌ Pipeline failed!'

            // Debug info
            sh '''
                echo "=== Current directory ==="
                pwd
                echo "=== taskmanager/ directory contents ==="
                ls -la taskmanager/ 2>/dev/null || echo "taskmanager directory not found"
                echo "=== Build directory contents ==="
                ls -la taskmanager/target/ 2>/dev/null || echo "No target directory"
            '''
        }
        always {
            // Clean up Docker resources
            sh 'docker-compose down 2>/dev/null || true'
        }
    }
}