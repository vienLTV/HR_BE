pipeline {
    agent any

    parameters {
        string(name: 'GIT_BRANCH', defaultValue: 'develop', description: 'Git branch to build')
    }

    environment {
        VPS_NAME = 'khanhtran'
        VPS_PUBLIC_IP = '34.142.170.23'
        DOCKER_IMAGE_NAME = 'khanhtran95/cetus-core-jvm'
        DOCKER_IMAGE_TAG = 'latest'
        GIT_REPO_URL = 'git@github.com:micro-cetus-app/cetus-core.git'
        GIT_CREDENTIAL_ID = 'github-cetus-core-ssh-key'
        APP_PORT = '8081'
    }

    stages {
        stage('Checkout Code') {
            steps {
                checkout([$class: 'GitSCM',
                          branches: [[name: "${GIT_BRANCH}"]],
                          doGenerateSubmoduleConfigurations: false,
                          extensions: [],
                          submoduleCfg: [],
                          userRemoteConfigs: [[credentialsId: "${GIT_CREDENTIAL_ID}", url: "${GIT_REPO_URL}"]]])
            }
        }

        stage('Maven Build') {
            steps {
                script {
                    sh 'chmod +x ./mvnw'
                    sh './mvnw clean package'
                }
            }
        }

         stage('Build Docker Image') {
            steps {
                script {
                    withCredentials([usernamePassword(credentialsId: 'dockerhub-credentials-id', usernameVariable: 'DOCKER_USERNAME', passwordVariable: 'DOCKER_PASSWORD')]) {
                        sh """
                            echo "\${DOCKER_PASSWORD}" | docker login -u \${DOCKER_USERNAME} --password-stdin
                            docker build -f src/main/docker/Dockerfile.jvm -t ${DOCKER_IMAGE_NAME}:${DOCKER_IMAGE_TAG} .
                            docker push ${DOCKER_IMAGE_NAME}:${DOCKER_IMAGE_TAG}
                        """
                    }
                }
            }
         }

        stage('Deploy to VPS') {
            steps {
                script {
                    withCredentials([
                        sshUserPrivateKey(credentialsId: 'vps-cetus-ssh', keyFileVariable: 'SSH_KEY_FILE'),
                        usernamePassword(credentialsId: 'dockerhub-credentials-id', usernameVariable: 'DOCKER_USERNAME', passwordVariable: 'DOCKER_PASSWORD')
                    ]) {
                        sh """
                            ssh -o StrictHostKeyChecking=no -i \$SSH_KEY_FILE ${VPS_NAME}@${VPS_PUBLIC_IP} << 'EOL'
                            set -e  # Exit on any error

                            echo "Setting up Docker authentication..."
                            echo '{"auths":{"https://index.docker.io/v1/":{"auth":"'"\$(echo -n "${DOCKER_USERNAME}:${DOCKER_PASSWORD}" | base64)"'"}}}' > ~/docker_config.json

                            echo "Checking if port ${APP_PORT} is in use..."
                            sudo lsof -ti:${APP_PORT} | xargs -r sudo kill -9

                            echo "Pulling latest image..."
                            sudo docker --config ~/docker_config.json pull ${DOCKER_IMAGE_NAME}:${DOCKER_IMAGE_TAG}

                            echo "Stopping and removing existing container..."
                            sudo docker stop cetus_core || true
                            sudo docker rm cetus_core || true

                            echo "Starting new container..."
                            sudo docker run -d --name cetus_core \
                                -p ${APP_PORT}:8080 \
                                --restart unless-stopped \
                                --network cetus_network \
                                -e QUARKUS_DATASOURCE_JDBC_URL=jdbc:postgresql://cetus_core_postgres:5432/cetus_core \
                                -e QUARKUS_HTTP_CORS_ORIGINS=http://cetus.site \
                                ${DOCKER_IMAGE_NAME}:${DOCKER_IMAGE_TAG}

                            echo "Verifying deployment..."
                            sleep 10  # Wait for container to start

                            # Check if container is running
                            if ! sudo docker ps | grep -q cetus_core; then
                                echo "Container failed to start!"
                                sudo docker logs cetus_core
                                exit 1
                            fi

                            echo "Cleaning up..."
                            rm -f ~/docker_config.json

                            echo "Deployment completed successfully!"
EOL
                        """
                    }
                }
            }
        }
    }

    post {
        success {
            echo 'Pipeline completed successfully!'
        }
        failure {
            echo 'Pipeline failed! Please check the logs for details.'
        }
        always {
            cleanWs()
        }
    }
}