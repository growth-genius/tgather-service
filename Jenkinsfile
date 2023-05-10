node {

    try {
        stage('Start') {
            // slackSend (channel: '#jenkins', color: '#FFFF00', message: "STARTED: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]' (${env.BUILD_URL})")
        }

        stage('git sslVerify off') {
            sh(script: "git config --global http.sslVerify false || true")
        }

        stage('git source Pull') {
            checkout scm
        }

        stage("Docker Image Delete") {
            sh(script: "docker rmi ${IMAGE_NAME}:latest  || true")
            sh(script: 'docker rmi $(docker images -f "dangling=true" -q) || true')
        }

        stage("Docker Image build") {
            sh(script: "chmod +x gradlew")
            sh(script: "./gradlew clean bootBuildImage --imageName=${DOCKER_HUB_USER}/${IMAGE_NAME}:latest")
        }

        stage("Docker Image Push") {
            withDockerRegistry(credentialsId: 'docker-hub', url: '') {
                // some block
                sh "docker push ${DOCKER_HUB_USER}/${IMAGE_NAME}:latest"
            }
        }

        stage("Docker Pushed Image delete") {
            sh(script: 'docker rmi ${DOCKER_HUB_USER}/${IMAGE_NAME}:latest || true')
        }

        def remote = [:]
        remote.name = "${SSH_USER}"
        remote.user = "${SSH_USER}"
        remote.host = "${SSH_HOST}"
        remote.port = "${SSH_PORT}"
        remote.password = "${SSH_PASSWORD}"
        remote.allowAnyHosts = true
        stage("SSH Docker Image Pull") {
            sshCommand remote: remote, command: "sudo docker login -u ${DOCKER_HUB_USER} -p ${DOCKER_HUB_PWD}"
            sshCommand remote: remote, command: "sudo docker stop ${IMAGE_NAME} || true"
            sshCommand remote: remote, command: "sudo docker rm ${IMAGE_NAME} || true"
            sshCommand remote: remote, command: "sudo docker rmi ${IMAGE_NAME} || true"
            sshCommand remote: remote, command: "sudo docker run --network ${DOCKER_NETWORK} -m 12g --env JAVA_OPTS='-Dspring.profiles.active=${SPRING_PROFILE} -Djasypt.encryptor.password={DJASYPT_PASSWORD} -Dfile.encoding=UTF-8 -Xmx8192m -XX:MaxMetaspaceSize=1024m' --user root -d -e TZ=Asia/Seoul --name ${IMAGE_NAME} ${DOCKER_HUB_USER}/${IMAGE_NAME}:latest"
        }
        // slackSend (channel: '#jenkins', color: '#00FF00', message: "SUCCESSFUL: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]' (${env.BUILD_URL})")
    }catch(e) {
        // slackSend (channel: '#jenkins', color: '#FF0000', message: "FAILED: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]' (${env.BUILD_URL})")
    }

}