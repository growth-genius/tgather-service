pipeline {
  stages{
        stage('Start') {
            step{
              // slackSend (channel: '#jenkins', color: '#FFFF00', message: "STARTED: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]' (${env.BUILD_URL})")
            }
        }

        stage('git sslVerify off') {
            step{
              sh(script: "git config --global http.sslVerify false || true")
            }
        }

        stage('git source Pull') {
          step{
              checkout scm
          }
        }

        stage("Docker Image Delete") {
          step{

            sh(script: "docker rmi ${IMAGE_NAME}:latest  || true")
            sh(script: 'docker rmi $(docker images -f "dangling=true" -q) || true')
          }
        }

        stage("Docker Image build") {
            step{

                sh(script: "chmod 775 .")
                withGradle {
                    // some block
                    sh "./gradlew clean bootBuildImage --imageName=${DOCKER_HUB_USER}/${IMAGE_NAME}:latest"
                }
            }
        }

        stage("Docker Image Push") {
          step{
            withDockerRegistry(credentialsId: 'docker_hub_id', url: '') {
                // some block
                sh "docker push ${DOCKER_HUB_USER}/${IMAGE_NAME}:latest"
            }
            }
        }

        stage("Docker Pushed Image delete") {
          step{
            sh(script: 'docker rmi ${DOCKER_HUB_USER}/${IMAGE_NAME}:latest || true')
            }
        }

        def remote = [:]
        remote.name = "${SSH_USER}"
        remote.user = "${SSH_USER}"
        remote.host = "${SSH_HOST}"
        remote.port = "${SSH_PORT}"
        remote.password = "${SSH_PASSWORD}"
        remote.allowAnyHosts = true

        stage("SSH Docker Image Pull") {
          step{

              sshCommand remote: remote, command: "docker login -u ${DOCKER_HUB_USER} -p ${DOCKER_HUB_PWD}"
              sshCommand remote: remote, command: "docker stop ${IMAGE_NAME} || true"
              sshCommand remote: remote, command: "docker rm ${IMAGE_NAME} || true"
              sshCommand remote: remote, command: "docker rmi ${IMAGE_NAME} || true"
              sshCommand remote: remote, command: "docker run --network ${DOCKER_NETWORK} -m 12g --env JAVA_OPTS='-Dspring.profiles.active=${SPRING_PROFILE} -Djasypt.encryptor.password={DJASYPT_PASSWORD} -Dfile.encoding=UTF-8 -Xmx8192m -XX:MaxMetaspaceSize=1024m' --user root -d -e TZ=Asia/Seoul --name ${IMAGE_NAME} ${DOCKER_HUB_USER}/${IMAGE_NAME}:latest"
            }
        }
  }

}